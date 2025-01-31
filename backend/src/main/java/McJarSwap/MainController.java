package McJarSwap;

import McJarSwap.service.RoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MainController {

    private final RoomService roomService;
    private final ObjectMapper objectMapper; // json을 room객체로 변환에 사용

    @Autowired
    public MainController(RoomService roomService, ObjectMapper objectMapper) {
        this.roomService = roomService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public List<Map<String, String>> getRooms() {

        // TODO return roomService.현재실행중인서버목록을조회해서room객체를만들어반환해주는메서드();

        // 날릴 내용(아래)
        return roomService.getRooms().stream()
                .map(room -> Map.of(
                        "port", room.getPort(),
                        "name", room.getName(),
                        "mode", room.getMode()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/addroom")
    public ResponseEntity<?> addRoom(
            @RequestParam("file") MultipartFile file,
            @RequestParam("data") String dataJson) {

        /*
         * file과 dataJSON을 분석해서 Service에 존재하는 메서드로 넘기기만 하면 됌.
         * 전제조건 : Service에 file과 dataJSON정보들을 받으면 새 서버를 실행시키는 메서드가 존재해야 함
         */
        // TODO roomService.파일과데이터를받으면서버를만드는메서드(file, data들....);
        return ResponseEntity.ok("Success");
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
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("data") String dataJson) {

        // TODO boolean updated=roomService.파일과dataJSON을받아서변경점이뭔지찾고변경한뒤결과를반환하는메서드(file, dataJson);
        boolean updated = true;
        /* 날릴 내용
        RoomSettings updateData = null;
        try {
            updateData = objectMapper.readValue(dataJson, RoomSettings.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("설정을 저장할 수 없습니다.");
        }
        boolean updated = roomService.updateRoomSettings(
                updateData.getPort(),
                updateData.getChangeport(),
                updateData.getMode(),
                file
        );
        */

        if (updated) {
            return ResponseEntity.ok("설정이 성공적으로 저장되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("설정을 저장할 수 없습니다. 포트를 확인하세요.");
        }
    }

    //@GetMapping("/delete") // localhost 에서는 GetMapping 으로해야 정상작동
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRoom(@RequestParam("port") String port) {
        boolean deleted = roomService.deleteByPort(port);

        if (deleted) {
            return ResponseEntity.ok("포트 " + port + "의 방이 삭제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("삭제할 방을 찾을 수 없습니다: " + port);
        }
    }

}
