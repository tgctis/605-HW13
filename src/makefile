

all: StorageServer.class Forge.class Anvil.class qClientTcp.class qServerTcp.class
	./killIt

StorageServer.class: StorageServer.java
	javac StorageServer.java

Forge.class: Forge.java
	javac Forge.java

Anvil.class: Anvil.java
	javac Anvil.java

qClientTcp.class: qClientTcp.java
	javac qClientTcp.java

qServerTcp.class: qServerTcp.java
	javac qServerTcp.java

localQuote:
	java qServerTcp 32010 quotes.txt &
	java qClientTcp localhost 32010 &
	java qClientTcp localhost 32010 &
	java qClientTcp localhost 32010

localServer:
	rmiregistry &
	sleep 4
	java StorageServer 100 localhost SteamHammerStorage &
	sleep 4
	java Forge 1 localhost SteamHammerStorage &
	java Anvil 2 1 10 localhost SteamHammerStorage &
	java Forge 3 localhost SteamHammerStorage &
	java Anvil 4 2 10 localhost SteamHammerStorage &
	java Anvil 4 3 10 localhost SteamHammerStorage

gladosServer:
	rmiregistry &
	sleep 4
	java StorageServer 100 localhost SteamHammerStorage &
glados:
	rmiregistry &
	rmic StorageServer
	java Forge 1 glados.cs.rit.edu SteamHammerStorage &
	java Anvil 2 1 10 glados.cs.rit.edu SteamHammerStorage &
	java Forge 3 glados.cs.rit.edu SteamHammerStorage &
	java Anvil 4 2 10 glados.cs.rit.edu SteamHammerStorage &
	java Anvil 4 3 10 glados.cs.rit.edu SteamHammerStorage

clean:
	rm -f *class
	./killIt

local:
	make -s all localServer

gladosRemote:
	make -s all gladosServer

gladosClient:
	make -s all glados

quote:
	make -s all localQuote