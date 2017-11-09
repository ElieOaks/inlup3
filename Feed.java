import java.util.List;
import java.util.LinkedList;

public class Feed {
    private List<Post> posts = new LinkedList<Post>();
    private Account account;

    public Feed(Account account) {
        this.account = account;
    }
    
    public void addPost(Post post) {
        posts.add(0, post);
    }

    public List<Post> getPosts() {
        return this.posts;
    }

    public String renderAll() {
        return this.render(posts.size());
    }

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
