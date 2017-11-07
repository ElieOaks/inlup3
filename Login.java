public class Login implements java.io.Serializable, Comparable<Login>{
    private Account account;
    private String password;

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
