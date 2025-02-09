package McJarSwap.service;

import McJarSwap.Room;
import McJarSwap.RoomSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;


//Room 객체 임의로 생성해둔것임
@Service
public class RoomService {

    private final List<Room> rooms = new ArrayList<>();
    private final MinecraftServersScanService scanService;
    private final String rootDir;  //이 뒤에 port를 붙여서 경로 사용. ex) port=12345면 minecraft_server12345


    @Autowired
    public RoomService(MinecraftServersScanService scanService) {
        this.scanService = scanService;

        //현재 작업 디렉토리의 부모 디렉토리에 /minecraft_server 붙인 주소
        this.rootDir = new File(System.getProperty("user.home"), "minecraft_server").getPath();
        //System.out.println("설정된 rootDir: " + this.rootDir);

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

            // 명령어 실행 위치
            File targetDir = new File(rootDir + port);

            // 서버 실행 (eula.txt, server.properties 생성)
            startServer(room, targetDir);

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
            startServer(room, targetDir);

            // 서버 리스트에 추가
            rooms.add(room);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //eula.txt 의 false 를 true 로 수정
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

    //server.properties 파일 room 의 name, port, mode 로 수정
    public void editProperties(File propertiesFile, Room room) {

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

           /* try (BufferedReader reader = new BufferedReader(new FileReader(propertiesFile))) {
                System.out.println("🔍 수정된 server.properties 내용:");
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }*/


        } catch (IOException e) {
            System.err.println("server.properties 수정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 서버의 port, mode, jar 파일 수정
    public boolean editSettings(MultipartFile file, String dataJson) {
        try {
            System.out.println("dataJson: " + dataJson);

            // JSON 데이터를 RoomSettings 객체로 변환
            RoomSettings settings = new ObjectMapper().readValue(dataJson, RoomSettings.class);

            String port = settings.getPort(); // 현재 서버의 포트
            String changeport = settings.getChangeport(); // 새로운 포트
            String mode = settings.getMode(); // 새로운 모드

            System.out.println("port: " + port + " changeport: " + changeport + " mode: " + mode);

            String serverDir = rootDir + port;
            File serverFolder = new File(serverDir); // 서버 디렉토리 경로를 나타내는 객체 생성 - startServer 에 사용
            if (!serverFolder.exists()) {
                System.err.println("서버 폴더가 존재하지 않습니다: " + serverDir);
                return false;
            }

            // Room 객체 찾기
            Optional<Room> roomOptional = findRoomByPort(port);
            if (roomOptional.isEmpty()) {
                System.err.println("해당 포트에 대한 Room이 존재하지 않습니다");
                return false;
            }

            Room room = roomOptional.get();

            String pid = getPidByPort(room.getPort());
            //System.out.println("Pid : " + pid);

            // 1. 포트 변경
            if (!changeport.isEmpty()) {

                String newServerDir = rootDir + changeport; //changeport 로 인해 이름이 변경된 디렉토리 주소
                File newServerFolder = new File(newServerDir);

                //서버 중단
                if (pid != null) stopServer(pid);

                //새로운 포트로 디렉토리 이름바꾸기
                if (!moveDirectoryWithCommand(serverFolder, newServerFolder)) {
                    System.err.println("mv 명령어 실패");
                    return false;
                }

                room.setPort(changeport);

                //server.properties 수정을 위한 객체
                File propertiesFile = new File(rootDir + room.getPort() + "/server.properties");

                //server.properties 에서 port 바꾸기
                editProperties(propertiesFile, room);

                startServer(room, newServerFolder);

                return true;
            }

            // 2. 모드 변경
            if (!mode.isEmpty()) {

                //서버 중단
                if (pid != null) stopServer(pid);

                room.setMode(mode);

                //server.properties 수정을 위한 객체
                File propertiesFile = new File(rootDir + room.getPort() + "/server.properties");

                //server.properties 에서 mode 바꾸기
                editProperties(propertiesFile, room);

                startServer(room, serverFolder);

                return true;
            }

            // 3. JAR 파일 교체 (file 이 존재할 경우) 현재 모든 jar 파일의 이름 server.jar 라고 가정
            if (!file.isEmpty()) {

                //서버 중단
                if (pid != null) stopServer(pid);

                File oldJarFile = new File(serverFolder, "server.jar");
                if (oldJarFile.exists()) {
                    oldJarFile.delete(); // 기존 server.jar 삭제
                }

                File newJarFile = new File(serverFolder, "server.jar");
                file.transferTo(newJarFile);

                startServer(room, serverFolder);

                return true;
            }

            //아무것도 수정되지 않은 경우 올바른 Json 이 아님
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //서버 중지
    public void stopServer(String pid) throws Exception {
        String command = "kill -9 " + pid;
        executeCommand(command);
    }

    //서버 시작
    public void startServer(Room room, File Directory) throws Exception {
        //nohup : Spring Boot 와 독립적으로 실행 disown : Spring Boot 종료해도 서버 유지
        String command = "nohup java -Xmx" + room.getXmx() + "M -Xms" + room.getXms() + "M -jar server.jar nogui  > server.log 2>&1 & disown";
        executeCommandByDir(command, Directory);
    }

    //changeport 로 변경된 이름의 dir 만들고 기존 dir 의 파일 옮기는 메서드
    private boolean moveDirectoryWithCommand(File source, File target) {
        try {
            String sourcePath = source.getPath().replace("\\", "/");
            String targetPath = target.getPath().replace("\\", "/");

            String command = "mv " + sourcePath + " " + targetPath;
            //System.out.println(command);

            Process process = new ProcessBuilder("bash", "-c", command).start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return true;
            } else {
                System.err.println("mv 명령어 실패 (exit code: " + exitCode + ")");
                return false;
            }
        } catch (Exception e) {
            System.err.println("mv 실행 중 오류 발생: " + e.getMessage());
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
                String pid = getPidByPort(port);
                if (pid != null) {
                    executeCommand("kill -9 " + pid);
                }

                // 서버 폴더 삭제
                String folderPath = getFolderPathByPort(port);

                System.out.println("Path : " + folderPath);

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

    //port 로 pid 만 가져오기 (getProcessIdByPort()는 한줄 그대로 반환)
    private String getPidByPort(String port) throws Exception {
        Process process = executeCommand("lsof -i :" + port + " | grep LISTEN | tr -s ' ' | cut -d' ' -f2");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String pid = reader.readLine();
            return (pid != null && !pid.isEmpty()) ? pid : null;
        }
    }

    // 특정 포트의 서버 실행 경로 찾기
    private String getFolderPathByPort(String port) throws Exception {
        return scanService.getFolderPath(getPidByPort(port));
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
