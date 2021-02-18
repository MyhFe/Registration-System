#ifndef ASSIGNMENT3_READ_H
#define ASSIGNMENT3_READ_H

#include "../include/connectionHandler.h"
#include "Write.h"

class Read{
private:
    bool terminate;
    ConnectionHandler* ch;
    std::condition_variable& cv;			// for synchronization and notifying the writing thread
public:
    Read( ConnectionHandler& ch, std::condition_variable& cv2);
    void run();
    void print(short op, short msOp, std::string msg);
    short bytesToShort(char* bytesArr, int start);
    std::string bytesToString(char* bytesArr);
};
#endif