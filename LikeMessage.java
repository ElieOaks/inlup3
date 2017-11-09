import java.io.Serializable;

public class LikeMessage implements Serializable {
    private Like like;

    public LikeMessage(Like like) {
        this.like = like;
    }

    public Like getLike() {
        return this.like;
    }
} 









