package McJarSwap;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


//Room 객체 임의로 생성해둔것임
@Service
public class RoomService {

    //초기 데이터 임의로 설정
    private final List<Room> rooms = new ArrayList<>(Arrays.asList(
            new Room("12345", "방 이름 1", "Creative"),
            new Room("12346", "방 이름 2", "Survival"),
            new Room("12347", "방 이름 3", "Adventure")
    ));

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

    public boolean updateRoomSettings(String port, String changePort, String mode, MultipartFile jarFile) {
        Optional<Room> roomOptional = findRoomByPort(port);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            if (changePort != null) room.setPort(changePort);
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

    public boolean deleteByPort(String port) {
        Optional<Room> roomOptional = findRoomByPort(port);

        if (roomOptional.isPresent()) {
            rooms.remove(roomOptional.get());
            return true;
        }
        return false;
    }

}
