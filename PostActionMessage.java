/**
 * The PostActionMessage class is used to allow the server to broadcast to clients that a Post
 * has been liked or commented. 
 *
 * @author  Ulf Sigvardsson
 * @version 1.0
 * @since   2017-11-09 
 */
public class PostActionMessage {
    private PostAction action;
    private int globalPostId;
    protected Post post;

     /**
      * Constructor for PostActionMessage
      * @param action PostAction object containing the action that was made.
      * @param globalPostId This id of this instance's PostAction.
      */
    public PostActionMessage(PostAction action, int globalPostId) {
        this.action = action;
        this.globalPostId = globalPostId;
    }

    /**
     * Get-method for an instance's PostAction object.
     * @return PostAction The instance's PostAction member. 
     */
    public PostAction getAction() {
        return this.action;
    }

    /**
     * Get-method for the post that has been liked or commented.
     * @return PostAction The instance's Post member. 
     */
    public Post getPost() {
        return this.post;
    }

}
