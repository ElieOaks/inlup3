
/**
 * The NameCHange class is used to signal to the server that a client has changed its name.
 *
 * @author  Ulf Sigvardsson
 * @version 1.0
 * @since   2017-11-09 
 */
public class NameChange implements java.io.Serializable {

    private String name;

    /**
     * Constructor for NameChange
     * @param name The new name of the account.
     */
    public NameChange(String name) {
        this.name = name;

    }

    /**
     * Get-method for an instances name member.
     * @return String The new name.
     */
    public String getName() {
        return this.name;
    }

}
