import java.io.Serializable;

public class RemoveFriend implements Serializable {
    private Account friendToBeRemoved;

    /**
     * Constructor for RemoveFriend
     * @param friendToBeRemoved The account to unfriend.
     */
    public RemoveFriend(Account friendToBeRemoved) {
        this.friendToBeRemoved = friendToBeRemoved;
    }

    /**
     * Get-method for the friend to be removed
     * @return Account The account to unfriend
     */
    public Account getFriend() {
        return this.friendToBeRemoved;
    }
}
