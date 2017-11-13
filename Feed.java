import java.util.List;
import java.util.LinkedList;

/**
 * The Feed class holds the message feed for an Account
 */
public class Feed {
    private List<Post> posts = new LinkedList<Post>();
    private Account account;

    /**
     * Constructor for Feed
     * @param account The account wich to associate with this object.
     */
    public Feed(Account account) {
        this.account = account;
    }

    /**
     * Adds a post to the feed
     * @param post Post to be added
     */
    public void addPost(Post post) {
        posts.add(0, post);
    }

    /**
     * Retrieves all posts stored in an object
     * @return List<Post> List of all posts within an object.
     */
    public List<Post> getPosts() {
        return this.posts;
    }

    /**
     * Renders the Feed
     * @return String The string representation of the feed in its current state.
     */
    public String renderAll() {
        return this.render(posts.size());
    }

    /**
     * Set-method for the feed's associated account.
     * @param account The account to associate with
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    public String renderLatest(int n) {
        return this.render(n);
    }

    private String render(int n) {
        String result = "";

        for (Post p : this.posts) {
            if(!this.account.isCurrentlyIgnoring(p.getPoster())) {
                result = result + p.render();                                
            }
            if (--n < 0) break;
        }

        return result;
    }
}
