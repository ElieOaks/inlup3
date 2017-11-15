import java.util.Set;
import java.util.TreeSet;
import java.io.Serializable;
import java.util.*;

/**
 * The Account class contains all the information that make up a user account
 */
public class Account implements Serializable, Comparable<Account> {
    private String name;
    private String userId;
    private boolean passwordReset;
    private Set<Account> friends = new TreeSet<Account>();
    private Set<Account> ignoredFriends = new TreeSet<Account>();
    private int postsAtLastSync;
    private int postActionsAtLastSync;
    private List<Post> likes = new LinkedList<Post>();

    /**
     * Constructor for Account.
     * @param userId String contain the user id to be associated with this account.
     */
    public Account(String userId) {
        this.userId   = userId;
    }

    public void likePost(Post p) {
        this.likes.add(p);
    }

    public boolean likesPost(Post p) {
        return this.likes.contains(p);
    }
    /**
     * Constructor for Account.
     * @param userId String contain the user id to be associated with this account.
     * @param name String containing the name to be associated with this account.
     */
    public Account(String userId, String name) {
        this(userId);
        this.name = name;
        this.addFriend(this);
    }

    /**
     * Get-function for an instance's name
     * @return String The name currently associated with the calling account.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set-function for an instance's name
     * @param name The new name
     */ 
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get-function for an instance's user id
     * @return The instance's uder id.
     */ 
    public String getUserId() {
        return this.userId;
    }

    /**
     * Set-function for an instance's user id.
     * @param userId String containing the new user id.
     */ 
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Adds a friend to the instance's list of friends
     * @param a Account to befriend.
     */ 
    public void addFriend(Account a) {
        this.friends.add(a);
    }

    /**
     * Removes a friend from the instance's list of friends
     * @param a Account to unfriend
     */
    public void removeFriend(Account a) {
        this.friends.remove(a);
        a.friends.remove(this);
    }

    /**
     * Adds an account to this instance's list of ignored friends.
     * @param a Account to ignore
     */
    public void ignoreFriend(Account a) {
        if (this.isFriendsWith(a)) this.ignoredFriends.add(a);
    }

    /**
     * Removes an account from this instance's list of ignored friends.
     * @param a Account to unignore
     */ 
    public void unIgnoreFriend(Account a) {
        if (this.isFriendsWith(a)) this.ignoredFriends.remove(a);
    }

    /**
     * Determines if this instance is friends with another account.
     * @param a Account to determine friendship for
     * @return boolean True if this account is friends with a, otherwise false
     */ 
    public boolean isFriendsWith(Account a) {
        return this.friends.contains(a);
    }

    /**
     * Determines if this instance is currently ignoring  another account.
     * @param a Account to determine ignoreship for
     * @return boolean True if this account is ignoring a, otherwise false
     */ 
    public boolean isCurrentlyIgnoring(Account a) {
        return this.ignoredFriends.contains(a);
    }

    /**
     * Compares this instance to another account
     * @param a Account to compare with
     * @return int A negative integer, zero, or a positive integer as this object's user id 
     * is less than, equal to, or greater than the specified object's user id.
     */ 
    public int compareTo(Account a) {
        return a.userId.compareTo(this.userId);
    }

    /**
     * Determines whether this instance has any friends or not
     * @return boolean True if this instance has at least one friend, otherwise false
     */ 
    public boolean hasFriends() {
        return this.friends.size() > 0;
    }

    /**
     * Retrieves all of this instance's friends
     * @return Account[] Array of all accounts in this intance's friends list.
     */ 
    public Account[] getFriends() {
        return (Account[]) this.friends.toArray(new Account[0]);
    }

    /**
     * Retrieves the number of PostActions on the server side at the last sync
     * @return int Number of posts at the last sync
     */  
    public int getPostActionsAtLastSync() {
        return this.postActionsAtLastSync;
    }

    /**
     * Sets the current number of PostActions on the server side
     * @param postActions The current number of PostActions on the server.
     */   
    public void setPostActionsAtLastSync(int postActions) {
        this.postActionsAtLastSync = postActions;
    }

    /**
     * Retrieves the number of PostActions on the server side at the last sync
     * @return int Number of posts at the last sync
     */   
    public int getPostAtLastSync() {
        return this.postsAtLastSync;
    }
    
    /**
     * Sets the current number of posts on the server side
     * @param posts The number of posts currently on the server.
     */   
    public void setPostAtLastSync(int posts) {
        this.postsAtLastSync = posts;
    }

    /**
     * Compares two Account object and determines if they are equal or not.
     * @param o Object to compare to this instance.
     * @return boolean True if o is equal to this instance, otherwise false.
     */       
    public boolean equals(Object o) {
        if (o instanceof Account) {
            return ((Account) o).userId.equals(this.userId);
        } else {
            return false;
        }
    }
}
