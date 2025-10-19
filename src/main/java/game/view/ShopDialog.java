package game.view;

import game.model.Shop;
import game.controller.ShopController;
import game.model.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ShopDialog extends JDialog {
    private JButton btnAtar, btnAiryaman, btnAnahita, btnClose;
    private ShopController shopController;
    private GameState state;

    public ShopDialog(JFrame parent, ShopController shopController, GameState state) {
        super(parent, "فروشگاه", true);
        this.shopController = shopController;
        this.state = state;
        setLayout(new GridLayout(5, 1, 10, 10));
        setSize(300, 250);
        setLocationRelativeTo(parent);

        btnAtar = new JButton("O' Atar (3 سکه): غیرفعال کردن Impact برای 10 ثانیه");
        btnAiryaman = new JButton("O' Airyaman (4 سکه): غیرفعال کردن برخورد برای 5 ثانیه");
        btnAnahita = new JButton("O' Anahita (5 سکه): صفر کردن نویز همه پکت‌ها");
        btnClose = new JButton("بستن");

        add(btnAtar);
        add(btnAiryaman);
        add(btnAnahita);
        add(btnClose);

        btnAtar.addActionListener(e -> buy(Shop.PowerUp.ATAR));
        btnAiryaman.addActionListener(e -> buy(Shop.PowerUp.AIRYAMAN));
        btnAnahita.addActionListener(e -> buy(Shop.PowerUp.ANAHITA));
    }

    public void setAtarAction(ActionListener l) { btnAtar.addActionListener(l); }
    public void setAiryamanAction(ActionListener l) { btnAiryaman.addActionListener(l); }
    public void setAnahitaAction(ActionListener l) { btnAnahita.addActionListener(l); }
    public void setCloseAction(ActionListener l) { btnClose.addActionListener(l); }

    private void buy(Shop.PowerUp powerUp) {
        if (shopController.buyPowerUp(powerUp)) {
            JOptionPane.showMessageDialog(this, "خرید موفقیت‌آمیز بود!");
        } else {
            JOptionPane.showMessageDialog(this, "سکه کافی ندارید.");
        }
    }
} 