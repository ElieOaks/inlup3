public class NameChange implements java.io.Serializable {
    private Account account;
    private String name;
    
    public NameChange(Account account, String name) {
        this.account = account; 
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Account getAccount() {
        return this.account;
    }

}
