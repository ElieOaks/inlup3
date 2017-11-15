import java.io.Serializable;
import java.util.*;

/**
 * The Post class represents post made by users on the server.
 */
public class Post implements Serializable {
    private int globalPostId;
    private Account poster;
    private String content;
    private List<Comment> comments;
    private int likes;
    private List<Like> whoLikes;
    
  
    /**
     * Constructor for Post
     * @param globalPostId Unique ID-number associated with this Post
     * @param poster Account that made this post.
     * @param content The string containing the content of the post.
     */
    public Post(int globalPostId, Account poster, String content) {
        this.globalPostId = globalPostId;
        this.poster       = poster;
        this.content      = content;
        this.comments     = new LinkedList<Comment>();
        this.whoLikes     = new LinkedList<Like>();
    }

    //For testing purposes only.
    public List<Comment> getComments() {
        return this.comments;
    }

    //For testing purposes only.
    public int getNumberOfLikes() {
        return this.likes;
    }
    
    public Account getPoster() {
        return this.poster;
    }

    /**
     * Converts the Post to its string respresentation.
     * @return String The string representation of the calling Post including possible comments and number of likes.
     */
    public String render() {
        String result = "{" + this.poster.getName() + "} says: " + this.content + "\n\n";

        if(this.likes>0 || this.comments.size()>0) {
            result += this.renderLikes() + this.renderComments() + "\n\t-----------------------------\n";
        }
        return result; 
    }

    /**
     * Fetches the string representations of all comments attached to this post.
     * @return String The combined string representation of all comments to the calling post.
     */
    private String renderComments() {
        String result = "";
        for(Comment comment : this.comments){
            result += comment.toString();
        }
        return result;
    }

    /**
     * Adds a comment to the posts list of comments.
     * @param comment The Comment-object to be added.
     */    
    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    /**
     * Returns the unique ID of the calling post.
     * @return int The ID-number of the calling post.
     */ 
    public int getPostId() {
        return this.globalPostId;
    }

    /**
     * Adds a Like to the posts list of likes and increments the like-counter by one.
     * @param like The Like-object to add.
     */    
    public void like(Like like) {
        this.whoLikes.add(like);
        this.likes += 1;
    }

    /**
     * Renders the amount of likes in a post as a string.
     * @return String The calling posts like count as a string if greater than 0, otherwise an empty string. 
     */    
    private String renderLikes() {
        String result = "";
        if(this.likes>0)
            return "\t+" + this.likes + "\n";
        else
            return result;
    }
}
