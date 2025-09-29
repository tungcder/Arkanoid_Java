import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ArkanoidGame extends JPanel implements ActionListener, MouseMotionListener {
    private Timer timer;
    private int delay = 8;

    // Paddle
    private int playerX = 310; // Vị trí X của paddle
    private final int paddleWidth = 100;
    private final int paddleHeight = 12;

    // Ball
    private int ballPosX = 120;
    private int ballPosY = 350;
    private int ballDirX = -2;
    private int ballDirY = -3;
    private final int ballSize = 20;

    // Bricks
    private boolean[][] bricks;
    private int brickRows = 5;
    private int brickCols = 7;
    private int brickWidth = 80;
    private int brickHeight = 25;
    private int totalBricks;

    private boolean play = false;
    private int score = 0;

    public ArkanoidGame() {
        bricks = new boolean[brickRows][brickCols];
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                bricks[i][j] = true;
            }
        }
        totalBricks = brickRows * brickCols;

        addMouseMotionListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        // Background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // Drawing bricks (nhiều màu đẹp hơn)
        Color[] rowColors = {Color.red, Color.orange, Color.yellow, Color.green, Color.cyan};
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                if (bricks[i][j]) {
                    g.setColor(rowColors[i % rowColors.length]);
                    g.fillRoundRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight, 10, 10);

                    g.setColor(Color.black);
                    g.drawRoundRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight, 10, 10);
                }
            }
        }

        // Scores
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("Score: " + score, 560, 30);

        // Paddle
        g.setColor(Color.blue);
        g.fillRoundRect(playerX, 550, paddleWidth, paddleHeight, 10, 10);

        // Ball
        g.setColor(Color.yellow);
        g.fillOval(ballPosX, ballPosY, ballSize, ballSize);

        if (totalBricks <= 0) {
            play = false;
            ballDirX = ballDirY = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won!", 260, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Click to Restart", 260, 350);
        }

        if (ballPosY > 570) {
            play = false;
            ballDirX = ballDirY = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over, Score: " + score, 180, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Click to Restart", 250, 350);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            // Ball - paddle collision
            if (new Rectangle(ballPosX, ballPosY, ballSize, ballSize)
                    .intersects(new Rectangle(playerX, 550, paddleWidth, paddleHeight))) {
                ballDirY = -ballDirY;
            }

            // Ball - bricks collision
            A:
            for (int i = 0; i < brickRows; i++) {
                for (int j = 0; j < brickCols; j++) {
                    if (bricks[i][j]) {
                        int brickX = j * brickWidth + 80;
                        int brickY = i * brickHeight + 50;
                        int brickW = brickWidth;
                        int brickH = brickHeight;

                        Rectangle brickRect = new Rectangle(brickX, brickY, brickW, brickH);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, ballSize, ballSize);

                        if (ballRect.intersects(brickRect)) {
                            bricks[i][j] = false;
                            totalBricks--;
                            score += 10;

                            if (ballPosX + ballSize - 1 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballDirX = -ballDirX;
                            } else {
                                ballDirY = -ballDirY;
                            }

                            break A;
                        }
                    }
                }
            }

            ballPosX += ballDirX;
            ballPosY += ballDirY;

            // Left border
            if (ballPosX < 0) {
                ballDirX = -ballDirX;
            }
            // Top border
            if (ballPosY < 0) {
                ballDirY = -ballDirY;
            }
            // Right border
            if (ballPosX > 670) {
                ballDirX = -ballDirX;
            }
        }

        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Paddle bám theo chuột (giữ trong biên)
        playerX = e.getX() - paddleWidth / 2;
        if (playerX < 0) playerX = 0;
        if (playerX > 600) playerX = 600;

        play = true; // bắt đầu khi di chuyển chuột
    }

    private void resetGame() {
        play = true;
        ballPosX = 120;
        ballPosY = 350;
        ballDirX = -2;
        ballDirY = -3;
        playerX = 310;
        score = 0;
        bricks = new boolean[brickRows][brickCols];
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                bricks[i][j] = true;
            }
        }
        totalBricks = brickRows * brickCols;
        repaint();
    }

    public static void main(String[] args) {
        JFrame obj = new JFrame();
        ArkanoidGame gamePlay = new ArkanoidGame();

        obj.setBounds(10, 10, 700, 600);
        obj.setTitle("Arkanoid Game");
        obj.setResizable(false);
        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.add(gamePlay);

        // Restart khi click chuột
        obj.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gamePlay.play) {
                    gamePlay.resetGame();
                }
            }
        });
    }
}
