package game.view;

import game.model.*;
import game.controller.WireController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener {
    private List<SystemNode> systems;
    private List<Wire> wires;
    private List<Packet> packets;
    private Port selectedPort = null;
    private int mouseX, mouseY;
    private boolean drawingWire = false;
    private WireController wireController = new WireController();
    private SystemNode draggedSystem = null;
    private int dragOffsetX, dragOffsetY;
    private GameState state;

    public GamePanel(List<SystemNode> systems, List<Wire> wires, List<Packet> packets, GameState state) {
        this.systems = systems;
        this.wires = wires;
        this.packets = packets;
        this.state = state;
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // رسم سیم‌ها
        g.setColor(Color.LIGHT_GRAY);
        for (Wire wire : wires) {
            int x1 = wire.getFrom().getX();
            int y1 = wire.getFrom().getY();
            int x2 = wire.getTo().getX();
            int y2 = wire.getTo().getY();
            g.drawLine(x1, y1, x2, y2);
        }

        // رسم سیستم‌ها
        for (SystemNode sys : systems) {
            int x = sys.getX();
            int y = sys.getY();
            g.setColor(sys.isReference() ? Color.ORANGE : Color.BLUE);
            g.fillRect(x, y, 80, 60);
            g.setColor(Color.WHITE);
            g.drawString(sys.isReference() ? "Ref" : "Sys", x + 30, y + 30);
            // تگ start/end
            if (sys.isStart()) {
                g.setColor(Color.GREEN);
                g.drawString("START", x + 5, y + 15);
            }
            if (sys.isEnd()) {
                g.setColor(Color.RED);
                g.drawString("END", x + 45, y + 15);
            }
            // اندیکاتور
            if (sys.isIndicatorOn()) {
                g.setColor(Color.GREEN);
                g.fillOval(x + 65, y - 10, 15, 15);
            }
            // پورت‌ها
            for (Port p : sys.getInputPorts()) {
                g.setColor(p.getType() == Port.PortType.SQUARE ? Color.RED : Color.MAGENTA);
                if (p.getType() == Port.PortType.SQUARE) {
                    g.fillRect(p.getX() - 5, p.getY() - 5, 10, 10);
                } else {
                    g.fillPolygon(new int[]{p.getX(), p.getX() - 5, p.getX() + 5}, 
                                 new int[]{p.getY() - 5, p.getY() + 5, p.getY() + 5}, 3);
                }
            }
            for (Port p : sys.getOutputPorts()) {
                g.setColor(p.getType() == Port.PortType.SQUARE ? Color.RED : Color.MAGENTA);
                if (p.getType() == Port.PortType.SQUARE) {
                    g.fillRect(p.getX() - 5, p.getY() - 5, 10, 10);
                } else {
                    g.fillPolygon(new int[]{p.getX(), p.getX() - 5, p.getX() + 5}, 
                                 new int[]{p.getY() - 5, p.getY() + 5, p.getY() + 5}, 3);
                }
            }
        }

        // رسم پکت‌ها
        for (Packet packet : packets) {
            if (packet.isLost()) continue;
            int px = 0, py = 0;
            if (packet.getCurrentWire() != null) {
                double pos = packet.position;
                int x1 = packet.getCurrentWire().getFrom().getX();
                int y1 = packet.getCurrentWire().getFrom().getY();
                int x2 = packet.getCurrentWire().getTo().getX();
                int y2 = packet.getCurrentWire().getTo().getY();
                px = (int) (x1 + (x2 - x1) * pos);
                py = (int) (y1 + (y2 - y1) * pos);
            } else if (packet.getCurrentPort() != null) {
                px = packet.getCurrentPort().getX();
                py = packet.getCurrentPort().getY();
            }
            g.setColor(packet.getType() == Packet.PacketType.SQUARE ? Color.YELLOW : Color.CYAN);
            if (packet.getType() == Packet.PacketType.SQUARE)
                g.fillRect(px - 8, py - 8, 16, 16);
            else
                g.fillPolygon(new int[]{px, px - 10, px + 10}, new int[]{py - 10, py + 10, py + 10}, 3);
        }

        // رسم سیم موقت
        if (drawingWire && selectedPort != null) {
            g.setColor(Color.GREEN);
            g.drawLine(selectedPort.getX(), selectedPort.getY(), mouseX, mouseY);
        }
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        for (Packet packet : packets) {
            int px = 0, py = 0;
            if (packet.getCurrentWire() != null) {
                double pos = packet.position;
                int x1 = packet.getCurrentWire().getFrom().getX();
                int y1 = packet.getCurrentWire().getFrom().getY();
                int x2 = packet.getCurrentWire().getTo().getX();
                int y2 = packet.getCurrentWire().getTo().getY();
                px = (int) (x1 + (x2 - x1) * pos);
                py = (int) (y1 + (y2 - y1) * pos);
            } else if (packet.getCurrentPort() != null) {
                px = packet.getCurrentPort().getX();
                py = packet.getCurrentPort().getY();
            }
            if (Math.abs(px - event.getX()) <= 10 && Math.abs(py - event.getY()) <= 10) {
                return packet.getType() == Packet.PacketType.SQUARE ? "مربع (1 سکه)" : "مثلث (2 سکه)";
            }
        }
        return null;
    }

    // MouseListener & MouseMotionListener
    @Override
    public void mousePressed(MouseEvent e) {
        // ابتدا بررسی جابه‌جایی سیستم
        for (SystemNode sys : systems) {
            int x = sys.getX(), y = sys.getY();
            if (e.getX() >= x && e.getX() <= x + 80 && e.getY() >= y && e.getY() <= y + 60) {
                draggedSystem = sys;
                dragOffsetX = e.getX() - x;
                dragOffsetY = e.getY() - y;
                return;
            }
        }
        // اگر روی سیستم نبود، بررسی انتخاب پورت برای سیم‌کشی
        for (SystemNode sys : systems) {
            for (Port p : sys.getInputPorts()) {
                if (isPortClicked(p, e.getX(), e.getY())) {
                    selectedPort = p;
                    drawingWire = true;
                    return;
                }
            }
            for (Port p : sys.getOutputPorts()) {
                if (isPortClicked(p, e.getX(), e.getY())) {
                    selectedPort = p;
                    drawingWire = true;
                    return;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (draggedSystem != null) {
            draggedSystem = null;
            repaint();
            return;
        }
        if (drawingWire && selectedPort != null) {
            for (SystemNode sys : systems) {
                for (Port p : sys.getInputPorts()) {
                    if (p != selectedPort && isPortClicked(p, e.getX(), e.getY())) {
                        double len = wireController.calcLength(selectedPort, p);
                        System.out.println("سیم جدید (ورودی): طول=" + len + ", موجود=" + state.getWireLength());
                        if (state.getWireLength() >= len) {
                            wires.add(new Wire(selectedPort, p, len));
                            state.setWireLength(state.getWireLength() - len);
                            System.out.println("سیم اضافه شد. طول باقی‌مانده: " + state.getWireLength());
                        } else {
                            System.out.println("طول سیم کافی نیست!");
                            JOptionPane.showMessageDialog(this, "طول سیم کافی نیست!");
                        }
                        break;
                    }
                }
                for (Port p : sys.getOutputPorts()) {
                    if (p != selectedPort && isPortClicked(p, e.getX(), e.getY())) {
                        double len = wireController.calcLength(selectedPort, p);
                        System.out.println("سیم جدید (خروجی): طول=" + len + ", موجود=" + state.getWireLength());
                        if (state.getWireLength() >= len) {
                            wires.add(new Wire(selectedPort, p, len));
                            state.setWireLength(state.getWireLength() - len);
                            System.out.println("سیم اضافه شد. طول باقی‌مانده: " + state.getWireLength());
                        } else {
                            System.out.println("طول سیم کافی نیست!");
                            JOptionPane.showMessageDialog(this, "طول سیم کافی نیست!");
                        }
                        break;
                    }
                }
            }
        }
        drawingWire = false;
        selectedPort = null;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if (draggedSystem != null) {
            draggedSystem.setX(e.getX() - dragOffsetX);
            draggedSystem.setY(e.getY() - dragOffsetY);
            repaint();
            return;
        }
        if (drawingWire) repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    private boolean isPortClicked(Port p, int x, int y) {
        int px = p.getX(), py = p.getY();
        return Math.abs(px - x) <= 10 && Math.abs(py - y) <= 10;
    }

    // سایر متدهای MouseListener
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    
    public void updateSystems(List<SystemNode> newSystems, List<Wire> newWires, List<Packet> newPackets) {
        this.systems = newSystems;
        this.wires = newWires;
        this.packets = newPackets;
        repaint();
    }
    
    public void updateSystems() {
        // به‌روزرسانی UI بدون تغییر داده‌ها
        repaint();
    }
} 