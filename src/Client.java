import java.net.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class Client {
    //final static int ARGC = 3;
    //final static int ARG_FILE = 0;
    //final static int ARG_PORT = 1;
    //final static int ARG_BUFF = 2;
    final static int ARGC = 2;
    final static int ARG_FILE = 0;
    final static int ARG_BUFF = 1;

    public static void checkArgs(String[] args) {
        if(args.length != ARGC) {
            //System.out.println("Client.java usage: java client input_file port buf_size");
            System.out.println("Client.java usage: java client input_file buf_size");
            System.exit(1);
        }
    }

    public static void main(String args[]) throws Exception {         
        checkArgs(args);

        final String REMOTE_PC = "127.0.0.1";
        final String FILE = args[ARG_FILE];
        //final int PORT = Integer.parseInt(args[ARG_PORT]);
        final int PORT = 42666;
        final int BUFFER_SIZE = Integer.parseInt(args[ARG_BUFF]);
        final int SRV_PORT = 42321;
        
        byte buffer[] = new byte[BUFFER_SIZE];
        FileInputStream inputStream = new FileInputStream(FILE);
        int byteCount = 0;
        while(inputStream.available()!=0) {
            buffer[byteCount] = (byte) inputStream.read();
            byteCount++;
        }                     
        inputStream.close();

        DatagramSocket socket = new DatagramSocket(PORT);
        List<Packet> packets = new ArrayList<Packet>();
        PacketList.initPackets(packets, buffer);
        //for(Packet p:packets) {
            //System.out.println(p);
        //} 
        PacketList.sendPackets(socket, packets, REMOTE_PC, SRV_PORT);
        PacketList.waitAck(socket, packets);

        socket.close();
    }
}

