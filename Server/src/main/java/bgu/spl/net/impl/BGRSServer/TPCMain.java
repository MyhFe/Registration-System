package bgu.spl.net.impl.BGRSServer;


public class TPCMain {
    public static void main(String[] args) {
                  Server.threadPerClient(
                    Integer.parseInt(args[0]),  //port
                    ()-> new MSGProtocol(), EncoderDecoder::new).serve();

    }
}
