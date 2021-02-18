package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class EncoderDecoder implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private short opCode=0;
    private Message msg;
    private ConcurrentHashMap<Integer,Integer> groups;
    private int zeroCounter=0;
	
    @Override
    public Message decodeNextByte(byte nextByte) {
        pushByte(nextByte);
        if(len>2) {  //after we have the op
            if (groups.get((int)opCode) == 1) {  //2 strings
                if (nextByte == '\0') {
                    if (zeroCounter < 2)
                        zeroCounter++;
                    if (zeroCounter == 2)
                        return type1or4();    //messages with strings
                }

            } else if (groups.get((int)opCode) == 3) {
                if (len == 4)
                    return type3();  //if op is login and read all bytes
            } else if (groups.get((int)opCode) == 4) {
                if (nextByte == '\0') {      //CHECK
                    return type1or4();
                }
            }
        }
        else if(len==2){
            opCode = bytesToShort(0);
            msg= new Message(null,null,opCode);
            initGroups();
            if(groups.get((int)opCode)==2){ //only op, no message
                inits();  //reset fields
                return msg;
            }
        }
        return null;
    }

    private Message type1or4(){   //messages with strings
        byte[] newBytes = Arrays.copyOfRange(bytes,2,len);
        String combined = new String(newBytes,StandardCharsets.UTF_8);  //bytes to string
        String[] words = combined.split("\0"); //get only words
        msg.addString(words);
		inits();
        return msg;
    }
    private Message type3() {
        short num = bytesToShort(2);  
        msg.addNum(num);
		inits();
        return msg;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);  //if more length is needed
        }
        bytes[len++] = nextByte;    //increment len after the command
    }

    @Override
    public byte[] encode(Message message) {
        String output = message.getStringOutput();
        if(message.getOp()==12)  //if ACK add final byte
            output = output+'\0';
        byte[] stringbytes = output.getBytes(StandardCharsets.UTF_8); //message to bytes
        byte[] opb = shortToBytes(message.getOp());                              // 12 or 13, ACK or ERR
        byte[] msopb = shortToBytes(message.getMsOp());                   //op for client, depends on the command
        byte[] code = new byte[4+stringbytes.length];                              //add all contents to byte array
        System.arraycopy(opb,0,code,0,opb.length);
        System.arraycopy(msopb,0,code,opb.length,msopb.length);
        System.arraycopy(stringbytes,0,code,opb.length+msopb.length,stringbytes.length);
        return code;
    }

    public short bytesToShort(int start)
    {
        short result = (short)((bytes[start] & 0xff) << 8);
        result += (short)(bytes[start+1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
	private void inits(){  //reset fields
	bytes = new byte[1 << 10]; //start with 1k
    len = 0;
    opCode=0;
    zeroCounter=0;
	}
    private void initGroups(){   //organize different ops by their message type
        groups = new ConcurrentHashMap<>();
        groups.put(1,1);
        groups.put(2,1);
        groups.put(3,1);
        groups.put(4,2);
        groups.put(5,3);
        groups.put(6,3);
        groups.put(7,3);
        groups.put(8,4);
        groups.put(9,3);
        groups.put(10,3);
        groups.put(11,2);
        groups.put(12,5);
        groups.put(13,3);
    }
}
