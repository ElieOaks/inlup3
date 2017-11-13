import java.io.Serializable;

public class Comment implements Serializable {
    private Account author;
    String comment;
    private int globalPostId;

    public Comment(Account author, String comment) {
        this.author = author;
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public Account getAuthor() {
        return this.author;
    }

    public String toString() {
        return "\t|" + this.author.getName() + ": " + this.comment+"\n";
    }
} 
