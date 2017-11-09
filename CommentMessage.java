import java.io.Serializable;

public class CommentMessage implements Serializable {
    private String comment;
    private int postId;
    private Account author;

    public CommentMessage(String comment, int postId, Account author) {
        this.comment = comment;
        this.postId = postId;
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public int getPostId() {
        return this.postId; 
    }

    public Account getAuthor() {
        return this.author;
    }
}
