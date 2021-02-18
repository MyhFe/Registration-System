#include <stdlib.h>
#include <iostream>
#include "../include/connectionHandler.h"
#include <thread>
#include "../include/Write.h"
#include "../include/Read.h"

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    std::condition_variable cv;
    Write w(connectionHandler,cv);
    Read r(connectionHandler,cv);
    std:: thread writer(&Write::run,&w);
    r.run();
    writer.join();

    return 0;
}
