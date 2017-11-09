import java.io.Serializable;
import java.util.*;

public class Post implements Serializable {
    private int globalPostId;
    private Account poster;
    private String content;
    private List<Comment> comments;
    private int likes;
    private List<Like> whoLikes;
    
    public Account getPoster() {
        return this.poster;
    }

    public Post(int globalPostId, Account poster, String content) {
        this.globalPostId = globalPostId;
        this.poster       = poster;
        this.content      = content;
        this.comments     = new LinkedList<Comment>();
        this.whoLikes     = new LinkedList<Like>();
    }

    public String render() {
        String result = "{" + this.poster.getName() + "} says: " + this.content + "\n\n";

        result += this.renderLikes();
        
        if (this.comments.size() > 0) {
            for(Comment comment : comments) {
                result += comment.toString();
            }
            result += "\t-----------------------------\n";
        }
        return result; 
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public int getPostId() {
        return this.globalPostId;
    }

    public void like(Like like) {
        this.whoLikes.add(like);
        this.likes++;
    }

    private String renderLikes() {
        String result = "";
        if(this.likes > 0) {
            result += this.likes + "people likes this. (";
            for(Like l : this.whoLikes) {
                result += l.toString()+" ";
            }
        }
        return result;
    }
}
