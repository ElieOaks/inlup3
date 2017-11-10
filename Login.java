public class Login implements java.io.Serializable {
    private Account account;
    private String password;
    private boolean passwordReset;

    public Login(Account a, String password) {
        this.account = a;
        this.password = password;
    }

    public Account getAccount() {
        return this.account;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean checkPassword(String writtenPassword) {
        if (writtenPassword.equals(this.password)) return true;
        return false;
        
    }
}
