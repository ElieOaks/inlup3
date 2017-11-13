import java.io.Serializable;

/**
 * The Like class extends the PostAction class. 
 * It is used within instances of Post to signify likes made by users.
 *
 * @author  Ulf Sigvardsson
 * @version 1.0
 * @since   2017-11-09 
 */
public class Like extends PostAction implements Serializable {

    /**
     * Constructor for Like
     * @param likesThis The account that likes this.
     * @param likedPost The post that is being liked.
     */
    public Like(Account likesThis, Post likedPost) {
        super(likesThis, likedPost);
    }

    /**
     * Converts the Like to its string respresentation.
     * @return String The string representation of the author of the liked post.
     */
    public String toString() {
        return this.madeBy.getName();
    }
} 
