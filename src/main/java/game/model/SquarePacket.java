package game.model;

import game.util.GameConstants;

public class SquarePacket extends Packet {
    public SquarePacket() {
        super(PacketType.SQUARE, GameConstants.SQUARE_SIZE, GameConstants.SQUARE_BASE_SPEED);
    }

    @Override
    public int getCoinReward() { return 1; }

    @Override
    public void updateSpeedOnPort(boolean compatible) {
        if (compatible) speed = GameConstants.SQUARE_BASE_SPEED / 2.0;
        else speed = GameConstants.SQUARE_BASE_SPEED;
    }
} 