package game.model;

import java.util.ArrayList;
import java.util.List;

public class Wire {
    private Port from;
    private Port to;
    private double length;
    private List<Packet> packetsOnWire;

    public Wire(Port from, Port to, double length) {
        this.from = from;
        this.to = to;
        this.length = length;
        this.packetsOnWire = new ArrayList<>();
    }

    public Port getFrom() { return from; }
    public Port getTo() { return to; }
    public double getLength() { return length; }
    public List<Packet> getPacketsOnWire() { return packetsOnWire; }
} 