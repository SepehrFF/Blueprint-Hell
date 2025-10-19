package game.controller;

import game.model.*;
import java.util.List;

public class GameController {
    private GameState state;
    private List<SystemNode> systems;
    private List<Wire> wires;
    private List<Packet> packets;
    private PacketController packetController;
    private SystemController systemController;
    private WireController wireController;
    private ShopController shopController;

    public GameController(GameState state, List<SystemNode> systems, List<Wire> wires, List<Packet> packets, Shop shop) {
        this.state = state;
        this.systems = systems;
        this.wires = wires;
        this.packets = packets;
        this.packetController = new PacketController();
        this.systemController = new SystemController();
        this.wireController = new WireController();
        this.shopController = new ShopController(shop, state);
    }

    public void update(double deltaTime) {
        for (Packet packet : packets) {
            if (!packet.isLost()) {
                packetController.movePacket(packet, deltaTime);
                packetController.checkPacketLoss(packet, state);
                // اگر بسته به مقصد رسید و سالم بود، سکه بده
                if (packet.getCurrentWire() == null && packet.getCurrentPort() != null && !packet.isLost()) {
                    if (packet.getCurrentPort().isInput()) {
                        state.setCoins(state.getCoins() + packet.getCoinReward());
                        packet.setLost(true); // بسته حذف شود
                    }
                }
            }
        }
        if (state.getPacketLoss() > state.getTotalPackets() / 2) {
            state.setGameOver(true);
        }
    }

    public void handleImpact(Packet source) {
        packetController.applyImpact(source, packets, 30.0);
    }

    public boolean buyPowerUp(Shop.PowerUp powerUp) {
        return shopController.buyPowerUp(powerUp);
    }

    public ShopController getShopController() {
        return shopController;
    }
    
    public List<SystemNode> getSystems() {
        return systems;
    }
    
    public List<Wire> getWires() {
        return wires;
    }
    
    public List<Packet> getPackets() {
        return packets;
    }
} 