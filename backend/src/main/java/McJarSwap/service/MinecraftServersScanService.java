package McJarSwap.service;

import McJarSwap.Room;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinecraftServersScanService {

    public List<Room> scanMinecraftServers() {
        List<Room> rooms = new ArrayList<>();

        try {
            // Step 1: PID 찾기
            List<String> pids = getPIDs();

            for (String pid : pids) {
                // Step 2: 폴더 경로 찾기
                String folderPath = getFolderPath(pid);

                if (folderPath == null || folderPath.isEmpty()) continue;

                // Step 3: server.properties 파일에서 정보 읽기
                File propertiesFile = new File(folderPath + "/server.properties");
                if (!propertiesFile.exists()) continue;

                Room room = parseServerProperties(propertiesFile);

                if (room != null) {
                    rooms.add(room);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rooms;
    }

    private List<String> getPIDs() throws Exception {
        List<String> pids = new ArrayList<>();
        String command = " \"unset $(compgen -v); ps aux | grep '[M]cJarSwap'  | awk '{print $2}'\"";

        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                pids.add(line.trim());
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to retrieve PIDs");
        }
        System.out.println("Found PIDs: " + pids); // 디버깅용 로그
        return pids;
    }

    public String getFolderPath(String pid) throws Exception {
        String command = "pwdx " + pid;

        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            if (line != null && line.contains(": ")) {
                return line.split(": ")[1].trim();
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to retrieve folder path for PID: " + pid);
        }

        return null;
    }

    private Room parseServerProperties(File propertiesFile) {
        String name = "world"; // 기본값
        String port = "25565"; // 기본값
        String mode = "survival"; // 기본값

        try (BufferedReader reader = new BufferedReader(new FileReader(propertiesFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("level-name")) {
                    name = line.split("=")[1].trim();
                } else if (line.startsWith("server-port")) {
                    port = line.split("=")[1].trim();
                } else if (line.startsWith("gamemode")) {
                    mode = line.split("=")[1].trim();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return new Room(port, name, mode);
    }
}
