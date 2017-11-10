all:
	javac *.java

client: all
	java Twitterish localhost 8082

server: all
	java Server 8082

clean:
	rm -f *.class
	rm -f *#
	rm -f *~
