all:
	javac ./source/*.java -d ./bin/

client: all
	java -cp ./bin/ Twitterish localhost 8081

server: all
	java -cp ./bin/ Server 8081

test: TestFile.class
	java -cp .:/usr/share/java/junit4.jar TestFile

TestFile.class: TestFile.java TestAccount.class
	javac -cp .:/usr/share/java/junit4.jar TestFile.java

TestAccount.class: TestAccount.java Account.class
	javac -cp .:/usr/share/java/junit4.jar TestAccount.java

clean:
	rm -f ./bin/*.class
	rm -f ./source/*#
	rm -f ./source/*~
