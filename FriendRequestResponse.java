import java.io.Serializable;

public class FriendRequestResponse implements Serializable, Comparable<FriendRequestResponse>{
    private Account respondingUser;
    private Account askingUser;
    private boolean requestAccepted; 
    private boolean requestTimedOut;

    public FriendRequestResponse(Account askingUser, Account respondingUser, boolean answer) {
        this.askingUser = askingUser;
        this.respondingUser = respondingUser;
        this.requestAccepted = answer;
    }

    public FriendRequestResponse(Account askingUser, Account respondingUser) {
        this.askingUser = askingUser;
        this.respondingUser = respondingUser;
        this.requestAccepted = false;
        this.requestTimedOut = true;
    }

    public void accept() {
        this.requestAccepted = true; 
    }

    public void decline() {
        this.requestAccepted = false; 
    }

    public boolean hasAccepted() {
        return this.requestAccepted == true;
    }

    public boolean hasTimedOut() {
        return this.requestTimedOut;
    }

    public Account getAskingUser() {
        return this.askingUser;
    }

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