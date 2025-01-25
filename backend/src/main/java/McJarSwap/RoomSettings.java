package McJarSwap;

public class RoomSettings {
    //room setting 을 위해 받는 data
    private String port;
    private String changePort;
    private String mode;


    public RoomSettings(String port, String changePort, String mode) {
        this.port = port;
        this.changePort = changePort;
        this.mode = mode;
    }

    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }

    public String getChangePort() { return changePort; }
    public void setChangePort(String changePort) { this.changePort = changePort; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
}
