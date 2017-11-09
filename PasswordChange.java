import java.io.Serializable;

public class PasswordChange implements Serializable{
    private String password;

    public PasswordChange(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }
}
