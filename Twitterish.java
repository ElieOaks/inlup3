import java.io.*;
import java.net.*;
import java.util.*;
/**
 * The Twitterish class is the main class in the Twitterish char application. It contains the nested class Client wich handles 
 * all client side logic such as posting messenges, adding friends etc.
 */
public class Twitterish {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Twitterish <server_ip> <port_number>");
        } else {
            try {
                new Client(args[0], Integer.parseInt(args[1])).start();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Something went wrong. Please debug this error.");
            }
        }
    }

    // This is a nested class, we will go into this later in the course.
    // For now, think of this as a class which is only usable by the Server.
    /**
     * The Client class contains all the logic for the client side of the application along with all information regarding
     * an individual user on the server.
     */
    private static class Client {
        private Account loggedInUser;
        private Set<Account> knownUsers = new TreeSet<Account>();
        private Feed feed;
        private Login login;
        private ObjectOutputStream outgoing;
        private ObjectInputStream incoming;
        private String serverIp;
        private String myIp;
        private int port;

        /**
         * Constructor for Client
         * @param serverIp Ip adress for the server to connect to
         * @param port Port number to use
         */
        public Client(String serverIp, int port) {
            this.serverIp = serverIp;
            this.port = port;
            this.feed = new Feed(this.loggedInUser);
        }

        /**
         * Adds a new account to the clients list of known accounts on the server
         * @param account The account to add
         * @return None
         */        
        private void newAccount(Account account) {
            this.knownUsers.add(account);
        }

        /**
         * Adds a post to the clients feed
         * @param post The post to add
         * @return None
         */        
        private void newPost(Post post) {
            if (this.loggedInUser.isFriendsWith(post.getPoster()) &&
                !this.loggedInUser.isCurrentlyIgnoring(post.getPoster())) {
                feed.addPost(post);
            } else {
                // Ignore post
            }
        }

        // This is the code that sends a message to the server.
        // You should not need to touch this code.
        /**
         * Sends a message to the server
         * @param o The message to be sent
         * @return None
         */        
        private void sendMessage(Object o) {
            try {
                this.outgoing.writeObject(o);
                this.outgoing.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        // This is the code that receives a message to the server.
        // You should not need to touch this code.
        /**
         * Retrieves messages from the server
         * @return Object Message received from the server
         */        
        private Object receiveMessage() {
            try {
                Object o = this.incoming.readObject();
                //System.out.printf("Received %s message\n",  o == null ? "<null>" : o.getClass().toString());
                return o;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
            return null;
        }

        /**
         * Reads a new post from the user and sends it to the server
         * @return None
         */        
        private void postMessage() {
            System.out.println("Write your message on a single line: ");

            String msg = System.console().readLine();
            sendMessage(new PostMessage(msg));

            System.out.println("Message sent");
        }

        /**
         * Reads a new comment from the user and sends it to the server
         * @param toComment The post that is being commented
         * @return None
         */        
        private void comment(Post toComment) {
            System.out.println("Write your comment: ");
            String commentString = System.console().readLine();

            if(commentString != "") {
                Comment comment = new Comment(this.loggedInUser, toComment, commentString); 
                sendMessage(new CommentMessage(comment));
            } 
        }

        /**
         * Likes a post and forwards this information to the server
         * @param toLike The post that is being liked
         */        
        private void like(Post toLike) {
            Like like = new Like(this.loggedInUser, toLike); 
            sendMessage(new LikeMessage(like));
        }

        /**
         * Prompts the user to select a post in the feed and wether to comment or like it.
         */        
        private void commentOrLike() {
            printPostsForComment();
            int choice = select("What to comment or like?", this.feed.getPosts().size());
            Post post = this.feed.getPosts().get(choice);
            System.out.println("Do You want to [c]omment or [l]ike this post?  [a] to abort.");
            String answer = System.console().readLine().toLowerCase();

            switch (answer.charAt(0)) {
            case 'c':
                this.comment(post);
                return;
            case 'l':
                this.like(post);
                return;
            case 'a':
                return;
            }
            System.out.println("Invalid input.\n");
            
        }

        /**
         * Prints all posts in the clients feed with associated indexes onto the screen.
         */      
        private void printPostsForComment() {
            int i = 0;
            for(Post p : this.feed.getPosts()) {
                System.out.print(i + " " + p.render());
                i++;
            }
        }

        /**
         * Prints a list of accounts with corresponding indexes onto the screen
         * @param choices A list of Accounts
         */      
        private void printEnumeratedChoices(Account[] choices) {
            for (int i = 0; i < choices.length; ++i) {
                System.out.println(i + "\t" + choices[i].getName());
            }
        }

        /**
         * Lists all users known to the client with corresponding indexes and prompts the user to select wich user to befriend.
         * The method then sends a FriendRequest to the server.
         */      
        private void addFriend() {
            if (this.knownUsers.size() == 0) {
                System.out.println("You seem to be alone in the universe, at this moment.");
                return;
            }

            Account[] knownUsers = (Account[]) this.knownUsers.toArray(new Account[0]);
            Arrays.sort(knownUsers);
            System.out.println("\nLogged in users:");
            printEnumeratedChoices(knownUsers);

            int choice = select("\nWho to befriend?", this.knownUsers.size());
            Account friend = knownUsers[choice];

            sendMessage(new FriendRequest(this.loggedInUser, friend));
            //this.loggedInUser.addFriend(friend);

            //System.out.println("Befriended " + friend.getName());
            System.out.println("Request sent to " + friend.getName()); 
        }

        /**
         * Determines whether or not an index is within the boundaries of a given range.
         * @param index The index to examine.
         * @param max The end of the allowed range, exclusive.
         * @return boolean true if index was within the range, otherwise false
         */      
        private boolean validIndex(int index, int max) {
            return index >= 0 && index < max;
        }

        /**
         * Prompts the user to select an Account to remove from the clients list of friends
         * @return None
         */      
        private void removeFriend() {
            if (this.loggedInUser.hasFriends() == false) {
                System.out.println("You don't have anyone to unfriend. Try to make a few friends first.");
                return;
            }

            Account[] friends = this.loggedInUser.getFriends();
            this.printEnumeratedChoices(friends); 
            
            int choice = select("Who to unfriend?", this.loggedInUser.getFriends().length);
            Account friend = friends[choice];

            sendMessage(new RemoveFriend(friend));
            this.loggedInUser.removeFriend(friend);

            System.out.println("Unfriended " + friend.getName());
        }

        /**
         * Prints a question onto the screen and reads an answer from the user and converts it to an integer.
         * @param question The question to be printed
         * @param max The smallest number greater than 0 to not be a valid answer
         * @return int The integer given by the user
         */      
        private int select(String question, int max) {
            int choice = 0;
            do {
                System.out.println(question);
                String choiceString = System.console().readLine();
                choice = Integer.parseInt(choiceString); 
            } while (!validIndex(choice, max)); 
            return choice;
        }

        /**
         * Removes an account from the clients list of ignored friends
         */      
        private void unIgnoreFriend() {
            if (this.loggedInUser.hasFriends() == false) {
                System.out.println("You don't have anyone to ignore. Try to make a few friends first.");
                return;
            }
            
            Account[] friends = this.loggedInUser.getFriends();
            this.printEnumeratedChoices(friends);
            
            int choice = select("Who to unignore?", this.loggedInUser.getFriends().length);
            Account friend = friends[choice];

            if(this.loggedInUser.isCurrentlyIgnoring(friend))
                this.loggedInUser.unIgnoreFriend(friend);

            System.out.println("Unignored " + friend.getName());
        }

        /**
         * Adds an account from the clients list of ignored friends
         */      
        private void ignoreFriend() {
            if (this.loggedInUser.hasFriends() == false) {
                System.out.println("You don't have anyone to ignore. Try to make a few friends first.");
                return;
            }

            Account[] friends = this.loggedInUser.getFriends();
            this.printEnumeratedChoices(friends);
            
            int choice = select("Who to ignore?", this.loggedInUser.getFriends().length);
            Account friend = friends[choice];

            this.loggedInUser.ignoreFriend(friend);
            System.out.println("Ignored " + friend.getName());
        }

        /**
         * Exits the program
         */      
        private void quit() {
            System.out.println("Logging out...");
            this.sendMessage(new Logout(this.loggedInUser));
        }

        /**
         * Edits the users name and sends the updated information to the server
         */      
        private void editName() {
            System.out.print("Enter your user name (presently " + this.loggedInUser.getName() + "): ");
            String name = System.console().readLine();
            this.loggedInUser.setName(name);
            this.sendMessage(new NameChange(name)); 
        }

        /**
         * Edits the users password and sends the updated information to the server
         */      
        private void editPassword() {
            System.out.print("Update your password: ");
            String password = new String(System.console().readPassword());
            this.login.setPassword(password);
            this.sendMessage(new PasswordChange(password));
        }

        /**
         * Edits the users password or name and sends the updated information to the server
         */      
        private void editAccount() {
            System.out.print("Enter your password: ");
            String password = new String(System.console().readPassword());

            if (password.equals(this.login.getPassword())) {
                System.out.println("Edit [n]ame or [p]assword:");
                String input = System.console().readLine().toLowerCase(); 
                if (input.length() != 1) {
                    System.out.println("Invalid input");
                    return;
                }
                switch (input.charAt(0)) {
                case 'n': {
                    editName();
                    break;
                }
                case 'p': {
                    editPassword();
                    break;
                }
                default: {
                    System.out.println("Invalid choice.");
                    return;
                }

                }

            }
            else {
                System.out.println("Wrong password!");
            }
        }

        /**
         * Prints all of the users friends onto the screen
         */      
        private void listFriends() {
            if (this.loggedInUser.hasFriends()) {
                Account[] friends = this.loggedInUser.getFriends();
                this.printEnumeratedChoices(friends);
            } else {
                System.out.println("Sorry, but you don't seem to have any friends.");
            }
        }

        /**
         * Synchronizes the client with the server and prints all posts in the users feed onto the screen.
         */      
        private void updateFeed() {
            this.syncWithServer();
            this.feed.setAccount(this.loggedInUser);
            System.out.print(this.feed.renderAll());
            return; 
        }

        /**
         * Updates all posts made by a given account
         * @param account The account whos posts are to be updated
         */      
        private void updatePosts(Account account) {
            for (Post p : this.feed.getPosts()) {
                if(p.getPoster().getUserId().equals(account.getUserId())) {
                    p.getPoster().setName(account.getName());
                }
            }
        }

        /**
         * Updates the names of all friends that has made a name change since the last sync.
         * @param accounts Set of Accounts containing the up-to-date accounts.
         */      
        private void updateFriends(Set<Account> accounts) {
            for(Account fromServer : accounts) {
                for(Account fromClient : this.loggedInUser.getFriends()) {
                    if(fromServer.getUserId().equals(fromClient.getUserId())) {
                        String newName = fromServer.getName();
                        fromClient.setName(newName);
                        updatePosts(fromClient);
                    }
                }
            } 
        }

        /**
         * Updates a post in the clients feed with either a like or comment from the server.
         * @param p PostAction containing either a Like or Comment.
         */      
        private void addActions(PostAction p) {
            for(Post post : this.feed.getPosts()) {
                if(post.getPostId() == p.getPostId()) {
                    if(p instanceof Like) 
                        post.like((Like)p); 
                    else 
                        post.addComment((Comment)p); 
                }
            }
        }

        /**
         * Adds an account to the users list of friends and sends the response to the server.
         * @param friend The Account to befriend.
         */      
        private void approveFriendRequest(Account friend) {
            this.loggedInUser.addFriend(friend);
            System.out.println("Du godkände " + friend.getName() + "'s förfrågan.");
            sendMessage(new FriendRequestResponse(friend, this.loggedInUser, true));
            sendMessage(new AddFriend(friend));
        }

        /**
         * Sends a message to the server to decline a pending friend request
         * @param friend The Account that made the friend request.
         */      
        private void declineFriendRequest(Account friend) {
            System.out.println("Du avböjde " + friend.getName() + "'s förfrågan.");
            sendMessage(new FriendRequestResponse(friend, this.loggedInUser, false));
        }

        /**
         * Prompts the user to either accept or decline an incoming friend request.
         * @param f The FriendRequest to answer.
         */      
        private void respondToFriend(FriendRequest f) {
            Account friend = f.getRequester(); 
            String name    = friend.getName();
            
            System.out.println("En vänförfrågan från " + name + "! [A]cceptera eller a[v]böj.");
            String answer = System.console().readLine().toLowerCase();
            
            if(answer.length() != 1) {
                System.out.println("Invalid input!");
                return;
            }

            while(true) {
                switch (answer.charAt(0)) {
                case 'a': {
                    this.approveFriendRequest(friend);
                    return;
                } 
                case 'v': {
                    this.declineFriendRequest(friend);
                    return;
                }
                default:
                    System.out.println("Ogiltigt val!");
                }
            }
        }

        /**
         * Handles an incoming FriendRequestResponse. If it was accepted, the user that was asked is added to this users friends,
         * if it was declined or if the request timed out this is printed onto the screen.
         * @param r The FriendRequestResponse to handle.
         */      
        private void handleResponse(FriendRequestResponse r) {
            if(r.hasAccepted()) {
                this.loggedInUser.addFriend(r.getRespondingUser());
                System.out.println(r.getRespondingUser().getName() + " accepterade din vänförfrågan!");
                sendMessage(new AddFriend(r.getRespondingUser()));
            }
            else {
                if(r.hasTimedOut()) {
                    System.out.println("Förfrågan besvarades inte i tid.");
                }
                else {
                    System.out.println(r.getRespondingUser().getName() + " avböjde din vänförfrågan!");
                } 
            }
        }

        /**
         * Synchronizes the client with the server. The client receives all new posts from friends since the last sync,
         * any friend request or responses to friend requests along with comments or likes made by users since the last sync.
         */      
        private void syncWithServer() {
            this.sendMessage(new SyncRequest());
            Object o = this.receiveMessage();
            
            if (o instanceof SyncResponse) {
                Set<Account> usersFromServer = ((SyncResponse)o).getUsers();
                this.knownUsers.addAll(usersFromServer); 
                this.updateFriends(usersFromServer);
                
                for (Post p : ((SyncResponse) o).getPosts()) this.newPost(p);
                for (FriendRequestResponse r : ((SyncResponse) o).getResponses()) this.handleResponse(r); 
                for (FriendRequest f : ((SyncResponse) o).getRequests()) this.respondToFriend(f);   
                for (PostAction p : ((SyncResponse)o).getPostActions()) this.addActions(p);
                // TODO
                // Only print the posts that I am interested in

            }
            else {
                System.out.println("Error: expected sync response, got " + o.getClass());
            }
        }

        /**
         * Asks the user for login information and sends this information to the server for an attempted login.
         */      

        private void loginOrCreateUser() throws IOException, UnknownHostException {
            Socket socket = new Socket(this.serverIp, port);
            this.outgoing = new ObjectOutputStream(socket.getOutputStream());

            System.out.print("Enter your user id (email address): ");
            String userid = System.console().readLine();
            System.out.print("Set your password: ");
            String password = new String(System.console().readPassword());
            System.out.print("Enter your user name: ");
            String name = System.console().readLine();

            assert(userid.length() > 0);
            assert(password.length() > 0);
            assert(name.length() > 0); 
             
            System.out.println("Logging in new user " + userid + "..."); 
            this.login = new Login(new Account(userid, name), password);
            outgoing.writeObject(this.login);
            this.outgoing = outgoing;
            incoming = new ObjectInputStream(socket.getInputStream());
            Account a = (Account) receiveMessage(); 
            this.loggedInUser = a;
        }

        /**
         * Prints the Twitterish "logo" onto the screen.
         */      
        private void displaySplashScreen() {
            System.out.println("");
            System.out.println("");
            System.out.println("  _______       _ _   _            _     _      ");
            System.out.println(" |__   __|     (_) | | |          (_)   | |     ");
            System.out.println("    | |_      ___| |_| |_ ___ _ __ _ ___| |__   ");
            System.out.println("    | \\ \\ /\\ / / | __| __/ _ \\ '__| / __| '_ \\  ");
            System.out.println("    | |\\ V  V /| | |_| ||  __/ |  | \\__ \\ | | | ");
            System.out.println("    |_| \\_/\\_/ |_|\\__|\\__\\___|_|  |_|___/_| |_| ");
            System.out.println("");
        }

        /**
         * Main loop. Prints the menu onto the screen and waits for user input.
         */      
        private boolean action() {
            System.out.println("\n" + this.loggedInUser.getName() +"'s " + "Actions:");
            System.out.print("[P]ost message    | ");
            System.out.print("[S]ync with server | ");
            System.out.print("[U]pdate feed     | ");
            System.out.print("[A]dd friend      | ");
            System.out.print("[R]emove friend | ");
            System.out.println();
            System.out.print("[I]gnore friend   | ");
            System.out.print("Uni[g]nore friend  | ");
            System.out.print("[L]ist friends    | ");
            System.out.print("[E]dit account    | ");
            System.out.print("[C]omment/Like  | ");
            System.out.print("\n[Q]uit");
            System.out.println();

            String input = System.console().readLine().toLowerCase(); 

            if (input.length() != 1 || input == null || ( input.equals("p") && input.equals("s") && input.equals("u") && input.equals("a") && input.equals("r") && input.equals("i") && input.equals("g") && input.equals("l") && input.equals("e") && input.equals("q"))) {
                System.out.println("Invalid input");
                return true;

            }

            switch (input.charAt(0)) {
            case 'p':
                this.postMessage();
                return true;
            case 's':
                this.syncWithServer();
                return true;
            case 'u':
                this.updateFeed();
                return true;
            case 'a':
                this.addFriend();
                return true;
            case 'r':
                this.removeFriend();
                return true;
            case 'i':
                this.ignoreFriend();
                return true;
            case 'g':
                this.unIgnoreFriend();
                return true;
            case 'e':
                this.editAccount();
                return true;
            case 'l':
                this.listFriends();
                return true;
            case 'c':
                this.commentOrLike();
                return true;
                case 'q':
                    this.quit();
                return false;
                            }

            return true;
        }

        public void start() throws IOException, UnknownHostException {
            this.displaySplashScreen();
            this.loginOrCreateUser();
            while (this.action())
                ;
        }
    }
}
