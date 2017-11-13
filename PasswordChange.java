import java.io.Serializable;


/**
 * The PassWordChange class is used to broadcast a password change made by a user to the server.
 *
 * @author  Ulf Sigvardsson
 * @version 1.0
 * @since   2017-11-09 
 */
public class PasswordChange implements Serializable{
    private String password;

    /**
     * Constructor for PassWordChange
     * @param password The new password
     */
    public PasswordChange(String password) {
        this.password = password;
    }

    /**
     * Gets the new password.
     * @return String The new password
     */
    public String getPassword() {
        return this.password;
    }
}
