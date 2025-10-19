package game.model;

public class GameState {
    private int totalPackets;
    private int healthyPackets;
    private int packetLoss;
    private int coins;
    private double time;
    private boolean gameOver;
    private double wireLength;
    private int currentLevel; // مرحله فعلی (1 یا 2)
    private boolean level1Completed; // آیا مرحله اول تکمیل شده
    private boolean level2Completed; // آیا مرحله دوم تکمیل شده

    public GameState() {
        this.currentLevel = 1;
        this.level1Completed = false;
        this.level2Completed = false;
        this.coins = 0;
        this.wireLength = 500.0;
    }

    public int getTotalPackets() { return totalPackets; }
    public void setTotalPackets(int totalPackets) { this.totalPackets = totalPackets; }
    public int getHealthyPackets() { return healthyPackets; }
    public void setHealthyPackets(int healthyPackets) { this.healthyPackets = healthyPackets; }
    public int getPacketLoss() { return packetLoss; }
    public void setPacketLoss(int packetLoss) { this.packetLoss = packetLoss; }
    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
    public double getTime() { return time; }
    public void setTime(double time) { this.time = time; }
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public double getWireLength() { return wireLength; }
    public void setWireLength(double wireLength) { this.wireLength = wireLength; }
    
    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
    public boolean isLevel1Completed() { return level1Completed; }
    public void setLevel1Completed(boolean level1Completed) { this.level1Completed = level1Completed; }
    public boolean isLevel2Completed() { return level2Completed; }
    public void setLevel2Completed(boolean level2Completed) { this.level2Completed = level2Completed; }
    
    // متد برای بررسی اینکه آیا مرحله بعدی در دسترس است
    public boolean canAccessNextLevel() {
        if (currentLevel == 1) {
            return level1Completed;
        } else if (currentLevel == 2) {
            return level2Completed;
        }
        return false;
    }
    
    // متد برای رفتن به مرحله بعدی
    public void goToNextLevel() {
        if (currentLevel == 1 && level1Completed) {
            currentLevel = 2;
        }
    }
} 