public class Logout implements java.io.Serializable {
    private Account account;

    /**
     * Constructor for Logout
     * @param a The account associated with this logout
     */ 
    public Logout(Account a) {
        this.account = a;
    }

    /**
     * Get-method for the account member.
     * @return Account The account associated with an instance.
     */ 
    public Account getAccount() {
        return this.account;
    }
}
