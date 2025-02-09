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

    public MainController(RoomService roomService, ObjectMapper objectMapper) {
        this.roomService = roomService;
    }

    @GetMapping("/")
    public List<Map<String, String>> getRooms() {
        return roomService.getRoomsAsMap();
    }

    @PostMapping("/addroom")
    public ResponseEntity<?> addRoom(
            @RequestParam("file") MultipartFile file,
            @RequestParam("data") String dataJson) {

        boolean added = roomService.makeServer(file, dataJson);

        if (added) {
            return ResponseEntity.ok("Success");
        } else {
            return ResponseEntity.badRequest().body("Error");
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
    public ResponseEntity<String> saveSettings(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("data") String dataJson) {

        boolean saved = roomService.editSettings(file, dataJson);

        if (saved) {
            return ResponseEntity.ok("정상적으로 수정되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("수정 중 오류 발생.");
        }
    }

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
