import java.io.IOException;
import java.net.*;
import java.util.List;

public abstract class PacketList {

    public static void initPackets(List<Packet> packets, byte[] data) {
        final int dataLength = data.length;
        final int packetCount = (dataLength/Packet.MAX_DATA_SIZE)+1;
        int dataCursor = 0;
        int packetId = 0;

        if(packetCount == 1) {
            Field id = new Field(Packet.ID_SIZE, packetId++);
            Packet packet = new Packet(id, data, dataLength);
            packets.add(packet);
            return;
        }

        for(int i = 0; i < packetCount-1; i++) {
            Field id = new Field(Packet.ID_SIZE, packetId++);
            Packet packet = new Packet(id, Packet.MAX_DATA_SIZE);
            for(int j = 0; j < Packet.MAX_DATA_SIZE; j++) {
                packet.setData(j, data[dataCursor++]);
            }
            packets.add(packet);
        }

        final int remainSize = dataLength-dataCursor;
        Field id = new Field(Packet.ID_SIZE, packetId++);
        Packet packet = new Packet(id, remainSize);
        for(int i = 0; i < remainSize; i++) {
            packet.setData(i, data[dataCursor++]);
        }

        packets.add(packet);
    }

    public static void waitAck(DatagramSocket socket, List<Packet> sentPackets) throws IOException {
        byte[] buffer = new byte[Packet.HEADER_SIZE];
        int ackCount = 0;

        while(ackCount < sentPackets.size()) {
            DatagramPacket recvPacket = new DatagramPacket(buffer, Packet.HEADER_SIZE);
            socket.receive(recvPacket);
            Packet packet = Packet.fromByte(recvPacket.getData());
            System.out.println("Received ack: "+packet);

            for(Packet p:sentPackets) {
                if(packet.getAck().getValue() == p.getId().getValue()+1) {
                    ackCount++;
                }
            }
            System.out.println("Acknowledged packets: "+ackCount+"/"+sentPackets.size());
        }
    }

    public static void sendPackets(DatagramSocket socket, List<Packet> packets, String ip, int port) throws Exception {
        for(Packet p:packets) {
            p.sendPacket(socket, ip, port); 
        }
    }
}