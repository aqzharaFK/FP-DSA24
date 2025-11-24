import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.Queue;
import javax.swing.Timer;

// ===========================
// 1. MODEL CLASSES
// ===========================

class Player {
    private String name;
    private int position;
    private Color color;
    private Stack<Integer> moveHistory;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.position = 1;
        this.moveHistory = new Stack<>();
        this.moveHistory.push(1);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void recordHistory() {
        this.moveHistory.push(this.position);
    }

    public int getPosition() { return position; }
    public String getName() { return name; }
    public Color getColor() { return color; }

    public String getHistoryString() {
        return "Jejak: " + moveHistory.toString();
    }
}

class Board {
    private int size;
    private Map<Integer, Integer> snakes;
    private Map<Integer, Integer> ladders;

    public Board(int size) {
        this.size = size;
        this.snakes = new HashMap<>();
        this.ladders = new HashMap<>();
//        initObstacles();
    }

//    private void initObstacles() {
//        // Tangga (Naik)
//        ladders.put(2, 23);
//        ladders.put(8, 34);
//        ladders.put(20, 77);
//        ladders.put(32, 68);
//        ladders.put(41, 79);
//        ladders.put(74, 88);
//
//        // Ular (Turun)
//        snakes.put(29, 9);
//        snakes.put(38, 15);
//        snakes.put(47, 5);
//        snakes.put(53, 33);
//        snakes.put(86, 54);
//        snakes.put(97, 25);
//    }

    public int getSize() { return size; }

    public int checkObstacle(int position) {
        if (snakes.containsKey(position)) return snakes.get(position);
        if (ladders.containsKey(position)) return ladders.get(position);
        return position;
    }

    public boolean isSnake(int pos) { return snakes.containsKey(pos); }
    public boolean isLadder(int pos) { return ladders.containsKey(pos); }

    // Method baru untuk mengambil tujuan (untuk visualisasi)
    public int getDest(int pos) {
        if (snakes.containsKey(pos)) return snakes.get(pos);
        if (ladders.containsKey(pos)) return ladders.get(pos);
        return -1;
    }
}

// ===========================
// 2. GAME LOGIC & UI
// ===========================

public class SnakeLadderGame extends JFrame {

    private Board board;
    private Queue<Player> playerQueue;
    private JPanel boardPanel;
    private JTextArea gameLog;
    private JLabel statusLabel;
    private JButton rollButton;
    private Map<Integer, JPanel> cells;

    public SnakeLadderGame() {
        board = new Board(100);
        playerQueue = new LinkedList<>();

        playerQueue.add(new Player("P1", Color.BLUE));
        playerQueue.add(new Player("P2", Color.RED));

        setTitle("Snake & Ladder Game");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Board UI
        boardPanel = new JPanel(new GridLayout(10, 10));
        cells = new HashMap<>();
        createBoardUI();
        add(boardPanel, BorderLayout.CENTER);

        // 2. Control UI
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setPreferredSize(new Dimension(250, 700));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        statusLabel = new JLabel("Giliran: " + playerQueue.peek().getName());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // --- UPDATE TOMBOL DISINI ---
        rollButton = new JButton("Roll Dice");
        rollButton.setFont(new Font("Arial", Font.BOLD, 18));
        rollButton.setBackground(new Color(230, 230, 250));
        rollButton.setFocusPainted(false);
        rollButton.addActionListener(e -> startTurnLogic());

        gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(gameLog);

        JPanel topControl = new JPanel(new GridLayout(2, 1, 5, 5));
        topControl.add(statusLabel);
        topControl.add(rollButton);

        controlPanel.add(topControl, BorderLayout.NORTH);
        controlPanel.add(scrollPane, BorderLayout.CENTER);

        add(controlPanel, BorderLayout.EAST);
        refreshBoard();
    }

