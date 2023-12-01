package pns.project.pz.utils;

public class Settings {
    private Settings(){}
    private static final Settings instanceOf = new Settings();
    private String host;
    private String password;
    private int port;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static Settings getInstanceOf() {
        return instanceOf;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
