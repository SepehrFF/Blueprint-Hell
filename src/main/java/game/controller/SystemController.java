package game.controller;

import game.model.*;
import java.util.List;
import java.util.Random;

public class SystemController {
    public void receivePacket(SystemNode system, Packet packet) {
        if (system.getStoredPackets().size() < SystemNode.MAX_STORAGE) {
            system.getStoredPackets().add(packet);
        } else {
            packet.setLost(true);
        }
    }

    public Port selectOutputPort(SystemNode system, Packet packet) {
        List<Port> outputs = system.getOutputPorts();
        for (Port port : outputs) {
            if (port.isCompatible(packet) && !port.isOccupied()) return port;
        }
        Random rand = new Random();
        Port randomFree = outputs.stream().filter(p -> !p.isOccupied()).findAny().orElse(null);
        return randomFree;
    }
} 