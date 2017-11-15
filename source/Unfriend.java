import java.io.Serializable;

public class Unfriend implements Serializable, Comparable<Unfriend> {
    private Account unfriender;
    private Account toUnfriend;

    public Unfriend(Account unfriender, Account toUnfriend) {
        this.unfriender = unfriender;
        this.toUnfriend = toUnfriend;
    }

    public Account getUnfriender() {
        return this.unfriender;
    }

    public Account getToUnfriend() {
        return this.toUnfriend;
    }

    public int compareTo(Unfriend u) {
        if(u.getUnfriender().getUserId().compareTo(this.getUnfriender().getUserId()) == 0) {
            return u.getToUnfriend().getUserId().compareTo(this.getToUnfriend().getUserId()); 
        }
        else {
            return u.getUnfriender().getUserId().compareTo(this.getUnfriender().getUserId());           
        }
    }
}
