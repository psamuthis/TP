import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Packet {
    public static final int MAX_DATA_SIZE = 420;
    public static final int ID_SIZE = Field.BYTE_CAPACITY;
    public static final int LEN_SIZE = Field.BYTE_CAPACITY;
    public static final int ACK_SIZE = Field.BYTE_CAPACITY;
    public static final int HEADER_SIZE = ID_SIZE+LEN_SIZE+ACK_SIZE;
    public static final int MAX_SIZE = MAX_DATA_SIZE+HEADER_SIZE;
    public static final int MIN_SIZE = HEADER_SIZE; 
    public static final int ACK_DEF = -1;

    private Field id;
    private Field length;
    private Field ack;
    private byte[] data; 
    private int dataSize;
    private int totalSize;

    public Packet(Field id, int dataSize) {
        this.setDataSize(dataSize);
        this.setTotalSize(HEADER_SIZE+dataSize);
        this.id = id;
        this.ack = new Field(ACK_SIZE, ACK_DEF);
        this.length = new Field(LEN_SIZE, this.totalSize);
        this.data = new byte[this.dataSize];
    }
    public Packet(Field id, Field ack, Field length) {
        this.id = id;
        this.ack = ack;
        this.length = length;
    }

    public Packet(Field id, byte[] data, int dataSize) {
        this.setTotalSize(HEADER_SIZE+dataSize);
        this.id = id;
        this.length = new Field(LEN_SIZE, this.totalSize);
        this.ack = new Field(ACK_SIZE, ACK_DEF);
        this.setData(data);
        this.setDataSize(dataSize);
    }

    public Packet(Field id, Field ack, Field length, int dataSize) {
        this.id = id;
        this.ack = ack;
        this.length = length;
        this.setDataSize(dataSize);
        this.setTotalSize(this.dataSize+HEADER_SIZE);
    }

    public Packet(Field id, Field ack, Field len, byte[] data, int dataSize) {
        this.id = id;
        this.ack = ack;
        this.length = len;
        this.setData(data);
        this.setDataSize(dataSize);
        this.setTotalSize(this.dataSize+HEADER_SIZE);
    }

    public Field getId() { return this.id; }
    public Field getLength() { return this.length; }
    public Field getAck() { return this.ack; }
    public byte[] getData() { return this.data; }
    public int getDataSize() { return this.dataSize; }
    public int getTotalSize() { return this.totalSize; }

    public void setId(Field id) { this.id = id; }
    public void setLength(Field length) { this.length = length; }
    public void setAck(Field ack) { this.ack = ack; }
    public void setData(int index, byte value) { this.data[index] = value; }

    public void setData(byte[] data) {
        if(data.length <= MAX_DATA_SIZE && data.length > 0) {
            this.data = data; 
        } else {
            this.data = new byte[0];
        }
    }

    public void setDataSize(int size) {
        if(size <= MAX_DATA_SIZE && size >= 0) {
            this.dataSize = size;
        } else {
            this.dataSize = MAX_DATA_SIZE;
        }
    }

    public void setTotalSize(int size) {
        if(size <= MAX_SIZE && size >= MIN_SIZE) {
            this.totalSize = size;
        } else {
            this.totalSize = MAX_SIZE;
        }
    }

    public static Packet generateAck(Packet recvPacket) {
        final int dataSize = 0;
        final Field id = new Field(Packet.ID_SIZE, 0);
        final Field ack = new Field(Packet.ACK_SIZE, recvPacket.getId().getValue()+1);
        final Field len = new Field(Packet.LEN_SIZE, Packet.ID_SIZE+Packet.ACK_SIZE+Packet.LEN_SIZE);
        return new Packet(id, ack, len, dataSize);
    }

    public byte[] toByte() {
        final byte[] id = Field.toByteArray(this.id.getValue());
        final byte[] length = Field.toByteArray(this.length.getValue());
        final byte[] ack = Field.toByteArray(this.ack.getValue());
        byte[] packet = new byte[this.length.getValue()];
        int cursor = 0;

        for(byte b:id) { packet[cursor++] = b; }
        for(byte b:length) { packet[cursor++] = b; }
        for(byte b:ack) { packet[cursor++] = b; }
        if(this.dataSize > 0) {
            for(byte b:this.data) { packet[cursor++] = b; }
        }
        return packet;
    }

    public static Packet fromByte(byte[] data) {
        byte[] id = new byte[ID_SIZE];
        byte[] length = new byte[LEN_SIZE];
        byte[] ack = new byte[ACK_SIZE];
        int cursor = 0;

        for(int i = 0; i < ID_SIZE; i++) { id[i] = data[cursor++]; }
        for(int i = 0; i < LEN_SIZE; i++) { length[i] = data[cursor++]; }
        for(int i = 0; i < ACK_SIZE; i++) { ack[i] = data[cursor++]; }
        final Field packetId = new Field(ID_SIZE, Field.toInteger(id));
        final Field packetLen = new Field(LEN_SIZE, Field.toInteger(length));
        final Field packetAck = new Field(ACK_SIZE, Field.toInteger(ack));
        final int contentLength = packetLen.getValue()-HEADER_SIZE;

        Packet packet = new Packet(packetId, packetAck, packetLen, contentLength);
        byte[] content = new byte[contentLength];
        for(int i = 0; i < contentLength && cursor < data.length; i++) {
            content[i] = data[cursor++]; 
        }
        packet.setData(content);

        return packet;
    }

    public void sendPacket(DatagramSocket socket, String ip, int port) throws IOException {
        //System.out.println("Sending packet " + packet);
        byte[] data = this.toByte();
        DatagramPacket datagramPacket = new DatagramPacket(
                data,
                this.getTotalSize(),
                InetAddress.getByName(ip),
                port);
        socket.send(datagramPacket);
    }

    public String toString() {
        final String title = "Packet data:\n";
        final String id = "\tID="+this.getId().getValue()+"\n";
        final String ack = "\tACK="+this.getAck().getValue()+"\n";
        final String len = "\tLEN="+this.getLength().getValue()+"\n";
        //final String tmp = new String(this.getData(), HEADER_SIZE, this.getDataSize()-HEADER_SIZE);
        //final String content = "\tCONTENT=\n"+tmp+"\n";
        return title+/*content+*/len+id+ack;
    }
}
