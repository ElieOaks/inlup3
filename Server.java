import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    private Set<Account> knownUsers = new TreeSet<Account>();
    private List<Post> posts = new LinkedList<Post>();

     //List of passwords with UserID:s,so p asswords are seperated from Accounts.
    private List<UserPassword> passwords = new LinkedList<UserPassword>();

    public static void main(String[] args) {
        try {
            ServerSocket socket = new ServerSocket(args.length > 0 ? Integer.parseInt(args[0]) : 8080);
            Server server = new Server();

            while (true) {
                System.out.println("!! Server listening for connections: " + socket.getInetAddress() + ":" + socket.getLocalPort());
                Socket clientConnection = socket.accept();
                System.out.println("!! Server got a connection from: " + clientConnection.getInetAddress() + ":" + clientConnection.getPort());
                try {
                    ClientProxy.attemptEstablishConnection(clientConnection, server);
                } catch (RuntimeException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    public Account getAccountFor(String userId) {
        for (Account a : this.knownUsers)
            if (a.getUserId().equals(userId)) return a;

        return null;
    }
    
    public synchronized void addAccount(Account a) {
        this.knownUsers.add(a);
    }

    public synchronized void removeAccount(Account a) {
        this.knownUsers.remove(a);
    }

    public synchronized Set<Account> getAccounts() {
        TreeSet<Account> allUsers = new TreeSet<Account>(this.knownUsers);
            for (Account a: allUsers)
                System.out.println("getAccount: "+ a.getName());
        return allUsers;
    }

    public synchronized List<Post> getPosts() {
        return new ArrayList<Post>(this.posts);
   }

    //returns only the latest posts from friends.
    public synchronized List<Post> getFriendsPosts(Account a) {
        List<Post> newPosts = this.posts.subList(a.getNewFeedNr(), this.posts.size());
        a.setNewFeedNr(this.posts.size()); //Only see posts from after start of friendship
        return getNewPosts(a, newPosts);
    }

    //Filters away posts not from friends.
    public synchronized List<Post> getNewPosts(Account a, List<Post> newPosts) {
        ArrayList<Post> friendsPost = new ArrayList<Post>();
        for (Post p : newPosts)
            if (a.isFriendsWith(p.getPoster())) {
                friendsPost.add(p);
            }       
        return friendsPost;
    }

    public synchronized void addPost(Post p) {
        this.posts.add(p);
    }

    //Lets the Server know name has changed
    public synchronized void updateName(Account neu, String name) {
        for(Account user : this.knownUsers) {
            if((user.getUserId()).equals(neu.getUserId())) {
                user.setName(name);
                System.out.println("New name: " + name);
            }
        }
    }

    static class ClientProxy extends Thread {
        private Account account;
        private Socket socket;
        private Server server;
        private ObjectOutputStream outgoing;
        private ObjectInputStream incoming;

        private ClientProxy(Account account, Socket socket, Server server, ObjectInputStream incoming) throws IOException {
            this.account = account;
            this.server  = server;
            this.socket  = socket;
            this.incoming = incoming;
            this.outgoing = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("<< Account");
            this.outgoing.writeObject(account);
            this.outgoing.flush();
        }

        public static void attemptEstablishConnection(Socket socket, Server server) throws IOException, ClassNotFoundException {
            ObjectInputStream incoming = new ObjectInputStream(socket.getInputStream());
            Object handShake = incoming.readObject();

            if (handShake instanceof Login) {
                Account account = ((Login) handShake).getAccount(); //User that is logging in.
                Account knownAccount = server.getAccountFor(account.getUserId()); //If this UserId is already in System
                UserPassword idAndPassword = new UserPassword(((Login) handShake).getPassword(), account.getUserId()); //given password matches the given userId? 

                if (knownAccount == null) {
                    System.out.println("En ny användare läggs in i servern!");
                    server.addAccount(account);
                    server.passwords.add(0, idAndPassword);
                    new ClientProxy(account, socket, server, incoming).start();
                }
                else {
                    if (server.passwords.contains(idAndPassword) == false) throw new RuntimeException("Wrong password");
                    System.out.println("En gammal användare har återvänt!");
                    new ClientProxy(knownAccount, socket, server, incoming).start();
                }
            } else {
                System.err.println("!! Bad connection attempt from: " + socket.getInetAddress() + ":" + socket.getPort());
            }
        }

        private int globalPostIdCounter = 0;
        // The synchronised keyword is required on all methods which may
        // be called in parallel on the server from multiple clients at
        // the same time.
        private synchronized int getUniqueGlobalPostId() {
            return ++this.globalPostIdCounter;
        }

        private void logout(Account a) {
            //this.server.removeAccount(a);
            System.out.println("!! " + a.getUserId() + " left the building");
            try {
                this.outgoing.close();
                this.incoming.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        private void postMessage(String msg) {
            this.server.addPost(new Post(this.getUniqueGlobalPostId(), this.account, msg));
        }

        private void addFriend(Account a) {
            this.account.addFriend(a);
            a.addFriend(this.account);
        }
 
        private void removeFriend(Account a) {
            this.account.removeFriend(a);
            a.removeFriend(this.account);
        }

        private void updateAccount(Account old, Account neu) {
            server.removeAccount(old);
            server.addAccount(neu);
        }

        private void sync() {
            try {
                System.out.println("<< SyncResponse");
                this.outgoing.writeObject(new SyncResponse(new HashSet<Account>(this.server.getAccounts()),
                                                           new LinkedList<Post>(this.server.getFriendsPosts(this.account))));
                this.outgoing.flush();  
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        public void run() {
            try {
                while (true) {
                    Object o = this.incoming.readObject();
                    System.err.println(">> Received: " + o.getClass().getName());
                    // o instanceof Account checks if o is an account
                    // (Account) o type casts o into an Account so that it can be used as one
                    if (o instanceof NameChange) {
                        this.server.updateName(((NameChange) o).getAccount(), ((NameChange) o).getName());
                    } else if (o instanceof Account) {
                        this.updateAccount(this.account, (Account) o); 
                    } else if (o instanceof PostMessage) {
                        this.postMessage(((PostMessage) o).getMsg());
                    } else if (o instanceof AddFriend) {
                        this.addFriend(((AddFriend) o).getFriend());
                    } else if (o instanceof RemoveFriend) {
                        this.removeFriend(((RemoveFriend) o).getFriend());
                    } else if (o instanceof SyncRequest) {
                        this.sync();
                    } else if (o instanceof Logout) {
                        this.logout(((Logout) o).getAccount());
                        return;
                    }
                }
            } catch (Exception e) {
                // BAD Practise. Never catch "Exception"s. Too general. Needs to be more specific. Caught by Findbugs
                e.printStackTrace();
            }
        }
    }
}
