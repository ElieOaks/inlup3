import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import junit.framework.*;


public class TestFile {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestAccount.class);

        for (Failure failure : result.getFailures())
            System.out.println(failure.toString());

        System.out.println(result.wasSuccessful());
    }
}
