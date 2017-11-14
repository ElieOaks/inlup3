import java.io.Serializable;

/**
 * The LikeMessage class is used to signal to the server that a client has liked a post.
 *
 * @author  Ulf Sigvardsson
 * @version 1.0
 * @since   2017-11-09 
 */
public class LikeMessage implements Serializable {
    private Like like;
    private int postId;

    /**
     * Constructor for LikeMessage
     * @param like The Like object that is being broadcasted.
     */
    public LikeMessage(Like like) {
        this.like = like;
        this.postId = like.getPostId();
    }

    /**
     * Get-method for the id of the post that was liked.
     * @return int The id nubmer of the psot that was liked.
     */
    public int getPostId() {
        return this.postId;
    }

    /**
     * Get-method for the Like object of an LikeMessage instance.
     * @return Like The Like object of the instance.
     */
    public Like getLike() {
        return this.like;
    }
} 









