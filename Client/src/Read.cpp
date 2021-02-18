#include "../include/Read.h"
#include <string>
#include <boost/scoped_array.hpp>

Read::Read( ConnectionHandler& ch2, std::condition_variable& cv2):terminate(false),ch(),cv(cv2) {
    ch = &ch2;
}

void Read::run() {
    while (!terminate){
        short op=0;
        short msOp=0;
        std::string msg = "";
        char c='&';
        char bytes[(1<<10)];					// where we will save the message bytes
        ch->getBytes(bytes, 4);		// the first 4 bits of the message are the op (12/13) and the op of the command we sent
        op = bytesToShort(bytes,0);
        msOp = bytesToShort(bytes,2);
        if(op==12) {		// if it is an ACK message we'll also get a String that will end with \0
            while (c!='\0') {
                ch->getBytes(&c, 1);
                msg = msg+c;
            }
            if(msOp==4){	// if we tried to log out and recieved ACK we will notify the writing thrad and end the while loop
                std::unique_lock<std::mutex> lck(std::mutex m);
                cv.notify_all();
                terminate=true;
            }
        }
        print(op,msOp,msg);
    }
}

short Read::bytesToShort(char* bytesArr, int start) {
    short result = (short)((bytesArr[start] & 0xff) << 8);
    result += (short)(bytesArr[start+1] & 0xff);
    return result;
}

void Read::print(short op, short msOp, std::string msg) {
    if(op==13){
        std:: cout << "ERR "<< msOp <<std::endl;
    }
    else if(op==12) {
        if(msg.length()>1)
            std::cout << "ACK " << msOp << "\n" << msg << std::endl;
        else
            std::cout << "ACK " << msOp << std::endl;
    }
}