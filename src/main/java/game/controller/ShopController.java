package game.controller;

import game.model.*;

public class ShopController {
    private Shop shop;
    private GameState state;

    public ShopController(Shop shop, GameState state) {
        this.shop = shop;
        this.state = state;
    }

    public boolean buyPowerUp(Shop.PowerUp powerUp) {
        int price = shop.getPrice(powerUp);
        if (state.getCoins() >= price) {
            state.setCoins(state.getCoins() - price);
            return true;
        }
        return false;
    }
    
    public Shop getShop() {
        return shop;
    }
} 