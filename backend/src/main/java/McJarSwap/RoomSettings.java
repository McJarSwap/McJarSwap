package McJarSwap;

public class RoomSettings {
    //room setting 을 위해 받는 data
    private String port;
    private String changeport;
    private String mode;

    //역직렬화를 위한 기본 생성자
    public RoomSettings() {

    }


    public RoomSettings(String port, String changeport, String mode) {
        this.port = port;
        this.changeport = changeport;
        this.mode = mode;
    }

    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }

    public String getChangeport() { return changeport; }
    public void setChangeport(String changeport) { this.changeport = changeport; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
}
