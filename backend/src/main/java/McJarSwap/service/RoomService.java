package McJarSwap.service;

import McJarSwap.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;


//Room 객체 임의로 생성해둔것임
@Service
public class RoomService {

    //초기 데이터 임의로 설정
    /*
    private final List<Room> rooms = new ArrayList<>(Arrays.asList(
           new Room("12345", "방 이름 1", "Creative"),
           new Room("12346", "방 이름 2", "Survival"),
           new Room("12347", "방 이름 3", "Adventure")
    ));
     */
    private final List<Room> rooms = new ArrayList<>();
    private final MinecraftServersScanService scanService;

    @Autowired
    public RoomService(MinecraftServersScanService scanService) {
        this.scanService = scanService;
       loadRoomsFromScan();
    }

    // Room 객체 리스트를 Map 리스트로 변환하는 새로운 메서드
    public List<Map<String, String>> getRoomsAsMap() {
        return rooms.stream()
                .map(this::convertRoomToMap)
                .collect(Collectors.toList());
    }

    // Room 객체 -> Map 변환 메서드 (private)
    private Map<String, String> convertRoomToMap(Room room) {
        return Map.of(
                "port", room.getPort(),
                "name", room.getName(),
                "mode", room.getMode()
        );
    }
    //실제 실행 중인 마인크래프트 서버 목록을 가져와 rooms 리스트를 초기화
    private void loadRoomsFromScan() {
        List<Room> scannedRooms = scanService.scanMinecraftServers();
        rooms.clear();
        rooms.addAll(scannedRooms);
    }

    //rooms 조회
    public List<Room> getRooms(){
        return rooms;
    }

    //새로운 room 생성
    public Room addRoom(Room room){
        rooms.add(room);
        return room;
    }

    //port 중복 검사
    public boolean isValidPort(String port) {
        return rooms.stream().noneMatch(r -> r.getPort().equals(port));
    }

    //port 로 room 찾기
    public Optional<Room> findRoomByPort(String port) {
        return rooms.stream().filter(r -> r.getPort().equals(port)).findFirst();
    }

    public boolean updateRoomSettings(String port, String changeport, String mode, MultipartFile jarFile) {
        Optional<Room> roomOptional = findRoomByPort(port);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            if (changeport != null) room.setPort(changeport);
            if (mode != null) room.setMode(mode);
            if (jarFile != null && !jarFile.isEmpty()) {
                saveJarFile(jarFile);
            }
            return true;
        }
        return false;
    }


    private void saveJarFile(MultipartFile jarFile) {
        String uploadDir = "uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            jarFile.transferTo(new File(uploadDir + jarFile.getOriginalFilename()));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage(), e);
        }
    }


    // 🔹 실행 중인 서버가 있는지 확인하고 삭제하는 메서드
    public boolean deleteRoomByPort(String port) {
        Optional<Room> roomOptional = findRoomByPort(port);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();

            // 🔥 실행 중인 서버 리스트 가져오기 (scan 기능 활용)
            List<Room> runningServers = scanService.scanMinecraftServers();

            // 🔍 실행 중인지 확인
            boolean isRunning = runningServers.stream()
                    .anyMatch(r -> r.getPort().equals(port));

            if (!isRunning) {
                System.out.println("🚫 실행 중인 서버가 아닙니다: " + port);
                return false;
            }

            try {
                // 1️⃣ 실행 중인 서버 프로세스 ID 찾기
                String pid = getProcessIdByPort(port);
                if (pid != null) {
                    executeCommand("kill -9 " + pid);
                }

                // 2️⃣ 서버 폴더 삭제
                String folderPath = getFolderPathByPort(port);
                if (folderPath != null) {
                    executeCommand("rm -rf " + folderPath);
                }

                // 3️⃣ Room 목록에서 삭제
                rooms.remove(room);
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    // 🔹 특정 포트에서 실행 중인 프로세스 ID 찾기
    private String getProcessIdByPort(String port) throws Exception {
        Process process = executeCommand("lsof -i :" + port + " | grep LISTEN | awk '{print $2}'");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine(); // 첫 번째 줄이 PID
        }
    }

    // 🔹 특정 포트의 서버 실행 경로 찾기
    private String getFolderPathByPort(String port) throws Exception {
        String pid = getProcessIdByPort(port);
        if (pid != null) {
            Process process = executeCommand("pwdx " + pid);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && line.contains(": ")) {
                    return line.split(": ")[1].trim();
                }
            }
        }
        return null;
    }

    // 🔹 리눅스 명령어 실행 메서드
    private Process executeCommand(String command) throws Exception {
        return new ProcessBuilder("bash", "-c", command).start();
    }
}
