import org.junit.Test;
import static org.junit.Assert.*;

public class TestAccount {

    public static void main(String[] args) {
            
    }
        
    public void testNewAccount() {
        Account account = new Account("123", "Foo");
        assertTrue(account.isFriendsWith(Account));
    }
}
