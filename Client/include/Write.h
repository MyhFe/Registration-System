#ifndef BOOST_ECHO_CLIENT_WRITE_H
#define BOOST_ECHO_CLIENT_WRITE_H

#include "../include/connectionHandler.h"
#include <mutex>
#include <condition_variable>
class Write{
private:
    ConnectionHandler* ch;
    std:: string line;
    short getOp(std::string cmd);
    void StringMsg(char buff[], std::string msg);
    void numMsg(char buff[], std::string msg);
    void shortToBytes(short num, char* bytesArr);
    bool terminate;
    std::mutex mtx;
    std::condition_variable& cv;
public:
    Write(ConnectionHandler& ch2, std::condition_variable& cv2);
    void run();
    void getLineBytes(std:: string s, char* bytesArr);
};
#endif