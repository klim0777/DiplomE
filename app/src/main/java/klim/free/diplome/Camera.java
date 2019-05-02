package klim.free.diplome;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Camera {
    private String ip, port;
    private String login, password;
    private Integer number;

    Camera(String ip, String port, Integer number) {
        this.ip = ip;
        this.port = port;
        this.number = number;
        login = "admin";
        password = "Supervisor";
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
