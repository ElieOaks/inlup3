JUNIT = ".:/usr/share/java/junit4.jar:source"


all:
	javac -cp $(JUNIT) ./source/*.java -d ./bin

client: all
	java -cp ./bin/ Twitterish localhost 8082

server: all
	java -cp ./bin/ Server 8082

test: all
	java -cp $(JUNIT) TestFile

clean:
	rm -f  ./bin/*.class
	rm -f ./source/*#
	rm -f ./source/*~
