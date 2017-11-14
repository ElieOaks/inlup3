import java.io.Serializable;
import java.util.Set;
import java.util.List;

//A list of user and a list of posts
//Context: After a syncrequest, these are sent from server to client.
public class SyncResponse implements Serializable {
    private Set<Account> users;
    private List<Post> posts;

    //Constructor
    public SyncResponse(Set<Account> users, List<Post> posts) {
        this.users = users;
        this.posts = posts;
    }

    //Methods:
    public List<Post> getPosts() {
        return this.posts;
    }

    public Set<Account> getUsers() {
        for (Account a: this.users)
            System.out.println("In Syncresponse: " + a.getName());
        return this.users;
    }
}
