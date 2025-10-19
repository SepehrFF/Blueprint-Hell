package game.view;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private HUDPanel hudPanel;

    public GameFrame(GamePanel gamePanel, HUDPanel hudPanel) {
        setTitle("BluePrint Hell - Game");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(false);
        setResizable(false);
        setLayout(new BorderLayout());

        this.gamePanel = gamePanel;
        this.hudPanel = hudPanel;

        add(hudPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    public GamePanel getGamePanel() { return gamePanel; }
    public HUDPanel getHudPanel() { return hudPanel; }
    
    public void setGamePanel(GamePanel newGamePanel) {
        if (this.gamePanel != null) {
            remove(this.gamePanel);
        }
        this.gamePanel = newGamePanel;
        add(this.gamePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
} 