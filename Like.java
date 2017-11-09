import java.io.Serializable;

public class Like implements Serializable {
    private Account likesThis;
    private Post liked;

    public Like(Account likesThis, Post liked) {
        this.likesThis = likesThis;
        this.liked = liked;
    }

    public getWhoLikes() {
        return this.likesThis;
    }

    public getLikedPost() {
        return this.liked;
    }
} 
