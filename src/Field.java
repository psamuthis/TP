import java.nio.ByteBuffer;

public class Field {
    public static final int BASE = 2;
    public static final int BYTE_CAPACITY = 4; 
    public static final int BITS_PER_BYTE = 8;
    public static final int BITS_COUNT = BYTE_CAPACITY*BITS_PER_BYTE;
    public static final int MAX_VALUE = ((Double)(Math.pow(BASE, BITS_COUNT))).intValue();
    public static final int MIN_VALUE = 0;

    private int value;
    private int size;

    public Field(int size, int value) {
        this.setSize(size);
        this.setValue(value);
    }

    public Field(int size) {
        this.setSize(size);
    }

    public int getValue() { return this.value; }
    public int getSize() { return this.size; }

    public void setSize(int size) {
        if(size <= BYTE_CAPACITY) {
            this.size = size;
        } else {
            this.size = BYTE_CAPACITY;
        }
    }

    public void setValue(int value) {
        if(value < MAX_VALUE || value > MIN_VALUE) {
            this.value = value;
        } else {
            this.value = 0;
        }
    }

    public static int toInteger(byte[] bytes) { 
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static byte[] toByteArray(int value) {
        return ByteBuffer.allocate(BYTE_CAPACITY).putInt(value).array();
    }
}
