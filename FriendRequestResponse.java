import java.io.Serializable;


/**
 * The FriendRequestResponse class is used to allow a client to 
 * communicate an answer to a friend request to the server wich in turn conveys this information
 * to the requester.
 *
 * @author  Ulf Sigvardsson
 * @version 1.0
 * @since   2017-11-09 
 */
public class FriendRequestResponse implements Serializable, Comparable<FriendRequestResponse>{
    private Account respondingUser;
    private Account askingUser;
    private boolean requestAccepted; 
    private boolean requestTimedOut;

    /**
     * Constructor for FriendRequestResponse
     * @param askingUser The account that made the request.
     * @param respondingUser Account that is answering the request.
     * @param answer Boolean representing if the request is accepted.
     */
    public FriendRequestResponse(Account askingUser, Account respondingUser, boolean answer) {
        this.askingUser = askingUser;
        this.respondingUser = respondingUser;
        this.requestAccepted = answer;
    }

    /**
     * Constructor for FriendRequestResponse
     * @param askingUser The account that made the request.
     * @param respondingUser Account that is answering the request.
     */
    public FriendRequestResponse(Account askingUser, Account respondingUser) {
        this.askingUser = askingUser;
        this.respondingUser = respondingUser;
        this.requestAccepted = false;
        this.requestTimedOut = true;
    }

    /**
     * Accepts a friend request
     */
    public void accept() {
        this.requestAccepted = true; 
    }

    /**
     * Rejects a friend request
     */
    public void decline() {
        this.requestAccepted = false; 
    }

    /**
     * Determines whether the response is affirmative or not.
     * @return boolean true if the request was accepted, otherwise false.
     */
    public boolean hasAccepted() {
        return this.requestAccepted == true;
    }

    /**
     * Determines if the response was made due to the request not being handled in time
     * @return boolean true if the request timed out, otherwise false
     */
    public boolean hasTimedOut() {
        return this.requestTimedOut;
    }

    /**
     * Get-method for the account that made the request.
     * @return Account The account that made the request.
     */
    public Account getAskingUser() {
        return this.askingUser;
    }

    /**
     * Get-method for the account that responded to the request.
     * @return Account The account that responded to the request.
     */
    public Account getRespondingUser() {
        return this.respondingUser;
    }

    public int compareTo(FriendRequestResponse f) {
        if(f.getAskingUser().getUserId().compareTo(this.getAskingUser().getUserId()) == 0) {
            return f.getRespondingUser().getUserId().compareTo(this.getRespondingUser().getUserId()); 
        }
        else {
            return f.getAskingUser().getUserId().compareTo(this.getAskingUser().getUserId());           
        }

    }

    public boolean equals(Object o) {
        if (o instanceof FriendRequestResponse) {
            return ((FriendRequestResponse) o).compareTo(this) == 0;
        } else {
            return false;
        }
    }
}
