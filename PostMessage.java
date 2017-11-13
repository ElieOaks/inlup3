import java.io.Serializable;

public class PostMessage implements Serializable {
    private String msg;

    
    /**
     * Constructor for PostMessage
     * @param msg String containing the post that was made.
     */
    public PostMessage(String msg) {
        this.msg = msg;
    }

    /**
     * Get-method for an instance's message
     * @return String The instance's message member.
     */
    public String getMsg() {
        return msg;
    }
}
