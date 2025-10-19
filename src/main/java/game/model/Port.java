package game.model;

public class Port {
    public enum PortType { SQUARE, TRIANGLE }
    private PortType type;
    private boolean isInput;
    private boolean occupied;
    private SystemNode parent;
    private int x, y; // موقعیت گرافیکی برای سیم‌کشی

    public Port(PortType type, boolean isInput, SystemNode parent, int x, int y) {
        this.type = type;
        this.isInput = isInput;
        this.parent = parent;
        this.occupied = false;
        this.x = x;
        this.y = y;
    }

    public boolean isCompatible(Packet packet) {
        return (type == PortType.SQUARE && packet.getType() == Packet.PacketType.SQUARE)
            || (type == PortType.TRIANGLE && packet.getType() == Packet.PacketType.TRIANGLE);
    }

    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
    public boolean isInput() { return isInput; }
    public SystemNode getParent() { return parent; }
    public int getX() { return x; }
    public int getY() { return y; }
    public PortType getType() { return type; }

    public void updatePositionToParent() {
        // پورت‌های ورودی سمت چپ، خروجی سمت راست
        if (isInput) {
            // برای ورودی‌های متعدد، موقعیت‌ها را توزیع کن
            java.util.List<Port> inputPorts = parent.getInputPorts();
            int index = inputPorts.indexOf(this);
            int count = inputPorts.size();
            int spacing = 60 / (count + 1);
            this.x = parent.getX() - 5;
            this.y = parent.getY() + spacing * (index + 1);
        } else {
            // برای خروجی‌های متعدد، موقعیت‌ها را توزیع کن
            java.util.List<Port> outputPorts = parent.getOutputPorts();
            int index = outputPorts.indexOf(this);
            int count = outputPorts.size();
            int spacing = 60 / (count + 1);
            this.x = parent.getX() + 85;
            this.y = parent.getY() + spacing * (index + 1);
        }
    }
} 