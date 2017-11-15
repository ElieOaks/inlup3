import java.io.Serializable;

public class AddFriend implements Serializable {
    private Account friendToBeAdded;
    /**
     * Constructor for AddFriend
     * @param friendToBeAdded Account to befriend.
     */
    public AddFriend(Account friendToBeAdded) {
        this.friendToBeAdded = friendToBeAdded;
    }

    /**
     * Returns the target Account for this AddFriend object
     * @return The target Account for this AddFriend object
     */
    public Account getFriend() {
        return this.friendToBeAdded;
    }
}
