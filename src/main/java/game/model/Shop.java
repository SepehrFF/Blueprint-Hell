package game.model;

import java.util.HashMap;
import java.util.Map;

public class Shop {
    public enum PowerUp { ATAR, AIRYAMAN, ANAHITA }
    private Map<PowerUp, Integer> prices;

    public Shop() {
        prices = new HashMap<>();
        prices.put(PowerUp.ATAR, 3);
        prices.put(PowerUp.AIRYAMAN, 4);
        prices.put(PowerUp.ANAHITA, 5);
    }

    public int getPrice(PowerUp powerUp) {
        return prices.getOrDefault(powerUp, Integer.MAX_VALUE);
    }
} 