package game.controller;

import game.model.*;

public class WireController {
    public boolean canConnect(Port from, Port to, double availableLength) {
        if (from == null || to == null) return false;
        if (from.getParent() == to.getParent()) return false;
        if (from.isInput() == to.isInput()) return false;
        double length = calcLength(from, to);
        return availableLength >= length;
    }

    public double calcLength(Port from, Port to) {
        int dx = from.getX() - to.getX();
        int dy = from.getY() - to.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
} 