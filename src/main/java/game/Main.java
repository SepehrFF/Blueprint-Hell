package game;

import game.model.*;
import game.view.*;
import game.controller.*;
import game.util.GameConstants;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Component;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // ساخت مدل‌های اولیه
            GameState state = new GameState();
            state.setWireLength(GameConstants.MAX_WIRE_LENGTH);
            Shop shop = new Shop();

            // راه‌اندازی View
            HUDPanel hudPanel = new HUDPanel();
            GamePanel initialGamePanel = new GamePanel(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), state);
            GameFrame gameFrame = new GameFrame(initialGamePanel, hudPanel);

            // متغیرهای کنترل زمان
            double[] timeStep = {0.02};
            boolean[] paused = {false};

            // راه‌اندازی کنترلر اصلی
            GameController gameController = new GameController(state, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), shop);

            // کنترل دکمه‌های حرکت زمان
            hudPanel.getBtnForward().addActionListener(e -> timeStep[0] = 0.03);
            hudPanel.getBtnBackward().addActionListener(e -> timeStep[0] = -0.03);
            
            // کنترل کلیدهای جهت
            gameFrame.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) timeStep[0] = 0.03;
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) timeStep[0] = -0.03;
                }
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) timeStep[0] = 0.02;
                }
            });
            gameFrame.setFocusable(true);
            gameFrame.requestFocusInWindow();

            // اتصال دکمه‌های HUD به دیالوگ‌ها
            hudPanel.setShopAction(e -> {
                paused[0] = true;
                ShopDialog shopDialog = new ShopDialog(gameFrame, gameController.getShopController(), state);
                shopDialog.setCloseAction(ev -> {
                    shopDialog.setVisible(false);
                    paused[0] = false;
                });
                shopDialog.setVisible(true);
            });
            hudPanel.setSettingsAction(e -> {
                SettingsDialog settings = new SettingsDialog(gameFrame);
                settings.setCloseAction(ev -> settings.setVisible(false));
                settings.setVisible(true);
            });
            
            // منوی اصلی
            MainMenu menu = new MainMenu();
            
            // دکمه بازگشت به منوی اصلی
            hudPanel.setMainMenuAction(e -> {
                int choice = JOptionPane.showConfirmDialog(gameFrame, 
                    "آیا می‌خواهید به منوی اصلی بازگردید؟\n" +
                    "پیشرفت فعلی ذخیره خواهد شد.",
                    "بازگشت به منو", JOptionPane.YES_NO_OPTION);
                
                if (choice == JOptionPane.YES_OPTION) {
                    gameFrame.setVisible(false);
                    menu.updateStatus(state.isLevel1Completed(), state.isLevel2Completed(), state.getCoins());
                    menu.setVisible(true);
                }
            });
            menu.setStartAction(e -> {
                menu.setVisible(false);
                gameFrame.setVisible(true);
                
                // بررسی اینکه کدام مرحله باید اجرا شود
                if (state.isLevel1Completed() && !state.isLevel2Completed()) {
                    // مرحله اول تکمیل شده، مرحله دوم را شروع کن
                    state.setCurrentLevel(2);
                    // ریست کردن زمان و packet loss برای مرحله جدید
                    state.setTime(0.0);
                    state.setPacketLoss(0);
                    Level2Manager.createLevel2(gameFrame, hudPanel, gameController, state, timeStep, paused);
                    System.out.println("ادامه بازی: مرحله دوم شروع شد. سکه‌های ذخیره شده: " + state.getCoins());
                } else if (!state.isLevel1Completed()) {
                    // مرحله اول هنوز تکمیل نشده
                    state.setCurrentLevel(1);
                    // ریست کردن زمان و packet loss برای مرحله جدید
                    state.setTime(0.0);
                    state.setPacketLoss(0);
                    Level1Manager.createLevel1(gameFrame, hudPanel, gameController, state, timeStep, paused);
                    System.out.println("ادامه بازی: مرحله اول شروع شد. سکه‌های ذخیره شده: " + state.getCoins());
                } else {
                    // هر دو مرحله تکمیل شده‌اند
                    JOptionPane.showMessageDialog(menu, 
                        "تبریک! 🎉\n\n" +
                        "شما تمام مراحل را تکمیل کرده‌اید!\n" +
                        "سکه‌های نهایی: " + state.getCoins() + "\n\n" +
                        "بازی تمام شد!",
                        "بازی تمام شد", JOptionPane.INFORMATION_MESSAGE);
                    menu.setVisible(true);
                    return;
                }
                
                Timer timer = setupGameTimer(gameFrame, hudPanel, gameController, state, timeStep, paused, menu);
                timer.start();
            });
            
            // دکمه‌های انتخاب مرحله
            menu.setLevel1Action(e -> {
                menu.setVisible(false);
                gameFrame.setVisible(true);
                state.setCurrentLevel(1);
                // ریست کردن زمان و packet loss برای مرحله جدید
                state.setTime(0.0);
                state.setPacketLoss(0);
                Level1Manager.createLevel1(gameFrame, hudPanel, gameController, state, timeStep, paused);
                Timer timer = setupGameTimer(gameFrame, hudPanel, gameController, state, timeStep, paused, menu);
                timer.start();
            });
            
            menu.setLevel2Action(e -> {
                // بررسی اینکه آیا مرحله اول تکمیل شده
                if (!state.isLevel1Completed()) {
                    JOptionPane.showMessageDialog(menu, 
                        "برای دسترسی به مرحله دوم، ابتدا مرحله اول را تکمیل کنید!",
                        "مرحله قفل شده", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                menu.setVisible(false);
                gameFrame.setVisible(true);
                state.setCurrentLevel(2);
                // ریست کردن زمان و packet loss برای مرحله جدید
                state.setTime(0.0);
                state.setPacketLoss(0);
                Level2Manager.createLevel2(gameFrame, hudPanel, gameController, state, timeStep, paused);
                Timer timer = setupGameTimer(gameFrame, hudPanel, gameController, state, timeStep, paused, menu);
                timer.start();
            });
            
            menu.setSettingsAction(e -> {
                SettingsDialog settings = new SettingsDialog(menu);
                settings.setCloseAction(ev -> settings.setVisible(false));
                settings.setVisible(true);
            });
            
            menu.setExitAction(e -> System.exit(0));
            
            // به‌روزرسانی وضعیت منو
            menu.updateStatus(state.isLevel1Completed(), state.isLevel2Completed(), state.getCoins());
            
            menu.setVisible(true);
        });
    }
    
    // راه‌اندازی تایمر اصلی بازی
    private static Timer setupGameTimer(GameFrame gameFrame, HUDPanel hudPanel, GameController gameController, 
                                      GameState state, double[] timeStep, boolean[] paused, MainMenu menu) {
        Timer timer = new Timer(20, evt -> {
            if (!paused[0]) {
                gameController.update(timeStep[0]);
                state.setTime(state.getTime() + timeStep[0]);
                
                // حرکت سیگنال‌ها و بررسی Packet Loss
                List<Packet> packets = gameController.getPackets();
                List<Wire> wires = gameController.getWires();
                List<SystemNode> systems = gameController.getSystems();
                
                // حرکت سیگنال‌ها
                for (int i = packets.size() - 1; i >= 0; i--) {
                    Packet signal = packets.get(i);
                    Wire wire = signal.getCurrentWire();
                    
                    if (wire != null) {
                        // بررسی نویز پکت - اگر نویز از اندازه پکت بیشتر شود، پکت از دست می‌رود
                        if (signal.getNoise() > signal.getSize()) {
                            System.out.println("پکت به دلیل نویز زیاد از دست رفت: نویز=" + signal.getNoise() + ", اندازه=" + signal.getSize());
                            state.setPacketLoss(state.getPacketLoss() + 1);
                            packets.remove(signal);
                            continue;
                        }
                        
                        // محاسبه سرعت بر اساس نوع پکت و سازگاری پورت
                        double speedMultiplier = 0.5; // سرعت پایه
                        
                        if (signal instanceof SquarePacket) {
                            // مربع: سرعت ثابت
                            speedMultiplier = 0.5;
                        } else if (signal instanceof TrianglePacket) {
                            // مثلث: سرعت متغیر بر اساس سازگاری
                            Port fromPort = wire.getFrom();
                            Port toPort = wire.getTo();
                            if (fromPort.getType() == toPort.getType()) {
                                speedMultiplier = 0.5; // سرعت ثابت برای پورت سازگار
                            } else {
                                speedMultiplier = 1.0; // سرعت بیشتر برای پورت ناسازگار
                            }
                        }
                        
                        // حرکت سیگنال
                        signal.position += timeStep[0] * speedMultiplier;
                        
                        // محدود کردن position
                        if (signal.position < 0) signal.position = 0;
                        if (signal.position > 1) signal.position = 1;
                        
                        // بررسی رسیدن به انتهای سیم
                        if (signal.position >= 1.0) {
                            Port to = wire.getTo();
                            signal.setCurrentWire(null);
                            signal.setCurrentPort(to);
                            wire.getPacketsOnWire().remove(signal);
                            
                            // بررسی رسیدن به END
                            SystemNode end = systems.stream().filter(SystemNode::isEnd).findFirst().orElse(null);
                            if (to.getParent() == end) {
                                // اضافه کردن سکه
                                int reward = signal.getCoinReward();
                                state.setCoins(state.getCoins() + reward);
                                packets.remove(signal);
                                
                                // بررسی اینکه آیا همه سیگنال‌ها به END رسیده‌اند
                                if (packets.isEmpty()) {
                                    // ذخیره وضعیت مرحله
                                    if (state.getCurrentLevel() == 1) {
                                        state.setLevel1Completed(true);
                                        System.out.println("مرحله اول تکمیل شد! سکه‌ها ذخیره شدند: " + state.getCoins());
                                    } else if (state.getCurrentLevel() == 2) {
                                        state.setLevel2Completed(true);
                                        System.out.println("مرحله دوم تکمیل شد!");
                                    }
                                    
                                    JOptionPane.showMessageDialog(gameFrame, 
                                        "تبریک! 🎉\n\n" +
                                        "همه سیگنال‌ها با موفقیت به END رسیدند!\n\n" +
                                        "سکه‌های کسب شده: " + state.getCoins() + "\n" +
                                        "زمان سپری شده: " + String.format("%.1f", state.getTime()) + " ثانیه\n" +
                                        "Packet Loss: " + state.getPacketLoss() + "%\n\n" +
                                        "مرحله با موفقیت تکمیل شد!",
                                        "مرحله تکمیل شد", JOptionPane.INFORMATION_MESSAGE);
                                    
                                    // ریست طول سیم و پکت لاست
                                    state.setWireLength(GameConstants.MAX_WIRE_LENGTH);
                                    state.setPacketLoss(0);
                                    
                                    // توقف تایمر و بازگشت به منوی اصلی
                                    SwingUtilities.invokeLater(() -> {
                                        gameFrame.setVisible(false);
                                        menu.updateStatus(state.isLevel1Completed(), state.isLevel2Completed(), state.getCoins());
                                        menu.setVisible(true);
                                    });
                                }
                            } else if (to.isInput()) {
                                // تقسیم سیگنال در سیستم میانی
                                SystemNode sys = to.getParent();
                                List<Port> outputPorts = sys.getOutputPorts();
                                
                                // اضافه کردن سکه بر اساس نوع سیگنال
                                if (signal instanceof SquarePacket) {
                                    state.setCoins(state.getCoins() + 1); // مربع: 1 سکه
                                    System.out.println("سیگنال مربعی وارد سیستم شد: +1 سکه");
                                } else if (signal instanceof TrianglePacket) {
                                    state.setCoins(state.getCoins() + 2); // مثلث: 2 سکه
                                    System.out.println("سیگنال مثلثی وارد سیستم شد: +2 سکه");
                                }
                                
                                // حذف سیگنال اصلی
                                packets.remove(signal);
                                
                                if (signal instanceof SquarePacket) {
                                    // سیگنال مربعی: هم سیگنال مربعی و هم مثلثی بساز
                                    for (Port out : outputPorts) {
                                        Packet newSignal = null;
                                        if (out.getType() == Port.PortType.SQUARE) {
                                            newSignal = new SquarePacket();
                                        } else if (out.getType() == Port.PortType.TRIANGLE) {
                                            newSignal = new TrianglePacket();
                                        }
                                        if (newSignal != null) {
                                            for (Wire next : wires) {
                                                if (next.getFrom() == out) {
                                                    newSignal.setCurrentWire(next);
                                                    newSignal.position = 0.0;
                                                    next.getPacketsOnWire().add(newSignal);
                                                    packets.add(newSignal);
                                                    System.out.println("سیگنال جدید ایجاد شد: " + newSignal.getClass().getSimpleName() + " روی سیم " + out.getType());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                } else if (signal instanceof TrianglePacket) {
                                    // سیگنال مثلثی فقط روی خروجی مثلثی
                                    for (Port out : outputPorts) {
                                        if (out.getType() == Port.PortType.TRIANGLE) {
                                            TrianglePacket newSignal = new TrianglePacket();
                                            for (Wire next : wires) {
                                                if (next.getFrom() == out) {
                                                    newSignal.setCurrentWire(next);
                                                    newSignal.position = 0.0;
                                                    next.getPacketsOnWire().add(newSignal);
                                                    packets.add(newSignal);
                                                    System.out.println("سیگنال جدید ایجاد شد: TrianglePacket روی سیم TRIANGLE");
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // بن‌بست - Packet Loss
                                state.setPacketLoss(state.getPacketLoss() + 1);
                                packets.remove(signal);
                            }
                        }
                    }
                }
                
                // بررسی برخورد پکت‌ها (Impact)
                checkPacketCollisions(packets, state);
                
                // بررسی Packet Loss و Game Over
                if (state.getPacketLoss() > 50) {
                    JOptionPane.showMessageDialog(gameFrame, 
                        "Game Over! 💀\n\n" +
                        "شبکه شما معیوب است!\n\n" +
                        "اطلاعات بازی:\n" +
                        "• Packet Loss: " + state.getPacketLoss() + "%\n" +
                        "• سکه‌های کسب شده: " + state.getCoins() + "\n" +
                        "• زمان سپری شده: " + String.format("%.1f", state.getTime()) + " ثانیه\n" +
                        "• پکت‌های سالم باقی‌مانده: " + packets.size() + "\n\n" +
                        "مرحله ریست می‌شود و از ابتدا شروع می‌شود.",
                        "Game Over", JOptionPane.ERROR_MESSAGE);
                    
                    // توقف تایمر و بازگشت به منوی اصلی
                    SwingUtilities.invokeLater(() -> {
                        gameFrame.setVisible(false);
                        menu.updateStatus(state.isLevel1Completed(), state.isLevel2Completed(), state.getCoins());
                        menu.setVisible(true);
                    });
                }
                
                // به‌روزرسانی UI
                GamePanel gamePanel = gameFrame.getGamePanel();
                if (gamePanel != null) {
                    gamePanel.updateSystems();
                    gamePanel.repaint();
                }
                hudPanel.updateHUD(state.getCoins(), state.getPacketLoss(), state.getWireLength(), state.getTime());
            }
        });
        return timer;
    }
    
    // بررسی برخورد پکت‌ها و ایجاد Impact
    private static void checkPacketCollisions(List<Packet> packets, GameState state) {
        for (int i = 0; i < packets.size(); i++) {
            for (int j = i + 1; j < packets.size(); j++) {
                Packet p1 = packets.get(i);
                Packet p2 = packets.get(j);
                
                // بررسی برخورد روی همان سیم
                if (p1.getCurrentWire() == p2.getCurrentWire() && p1.getCurrentWire() != null) {
                    double distance = Math.abs(p1.position - p2.position);
                    if (distance < 0.1) { // فاصله کمتر از 0.1 = برخورد
                        System.out.println("برخورد پکت‌ها در موقعیت: " + p1.position);
                        
                        // اضافه کردن نویز به پکت‌های برخورد کرده
                        p1.addNoise(15.0);
                        p2.addNoise(15.0);
                        
                        // ایجاد Impact - موج ضربه
                        createImpactWave(p1, p2, packets, state);
                        
                        // حذف پکت‌های برخورد کرده (برخورد باعث از دست رفتن پکت‌ها می‌شود)
                        state.setPacketLoss(state.getPacketLoss() + 2); // دو پکت از دست رفت
                        packets.remove(p1);
                        packets.remove(p2);
                        i--; // تنظیم index
                        break;
                    }
                }
            }
        }
    }
    
    // ایجاد موج ضربه (Impact Wave)
    private static void createImpactWave(Packet p1, Packet p2, List<Packet> packets, GameState state) {
        // موج ضربه از نقطه برخورد به همه جهات ساطع می‌شود
        double impactPosition = p1.position; // موقعیت نقطه برخورد
        Wire impactWire = p1.getCurrentWire(); // سیم محل برخورد
        
        System.out.println("Impact Wave ایجاد شد در موقعیت: " + impactPosition);
        
        for (Packet p : packets) {
            if (p != p1 && p != p2 && p.getCurrentWire() != null) {
                // محاسبه فاصله از نقطه برخورد
                double impactDistance = Math.abs(p.position - impactPosition);
                
                // اگر پکت روی همان سیم باشد
                if (p.getCurrentWire() == impactWire) {
                    if (impactDistance < 0.2) { // فاصله نزدیک
                        // احتمال بالای منحرف شدن (50%)
                        if (Math.random() < 0.5) {
                            System.out.println("پکت از سیم منحرف شد (فاصله نزدیک)");
                            state.setPacketLoss(state.getPacketLoss() + 1);
                            packets.remove(p);
                        } else {
                            // اضافه کردن نویز به پکت
                            p.addNoise(10.0);
                            System.out.println("نویز به پکت اضافه شد: " + p.getNoise());
                        }
                    } else if (impactDistance < 0.4) { // فاصله متوسط
                        // احتمال متوسط منحرف شدن (30%)
                        if (Math.random() < 0.3) {
                            System.out.println("پکت از سیم منحرف شد (فاصله متوسط)");
                            state.setPacketLoss(state.getPacketLoss() + 1);
                            packets.remove(p);
                        } else {
                            // اضافه کردن نویز کمتر
                            p.addNoise(5.0);
                            System.out.println("نویز کم به پکت اضافه شد: " + p.getNoise());
                        }
                    }
                } else {
                    // پکت روی سیم دیگر - اثر کمتر
                    if (impactDistance < 0.1) { // فقط اگر خیلی نزدیک باشد
                        if (Math.random() < 0.1) { // احتمال کم (10%)
                            System.out.println("پکت از سیم منحرف شد (سیم دیگر)");
                            state.setPacketLoss(state.getPacketLoss() + 1);
                            packets.remove(p);
                        }
                    }
                }
            }
        }
    }
}

// کلاس مدیریت مرحله اول
class Level1Manager {
    public static void createLevel1(GameFrame gameFrame, HUDPanel hudPanel, GameController gameController, 
                                   GameState state, double[] timeStep, boolean[] paused) {
        
        List<SystemNode> systems = gameController.getSystems();
        List<Wire> wires = gameController.getWires();
        List<Packet> packets = gameController.getPackets();
        
        // پاک کردن سیستم‌های قبلی
        systems.clear();
        wires.clear();
        packets.clear();
        
        // ساخت سیستم‌های مرحله اول
        SystemNode start = new SystemNode(false, 150, 300);
        SystemNode end = new SystemNode(false, 650, 300);
        SystemNode ref = new SystemNode(false, 400, 300);
        
        // پورت‌های مربعی - موقعیت نسبی به سیستم
        Port startIn = new Port(Port.PortType.SQUARE, true, start, 0, 0);
        Port startOut = new Port(Port.PortType.SQUARE, false, start, 0, 0);
        start.addInputPort(startIn);
        startIn.updatePositionToParent();
        start.addOutputPort(startOut);
        startOut.updatePositionToParent();
        
        Port refIn = new Port(Port.PortType.SQUARE, true, ref, 0, 0);
        Port refOut = new Port(Port.PortType.SQUARE, false, ref, 0, 0);
        ref.addInputPort(refIn);
        refIn.updatePositionToParent();
        ref.addOutputPort(refOut);
        refOut.updatePositionToParent();
        
        Port endIn = new Port(Port.PortType.SQUARE, true, end, 0, 0);
        Port endOut = new Port(Port.PortType.SQUARE, false, end, 0, 0);
        end.addInputPort(endIn);
        endIn.updatePositionToParent();
        end.addOutputPort(endOut);
        endOut.updatePositionToParent();
        
        systems.add(start);
        systems.add(ref);
        systems.add(end);
        
        // تعیین start و end
        start.setStart(true);
        end.setEnd(true);
        
        // به‌روزرسانی GamePanel
        GamePanel gamePanel = new GamePanel(systems, wires, packets, state);
        gameFrame.setGamePanel(gamePanel);
        
        // حذف دکمه‌های قبلی "شروع سیگنال"
        for (Component comp : hudPanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("شروع سیگنال")) {
                hudPanel.remove(comp);
            }
        }
        hudPanel.revalidate();
        hudPanel.repaint();
        
        // دکمه شروع سیگنال
        JButton btnStart = new JButton("شروع سیگنال");
        hudPanel.add(btnStart);
        
        btnStart.addActionListener(e -> {
            // تست مسیر
            if (!hasPath(start, end, wires, new ArrayList<>())) {
                JOptionPane.showMessageDialog(gameFrame, "سیم‌کشی اشتباه است!");
                return;
            }
            
            // پیام موفقیت‌آمیز بودن سیم‌کشی
            System.out.println("سیم‌کشی موفقیت‌آمیز است! مسیر از START به END برقرار است.");
            
            // ایجاد سیگنال
            packets.clear();
            if (!start.getOutputPorts().isEmpty()) {
                Port startOutPort = start.getOutputPorts().get(0);
                for (Wire w : wires) {
                    if (w.getFrom() == startOutPort) {
                        SquarePacket signal = new SquarePacket();
                        signal.setCurrentWire(w);
                        signal.position = 0.0;
                        w.getPacketsOnWire().add(signal);
                        packets.add(signal);
                        break;
                    }
                }
            }
        });
        
        // منطق حرکت سیگنال در تایمر اصلی
        setupSignalMovement(gameFrame, hudPanel, gameController, state, timeStep, paused);
    }
    
    private static void setupSignalMovement(GameFrame gameFrame, HUDPanel hudPanel, GameController gameController, 
                                          GameState state, double[] timeStep, boolean[] paused) {
        // این منطق در تایمر اصلی Main اجرا می‌شود
    }
    
    private static boolean hasPath(SystemNode from, SystemNode to, List<Wire> wires, List<SystemNode> visited) {
        if (from == to) return true;
        visited.add(from);
        
        for (Port out : from.getOutputPorts()) {
            for (Wire w : wires) {
                if (w.getFrom() == out) {
                    Port destPort = w.getTo();
                    SystemNode next = destPort.getParent();
                    if (!visited.contains(next)) {
                        if (destPort.isInput()) {
                            if (hasPath(next, to, wires, visited)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // بررسی سیم‌کشی صحیح (مربع به مربع، مثلث به مثلث)
    private static boolean checkCorrectWiring(List<Wire> wires) {
        for (Wire wire : wires) {
            Port from = wire.getFrom();
            Port to = wire.getTo();
            
            // بررسی سازگاری نوع پورت
            if (from.getType() != to.getType()) {
                System.out.println("سیم‌کشی اشتباه: " + from.getType() + " به " + to.getType());
                return false;
            }
        }
        return true;
    }
}

// کلاس مدیریت مرحله دوم
class Level2Manager {
    public static void createLevel2(GameFrame gameFrame, HUDPanel hudPanel, GameController gameController, 
                                   GameState state, double[] timeStep, boolean[] paused) {
        
        List<SystemNode> systems = gameController.getSystems();
        List<Wire> wires = gameController.getWires();
        List<Packet> packets = gameController.getPackets();
        
        // پاک کردن سیستم‌های قبلی
        systems.clear();
        wires.clear();
        packets.clear();
        
        // ساخت سیستم‌های مرحله دوم
        SystemNode start = new SystemNode(false, 150, 300);
        SystemNode mid = new SystemNode(false, 400, 300);
        SystemNode end = new SystemNode(false, 650, 300);
        
        // پورت‌های start (مربع) - موقعیت نسبی
        Port startIn = new Port(Port.PortType.SQUARE, true, start, 0, 0);
        Port startOut = new Port(Port.PortType.SQUARE, false, start, 0, 0);
        start.addInputPort(startIn);
        startIn.updatePositionToParent();
        start.addOutputPort(startOut);
        startOut.updatePositionToParent();
        
        // پورت‌های mid (ورودی مربع، خروجی‌ها مربع و مثلث) - موقعیت نسبی
        Port midIn = new Port(Port.PortType.SQUARE, true, mid, 0, 0);
        Port midOut1 = new Port(Port.PortType.SQUARE, false, mid, 0, 0);
        Port midOut2 = new Port(Port.PortType.TRIANGLE, false, mid, 0, 0);
        mid.addInputPort(midIn);
        midIn.updatePositionToParent();
        mid.addOutputPort(midOut1);
        midOut1.updatePositionToParent();
        mid.addOutputPort(midOut2);
        midOut2.updatePositionToParent();
        
        // پورت‌های end (ورودی‌ها مربع و مثلث) - موقعیت نسبی
        Port endIn1 = new Port(Port.PortType.SQUARE, true, end, 0, 0);
        Port endIn2 = new Port(Port.PortType.TRIANGLE, true, end, 0, 0);
        end.addInputPort(endIn1);
        endIn1.updatePositionToParent();
        end.addInputPort(endIn2);
        endIn2.updatePositionToParent();
        
        systems.add(start);
        systems.add(mid);
        systems.add(end);
        
        // تعیین start و end
        start.setStart(true);
        end.setEnd(true);
        
        // به‌روزرسانی GamePanel
        GamePanel gamePanel = new GamePanel(systems, wires, packets, state);
        gameFrame.setGamePanel(gamePanel);
        
        // حذف دکمه‌های قبلی "شروع سیگنال"
        for (Component comp : hudPanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("شروع سیگنال")) {
                hudPanel.remove(comp);
            }
        }
        hudPanel.revalidate();
        hudPanel.repaint();
        
        // دکمه شروع سیگنال
        JButton btnStart = new JButton("شروع سیگنال");
        hudPanel.add(btnStart);
        
        btnStart.addActionListener(e -> {
            // تست مسیر
            if (!hasPath(start, end, wires, new ArrayList<>())) {
                JOptionPane.showMessageDialog(gameFrame, "سیم‌کشی اشتباه است!");
                return;
            }
            
            // پیام موفقیت‌آمیز بودن سیم‌کشی
            System.out.println("سیم‌کشی موفقیت‌آمیز است! مسیر از START به END برقرار است.");
            
            // ایجاد سیگنال
            packets.clear();
            if (!start.getOutputPorts().isEmpty()) {
                Port startOutPort = start.getOutputPorts().get(0);
                for (Wire w : wires) {
                    if (w.getFrom() == startOutPort) {
                        SquarePacket signal = new SquarePacket();
                        signal.setCurrentWire(w);
                        signal.position = 0.0;
                        w.getPacketsOnWire().add(signal);
                        packets.add(signal);
                        break;
                    }
                }
            }
        });
        
        // منطق حرکت سیگنال در تایمر اصلی
        setupSignalMovement(gameFrame, hudPanel, gameController, state, timeStep, paused);
    }
    
    private static void setupSignalMovement(GameFrame gameFrame, HUDPanel hudPanel, GameController gameController, 
                                          GameState state, double[] timeStep, boolean[] paused) {
        // این منطق در تایمر اصلی Main اجرا می‌شود
    }
    
    private static boolean hasPath(SystemNode from, SystemNode to, List<Wire> wires, List<SystemNode> visited) {
        if (from == to) return true;
        visited.add(from);
        
        for (Port out : from.getOutputPorts()) {
            for (Wire w : wires) {
                if (w.getFrom() == out) {
                    Port destPort = w.getTo();
                    SystemNode next = destPort.getParent();
                    if (!visited.contains(next)) {
                        if (destPort.isInput()) {
                            if (hasPath(next, to, wires, visited)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // بررسی سیم‌کشی صحیح (مربع به مربع، مثلث به مثلث)
    private static boolean checkCorrectWiring(List<Wire> wires) {
        for (Wire wire : wires) {
            Port from = wire.getFrom();
            Port to = wire.getTo();
            
            // بررسی سازگاری نوع پورت
            if (from.getType() != to.getType()) {
                System.out.println("سیم‌کشی اشتباه: " + from.getType() + " به " + to.getType());
                return false;
            }
        }
        return true;
    }
} 