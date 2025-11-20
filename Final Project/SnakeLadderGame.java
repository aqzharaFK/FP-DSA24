import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

// 1. Class Dice (Enkapsulasi logika pengacaakan)
class Dice {
    private Random random;

    public Dice() {
        this.random = new Random();
    }

    public int roll() {
        // Mengembalikan angka 1 sampai 6
        return random.nextInt(6) + 1;
    }
}

// 2. Class Player (Menyimpan state pemain)
class Player {
    private String name;
    private int position;

    public Player(String name) {
        this.name = name;
        this.position = 0; // Mulai dari posisi 0 (sebelum masuk papan 1)
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

// 3. Class Board (Mengelola logika papan, ular, dan tangga)
class Board {
    private int size;
    // Map menyimpan: Kunci (Posisi Awal) -> Nilai (Posisi Akhir)
    private Map<Integer, Integer> specialSquares; 

    public Board(int size) {
        this.size = size;
        this.specialSquares = new HashMap<>();
        initializeBoard();
    }

    private void initializeBoard() {
        // Contoh Tangga (Naik)
        specialSquares.put(2, 23);
        specialSquares.put(6, 45);
        specialSquares.put(20, 59);
        specialSquares.put(52, 72);

        // Contoh Ular (Turun)
        specialSquares.put(43, 17);
        specialSquares.put(50, 5);
        specialSquares.put(56, 8);
        specialSquares.put(73, 15);
        specialSquares.put(84, 58);
        specialSquares.put(98, 40);
    }

    public int getSize() {
        return size;
    }

    // Menghitung posisi akhir setelah terkena ular atau tangga
    public int getFinalPosition(int currentPosition) {
        if (currentPosition > size) {
            return currentPosition; // Jika melebihi papan, logika ditangani di Game
        }
        
        if (specialSquares.containsKey(currentPosition)) {
            int newPosition = specialSquares.get(currentPosition);
            if (newPosition > currentPosition) {
                System.out.println("\t*** NAIK TANGGA! Menuju " + newPosition + " ***");
            } else {
                System.out.println("\t*** DIGIGIT ULAR! Turun ke " + newPosition + " ***");
            }
            return newPosition;
        }
        return currentPosition;
    }
}

// 4. Class Game (Controller utama)
public class SnakeLadderGame {
    private Board board;
    private Dice dice;
    private List<Player> players;
    private boolean isGameOver;

    public SnakeLadderGame(int boardSize) {
        this.board = new Board(boardSize);
        this.dice = new Dice();
        this.players = new ArrayList<>();
        this.isGameOver = false;
    }

    public void addPlayer(String name) {
        players.add(new Player(name));
    }

    public void play() {
        if (players.isEmpty()) {
            System.out.println("Tidak ada pemain!");
            return;
        }

        System.out.println("=== PERMAINAN DIMULAI ===");
        Scanner scanner = new Scanner(System.in);

        while (!isGameOver) {
            for (Player player : players) {
                System.out.println("\nGiliran: " + player.getName() + " (Posisi: " + player.getPosition() + ")");
                System.out.print("Tekan Enter untuk melempar dadu...");
                scanner.nextLine();

                int rollValue = dice.roll();
                System.out.println(player.getName() + " melempar angka: " + rollValue);

                int newPosition = player.getPosition() + rollValue;

                // Aturan: Harus pas di angka 100. Jika lebih, tetap di tempat.
                if (newPosition > board.getSize()) {
                    System.out.println("Angka melebihi batas! Tetap di posisi " + player.getPosition());
                } else {
                    // Cek apakah kena ular/tangga
                    newPosition = board.getFinalPosition(newPosition);
                    player.setPosition(newPosition);
                    System.out.println("Posisi sekarang: " + player.getPosition());

                    // Cek kemenangan
                    if (player.getPosition() == board.getSize()) {
                        System.out.println("\n======================================");
                        System.out.println("SELAMAT! " + player.getName() + " MENANG!");
                        System.out.println("======================================");
                        isGameOver = true;
                        break; 
                    }
                }
            }
        }
        scanner.close();
    }

    // Main Method
    public static void main(String[] args) {
        // Inisialisasi Game dengan papan ukuran 100
        SnakeLadderGame game = new SnakeLadderGame(100);

        // Tambah Pemain
        game.addPlayer("Budi");
        game.addPlayer("Siti");

        // Mulai
        game.play();
    }
}