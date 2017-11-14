import java.io.Serializable;

/**
 * The Comment class extends the PostAction class. 
 * It is used within instances of Post to signify comments made by users.
 *
 * @author  Ulf Sigvardsson
 * @version 1.0
 * @since   2017-11-09 
 */

public class Comment extends PostAction implements Serializable {
    private String comment;

    /**
     * Constructor for Comment
     * @param commenter The account making the comment.
     * @param madeTo The post that is being commented.
     * @param comment The string making up the comment itself
     */
    public Comment(Account commenter, Post madeTo, String comment) {
        super(commenter, madeTo);
        this.comment = comment;
    }

    /**
     * Converts the Comment to its string respresentation.
     * @return String The string representation of the calling Comment.
     */
    public String toString() {
        return "\t" + this.madeBy.getName() + ": " + comment;
    }
} 
