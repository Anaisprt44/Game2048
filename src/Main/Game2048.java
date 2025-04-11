package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Game2048 extends JFrame {
    private static final int SIZE = 4;
    private static final int TARGET = 2048;
    private GameBoard board;
    private int score = 0;
    private JLabel scoreLabel;
    private Random random = new Random();

    public Game2048() {
        setTitle("2048 Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Top panel for score and controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(187, 173, 160));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Add score display
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setForeground(Color.WHITE);
        
        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 14));
        newGameButton.addActionListener(e -> resetGame());
        
        topPanel.add(scoreLabel, BorderLayout.WEST);
        topPanel.add(newGameButton, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Game board
        board = new GameBoard();
        board.setPreferredSize(new Dimension(450, 450));
        board.setBackground(new Color(187, 173, 160));
        board.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(board, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Key listener for arrow keys
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean moved = false;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        moved = moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moved = moveRight();
                        break;
                    case KeyEvent.VK_UP:
                        moved = moveUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        moved = moveDown();
                        break;
                }
                
                if (moved) {
                    addRandomTile();
                    updateScore();
                    board.repaint();
                    
                    if (isGameOver()) {
                        JOptionPane.showMessageDialog(Game2048.this, 
                            "Game Over! Your score: " + score, 
                            "Game Over", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else if (hasWon()) {
                        JOptionPane.showMessageDialog(Game2048.this, 
                            "You Win! You reached " + TARGET + "! Your score: " + score, 
                            "Congratulations", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        
        setFocusable(true);
        
        // Initialize game
        resetGame();
        
        setVisible(true);
    }
    
    private void resetGame() {
        score = 0;
        updateScore();
        
        // Clear board
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board.grid[i][j] = 0;
            }
        }
        
        // Add initial tiles
        addRandomTile();
        addRandomTile();
        
        board.repaint();
    }
    
    private void addRandomTile() {
        // Find all empty cells
        List<Point> emptyCells = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board.grid[i][j] == 0) {
                    emptyCells.add(new Point(i, j));
                }
            }
        }
        
        if (emptyCells.isEmpty()) {
            return;
        }
        
        // Randomly select an empty cell
        Point selectedCell = emptyCells.get(random.nextInt(emptyCells.size()));
        
        // Add a tile with value 2 (90% chance) or 4 (10% chance)
        board.grid[selectedCell.x][selectedCell.y] = random.nextFloat() < 0.9 ? 2 : 4;
    }
    
    private boolean moveLeft() {
        boolean moved = false;
        
        for (int i = 0; i < SIZE; i++) {
            int[] row = new int[SIZE];
            for (int j = 0; j < SIZE; j++) {
                row[j] = board.grid[i][j];
            }
            
            int[] newRow = slideAndMerge(row);
            
            for (int j = 0; j < SIZE; j++) {
                if (board.grid[i][j] != newRow[j]) {
                    moved = true;
                    board.grid[i][j] = newRow[j];
                }
            }
        }
        
        return moved;
    }
    
    private boolean moveRight() {
        boolean moved = false;
        
        for (int i = 0; i < SIZE; i++) {
            int[] row = new int[SIZE];
            for (int j = 0; j < SIZE; j++) {
                row[j] = board.grid[i][SIZE - 1 - j];
            }
            
            int[] newRow = slideAndMerge(row);
            
            for (int j = 0; j < SIZE; j++) {
                if (board.grid[i][SIZE - 1 - j] != newRow[j]) {
                    moved = true;
                    board.grid[i][SIZE - 1 - j] = newRow[j];
                }
            }
        }
        
        return moved;
    }
    
    private boolean moveUp() {
        boolean moved = false;
        
        for (int j = 0; j < SIZE; j++) {
            int[] column = new int[SIZE];
            for (int i = 0; i < SIZE; i++) {
                column[i] = board.grid[i][j];
            }
            
            int[] newColumn = slideAndMerge(column);
            
            for (int i = 0; i < SIZE; i++) {
                if (board.grid[i][j] != newColumn[i]) {
                    moved = true;
                    board.grid[i][j] = newColumn[i];
                }
            }
        }
        
        return moved;
    }
    
    private boolean moveDown() {
        boolean moved = false;
        
        for (int j = 0; j < SIZE; j++) {
            int[] column = new int[SIZE];
            for (int i = 0; i < SIZE; i++) {
                column[i] = board.grid[SIZE - 1 - i][j];
            }
            
            int[] newColumn = slideAndMerge(column);
            
            for (int i = 0; i < SIZE; i++) {
                if (board.grid[SIZE - 1 - i][j] != newColumn[i]) {
                    moved = true;
                    board.grid[SIZE - 1 - i][j] = newColumn[i];
                }
            }
        }
        
        return moved;
    }
    
    private int[] slideAndMerge(int[] line) {
        int[] result = new int[SIZE];
        int pos = 0;
        
        // Slide non-zero values to the left
        for (int i = 0; i < SIZE; i++) {
            if (line[i] != 0) {
                result[pos] = line[i];
                pos++;
            }
        }
        
        // Merge tiles with the same value
        for (int i = 0; i < SIZE - 1; i++) {
            if (result[i] != 0 && result[i] == result[i + 1]) {
                result[i] *= 2;
                score += result[i];
                result[i + 1] = 0;
            }
        }
        
        // Slide again to fill gaps after merging
        int[] finalResult = new int[SIZE];
        pos = 0;
        for (int i = 0; i < SIZE; i++) {
            if (result[i] != 0) {
                finalResult[pos] = result[i];
                pos++;
            }
        }
        
        return finalResult;
    }
    
    private void updateScore() {
        scoreLabel.setText("Score: " + score);
    }
    
    private boolean isGameOver() {
        // Check if the board is full
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board.grid[i][j] == 0) {
                    return false;
                }
            }
        }
        
        // Check if any adjacent tiles have the same value
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE - 1; j++) {
                if (board.grid[i][j] == board.grid[i][j + 1]) {
                    return false;
                }
            }
        }
        
        for (int j = 0; j < SIZE; j++) {
            for (int i = 0; i < SIZE - 1; i++) {
                if (board.grid[i][j] == board.grid[i + 1][j]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private boolean hasWon() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board.grid[i][j] == TARGET) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Inner class for the game board
    private class GameBoard extends JPanel {
        private int[][] grid = new int[SIZE][SIZE];
        private final int TILE_SIZE = 100;
        private final int TILE_MARGIN = 10;
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw tiles
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    int x = j * (TILE_SIZE + TILE_MARGIN) + TILE_MARGIN;
                    int y = i * (TILE_SIZE + TILE_MARGIN) + TILE_MARGIN;
                    
                    // Draw tile background
                    g2d.setColor(getTileColor(grid[i][j]));
                    g2d.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 10, 10);
                    
                    // Draw tile value
                    if (grid[i][j] != 0) {
                        g2d.setColor(grid[i][j] <= 4 ? new Color(119, 110, 101) : Color.WHITE);
                        g2d.setFont(new Font("Arial", Font.BOLD, getFontSize(grid[i][j])));
                        
                        String text = String.valueOf(grid[i][j]);
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(text);
                        int textHeight = fm.getHeight();
                        
                        g2d.drawString(text, 
                                      x + (TILE_SIZE - textWidth) / 2, 
                                      y + (TILE_SIZE - textHeight) / 2 + fm.getAscent());
                    }
                }
            }
        }
        
        private Color getTileColor(int value) {
            switch (value) {
                case 0: return new Color(205, 192, 180); // Empty tile
                case 2: return new Color(238, 228, 218);
                case 4: return new Color(237, 224, 200);
                case 8: return new Color(242, 177, 121);
                case 16: return new Color(245, 149, 99);
                case 32: return new Color(246, 124, 95);
                case 64: return new Color(246, 94, 59);
                case 128: return new Color(237, 207, 114);
                case 256: return new Color(237, 204, 97);
                case 512: return new Color(237, 200, 80);
                case 1024: return new Color(237, 197, 63);
                case 2048: return new Color(237, 194, 46);
                default: return new Color(60, 58, 50); // For tiles > 2048
            }
        }
        
        private int getFontSize(int value) {
            String text = String.valueOf(value);
            
            if (text.length() <= 2) {
                return 36;
            } else if (text.length() == 3) {
                return 32;
            } else {
                return 24;
            }
        }
    }
}