    private void createBoardUI() {
        boardPanel.removeAll();
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                int cellNumber;
                int realRow = 9 - r;
                if (realRow % 2 == 0) {
                    cellNumber = (realRow * 10) + c + 1;
                } else {
                    cellNumber = (realRow * 10) + (10 - c);
                }

                JPanel cell = new JPanel(new BorderLayout());
                cell.setBorder(new LineBorder(Color.GRAY));

                // --- VISUALISASI ULAR & TANGGA ---
                JLabel infoLabel = new JLabel();
                infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                infoLabel.setFont(new Font("Arial", Font.BOLD, 10));

                if (board.isSnake(cellNumber)) {
                    cell.setBackground(new Color(255, 182, 193)); // Merah Muda (Ular)
                    infoLabel.setText("🐍 ke " + board.getDest(cellNumber));
                    infoLabel.setForeground(new Color(139, 0, 0));
                    cell.add(infoLabel, BorderLayout.SOUTH);
                }
                else if (board.isLadder(cellNumber)) {
                    cell.setBackground(new Color(173, 255, 173)); // Hijau Muda (Tangga)
                    infoLabel.setText("🪜 ke " + board.getDest(cellNumber));
                    infoLabel.setForeground(new Color(0, 100, 0));
                    cell.add(infoLabel, BorderLayout.SOUTH);
                }
                else {
                    cell.setBackground(Color.WHITE);
                }

                JLabel numLabel = new JLabel(String.valueOf(cellNumber));
                numLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                numLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 0));

                JLabel playerToken = new JLabel("", SwingConstants.CENTER);
                playerToken.setFont(new Font("Arial", Font.BOLD, 24));

                cell.add(numLabel, BorderLayout.NORTH);
                cell.add(playerToken, BorderLayout.CENTER);

                cells.put(cellNumber, cell);
                boardPanel.add(cell);
            }
        }
    }

    private void startTurnLogic() {
        rollButton.setEnabled(false);
        Player currentPlayer = playerQueue.peek();

        // --- UPDATE DADU MAKSIMAL 6 ---
        int diceValue = (int) (Math.random() * 6) + 1;

        // Probabilitas Gerak (Green/Red)
        double probabilitas = Math.random();
        int movement;
        String diceType;

        if (probabilitas < 0.20) {
            movement = -diceValue;
            diceType = "🔴 (Mundur)";
        } else {
            movement = diceValue;
            diceType = "🟢 (Maju)";
        }

        log("--------------------------------");
        log(currentPlayer.getName() + " Roll Dice: " + diceValue + " " + diceType);

        int currentPos = currentPlayer.getPosition();
        int targetPos = currentPos + movement;

        if (targetPos < 1) targetPos = 1;
        if (targetPos > 100) targetPos = currentPos;

        animateMovement(currentPlayer, targetPos, () -> {
            checkObstaclesAndFinish(currentPlayer);
        });
    }

    private void animateMovement(Player player, int targetPos, Runnable onComplete) {
        Timer timer = new Timer(150, null);

        timer.addActionListener(e -> {
            int current = player.getPosition();

            if (current < targetPos) {
                player.setPosition(current + 1);
            } else if (current > targetPos) {
                player.setPosition(current - 1);
            } else {
                timer.stop();
                if (onComplete != null) {
                    onComplete.run();
                }
                return;
            }
            refreshBoard();
        });

        timer.start();
    }

    private void checkObstaclesAndFinish(Player player) {
        int currentPos = player.getPosition();
        int finalPos = board.checkObstacle(currentPos);

        if (finalPos != currentPos) {
            String type = (finalPos > currentPos) ? "NAIK TANGGA!" : "DIGIGIT ULAR!";
            log(type + " -> " + finalPos);

            animateMovement(player, finalPos, () -> finalizeTurn(player));
        } else {
            finalizeTurn(player);
        }
    }

    private void finalizeTurn(Player player) {
        player.recordHistory();

        if (player.getPosition() == 100) {
            JOptionPane.showMessageDialog(this,
                    "SELAMAT! " + player.getName() + " MENANG!\n" + player.getHistoryString());
            System.exit(0);
        }

        playerQueue.poll();
        playerQueue.add(player);

        statusLabel.setText("Giliran: " + playerQueue.peek().getName());
        rollButton.setEnabled(true);
    }

    private void refreshBoard() {
        for (JPanel cell : cells.values()) {
            if (cell.getComponentCount() > 1) {
                // Component index 1 adalah token pemain (index 0=nomor, index 2=info ular/tangga jika ada)
                // Kita cari component yang instance of JLabel dan berada di CENTER
                Component[] comps = cell.getComponents();
                for(Component c : comps) {
                    if (c instanceof JLabel) {
                        JLabel lbl = (JLabel) c;
                        // Cek font size atau lokasi untuk membedakan token dengan label nomor/info
                        // Token font size 24, Alignment Center
                        if (lbl.getHorizontalAlignment() == SwingConstants.CENTER && lbl.getFont().getSize() == 24) {
                            lbl.setText("");
                        }
                    }
                }
            }
        }

        for (Player p : playerQueue) {
            int pos = p.getPosition();
            if (cells.containsKey(pos)) {
                JPanel cell = cells.get(pos);

                // Cari lagi label token untuk diisi
                Component[] comps = cell.getComponents();
                for(Component c : comps) {
                    if (c instanceof JLabel) {
                        JLabel lbl = (JLabel) c;
                        if (lbl.getHorizontalAlignment() == SwingConstants.CENTER && lbl.getFont().getSize() == 24) {
                            String currentText = lbl.getText();
                            String tokenSymbol = p.getName().equals("P1") ? "●" : "■";
                            lbl.setForeground(p.getColor());
                            lbl.setText(currentText + tokenSymbol);
                        }
                    }
                }
            }
        }
    }

    private void log(String msg) {
        gameLog.append(msg + "\n");
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SnakeLadderGame().setVisible(true);
        });
    }
}