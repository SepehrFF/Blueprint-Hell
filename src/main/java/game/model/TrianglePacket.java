package game.model;

import game.util.GameConstants;

public class TrianglePacket extends Packet {
    public TrianglePacket() {
        super(PacketType.TRIANGLE, GameConstants.TRIANGLE_SIZE, GameConstants.TRIANGLE_BASE_SPEED);
    }

    @Override
    public int getCoinReward() { return 2; }

    @Override
    public void updateSpeedOnPort(boolean compatible) {
        if (compatible) speed = GameConstants.TRIANGLE_BASE_SPEED;
        else speed += GameConstants.TRIANGLE_ACCELERATION;
    }
} 