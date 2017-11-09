import java.io.Serializable;
import java.util.Date;

public class FriendRequest implements Serializable, Comparable<FriendRequest>{
    private Account requester;
    private Account toBefriend;
    private Date timeStamp;
    
    public FriendRequest(Account requester, Account toBefriend) {
        this.requester  = requester;
        this.toBefriend = toBefriend;
        this.timeStamp  = new Date(); 
    }

    public Account getRequester() {
        return this.requester;
    }

    public Account getToBefriend() {
        return this.toBefriend;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public boolean hasTimedOut() {
        Date compare = new Date();
        return compare.getTime() - this.timeStamp.getTime() > 24000;
    }
    public int compareTo(FriendRequest f) {
        if(f.getRequester().getUserId().compareTo(this.getRequester().getUserId()) == 0) {
            return f.getToBefriend().getUserId().compareTo(this.getToBefriend().getUserId()); 
        }
        else {
            return f.getRequester().getUserId().compareTo(this.getRequester().getUserId());           
        }

    }

    public boolean equals(Object o) {
        if (o instanceof FriendRequest) {
            return ((FriendRequest) o).compareTo(this) == 0;
        } else {
            return false;
        }
    }
}
