package game.controller;

import game.model.*;
import java.util.List;

public class PacketController {
    public void movePacket(Packet packet, double deltaTime) {
        Wire wire = packet.getCurrentWire();
        if (wire != null) {
            double position = packet.position;
            double speed = packet.getSpeed();
            position += (speed * deltaTime) / wire.getLength();
            if (position >= 1.0) {
                Port to = wire.getTo();
                packet.setCurrentWire(null);
                packet.setCurrentPort(to);
                to.setOccupied(true);
                wire.getPacketsOnWire().remove(packet);
                packet.position = 0.0;
            } else {
                packet.position = position;
            }
        }
    }

    public void applyImpact(Packet source, List<Packet> allPackets, double impactRadius) {
        for (Packet p : allPackets) {
            if (p == source || p.isLost()) continue;
            double dist = distanceBetweenPackets(source, p);
            if (dist < impactRadius) {
                double effect = (impactRadius - dist) / impactRadius;
                p.addNoise(effect * 2.0);
                if (p.getNoise() > game.util.GameConstants.MAX_NOISE) {
                    p.setLost(true);
                }
            }
        }
    }

    public void checkPacketLoss(Packet packet, game.model.GameState state) {
        if (packet.getNoise() > game.util.GameConstants.MAX_NOISE) {
            packet.setLost(true);
            state.setPacketLoss(state.getPacketLoss() + 1);
        }
    }

    private double distanceBetweenPackets(Packet a, Packet b) {
        if (a.getCurrentWire() != b.getCurrentWire()) return Double.MAX_VALUE;
        return Math.abs(a.position - b.position) * a.getCurrentWire().getLength();
    }
} 