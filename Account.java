import java.util.Set;
import java.util.TreeSet;
import java.io.Serializable;

public class Account implements Serializable, Comparable<Account> {
    private String name;
    private String userId;
    private Set<Account> friends = new TreeSet<Account>();
    private Set<Account> ignoredFriends = new TreeSet<Account>();

    private int newFeedNr;

    public Account(String userId) {
        this.userId   = userId;
    }

    public Account(String userId, String name) {
        this(userId);
        this.name = name;
    }

    //Kepps track of how many new posts since last sync.
    public void setNewFeedNr(int newPostNr) {
        this.newFeedNr = newPostNr;
    }

    public int getNewFeedNr() {
        return this.newFeedNr;
    }
    
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
       
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void addFriend(Account a) {
        this.friends.add(a);
    }

    public void removeFriend(Account a) {
        this.friends.remove(a);
    }

    public void ignoreFriend(Account a) {
        if (this.isFriendsWith(a)) this.ignoredFriends.add(a);
    }

    public void unIgnoreFriend(Account a) {
        if (this.isFriendsWith(a)) this.ignoredFriends.remove(a);
    }

    public boolean isFriendsWith(Account a) {
        return this.friends.contains(a);
    }

    public boolean isCurrentlyIgnoring(Account a) {
        return this.ignoredFriends.contains(a);
    }

    public int compareTo(Account a) {
        return a.userId.compareTo(this.userId);
    }

    public boolean hasFriends() {
        return this.friends.size() > 0;
    }

    public Account[] getFriends() {
        return (Account[]) this.friends.toArray(new Account[0]);
    }

    public boolean equals(Object o) {
        if (o instanceof Account) {
            return ((Account) o).userId.equals(this.userId);
        } else {
            return false;
        }
    }
}
