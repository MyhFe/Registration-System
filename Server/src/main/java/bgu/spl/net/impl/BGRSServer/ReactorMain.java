package bgu.spl.net.impl.BGRSServer;



public class ReactorMain {
    public static void main(String[] args) {

        Server.reactor(
                Integer.parseInt(args[1]), // num of threads
                Integer.parseInt(args[0]),  //port
                () ->  new MSGProtocol(), EncoderDecoder::new).serve();
	  
    }
}