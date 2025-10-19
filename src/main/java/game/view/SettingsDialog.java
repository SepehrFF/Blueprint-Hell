package game.view;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {
    private JSlider volumeSlider;
    private JButton btnClose;

    public SettingsDialog(JFrame parent) {
        super(parent, "تنظیمات صدا", true);
        setLayout(new BorderLayout());
        setSize(300, 120);
        setLocationRelativeTo(parent);

        volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        btnClose = new JButton("بستن");

        add(new JLabel("تنظیم صدا:"), BorderLayout.NORTH);
        add(volumeSlider, BorderLayout.CENTER);
        add(btnClose, BorderLayout.SOUTH);
    }

    public int getVolume() { return volumeSlider.getValue(); }
    public void setVolume(int value) { volumeSlider.setValue(value); }
    public void setCloseAction(java.awt.event.ActionListener l) { btnClose.addActionListener(l); }
} 