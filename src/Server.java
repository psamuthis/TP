import java.net.*;

public class Server
{
    public static void main(String args[]) throws Exception
    {
        final String REMOTE_PC = "127.0.0.1";
        final int CLIENT_PORT = 42666; 
        final int PORT = 42321;
        final int recvBuffer_SIZE = 66000;

        DatagramSocket socket = new DatagramSocket(PORT);

        while(true)
        {
            byte[] recvBuffer = new byte[recvBuffer_SIZE];
            DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
            socket.receive(recvPacket);
            Packet packet = Packet.fromByte(recvPacket.getData());
            System.out.println(packet);

            Packet ack = Packet.generateAck(packet);
            ack.sendPacket(socket, REMOTE_PC, CLIENT_PORT);
        }

        //outputStream.close();
        //socket.close();
    }
}

