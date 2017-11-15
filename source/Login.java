public class Login implements java.io.Serializable, Comparable<Login>{
    private Account account;
    private String password;

    /**
     * Constructor for Login
     * @param a Account associated with this login.
     * @param password String containing the password associated with this login.
     */
    public Login(Account a, String password) {
        this.account = a;
        this.password = password;
    }

    /**
     * Get-method for the Account member of an instance.
     * @return Account The account associated with an instance.
     */
    public Account getAccount() {
        return this.account;
    }

    /**
     * Get-method for the password associated with an instance.
     * @return String The password assoiciated with an instance.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Set-method for the password associated with an instance.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    
    public int compareTo(Login l) {
        return l.getAccount().getUserId().compareTo(this.getAccount().getUserId());
    }

    public boolean equals(Object o) {
        if (o instanceof Login) {
            return ((Login) o).compareTo(this) == 0;
        } else {
            return false;
        }
    }
}
