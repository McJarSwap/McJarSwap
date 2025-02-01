package McJarSwap;

import McJarSwap.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MainController {

    private RoomService roomService;
    private ObjectMapper objectMapper; // json을 room객체로 변환에 사용

    public MainController(RoomService roomService, ObjectMapper objectMapper) {
        this.roomService = roomService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public List<Map<String, String>> getRooms() {
        return roomService.getRoomsAsMap();
    }

    @PostMapping("/addroom")
    public ResponseEntity<?> addRoom(
            @RequestParam("file") MultipartFile file,
            @RequestParam("data") String dataJson) {


        try {
            // JSON 문자열을 Room 객체로 변환
            Room room = objectMapper.readValue(dataJson, Room.class);

            // 포트 중복 확인
            if (!roomService.isValidPort(room.getPort())) {
                return ResponseEntity.badRequest().body("포트가 이미 사용 중입니다: " + room.getPort());
            }

            /*
            // 파일 저장 처리 아직 구현 안됨(주석 해제하면 오류)
            String uploadDir = "uploads/";
            file.transferTo(new java.io.File(uploadDir + file.getOriginalFilename()));
            */

            // 방 추가 처리
            Room createdRoom = roomService.addRoom(room);

            return ResponseEntity.ok(objectMapper.writeValueAsString(createdRoom));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("방 생성 중 오류 발생: " + e.getMessage());
        }

    }

    @GetMapping("/checkup")
    public Map<String, String> checkPortAvailability(@RequestParam("port") String port) {
        boolean valid = roomService.isValidPort(port);

        Map<String, String> response = new HashMap<>();
        response.put("validate", String.valueOf(valid));

        return response;
    }


    @PostMapping("/settings/save")
    public ResponseEntity<?> saveSettings(
            @RequestParam("file") MultipartFile file,
            @RequestParam("data") String dataJson) {

        try {
            //data는 port, changePort, mode로 구성
            RoomSettings updateData = objectMapper.readValue(dataJson, RoomSettings.class);

            if (updateData.getPort() == null || updateData.getPort().isEmpty()) {
                return ResponseEntity.badRequest().body("포트 번호는 필수입니다.");
            }

            boolean updated = roomService.updateRoomSettings(
                    updateData.getPort(),
                    updateData.getChangeport(),
                    updateData.getMode(),
                    file
            );

            if (updated) {
                return ResponseEntity.ok("설정이 성공적으로 저장되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("설정을 저장할 수 없습니다. 포트를 확인하세요.");
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("설정 저장 중 오류 발생: " + e.getMessage());
        }
    }

    // @GetMapping("/delete")
    // localhost 에서는 GetMapping 으로 해야 정상작동
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRoom(@RequestParam("port") String port) {
        boolean deleted = roomService.deleteRoomByPort(port);
        if (deleted) {
            return ResponseEntity.ok("정상적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("서버가 실행 중인지 확인하세요.");
        }
    }
}
