public class NameChange implements java.io.Serializable {
    private Account account;

    public NameChange(Account account) {
        this.account = account;
    }
    
    public Account getAccount() {
        return this.account;
    }

}
