

#all:
    ./killIt
	javac *Tcp.java
	# javac HelloImplementation.java
	# rmic HelloImplementation
	# javac HelloC.java HelloServer.java
	# rmiregistry &
	sleep 4
	java qServerTcp 32010 quotes.txt &
	sleep 4
	# java HelloC
	java qClientTcp localhost 32010
	java qClientTcp localhost 32010
	java qClientTcp localhost 32010


#clean:
	rm -f *class
	
