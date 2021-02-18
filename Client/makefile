CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread


all: bin/BGRSclient


bin/BGRSclient: bin/Client.o bin/connectionHandler.o bin/Read.o bin/Write.o
	@echo 'Building target: Client'
	@echo 'Invoking: C++ Linker'
	g++ -Wall -Weffc++ -o bin/BGRSclient bin/Client.o bin/connectionHandler.o bin/Read.o bin/Write.o $(LDFLAGS)
	@echo 'Finished building target: Client'
	@echo ' '

bin/Client.o: src/Client.cpp
	g++ $(CFLAGS) -o bin/Client.o src/Client.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/Read.o: src/Read.cpp
	g++ $(CFLAGS) -o bin/Read.o src/Read.cpp

bin/Write.o: src/Write.cpp
	g++ $(CFLAGS) -o bin/Write.o src/Write.cpp

clean:
	rm -f bin/*
