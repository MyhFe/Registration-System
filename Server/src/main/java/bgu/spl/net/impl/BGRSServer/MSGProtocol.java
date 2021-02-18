package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessagingProtocol;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class MSGProtocol implements MessagingProtocol<Message> {
    private boolean shouldTerminate;
    private boolean isLoggedIn;
    Database db;
    Student user;
    public MSGProtocol(){
        isLoggedIn = false;
        db = Database.getInstance();
        user = null;
        shouldTerminate = false;
    }
    @Override
    public Message process(Message msg) {
        short op = msg.getOp();
        Commands cmd = new Commands(user);				// initialize a Command object
        Message output = cmd.execute(msg)	// executing the command that was received from the client. execute(msg) returns a message with op=12 or 13
		
        if(!isLoggedIn&&op==3){							// LOGIN command
            if(output.getOp()==12){
                isLoggedIn=true;
                user = db.getUser(msg.getUsername());								// MSGProtocol and Commands save the user object of the user that logged in
            }
        }
        else if(isLoggedIn&&op==4){							// LOGOUT
            isLoggedIn=false;
        }
        return output;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

 }
class Commands{				// All the commands the client sends to the server
    private Student st;						// if a user logs in we will define st to be the Student object of the user.
    Database db;
    public Commands(Student st){
        this.st = st;
        db = Database.getInstance();
    }
    public Message execute(Message msg){
        short op = msg.getOp();
        if(st==null&&op>3)					// if st=null it means the client didn't log in yet
            return cmd13(op);
        switch(op){
            case 1:
                return cmd1(msg);
            case 2:
                return cmd2(msg);
            case 3:
                return cmd3(msg);
            case 4:
                return cmd4();
            case 5:
                return cmd5(msg.getCourseNum());
            case 6:
                return cmd6(msg.getCourseNum());
            case 7:
                return cmd7(msg.getCourseNum());
            case 8:
                return cmd8(msg.getUsername());
            case 9:
                return cmd9(msg.getCourseNum());
            case 10:
                return cmd10(msg.getCourseNum());
            case 11:
                return cmd11();
            case 12:
                return cmd12(op,"");
            case 13:
                return cmd13(op);
        }
        return null;
    }
    public Message cmd1(Message msg){							// registering an Admin
        if(st!=null)
            return cmd13(msg.getOp());
        boolean reg = db.registerUser(msg.getUsername(), msg.getPassword(), true);
        if(!reg)
            return cmd13(msg.getOp());
        return cmd12(msg.getOp(),"");
    }
    public Message cmd2(Message msg){							// registering a Student
        if(st!=null)
            return cmd13(msg.getOp());
        boolean reg = db.registerUser(msg.getUsername(), msg.getPassword(), false);
        if(!reg)
            return cmd13(msg.getOp());
        return cmd12(msg.getOp(),"");
    }
    public Message cmd3(Message msg){						// LOGIN
        short op = 3;
        boolean success = false;
        Student user = db.getUser(msg.getUsername());
        synchronized (user) {
            if (st == null && user != null && !user.isLoggedIn() && msg.getPassword().equals(user.getPassword())) {
                success = true;
                user.logIn();
            }
        }
        if(!success)
            return cmd13(op);
        return cmd12(op,"");
    }
    public Message cmd4(){						// LOGOUT
        short op = 4;
        if (!st.isLoggedIn())
            return cmd13(op);
        st.logOut();
        return cmd12(op,"");
    }

    public Message cmd5(int courseNum){								// Register to a course
        short op = 5;
        boolean reg = db.registerCourse(st,courseNum);
        if(!reg)
            return cmd13(op);
        return cmd12(op,"");
    }
    public Message cmd6(int courseNum){									//KDAMCHECK
        short op = 6;
        if (st.getIsAdmin()||!db.getCourses().containsKey(courseNum))
            return cmd13(op);
        String kdam = db.getCourses().get(courseNum).getKdamString();
        return cmd12(op,kdam);
    }

    public Message cmd7(int courseNum){									//COURSESTAT
        short op = 7;
        if(!st.getIsAdmin())
            return cmd13(op);
        if (!db.getCourses().containsKey(courseNum))
            return cmd13(op);
        String output = db.getCourses().get(courseNum).courseStat();
        return cmd12(op,output);
    }
    public Message cmd8(String username){								//STUDENTSTAT
        short op = 8;
        if(!st.getIsAdmin())
            return cmd13(op);
		if (db.getUsers().get(username).getIsAdmin()) 
			return cmd13(op);
        if(!db.getUsers().containsKey(username))
            return cmd13(op);
        return cmd12(op,db.getUsers().get(username).getStats());
    }
    public Message cmd9(int courseNum){										//ISREGISTERED
        short op=9;
        if(st.getIsAdmin())
            return cmd13(op);
        if(db.getCourses().get(courseNum).getStudents().contains(st))
            return cmd12(op,"REGISTERED");
        return cmd12(op,"NOT REGISTERED");
    }
    public Message cmd10(int courseNum){									//UNREGISTER
        short op = 10;
        if(st.getIsAdmin()||cmd9(courseNum).getStringOutput().equals("NOT REGISTERED"))
            return cmd13(op);
        db.unregisterStudent(st,courseNum);
        return cmd12(op,"");
    }
    public Message cmd11(){																//MYCOURSES
        short op = 11;
        if (st.getIsAdmin()) {
            return cmd13(op);
        }
        LinkedList<Integer> courses = st.getSortedCourses();
        String output = "[";
        if (!courses.isEmpty()) {
            for (Integer i : courses) {
                output = output + i + ",";
            }
            output = output.substring(0, output.length() - 1);
        }
        output = output+"]";
        return cmd12(op,output);
    }
    public Message cmd12(short msop,String extra){return new Message((short)12,msop,extra);}				// ACK message
    public Message cmd13(short msop){return new Message((short)13,msop,"");}										// ERR message
}
