package McJarSwap;

public class Room {
    private String port;
    private String name;
    private String mode;

    private String xmx;
    private String xms;

    public Room(String port, String name, String mode) {
        this.port = port;
        this.name = name;
        this.mode = mode;
    }

    public Room(String port, String name, String mode, String xmx, String xms) {
        this.port = port;
        this.name = name;
        this.mode = mode;
        this.xmx = xmx;
        this.xms = xms;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
    public String getXmx() {
        return xmx;
    }

    public void setXmx(String xmx) {
        this.xmx = xmx;
    }
    public String getXms() {
        return xms;
    }

    public void setXms(String xms) {
        this.xms = xms;
    }
}
