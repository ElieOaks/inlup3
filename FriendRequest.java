import java.io.Serializable;
import java.util.Date;

/**
 * The FriendRequest class is used to allow a client to 
 * notify the server that it has requested to befriend another user.
 *
 * @author  Ulf Sigvardsson
 * @version 1.0
 * @since   2017-11-09 
 */
public class FriendRequest implements Serializable, Comparable<FriendRequest>{
    private Account requester;
    private Account toBefriend;
    private Date timeStamp;
    private static int timeLimit = 24000;
    /**
     * Constructor for FriendRequest
     * @param requester The account making the requests.
     * @param toBefriend The account that is the target of the request.
     */    
    public FriendRequest(Account requester, Account toBefriend) {
        this.requester  = requester;
        this.toBefriend = toBefriend;
        this.timeStamp  = new Date(); 
    }

    /**
     * Get-method for the object's requester member.
     * @return Account The object's requester
     */    
    public Account getRequester() {
        return this.requester;
    }

    /**
     * Get-method for the object's targer member.
     * @return Account The object's target.
     */    
    public Account getToBefriend() {
        return this.toBefriend;
    }

    /**
     * Get-method for the object's timestamp member.
     * @return Date The object's timestamp
     */    
    public Date getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * Determines if the friend request has timed out.
     * @return boolean true if more than 24 hours has elapsed since the request was made, otherwise false.
     */    
    public boolean hasTimedOut() {
        Date compare = new Date();
        return compare.getTime() - this.timeStamp.getTime() > timeLimit;
    }

    /**
     * Compares this instance with another FriendRequest object.
     * @param f The object to compare with.
     * @return int A negative integer  or a positive integer as this object's target user id 
     * is less than  or greater than the specified object's target user id. If the previous comparison is equal then
     * the same comparison is made between the object's requester members. If these are equal as well, returns 0.
     */    
    public int compareTo(FriendRequest f) {
        if(f.getRequester().getUserId().compareTo(this.getRequester().getUserId()) == 0) {
            return f.getToBefriend().getUserId().compareTo(this.getToBefriend().getUserId()); 
        }
        else {
            return f.getRequester().getUserId().compareTo(this.getRequester().getUserId());           
        }

    }

    /**
     * Determines if two FriendRequests are equal.
     * @param o The object to compare with.
     * @return boolean true if the objects are equal, otherwise false.
    */    
    public boolean equals(Object o) {
        if (o instanceof FriendRequest) {
            return ((FriendRequest) o).compareTo(this) == 0;
        } else {
            return false;
        }
    }
}
