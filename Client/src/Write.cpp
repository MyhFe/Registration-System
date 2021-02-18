#include "../include/Write.h"
#include <string>


Write::Write(ConnectionHandler& ch2, std::condition_variable& cv2): ch(),line(""),terminate(false),mtx(),cv(cv2){
    ch = &ch2;
}

 void Write::run() {
    short op;
     while (!terminate) {
         getline(std::cin, line);  //input
         int split = line.find(' ');
         std::string cmd = line.substr(0, split);  //get command
         std::string msg = line.substr(split + 1);//get string message
         op = getOp(cmd);
         int bytestowrite = 2;   //start after op
         char buff[(1 << 10)];
         shortToBytes(op, buff);
         char buff2[(1 << 10)];
         if (op == 1 || op == 2 || op == 3 || op == 8) {    //if message contains string/s
             StringMsg(buff2, msg);
             int zero = 0;
             int i = 0;
             int end = 2;   //2 strings
             if (op == 8)
                 end = 1;   //1 string
             while (zero < end) {
                 if (buff2[i] == '\0')  //reached space
                     zero++;
                 buff[i + 2] = buff2[i];
                 bytestowrite++;
                 i++;
             }
         } else if (op == 5 || op == 6 || op == 7 || op == 9 || op == 10) {  //message contains number only
             numMsg(buff2, msg);   //convert to bytes
             buff[2] = buff2[0];
             buff[3] = buff2[1];
             bytestowrite += 2;
         }
         ch->sendBytes(buff, bytestowrite);  //send to server
         if(op==4){
             std::unique_lock<std::mutex> lck(mtx);
             cv.wait(lck);    //if logout, wait here until till ack
             terminate = true; 
         }
     }
}

short Write::getOp(std::string cmd) {   //string commands and their op's
    if(cmd.compare("ADMINREG")==0)
        return 1;
    else if (cmd.compare("STUDENTREG")==0)
        return 2;
    else if (cmd.compare("LOGIN")==0)
        return 3;
    else if (cmd.compare("LOGOUT")==0)
        return 4;
    else if (cmd.compare("COURSEREG")==0)
        return 5;
    else if (cmd.compare("KDAMCHECK")==0)
        return 6;
    else if (cmd.compare("COURSESTAT")==0)
        return 7;
    else if (cmd.compare("STUDENTSTAT")==0)
        return 8;
    else if (cmd.compare("ISREGISTERED")==0)
        return 9;
    else if (cmd.compare("UNREGISTER")==0)
        return 10;
    else if (cmd.compare("MYCOURSES")==0)
        return 11;
    return 0;
}

void Write::StringMsg(char buff[], std::string msg) {  //convert string to bytes and add \0 between words and at the end
    int space = msg.find(' ');
    if(space>=0)
        msg[space] = '\0';
    msg = msg+'\0';
    msg.copy(buff,msg.length(),0);
}

void Write::numMsg(char *buff, std::string msg) {  //convert string to short to bytes
    short cn = (short)std::stoi(msg,0,10);
    shortToBytes(cn,buff);
}

void Write:: shortToBytes(short num, char* bytesArr){
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}