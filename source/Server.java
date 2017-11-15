import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    private Set<Account> knownUsers = new TreeSet<Account>();
    private Set<Login> knownLogins = new TreeSet<Login>();
    private List<Post> posts = new LinkedList<Post>();
    private Set<FriendRequest> pendingFriendRequests = new TreeSet<FriendRequest>();
    private Set<FriendRequestResponse> friendRequestResponses = new TreeSet<FriendRequestResponse>();
    private Set<Unfriend> pendingUnfriends = new TreeSet<Unfriend>();
    private List<PostAction> postActions = new LinkedList<PostAction>();
    private int globalPostIdCounter = 0;
    private int globalActionIdCounter = 0;
    
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

    /**
     * Retrieves the account in the servers known accounts that corresponds to a user id.
     * @param userId The user id to match with
     * @return Account The account associated with the provided user id or, if not found, null.
     */
    public Account getAccountFor(String userId) {
        for (Account a : this.knownUsers)
            if (a.getUserId().equals(userId)) return a;

        return null;
    }

    /**
     * Retrieves the account in the servers known accounts that corresponds to a user id.
     * @param userId The user id to match with
     * @return Account The account associated with the provided user id or, if not found, null.
     */    
    private Login getLoginFor(String userId) {
        for (Login a : this.knownLogins)
            if (a.getAccount().getUserId().equals(userId)) return a;

        return null;
    }

    /**
     * Adds an account to the servers known users.
     * @param a The account to add.
     */
    public synchronized void addAccount(Account a) {
        this.knownUsers.add(a);
    }

    /**
     * Adds new login information to the servers list of known login.
     * @param l The Login object to add.
     */
    private synchronized void addLogin(Login l) {
        this.knownLogins.add(l);
    }

    /**
     * Removes an account from the servers known users.
     * @param a The account to remove.
     */
    public synchronized void removeAccount(Account a) {
        this.knownUsers.remove(a);
    }

    /**
     * Retrives all accounts known to the server.
     * @return Set<Account> Set containing the servers known users.
     */
    public synchronized Set<Account> getAccounts() {
        return new TreeSet<Account>(this.knownUsers);
    }

    /**
     * Retrives all comments or likes relevant to a certain user made since the last sync with said user.
     * @param account The user asking for hte new comments and likes.
     * @return List<PostAction> List of comments and likes made to posts, authored by friends to the asking user, since the last sync.
     */
    private  List<PostAction> getPostActions(Account account) {
        int since = account.getPostActionsAtLastSync();
        List<PostAction> result = new LinkedList<PostAction>();
        for(PostAction p : this.postActions) {
            if(account.isFriendsWith(p.getPoster())) {
                result.add(p);
            }
        }
        System.out.println("Antal postActions vid förra: "+since);
        account.setPostActionsAtLastSync(this.postActions.size());
        System.out.println("Antal postActions nu: "+this.postActions.size());
        return new LinkedList<PostAction>(this.postActions.subList(since, this.postActions.size()));
    }

    /**
     * Retrives all posts made by friends of a certain user since the last sync.
     * @param account The user asking for the new posts.
     * @return List<Post> List of new posts made by friends
     */
    public synchronized List<Post> getNewFriendPosts(Account account) {
        List<Post> result = new LinkedList<Post>(); 
        for (Post p : this.getNewPosts(account)) {
            if (p.getPoster().isFriendsWith(account)){
                result.add(p);  
            } 
        }

        return result;
    }

    /**
     * Retrives all posts made since the last sync between the server and a certain user.
     * @param account The user asking for the new posts.
     * @return List<Post> List of new posts made by friends of the asking user.
     */
    public synchronized List<Post> getNewPosts(Account account) {
        int since = account.getPostAtLastSync();
        System.out.println("Antal posts vid förra: "+since);
        account.setPostAtLastSync(this.posts.size());
        System.out.println("Antal posts nu: "+this.posts.size());

        return new ArrayList<Post>(this.posts.subList(since, this.posts.size())); 
    }

    /**
     * Retrives all posts ever made.
     * @return List<Post> List of all posts made to the server.
     */
    public synchronized List<Post> getPosts() {
        return new ArrayList<Post>(this.posts);
    }

    /**
     * Adds a post to the servers list of posts.
     * @param p The post to add
     */
    public synchronized void addPost(Post p) {
        this.posts.add(p);
    }

    /**
     * Adds a PostAction, containing a comment or a like, to the servers list of PostActions.
     * @param p The PostAction object to add.
     */
    public synchronized void addPostAction(PostAction p) {
        this.postActions.add(p);
    }

    /**
     * The ClientProxy class is an interface between the client and the server.
     */
    static class ClientProxy extends Thread {
        private Account account;
        // private Login login;
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

        /**
         * Connects a new or existing user to the server
         * @param socket 
         * @param server
         */
        public static void attemptEstablishConnection(Socket socket, Server server) throws IOException, ClassNotFoundException {
            ObjectInputStream incoming = new ObjectInputStream(socket.getInputStream());
            Object handShake = incoming.readObject();

            if (handShake instanceof Login) {
                Account account      = ((Login) handShake).getAccount(); //User that wants to login
                Login knownLogin     = server.getLoginFor(account.getUserId()); //Tries to find exesting login for this login attempt
                Account knownAccount = server.getAccountFor(account.getUserId()); //Tries to retrieve existing account for this login attempt

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
                    System.out.println("Välkommen tillbaka " + account.getName());
                    new ClientProxy(knownAccount, socket, server, incoming).start(); //Denna ska troligen vara knownAccount men då får vi nullPointerException.
                }
            }

            else {
                System.err.println("!! Bad connection attempt from: " + socket.getInetAddress() + ":" + socket.getPort());
            }
        }

      
        
        // The synchronised keyword is required on all methods which may
        // be called in parallel on the server from multiple clients at
        // the same time.
        
        /**
         * Increments and returns the servers post counter. Used as identification for posts. 
             * @return int The current post counter
                 */
        private synchronized int getUniqueGlobalPostId() {
            return ++this.server.globalPostIdCounter;
        }

        /**
         * Increments and returns the servers post action counter. Used as identification for comments and likes. 
         * @return int The current post action counter
         */
        private synchronized int getUniqueGlobalActionId() {
            return ++this.server.globalActionIdCounter;
        }

        /**
         * Logs an account out from the server.
         * @param a The account to log out.
         */
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

        /**
         * Retrieves a post in the servers list of post that matches the given id number.
         * @param postId The id number to scan for.
         * @return Post the post that corresponds to the id number.
         */
        private Post getPostFor(int postId) {
            for(Post p : this.server.posts) {
                if(postId == p.getPostId())
                    return p; 
            }
            return null;
        }

        /**
         * Adds a comment to a post
         * @param c CommentMessage containing the actual comment.
         */
        private void addComment(CommentMessage c) {
            Comment comment = c.getComment();
            Post post = getPostFor(c.getPostId()); 
            comment.setActionId(this.getUniqueGlobalActionId());
            this.server.addPostAction(comment);
        }

        /**
         * Adds a like to a post
         * @param l LikeMessage containing the actual comment.
         */
        private void addLike(LikeMessage l) {
            Like like = l.getLike();
            Post post = getPostFor(l.getPostId()); 
            like.setActionId(this.getUniqueGlobalActionId());
            this.server.addPostAction(like);
        }

        /**
         * Adds a post to the servers list of posts.
         * @param msg The message to post.
         */
        private void postMessage(String msg) {
            this.server.addPost(new Post(this.getUniqueGlobalPostId(), this.account, msg));
        }

        /**
         * Befriends two accounts
         * @param a Account to befriend with the ClientProxy's associated account.
         */
        private void addFriend(Account a) {
            this.account.addFriend(a);
            a.addFriend(this.account);
        }

        /**
         * Unfriends two accounts
         * @param a Account to unfriend from the ClientProxy's associated account.
         */
        private void removeFriend(Account a) {
            System.out.println("Inne i removefriend");
            this.account.removeFriend(a);
            this.server.pendingUnfriends.add(new Unfriend(this.account, a));
        }

        /**
         * Updates an account by replacing it with another
         * @param old The old Account
         * @param neu The new Account
         */
        private void updateAccount(Account old, Account neu) {
            server.removeAccount(old);
            server.addAccount(neu);
        }

        /**
         * Updates the name for the ClientProxy's associated account
         * @param name The new name
         */
        private void updateName(String name) {
            this.account.setName(name); 
        }

        /**
         * Updates the password for an account
         * @param account The account for wich to change password
         * @param password The new password
         */
        private void updatePassword(Account account, String password) {
            Login l = this.server.getLoginFor(account.getUserId()); 
            l.setPassword(password);
        }

        private Set<Unfriend> getUnfriends() {
            String user = this.account.getUserId();
            Set<Unfriend> unfriends = new TreeSet<Unfriend>();

            for (Unfriend u : this.server.pendingUnfriends) {
                String toUnfriend = u.getToUnfriend().getUserId();
                if(toUnfriend.equals(user)) {
                    this.account.removeFriend(u.getUnfriender());
                    unfriends.add(u);
                    this.server.pendingUnfriends.remove(u);
                }
            }

            return unfriends;
        }
        
        /**
         * Retrieves all new friend requests for the ClientProxy's associated account
         * @return Set<FriendRequest> All new FriendRequests for the user.
         */
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

        /**
         * Adds a response to a friend request to the servers list of responses.
         * @param response The FriendRequestResponse object to add.
         */
        private void queueFriendRequestResponses(FriendRequestResponse response) {
            this.server.friendRequestResponses.add(response);
        }

        /** 
         * Retrieves all new friend requests responses adressed to the ClientProxy's associated account
         * @return Set<FriendRequestResponse> All new responses adressed to the user.
         */
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

        /** 
         * Adds a friend request to the servers list of pending friend requests.
         * @param request The friend request to be queued.
         */
        private void queueFriendRequest(FriendRequest request) {
            this.server.pendingFriendRequests.add(request);
        }

        /** 
         * Synchronizes a client with the server
         */
        private void sync() {
            try {
                System.out.println("<< SyncResponse");
                this.outgoing.reset();
                this.outgoing.
                    writeObject(new SyncResponse(new HashSet<Account>(this.server.getAccounts()),
                                                 new LinkedList<Post>(this.server.getNewFriendPosts(this.account)),
                                                 new TreeSet<FriendRequestResponse>(this.getFriendRequestResponses()),
                                                 new TreeSet<FriendRequest>(this.getFriendRequests()),
                                                 new LinkedList<PostAction>(this.server.getPostActions(this.account)),
                                                 new TreeSet<Unfriend>(this.getUnfriends()))); 
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
                    } else if (o instanceof CommentMessage) {
                        this.addComment((CommentMessage)o); 
                    } else if (o instanceof LikeMessage){
                        this.addLike((LikeMessage)o);
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
