import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    private Set<Account> knownUsers = new TreeSet<Account>();
    private Set<Login> knownLogins = new TreeSet<Login>();
    private List<Post> posts = new LinkedList<Post>();
    private Set<FriendRequest> pendingFriendRequests = new TreeSet<FriendRequest>();
    private Set<FriendRequestResponse> friendRequestResponses = new TreeSet<FriendRequestResponse>();
    
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

    
    private Login getLoginFor(String userId) {
        for (Login a : this.knownLogins)
            if (a.getAccount().getUserId().equals(userId)) return a;

        return null;
    }
    
    public synchronized void addAccount(Account a) {
        this.knownUsers.add(a);
    }

    private synchronized void addLogin(Login l) {
        this.knownLogins.add(l);
    }

    public synchronized void removeAccount(Account a) {
        this.knownUsers.remove(a);
    }

    public synchronized Set<Account> getAccounts() {
        return new TreeSet<Account>(this.knownUsers);
    }

    /// Get all new posts from friends is a special case of get new posts
    public synchronized List<Post> getNewFriendPosts(Account account) {
        List<Post> result = new LinkedList<Post>(); 
        
        for (Post p : this.getNewPosts(account)) {
            if (p.getPoster().isFriendsWith(account)){
                result.add(p);  
            } 
        }

        return result;
    }

    /// Read "time stamp" from account, then update it 
    public synchronized List<Post> getNewPosts(Account account) {
        int since = account.getPostAtLastSync();
        System.out.println("Antal posts vid förra: "+since);
        account.setPostAtLastSync(this.posts.size());
        System.out.println("Antal posts nu: "+this.posts.size());

        return new ArrayList<Post>(this.posts.subList(since, this.posts.size())); 
    }
    
    public synchronized List<Post> getPosts() {
        return new ArrayList<Post>(this.posts);
    }

    public synchronized void addPost(Post p) {
        this.posts.add(p);
    }

    static class ClientProxy extends Thread {
        private Account account;
        // private Login login;
        private Socket socket;
        private Server server;
        private ObjectOutputStream outgoing;
        private ObjectInputStream incoming;

        private ClientProxy(Account account, Socket socket, Server server, ObjectInputStream incoming) throws IOException {
            this.account = account;
            //this.login = login;
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
                Account account      = ((Login) handShake).getAccount();
                Login knownLogin     = server.getLoginFor(account.getUserId());
                Account knownAccount = server.getAccountFor(account.getUserId());

                if (knownLogin == null) {
                    System.out.println("Lägger till ett nytt Login till servern");
                    server.addAccount(account);
                    server.addLogin((Login) handShake); 
                    new ClientProxy(account, socket, server, incoming).start();
                }
                
                else {
                    if (knownLogin.getPassword().equals(((Login) handShake).getPassword()) == false){
                        throw new RuntimeException("Wrong password");
                    }
                    System.out.println("Servern känner igen användaren...");
                    new ClientProxy(knownAccount, socket, server, incoming).start(); //Denna ska troligen vara knownAccount men då får vi nullPointerException.
                }
            }

            else {
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
            //tar inte bort kontot vid utloggning.
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

        private void updateName(String name) {
            this.account.setName(name); 
        }

        private void updatePassword(Account account, String password) {
            Login l = this.server.getLoginFor(account.getUserId()); 
            l.setPassword(password);
        }

        private Set<FriendRequest> getFriendRequests() {
            String user = this.account.getUserId();
            Set<FriendRequest> requests = new TreeSet<FriendRequest>();

            for(FriendRequest f : this.server.pendingFriendRequests) {
                String toBefriend = f.getToBefriend().getUserId();
                if(toBefriend.equals(user)) {
                    requests.add(f);
                    this.server.pendingFriendRequests.remove(f);
                }
            }
            return requests;
        }

        private void queueFriendRequestResponses(FriendRequestResponse response) {
            this.server.friendRequestResponses.add(response);
        }

        
        private Set<FriendRequestResponse> getFriendRequestResponses() {
            Set<FriendRequestResponse> responses = new TreeSet<FriendRequestResponse>();
            String userId = this.account.getUserId();
            
            for(FriendRequestResponse r : this.server.friendRequestResponses) {
                String askingUserId = r.getAskingUser().getUserId();
                if(askingUserId.equals(userId)) {
                    responses.add(r);
                    this.server.friendRequestResponses.remove(r);
                }
            }

            for(FriendRequest f : this.server.pendingFriendRequests) {
                String askingUserId = f.getRequester().getUserId();
                if(askingUserId.equals(userId) && f.hasTimedOut()) {
                    responses.add(new FriendRequestResponse(this.account, f.getToBefriend()));
                    this.server.pendingFriendRequests.remove(f);
                }
            }
            return responses;
        }
        
        private void queueFriendRequest(FriendRequest request) {
            this.server.pendingFriendRequests.add(request);
        }

        private void sync() {
            try {
                System.out.println("<< SyncResponse");
                this.outgoing.reset();
                this.outgoing.
                    writeObject(new SyncResponse(new HashSet<Account>(this.server.getAccounts()),
                                                 new LinkedList<Post>(this.server.getNewFriendPosts(this.account)),
                                                 new TreeSet<FriendRequestResponse>(this.getFriendRequestResponses()),
                                                 new TreeSet<FriendRequest>(this.getFriendRequests())));
                this.outgoing.flush();
                                } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        public void run() {
            try {
                while (true) {
                    Object o = this.incoming.readObject();
                    //System.err.println(">> Received: " + o.getClass().getName());
                    // o instanceof Account checks if o is an account
                    // (Account) o type casts o into an Account so that it can be used as one
                    if (o instanceof FriendRequest) {
                        queueFriendRequest((FriendRequest)o);
                    } else if (o instanceof FriendRequestResponse) {
                        queueFriendRequestResponses((FriendRequestResponse)o);
                    } else if (o instanceof PasswordChange) {
                        updatePassword(this.account, ((PasswordChange) o).getPassword());
                    } else if (o instanceof NameChange) {
                        this.updateName( ((NameChange) o).getName()); 
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
                // BAD Practise. Never catch "Exception"s. Too general.
                e.printStackTrace();
            }
        }
    }
}
