package game.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    private JButton btnStart, btnLevel1, btnLevel2, btnSettings, btnExit;
    private JLabel lblStatus, lblCoins;

    public MainMenu() {
        setTitle("BluePrint Hell - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("BluePrint Hell", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(title);

        lblStatus = new JLabel("وضعیت: مرحله اول قفل شده", SwingConstants.CENTER);
        lblStatus.setForeground(Color.ORANGE);
        panel.add(lblStatus);

        lblCoins = new JLabel("سکه‌ها: 0", SwingConstants.CENTER);
        lblCoins.setForeground(Color.YELLOW);
        panel.add(lblCoins);

        btnStart = new JButton("شروع بازی (ادامه از آخرین مرحله)");
        btnLevel1 = new JButton("مرحله اول - سیستم‌های ساده");
        btnLevel2 = new JButton("مرحله دوم - سیستم‌های پیچیده");
        btnSettings = new JButton("تنظیمات بازی");
        btnExit = new JButton("خروج از بازی");

        panel.add(btnStart);
        panel.add(btnLevel1);
        panel.add(btnLevel2);
        panel.add(btnSettings);
        panel.add(btnExit);

        add(panel);
    }

    public void setStartAction(ActionListener l) { btnStart.addActionListener(l); }
    public void setLevel1Action(ActionListener l) { btnLevel1.addActionListener(l); }
    public void setLevel2Action(ActionListener l) { btnLevel2.addActionListener(l); }
    public void setSettingsAction(ActionListener l) { btnSettings.addActionListener(l); }
    public void setExitAction(ActionListener l) { btnExit.addActionListener(l); }
    
    // متد برای به‌روزرسانی وضعیت منو
    public void updateStatus(boolean level1Completed, boolean level2Completed, int coins) {
        if (level2Completed) {
            lblStatus.setText("وضعیت: تمام مراحل تکمیل شده!");
            lblStatus.setForeground(Color.GREEN);
        } else if (level1Completed) {
            lblStatus.setText("وضعیت: مرحله اول تکمیل شده - مرحله دوم باز است");
            lblStatus.setForeground(Color.BLUE);
        } else {
            lblStatus.setText("وضعیت: مرحله اول قفل شده");
            lblStatus.setForeground(Color.ORANGE);
        }
        
        lblCoins.setText("سکه‌ها: " + coins);
    }
} 