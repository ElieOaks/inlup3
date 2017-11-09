import java.io.*;
import java.net.*;
import java.util.*;

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

        public Client(String serverIp, int port) {
            this.serverIp = serverIp;
            this.port = port;
            this.feed = new Feed(this.loggedInUser);
        }

        private void newAccount(Account account) {
            this.knownUsers.add(account);
        }

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

        private void postMessage() {
            System.out.println("Write your message on a single line: ");

            String msg = System.console().readLine();
            sendMessage(new PostMessage(msg));

            System.out.println("Message sent");
        }

        private void comment() {
            printPostsForComment();
            int choice = select("What to comment?", this.feed.getPosts().size());
            Post toComment = this.feed.getPosts().get(choice);

            System.out.println("Write your comment: ");
            String comment = System.console().readLine();

            if(comment != "") {
                toComment.addComment(new Comment(this.loggedInUser, comment));
                sendMessage(new CommentMessage(comment, toComment.getPostId(), this.loggedInUser));
            }   
        }

        private void printPostsForComment() {
            int i = 0;
            for(Post p : this.feed.getPosts()) {
                System.out.print(i + " " + p.render());
                i++;
            }
        }

        private void printEnumeratedChoices(Account[] choices) {
            for (int i = 0; i < choices.length; ++i) {
                System.out.println(i + "\t" + choices[i].getName());
            }
        }

        private void addFriend() {
            if (this.knownUsers.size() == 0) {
                System.out.println("You seem to be alone in the universe, at this moment.");
                return;
            }

            Account[] knownUsers = (Account[]) this.knownUsers.toArray(new Account[0]);
            Arrays.sort(knownUsers);
            printEnumeratedChoices(knownUsers);

            int choice = select("Who to befriend?", this.knownUsers.size());
            Account friend = knownUsers[choice];

            sendMessage(new FriendRequest(this.loggedInUser, friend));
            //this.loggedInUser.addFriend(friend);

            //System.out.println("Befriended " + friend.getName());
            System.out.println("Request sent to " + friend.getName()); 
        }

        private boolean validIndex(int index, int max) {
            return index >= 0 && index < max;
        }
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

        private int select(String question, int max) {
            int choice = 0;
            do {
                System.out.println(question);
                String choiceString = System.console().readLine();
                choice = Integer.parseInt(choiceString); 
            } while (!validIndex(choice, max)); 
            return choice;
        }

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

        private void quit() {
            System.out.println("Logging out...");
            this.sendMessage(new Logout(this.loggedInUser));
        }

        private void editName() {
            System.out.print("Enter your user name (presently " + this.loggedInUser.getName() + "): ");
            String name = System.console().readLine();
            this.loggedInUser.setName(name);
            this.sendMessage(new NameChange(name)); 
        }

        private void editPassword() {
            System.out.print("Update your password: ");
            String password = new String(System.console().readPassword());
            this.login.setPassword(password);
            this.sendMessage(new PasswordChange(password));
        }
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
        
        private void listFriends() {
            if (this.loggedInUser.hasFriends()) {
                Account[] friends = this.loggedInUser.getFriends();
                this.printEnumeratedChoices(friends);
            } else {
                System.out.println("Sorry, but you don't seem to have any friends.");
            }
        }

        private void updateFeed() {
            this.syncWithServer();
            this.feed.setAccount(this.loggedInUser);
            System.out.print(this.feed.renderAll());
            return; 
        }

        private void updatePosts(Account account) {
            for (Post p : this.feed.getPosts()) {
                if(p.getPoster().getUserId().equals(account.getUserId())) {
                    p.getPoster().setName(account.getName());
                }
            }
        }

        /// Uppdaterar namnändringar i listan av vänner
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

        private void approveFriendRequest(Account friend) {
            this.loggedInUser.addFriend(friend);
            System.out.println("Du godkände " + friend.getName() + "'s förfrågan.");
            sendMessage(new FriendRequestResponse(friend, this.loggedInUser, true));
            sendMessage(new AddFriend(friend));
        }

        private void declineFriendRequest(Account friend) {
            System.out.println("Du avböjde " + friend.getName() + "'s förfrågan.");
            sendMessage(new FriendRequestResponse(friend, this.loggedInUser, false));
        }

        /// Hanterar inkommande vänförfrågningar
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

        private void commentPost() {
            
        }

        //Hanterar inkommande svar på vänförfrågningar
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
                
                // TODO
                // Only print the posts that I am interested in

            }
            else {
                System.out.println("Error: expected sync response, got " + o.getClass());
            }
        }



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

        private boolean action() {
            System.out.println("\nActions:");
            System.out.print("[P]ost message    | ");
            System.out.print("[S]ync with server | ");
            System.out.print("[U]pdate feed     | ");
            System.out.print("[A]dd friend      | ");
            System.out.print("[R]emove friend   | ");
            System.out.println();
            System.out.print("[I]gnore friend   | ");
            System.out.print("Uni[g]nore friend  | ");
            System.out.print("[L]ist friends    | ");
            System.out.print("[E]dit account    | ");
            System.out.print("[C]omment post    | ");
            System.out.print("[Q]uit");
            System.out.println();

            String input = System.console().readLine().toLowerCase();

            if (input.length() != 1) {
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
                this.comment();
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
