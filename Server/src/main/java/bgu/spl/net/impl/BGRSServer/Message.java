package bgu.spl.net.impl.BGRSServer;

public class Message {
    private short op;
    private String username;
    private String password;
    private short courseNum=0;
    private String output="";
    private short msOp;

    public Message(String username, String password, short op){
        this.username = username;
        this.password = password;
        this.op=op;
    }
    public Message(short op, short msOp,String output){
        this.op = op;
        this.msOp = msOp;
        this.output=output;
    }
    public void addString(String[] s) {
        username = s[0];
        if(s.length>1)
            password = s[1];
    }

    public void addOp(short opCode) {
        this.op=opCode;
    }

    public void addNum(short num) {
        this.courseNum=num;
    }

    public short getOp() {
        return op;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }


    public String getStringOutput() {
        return output;
    }

    public short getCourseNum() {
        return courseNum;
    }

    public short getMsOp() {
        return msOp;
    }
}
