# Network Simulation Puzzle Game

This project is a Java Swing-based puzzle game where players design and simulate a simple computer network. The main goal is to successfully route "Packets" from a `START` node to an `END` node while managing challenges like "Packet Loss", "Noise", and "Packet Collisions".

## 🎮 Gameplay

Players use a limited amount of "Wire" to connect "System Nodes" to each other. Each node has input and output "Ports" of different types (like `SQUARE` and `TRIANGLE`).

After completing the wiring, the player presses the "Start Signal" button. Packets then begin moving from the `START` node and travel along the wires.

### Objectives
1.  **Deliver Packets:** All packets must successfully reach the `END` node.
2.  **Earn Coins:** Delivering packets to intermediate nodes or the final node awards the player with "Coins".
3.  **Prevent Packet Loss:** The `PacketLoss` rate must not exceed 50%.

---

## ✨ Key Features

### Core Mechanics
* **Wiring:** Players must connect ports by dragging wires between them.
* **Resource Management:** The total available wire length is limited.
* **Packet Types:**
    * **SquarePacket:** The basic packet. Moves at a constant speed.
    * **TrianglePacket:** An advanced packet. Its speed changes based on the port compatibility of the wire it's on (it moves faster on incompatible wires).
* **Packet Splitting (in Level 2):** An intermediate node can split an incoming `SquarePacket` into two outgoing packets (`Square` and `Triangle`).

### Challenges
* **Packet Loss:** Packets can be lost for the following reasons:
    * **Excessive Noise:** If a packet's noise level exceeds a certain threshold.
    * **Dead End:** Reaching a port that is not connected to anything.
    * **Collision:** Colliding with another packet.
* **Collision:**
    * If two packets collide on the same wire, both are destroyed, and `PacketLoss` increases.
    * A collision creates an **Impact Wave** that can add noise to nearby packets or even destroy them.
* **Game Over:** If `PacketLoss` exceeds 50%, the game ends, and the level resets.

### Controls & UI
* **Time Control:** The player can fast-forward or rewind time using the arrow keys (left and right) or buttons on the HUD.
* **HUD (Heads-Up Display):** Shows a live display of coins, packet loss percentage, remaining wire length, and elapsed time.
* **Shop:** Players can use earned coins to purchase items or upgrades (like more wire length).
* **Main Menu:** Allows for level selection, access to settings, and exiting the game.

---

## 🛠️ Project Structure

The project is designed using an MVC (Model-View-Controller) architecture:

* `game.model.*`: Contains the core logic classes like `GameState`, `SystemNode`, `Port`, `Wire`, and the `Packet` types.
* `game.view.*`: Includes all Swing UI components like `GameFrame`, `GamePanel`, `HUDPanel`, `MainMenu`, and dialogs.
* `game.controller.*`: Contains management and controller classes like `GameController` (for handling mouse events and wiring) and `ShopController`.
* `game.Main`: The main entry point of the application, which sets up the main game loop using a `javax.swing.Timer`.
* `Level1Manager` / `Level2Manager`: Helper classes for setting up and loading the objects and logic specific to each level.

---

## 🚀 How to Run

1.  Ensure you have the **Java Development Kit (JDK)** (preferably version 11 or higher) installed on your system.
2.  Open the project in your preferred IDE (like IntelliJ IDEA, Eclipse, or VS Code).
3.  Find and run the `game.Main` class.
4.  The game will start from the `MainMenu`.
