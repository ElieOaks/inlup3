import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class TestPost {
    public Post post = new Post(100, new Account("123", "Foo Barsson"), "Foo");
    public Account account = new Account("321", "Bar Foosson");

    @Test 
    public void testGetPoster() {
        String name = post.getPoster().getName();
        assertEquals(name, "Foo Barsson");
    }

    @Test 
    public void testAddComment() {
        Comment comment = new Comment(account, post, "A comment");
        post.addComment(comment);
        List<Comment> comments = post.getComments();
        assertEquals(1, comments.size());
    }
    
    @Test 
    public void testGetPostId() {
        assertEquals(100, post.getPostId());
    }
    
    @Test
    public void testLike() {
        Like like = new Like(account, post);
        post.like(like);
        assertEquals(1, post.getNumberOfLikes());
    }

}
