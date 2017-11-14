import java.io.Serializable;

/**
 * The CommentMessage class is used to allow a client to 
 * notify the server that it has commented on a post.
 *
 * @author  Ulf Sigvardsson
 * @version 1.0
 * @since   2017-11-09 
 */

public class CommentMessage implements Serializable {
    private Comment comment;
    private int postId;

    /**
     * Constructor for CommentMessage
     * @param comment Comment object containing information regarding the comment itself, poster, post etc.
     */
    public CommentMessage(Comment comment) {
        this.comment = comment;
        this.postId = comment.getPostId();
    }

    /**
     * Get-method for an instance's Comment member.
     * @return Comment The Comment object associated with this instance.
     */
    public Comment getComment() {
        return this.comment;
    }

    /**
     * Get-method for the post id of the post stored in an instance's Comment member.
     * @return int The post id
     */    
    public int getPostId() {
        return this.postId;
    }

}
