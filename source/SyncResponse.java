import java.io.Serializable;
import java.util.Set;
import java.util.List;

public class SyncResponse implements Serializable {
    private Set<Account> users;
    private List<Post> posts;
    private Set<FriendRequestResponse> responses;
    private Set<FriendRequest> friendRequests;
    private List<PostAction> postActions;
    private Set<Unfriend> unfriends;

    /**
     * Constructor for SyncResponse
     * @param users All to the server known users at the time of the synchronization
     * @param posts All new posts made by friends of the synchronizing account since the last synchronization
     * @param responses All answered or timed out friend requests adressed to the synchronizing account
     * @param friendRequests All new friend requests adressed to the synchronizing account
     * @param postActions All new PostActions made since the last synchronization to posts authored by friends to the synchronizing account
     */
    public SyncResponse(Set<Account> users, List<Post> posts, Set<FriendRequestResponse> responses, Set<FriendRequest> friendRequests, List<PostAction> postActions, Set<Unfriend> unfriends) {
        this.users = users;
        this.posts = posts;
        this.responses = responses;
        this.friendRequests = friendRequests;
        this.postActions = postActions;
        this.unfriends = unfriends;
    }

    public Set<Unfriend> getUnfriends() {
        return this.unfriends;
    }
    /**
     * Retrieves new posts made by friends of the synchronizing account since the last synchronization
     * @return List<Post> All posts in the objects list of posts.
     */
    public List<Post> getPosts() {
        return this.posts;
    }

    /**
     * Retrieves all to the server known users at the time of the synchronization
     * @return Set<Account> All Accounts in the objects set of accounts.
     */
    public Set<Account> getUsers() {
        return this.users;
    }

    /**
     * Retrieves all answered or timed out friend requests adressed to the synchronizing account
     * @return Set<FriendRequestResponse> All friend request responses in the objects set of responses.
     */
    public Set<FriendRequestResponse> getResponses() {
        return this.responses;
    }

    /**
     * Retrieves all new friend requests adressed to the synchronizing account
     * @return Set<FriendRequestResponse> All friend request in the objects set of requests.
     */
    public Set<FriendRequest> getRequests() {
        return this.friendRequests;
    }

    /**
     * Retrieves all new PostActions made since the last synchronization to posts authored by friends to the synchronizing account
     * @return List<FriendRequestResponse> All PostActions in the objects list of actions.
     */
    public List<PostAction> getPostActions() {
        return this.postActions;
    }
}
