import java.io.Serializable;

/**
 * The PostAction class is used as a super class to Like and Comment.
 *
 * @author  Ulf Sigvardsson
 * @version 1.0
 * @since   2017-11-09 
 */
public class PostAction implements Serializable {
    protected Account madeBy; 
    protected Post madeTo;
    protected int postId;
    protected int actionId;

    /**
     * Constructor for PostAction
     * @param madeBy The account that made the like or comment
     * @param madeTo The post that was liked or commented.
     */
    public PostAction(Account madeBy, Post madeTo) {
        this.madeBy = madeBy;
        this.madeTo = madeTo;
        this.postId = madeTo.getPostId();
    }

    /**
     * Get-method for the account that made the action.
     * @return Account The account that made the action.
     */
    public Account getMaker() {
        return this.madeBy;
    }

    public void setMaker(Account maker) {
        this.madeBy = maker;
    }

    /**
     * Get-method for the account that made the post contained in an instance.
     * @return Account The account that authored the post.
     */
    public Account getPoster(){
        return this.madeTo.getPoster();
    }

    /**
     * Get-method for the id of the post contained in an instance.
     * @return int The id of the post being liked or commented.
     */
    public int getPostId() {
        return this.postId;
    }

    /**
     * Set-method for the actions unique id number.
     * @param id The new id number
     */
    public void setActionId(int id) {
        this.actionId = id;
    }

} 
