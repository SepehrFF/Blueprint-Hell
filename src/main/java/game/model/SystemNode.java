package game.model;

import java.util.ArrayList;
import java.util.List;

public class SystemNode {
    private List<Port> inputPorts;
    private List<Port> outputPorts;
    private List<Packet> storedPackets;
    private boolean isReference;
    private boolean indicatorOn;
    private boolean isStart;
    private boolean isEnd;
    private int x, y; // موقعیت گرافیکی

    public static final int MAX_STORAGE = 5;

    public SystemNode(boolean isReference, int x, int y) {
        this.isReference = isReference;
        this.inputPorts = new ArrayList<>();
        this.outputPorts = new ArrayList<>();
        this.storedPackets = new ArrayList<>();
        this.indicatorOn = false;
        this.x = x;
        this.y = y;
    }

    public void addInputPort(Port port) { inputPorts.add(port); }
    public void addOutputPort(Port port) { outputPorts.add(port); }
    public List<Port> getInputPorts() { return inputPorts; }
    public List<Port> getOutputPorts() { return outputPorts; }
    public List<Packet> getStoredPackets() { return storedPackets; }
    public boolean isReference() { return isReference; }
    public boolean isIndicatorOn() { return indicatorOn; }
    public void setIndicatorOn(boolean indicatorOn) { this.indicatorOn = indicatorOn; }
    public boolean isStart() { return isStart; }
    public void setStart(boolean isStart) { this.isStart = isStart; }
    public boolean isEnd() { return isEnd; }
    public void setEnd(boolean isEnd) { this.isEnd = isEnd; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; updatePortsPosition(); }
    public void setY(int y) { this.y = y; updatePortsPosition(); }
    private void updatePortsPosition() {
        for (Port p : inputPorts) p.updatePositionToParent();
        for (Port p : outputPorts) p.updatePositionToParent();
    }
} 