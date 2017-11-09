import java.io.Serializable;
import java.util.Set;
import java.util.List;

public class SyncResponse implements Serializable {
    private Set<Account> users;
    private List<Post> posts;
    private Set<FriendRequestResponse> responses;
    private Set<FriendRequest> friendRequests;
    
    public SyncResponse(Set<Account> users, List<Post> posts, Set<FriendRequestResponse> responses, Set<FriendRequest> friendRequests) {
        this.users = users;
        this.posts = posts;
        this.responses = responses;
        this.friendRequests = friendRequests;
    }

    public List<Post> getPosts() {
        return this.posts;
    }

    public Set<Account> getUsers() {
        return this.users;
    }

    public Set<FriendRequestResponse> getResponses() {
        return this.responses;
    }

    public Set<FriendRequest> getRequests() {
        return this.friendRequests;
    } 
}
