all:
	javac *.java

client: all
	java Twitterish localhost 8081

server: all
	java Server 8081

test: TestFile.class
	java -cp .:/usr/share/java/junit4.jar TestFile

TestFile.class: TestFile.java TestAccount.class
	javac -cp .:/usr/share/java/junit4.jar TestFile.java

TestAccount.class: TestAccount.java Account.class
	javac -cp .:/usr/share/java/junit4.jar TestAccount.java

clean:
	rm -f *.class
	rm -f *#
	rm -f *~
