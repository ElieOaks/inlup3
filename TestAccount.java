import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class TestAccount {
    public Account acc = new Account("vde.joy", "jevde");
    public Account friend = new Account("vde.eli", "Elie");

    @Test
    public void testGetUserId() {
        String userID = acc.getUserId();
        assertEquals("vde.joy", userID);
    }

    @Test
    public void testGetName() {
        String name = acc.getName();
        assertEquals("jevde", name);
    }

    @Test
    public void testAddFriend() {
        acc.addFriend(friend);
        assertTrue(acc.hasFriends());
    }
}
