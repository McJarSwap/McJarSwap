package McJarSwap.service;

import McJarSwap.Room;
import McJarSwap.RoomSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
    //이 뒤에 port를 붙여서 경로 사용. ex) port=12345면 kkang5430\\minecraft_server12345
    private final String rootDir = "\\\\wsl.localhost\\Ubuntu\\home\\kkang5430\\minecraft_server";


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

    public boolean makeServer(MultipartFile file, String dataJson) {

        try {
            // JSON 데이터를 Room 객체로 변환
            Room room = new ObjectMapper().readValue(dataJson, Room.class);
            String port = room.getPort();

            // 업로드된 JAR 파일 저장 경로
            String serverDir = rootDir + port;
            File directory = new File(serverDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // JAR 파일 저장
            File jarFile = new File(serverDir + "/server.jar");
            file.transferTo(jarFile);


            File targetDir = new File(rootDir + port);

            // 서버 실행 명령어 작성
            String command = "java -Xmx" + room.getXmx() + "M -Xms" + room.getXms() + "M -jar server.jar nogui";

            // 프로세스 실행
            executeCommandByDir(command, targetDir);

            // 8초 시간 두기

            Thread.sleep(8000);

            //eula.txt 수정
            boolean eulaEdited = editEula(port);

            //server.properties 수정
            File propertiesFile = new File(rootDir + port + "/server.properties");
            if (propertiesFile.exists()) {
                editProperties(propertiesFile, room);
            }

            // 다시 서버 실행
            executeCommandByDir(command, targetDir);

            // 서버 리스트에 추가
            rooms.add(room);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean editEula(String port) {
        String serverDir = rootDir + port;
        File eulaFile = new File(serverDir + "/eula.txt");

        if (!eulaFile.exists()) {
            System.err.println("eula.txt 파일이 존재하지 않습니다");
            return false;
        }

        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(eulaFile))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null) {
                    if (lineCount == 2) { // 3번째 줄을 수정
                        lines.add("eula=true");
                    } else {
                        lines.add(line);
                    }
                    lineCount++;
                }
            }

            // 파일 다시 쓰기
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(eulaFile))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("eula.txt 수정 완료");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void editProperties(File propertiesFile, Room room) {

        System.out.println("mode :" + room.getMode());

        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(propertiesFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("level-name=")) {
                        lines.add("level-name=" + room.getName());
                    } else if (line.startsWith("server-port=")) {
                        lines.add("server-port=" + room.getPort());
                    } else if (line.startsWith("gamemode=")) {
                        lines.add("gamemode=" + room.getMode());
                    } else {
                        lines.add(line);
                    }
                }
            }

            // 파일 다시 쓰기
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(propertiesFile))) {
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            }

            System.out.println("server.properties 수정 완료");

            //수정 내용 확인
            try (BufferedReader reader = new BufferedReader(new FileReader(propertiesFile))) {
                System.out.println("🔍 수정된 server.properties 내용:");
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }


        } catch (IOException e) {
            System.err.println("server.properties 수정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean editSettings(MultipartFile file, String dataJson) {
        try {
            System.out.println("여기까지 오케이");
            System.out.println("dataJson: " + dataJson);

            // JSON 데이터를 RoomSettings 객체로 변환
            RoomSettings settings = new ObjectMapper().readValue(dataJson, RoomSettings.class);
            System.out.println("여기까지 오케이");
            String port = settings.getPort(); // 현재 서버의 포트
            String changeport = settings.getChangeport(); // 새로운 포트
            String mode = settings.getMode(); // 변경할 모드

            System.out.println("여기까지 오케이");
            // 서버 디렉토리 경로를 나타내는 객체 생성
            String serverDir = rootDir + port;
            File serverFolder = new File(serverDir);
            if (!serverFolder.exists()) {
                System.err.println("서버 폴더가 존재하지 않습니다: " + serverDir);
                return false;
            }

            System.out.println("여기까지 오케이");
            // Room 객체 찾기
            Optional<Room> roomOptional = findRoomByPort(port);
            if (roomOptional.isEmpty()) {
                System.err.println("해당 포트에 대한 Room이 존재하지 않습니다");
                return false;
            }

            Room room = roomOptional.get();

            //Todo 1,2,3 각각 server.properties 수정
            System.out.println("여기까지 오케이");
            // 1. 포트 변경
            if (changeport != null && !changeport.isEmpty()) {
                String newServerDir = rootDir + changeport;
                File newServerFolder = new File(newServerDir);

                //디렉토리 이름 변경
                if (serverFolder.renameTo(newServerFolder)) {
                    room.setPort(changeport); // Room 객체의 포트 업데이트
                    return true;
                } else {
                    return false;
                }
            }

            // 2. 모드 변경 (mode가 존재할 경우)
            if (mode != null && !mode.isEmpty()) {
                room.setMode(mode);
                return true;
            }

            // 3. JAR 파일 교체 (file이 존재할 경우) 현재, 모든 jar파일의 이름 server.jar라고 가정
            if (file != null && !file.isEmpty()) {
                File oldJarFile = new File(serverFolder, "server.jar");
                if (oldJarFile.exists()) {
                    oldJarFile.delete(); // 기존 server.jar 삭제
                }

                File newJarFile = new File(serverFolder, "server.jar");
                file.transferTo(newJarFile);
                return true;
            }

            //아무것도 수정되지 않은 경우
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 실행 중인 서버가 있는지 확인하고 삭제하는 메서드
    public boolean deleteRoomByPort(String port) {
        Optional<Room> roomOptional = findRoomByPort(port);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();

            // 실행 중인 서버 리스트 가져오기 (scan 기능 활용)
            List<Room> runningServers = scanService.scanMinecraftServers();

            // 실행 중인지 확인
            boolean isRunning = runningServers.stream()
                    .anyMatch(r -> r.getPort().equals(port));

            if (!isRunning) {
                System.out.println("실행 중인 서버가 아닙니다: " + port);
                return false;
            }

            try {
                // 실행 중인 서버 프로세스 ID 찾기
                String pid = getProcessIdByPort(port);
                if (pid != null) {
                    executeCommand("kill -9 " + pid);
                }

                // 서버 폴더 삭제
                String folderPath = getFolderPathByPort(port);
                if (folderPath != null) {
                    executeCommand("rm -rf " + folderPath);
                }

                // Room 목록에서 삭제
                rooms.remove(room);
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    // 특정 포트에서 실행 중인 프로세스 ID 찾기
    private String getProcessIdByPort(String port) throws Exception {
        Process process = executeCommand("lsof -i :" + port + " | grep LISTEN | awk '{print $2}'");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine(); // 첫 번째 줄이 PID
        }
    }

    // 특정 포트의 서버 실행 경로 찾기
    private String getFolderPathByPort(String port) throws Exception {
        return scanService.getFolderPath(getProcessIdByPort(port));
    }

    // 리눅스 명령어 실행 메서드
    private Process executeCommand(String command) throws Exception {
        return new ProcessBuilder("bash", "-c", command).start();
    }

    // 실행하려는 위치도 받아서 리눅스 명령어 실행
    private void executeCommandByDir(String command, File directory) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        processBuilder.directory(directory); // 실행할 디렉토리 지정
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // 실행된 명령어 출력
        System.out.println("실행 중: " + command + " (디렉토리: " + directory.getAbsolutePath() + ")");

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
