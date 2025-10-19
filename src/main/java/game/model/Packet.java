package game.model;

public abstract class Packet {
    protected int size;
    protected double speed;
    protected double noise;
    protected boolean lost;
    protected PacketType type;
    protected Port currentPort;
    protected Wire currentWire;
    public double position = 0.0; // موقعیت روی سیم

    public enum PacketType { SQUARE, TRIANGLE }

    public Packet(PacketType type, int size, double speed) {
        this.type = type;
        this.size = size;
        this.speed = speed;
        this.noise = 0;
        this.lost = false;
    }

    public abstract int getCoinReward();
    public abstract void updateSpeedOnPort(boolean compatible);

    public void addNoise(double amount) {
        this.noise += amount;
    }

    public boolean isLost() {
        return lost;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    public PacketType getType() {
        return type;
    }

    public double getNoise() {
        return noise;
    }

    public void setCurrentPort(Port port) {
        this.currentPort = port;
    }

    public void setCurrentWire(Wire wire) {
        this.currentWire = wire;
    }

    public Port getCurrentPort() {
        return currentPort;
    }

    public Wire getCurrentWire() {
        return currentWire;
    }

    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
} 