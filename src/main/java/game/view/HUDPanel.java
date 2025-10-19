package game.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class HUDPanel extends JPanel {
    private JLabel lblCoins, lblPacketLoss, lblWireLength, lblTime;
    private JButton btnShop, btnSettings, btnForward, btnBackward, btnMainMenu;

    public HUDPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(Color.BLACK);

        lblCoins = new JLabel("سکه: 0");
        lblPacketLoss = new JLabel("Packet Loss: 0");
        lblWireLength = new JLabel("Wire Left: 0");
        lblTime = new JLabel("زمان: 0");

        lblCoins.setForeground(Color.YELLOW);
        lblPacketLoss.setForeground(Color.RED);
        lblWireLength.setForeground(Color.CYAN);
        lblTime.setForeground(Color.GREEN);

        btnShop = new JButton("فروشگاه");
        btnSettings = new JButton("تنظیمات");
        btnBackward = new JButton("<<");
        btnForward = new JButton(">>");
        btnMainMenu = new JButton("منوی اصلی");
        btnMainMenu.setBackground(Color.ORANGE);
        btnMainMenu.setForeground(Color.WHITE);
        
        add(btnMainMenu);
        add(btnShop);
        add(btnSettings);
        add(btnBackward);
        add(btnForward);

        add(lblCoins);
        add(lblPacketLoss);
        add(lblWireLength);
        add(lblTime);
    }

    public void updateHUD(int coins, int packetLoss, double wireLeft, double time) {
        lblCoins.setText("سکه: " + coins);
        lblPacketLoss.setText("Packet Loss: " + packetLoss);
        lblWireLength.setText("Wire Left: " + String.format("%.1f", wireLeft));
        lblTime.setText("زمان: " + String.format("%.1f", time));
    }

    public void setShopAction(ActionListener l) { btnShop.addActionListener(l); }
    public void setSettingsAction(ActionListener l) { btnSettings.addActionListener(l); }
    public void setForwardAction(ActionListener l) { btnForward.addActionListener(l); }
    public void setBackwardAction(ActionListener l) { btnBackward.addActionListener(l); }
    public void setMainMenuAction(ActionListener l) { btnMainMenu.addActionListener(l); }

    public void showGameOver(int packetLoss) {
        JOptionPane.showMessageDialog(this, "Game Over!\nPacket Loss: " + packetLoss);
    }

    public JButton getBtnForward() { return btnForward; }
    public JButton getBtnBackward() { return btnBackward; }
} 