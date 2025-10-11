import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Locale;

public class ArkanoidPlus {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Arkanoid Plus");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(800, 600);
            f.setResizable(false);
            f.setLocationRelativeTo(null);

            CardLayout cards = new CardLayout();
            JPanel root = new JPanel(cards);

            final GamePanel[] gameRef = new GamePanel[1];

            GamePanel game = new GamePanel(() -> {
                cards.show(root, "start");
                gameRef[0].resetAll();
            });
            gameRef[0] = game;

            StartPanel start = new StartPanel(
                    () -> { cards.show(root, "game"); gameRef[0].startNewGame(); gameRef[0].requestFocusInWindow(); },
                    () -> System.exit(0)
            );

            root.add(start, "start");
            root.add(game, "game");
            f.setContentPane(root);
            f.setVisible(true);
        });
    }

    // ---------- Start Screen ----------
    static class StartPanel extends JPanel {
        StartPanel(Runnable onPlay, Runnable onExit) {
            setBackground(new Color(12,12,16));
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx=0; gbc.fill=GridBagConstraints.HORIZONTAL; gbc.insets=new Insets(10,0,10,0);

            JLabel title = new JLabel("ARKANOID PLUS", SwingConstants.CENTER);
            title.setForeground(new Color(200,220,255));
            title.setFont(new Font("Serif", Font.BOLD, 36));
            title.setBorder(BorderFactory.createEmptyBorder(0,0,30,0));

            JButton play = new JButton("Play");
            JButton exit = new JButton("Exit");
            play.setFont(new Font("SansSerif", Font.BOLD, 18));
            exit.setFont(new Font("SansSerif", Font.BOLD, 18));
            play.addActionListener(e -> onPlay.run());
            exit.addActionListener(e -> onExit.run());

            add(title, gbc); add(play, gbc); add(exit, gbc);
        }
    }

    // ---------- Game Panel ----------
    static class GamePanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener, KeyListener {
        private static final int WIDTH = 800, HEIGHT = 600;
        private static final int TICK_MS = 8;
        private static final int BUFF_SECONDS = 7;
        private static final int BUFF_TICKS = (BUFF_SECONDS * 1000) / TICK_MS;

        private final javax.swing.Timer timer = new javax.swing.Timer(TICK_MS, this);
        private final Random rng = new Random();

        private final Runnable onQuitToStart;

        private boolean playing = false;
        private boolean paused = false;
        private boolean gameOver = false;
        private boolean levelComplete = false; // ⬅️ trạng thái menu NEXT LEVEL
        private boolean ballHeld = false;      // bóng dính trên paddle

        private int score = 0, coins = 0, lives = 3;
        private int level = 1;

        // Paddle
        private int playerX = WIDTH / 2 - 75;
        private int paddleW = 150;
        private final int basePaddleW = 150;
        private final int paddleH = 16;

        // Ball
        private static class Ball {
            double x, y, dx, dy;
            int r;
            boolean explosive = false;   // nổ 1 lần
            boolean unstoppable = false; // pierce

            Ball(int x, int y, double dx, double dy, int r) {
                this.x = x; this.y = y; this.dx = dx; this.dy = dy; this.r = r;
            }
            Rectangle bounds() { return new Rectangle((int)(x - r), (int)(y - r), 2*r, 2*r); }
        }
        private final List<Ball> balls = new ArrayList<>();
        private final int baseBallR = 10;
        private int baseSpeed = 3;

        // Bricks
        private boolean[][] bricks;
        private int rows, cols, brickW, brickH, offsetX = 50, offsetY = 60, totalBricks;

        // PowerUps
        private enum PType { BigBall, MultiBall, Explosive, BigPaddle, Slow, ExtraLife, Unstoppable, Coin }
        private static class PowerUp {
            final PType type; int x, y; int w = 24, h = 12, dy = 2;
            PowerUp(PType t, int x, int y){ this.type=t; this.x=x; this.y=y; }
            Rectangle rect(){ return new Rectangle(x, y, w, h); }
        }
        private final List<PowerUp> drops = new ArrayList<>();

        // Buff timers + pierce hit-count
        private int bigBallTicks = 0, bigPaddleTicks = 0, slowTicks = 0;
        private int unstoppableTicks = 0;     // pierce timeout (7s)
        private int unstoppableHitsLeft = 0;  // pierce còn bao nhiêu lần chạm paddle

        // 15 pattern + random theo block 15 level
        private final List<boolean[][]> patterns = new ArrayList<>();
        private List<Integer> randomList = new ArrayList<>();
        private int patternIndex = 0;

        // Colors
        private final Color brickColor = new Color(100, 180, 255);

        // Overlays
        private final JPanel pauseOverlay = new JPanel(new GridBagLayout());
        private final JPanel gameOverOverlay = new JPanel(new GridBagLayout());
        private final JLabel gameOverLabel = new JLabel("", SwingConstants.CENTER);

        private final JPanel levelOverlay = new JPanel(new GridBagLayout());   // ⬅️ overlay NEXT LEVEL
        private final JLabel levelLabel = new JLabel("", SwingConstants.CENTER);

        GamePanel(Runnable onQuitToStart) {
            this.onQuitToStart = onQuitToStart;

            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(Color.black);
            setFocusable(true);
            setLayout(null);
            addMouseMotionListener(this);
            addMouseListener(this);
            addKeyListener(this);

            buildPauseOverlay();
            buildGameOverOverlay();
            buildLevelOverlay();

            generatePatterns();
            randomizeBlock();
            initLevel(level);

            timer.start();
        }

        private void buildPauseOverlay() {
            pauseOverlay.setOpaque(true);
            pauseOverlay.setBackground(new Color(20,20,20,220));
            pauseOverlay.setBounds((WIDTH-300)/2, (HEIGHT-200)/2, 300, 200);

            JLabel pausedLbl = new JLabel("PAUSED", SwingConstants.CENTER);
            pausedLbl.setForeground(new Color(255,210,120));
            pausedLbl.setFont(new Font("Serif", Font.BOLD, 26));

            JButton resumeBtn = new JButton("Resume");
            JButton quitBtn = new JButton("Quit");
            resumeBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
            quitBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

            resumeBtn.addActionListener(e -> togglePause(false));
            // Quit trong trận → hiện ngay Game Over
            quitBtn.addActionListener(e -> showGameOver());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx=0; gbc.fill=GridBagConstraints.HORIZONTAL; gbc.insets=new Insets(8, 20, 8, 20);
            gbc.gridy=0; pauseOverlay.add(pausedLbl, gbc);
            gbc.gridy=1; pauseOverlay.add(resumeBtn, gbc);
            gbc.gridy=2; pauseOverlay.add(quitBtn, gbc);

            pauseOverlay.setVisible(false);
            add(pauseOverlay);
        }

        private void buildGameOverOverlay() {
            gameOverOverlay.setOpaque(true);
            gameOverOverlay.setBackground(new Color(20,20,20,230));
            gameOverOverlay.setBounds((WIDTH-360)/2, (HEIGHT-220)/2, 360, 220);

            gameOverLabel.setForeground(new Color(255,230,160));
            gameOverLabel.setFont(new Font("Serif", Font.BOLD, 22));

            JButton restartBtn = new JButton("Restart");
            JButton quitBtn = new JButton("Quit");
            restartBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
            quitBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

            restartBtn.addActionListener(e -> {
                gameOver = false;
                gameOverOverlay.setVisible(false);
                resetAll();
                startNewGame(); // bóng dính
                requestFocusInWindow();
            });
            quitBtn.addActionListener(e -> onQuitToStart.run());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx=0; gbc.fill=GridBagConstraints.HORIZONTAL; gbc.insets=new Insets(8, 24, 8, 24);
            gbc.gridy=0; gameOverOverlay.add(gameOverLabel, gbc);
            gbc.gridy=1; gameOverOverlay.add(restartBtn, gbc);
            gbc.gridy=2; gameOverOverlay.add(quitBtn, gbc);

            gameOverOverlay.setVisible(false);
            add(gameOverOverlay);
        }

        // ⬇️ Level complete (NEXT LEVEL / Quit)
        private void buildLevelOverlay() {
            levelOverlay.setOpaque(true);
            levelOverlay.setBackground(new Color(20,20,20,230));
            levelOverlay.setBounds((WIDTH-360)/2, (HEIGHT-220)/2, 360, 220);

            levelLabel.setForeground(new Color(180, 255, 180));
            levelLabel.setFont(new Font("Serif", Font.BOLD, 22));

            JButton nextBtn = new JButton("NEXT LEVEL");
            JButton quitBtn = new JButton("Quit");
            nextBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
            quitBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

            nextBtn.addActionListener(e -> {
                levelComplete = false;
                levelOverlay.setVisible(false);
                level++;                         // sang màn mới
                resetBuffs();
                initLevel(level);
                spawnHeldBall();                 // bắt đầu màn mới với bóng dính
                requestFocusInWindow();
            });
            quitBtn.addActionListener(e -> showGameOver());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx=0; gbc.fill=GridBagConstraints.HORIZONTAL; gbc.insets=new Insets(8, 24, 8, 24);
            gbc.gridy=0; levelOverlay.add(levelLabel, gbc);
            gbc.gridy=1; levelOverlay.add(nextBtn, gbc);
            gbc.gridy=2; levelOverlay.add(quitBtn, gbc);

            levelOverlay.setVisible(false);
            add(levelOverlay);
        }

        // --- API từ Start ---
        void startNewGame() {
            resetAll();
            spawnHeldBall(); // bóng dính lúc bắt đầu
            playing = true;
        }
        void resetAll() {
            paused=false; gameOver=false; levelComplete=false; playing=false;
            score=0; coins=0; lives=3; level=1;
            resetBuffs();
            randomizeBlock();
            initLevel(level);
            balls.clear();
            pauseOverlay.setVisible(false);
            gameOverOverlay.setVisible(false);
            levelOverlay.setVisible(false);
            repaint();
        }

        // --- 15 pattern ---
        private void generatePatterns() {
            int r = 8, c = 10;
            for (int i = 0; i < 15; i++) {
                boolean[][] m = new boolean[r][c];
                for (int rr = 0; rr < r; rr++) Arrays.fill(m[rr], false);
                switch (i) {
                    case 0: for (int rr=0; rr<r; rr++) Arrays.fill(m[rr], true); break;
                    case 1: for (int rr=0; rr<r; rr++) for (int cc=0; cc<=rr && cc<c; cc++) m[rr][cc]=true; break;
                    case 2: for (int rr=0; rr<r; rr++) for (int cc=0; cc<r-rr && cc<c; cc++) m[rr][cc]=true; break;
                    case 3: for (int cc=0; cc<c; cc++){ m[0][cc]=m[r-1][cc]=true; } for (int rr=0; rr<r; rr++){ m[rr][0]=m[rr][c-1]=true; } break;
                    case 4: for (int rr=0; rr<r; rr+=2) Arrays.fill(m[rr], true); break;
                    case 5: for (int cc=0; cc<c; cc+=2) for (int rr=0; rr<r; rr++) m[rr][cc]=true; break;
                    case 6: for (int rr=0; rr<r; rr++){ if(rr<c)m[rr][rr]=true; if(c-rr-1>=0)m[rr][c-rr-1]=true; } break;
                    case 7: for (int rr=0; rr<r; rr++) for (int cc=0; cc<c/2; cc++) m[rr][cc]=true; break;
                    case 8: for (int rr=0; rr<r; rr++) for (int cc=c/2; cc<c; cc++) m[rr][cc]=true; break;
                    case 9: for (int rr=0; rr<r; rr++) for (int cc=0; cc<c; cc++) if((rr+cc)%2==0) m[rr][cc]=true; break;
                    case 10: Arrays.fill(m[r/2], true); if(r/2+1<r) Arrays.fill(m[r/2+1], true); break;
                    case 11: for (int rr=0; rr<r; rr++){ if(c/2<c)m[rr][c/2]=true; if(c/2+1<c)m[rr][c/2+1]=true; } break;
                    case 12: for (int rr=0; rr<r; rr++) for (int cc=0; cc<c; cc++) if(Math.abs(rr-r/2)+Math.abs(cc-c/2)<=r/2) m[rr][cc]=true; break;
                    case 13: for (int cc=0; cc<c; cc++){ m[0][cc]=m[1][cc]=m[r-1][cc]=m[r-2][cc]=true; } for (int rr=0; rr<r; rr++){ m[rr][0]=m[rr][1]=m[rr][c-1]=m[rr][c-2]=true; } break;
                    case 14: for (int rr=0; rr<r; rr++) for (int cc=0; cc<c; cc++) m[rr][cc]=rng.nextBoolean(); break;
                }
                patterns.add(m);
            }
        }
        private void randomizeBlock() {
            List<Integer> base = new ArrayList<>();
            for (int i = 0; i < 15; i++) base.add(i);
            Collections.shuffle(base, rng);
            randomList = base;
            patternIndex = 0;
        }
        private void initLevel(int lvl) {
            if ((lvl - 1) % 15 == 0) randomizeBlock();
            boolean[][] p = patterns.get(randomList.get(patternIndex % 15));
            patternIndex++;

            rows = p.length; cols = p[0].length;
            brickW = (WIDTH - 2 * offsetX) / cols;
            brickH = 24;

            bricks = new boolean[rows][cols];
            totalBricks = 0;
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++){
                    bricks[r][c] = p[r][c];
                    if (p[r][c]) totalBricks++;
                }

            baseSpeed = 4 + ((lvl - 1) / 5);
        }

        // --- Spawn bóng giữ trên paddle ---
        private void spawnHeldBall(){
            balls.clear();
            int r = baseBallR * (bigBallTicks > 0 ? 2 : 1);
            int pY = HEIGHT - 60;
            Ball b = new Ball((int)(playerX + paddleW/2.0), pY - r - 1, 0, 0, r);
            b.unstoppable = isPierceActive();
            balls.add(b);
            ballHeld = true;
            playing = true;
        }
        // --- Spawn bóng bay ngay (cần khi muốn thả ngay) ---
        private void spawnSingleBall(){
            balls.clear();
            int r = baseBallR * (bigBallTicks > 0 ? 2 : 1);
            double dx = (rng.nextBoolean()?1:-1) * (baseSpeed+1);
            double dy = -(baseSpeed+2);
            balls.add(new Ball(WIDTH/2, HEIGHT-120, dx, dy, r));
            ballHeld = false;
        }
        private void releaseHeldBall(){
            if (ballHeld && !balls.isEmpty()){
                Ball b = balls.get(0);
                double dx = (rng.nextBoolean()?1:-1) * (baseSpeed+1);
                double dy = -(baseSpeed+2);
                b.dx = dx; b.dy = dy;
                ballHeld = false;
            }
        }

        // ---------- Render ----------
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Bricks
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    if (bricks[r][c]) {
                        int x = offsetX + c * brickW, y = offsetY + r * brickH;
                        g2.setColor(brickColor);
                        g2.fillRect(x, y, brickW - 2, brickH - 2);
                        g2.setColor(Color.black);
                        g2.drawRect(x, y, brickW - 2, brickH - 2);
                    }

            // HUD
            g2.setColor(Color.white);
            g2.setFont(new Font("Consolas", Font.BOLD, 18));
            g2.drawString("Score: " + score, 20, 25);
            g2.drawString("Coins: " + coins, 150, 25);
            g2.drawString("Lives: " + lives, 280, 25);
            g2.drawString("Level: " + level, 400, 25);

            // Buffs + thời gian còn lại
            int bx = WIDTH - 300, by = 25, step = 22;
            g2.drawString("Buffs:", bx - 60, by);
            if (bigBallTicks > 0)      g2.drawString("BigBall " + timeLeft(bigBallTicks),  bx, by);
            if (bigPaddleTicks > 0)    g2.drawString("BigPad "  + timeLeft(bigPaddleTicks),bx+140, by);
            if (slowTicks > 0)         g2.drawString("Slow "    + timeLeft(slowTicks),     bx, by+step);
            if (unstoppableTicks > 0)  g2.drawString("Pierce "  + timeLeft(unstoppableTicks) + " | hits:" + unstoppableHitsLeft, bx+140, by+step);

            // Paddle
            int paddleY = HEIGHT - 60;
            g2.setColor(new Color(180, 180, 255));
            g2.fillRoundRect(playerX, paddleY, paddleW, paddleH, 10, 10);

            // Balls
            for (Ball b : balls) {
                g2.setColor(b.unstoppable ? Color.red : (b.explosive ? Color.orange : Color.yellow));
                g2.fillOval((int)(b.x - b.r), (int)(b.y - b.r), 2 * b.r, 2 * b.r);
            }

            // Drops
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            for (PowerUp p : drops) {
                g2.setColor(Color.white);
                g2.drawRect(p.x, p.y, p.w, p.h);
                g2.drawString(shortName(p.type), p.x + 4, p.y + 10);
            }

            pauseOverlay.setVisible(paused);
            gameOverOverlay.setVisible(gameOver);
            levelOverlay.setVisible(levelComplete);
        }

        private String timeLeft(int ticks){
            double sec = ticks * (TICK_MS / 1000.0);
            return String.format(Locale.US, "%.1fs", sec);
        }
        private String shortName(PType t){
            switch (t){
                case BigBall: return "BB";
                case MultiBall: return "MB";
                case Explosive: return "EX";
                case BigPaddle: return "PW";
                case Slow: return "SL";
                case ExtraLife: return "+1";
                case Unstoppable: return "PI";
                case Coin: return "$";
            }
            return "?";
        }

        // ---------- Loop ----------
        @Override public void actionPerformed(ActionEvent e){
            if (!playing || paused || gameOver || levelComplete) { repaint(); return; }

            int W = WIDTH, H = HEIGHT, pY = H - 60;

            // Timers
            if (bigBallTicks > 0 && --bigBallTicks == 0)   balls.forEach(b -> b.r = baseBallR);
            if (bigPaddleTicks > 0 && --bigPaddleTicks == 0) paddleW = basePaddleW;
            if (slowTicks > 0) slowTicks--;
            if (unstoppableTicks > 0 && --unstoppableTicks == 0) disablePierce();

            // Nếu đang giữ bóng: dán bóng theo paddle & bỏ qua va chạm
            if (ballHeld && !balls.isEmpty()){
                Ball b = balls.get(0);
                b.x = playerX + paddleW / 2.0;
                b.y = pY - b.r - 1;
                repaint();
                return;
            }

            // drops
            Iterator<PowerUp> itD = drops.iterator();
            while (itD.hasNext()){
                PowerUp p = itD.next(); p.y += p.dy;
                if (p.y > H) { itD.remove(); continue; }
                Rectangle pad = new Rectangle(playerX, pY, paddleW, paddleH);
                if (p.rect().intersects(pad)) { applyPowerUp(p.type); itD.remove(); }
            }

            // balls
            Iterator<Ball> it = balls.iterator();
            while (it.hasNext()){
                Ball b = it.next();
                double f = (slowTicks > 0) ? 0.6 : 1.0;
                b.x += b.dx * f; b.y += b.dy * f;

                if (b.x - b.r <= 0) { b.x = b.r; b.dx = Math.abs(b.dx); }
                if (b.x + b.r >= W) { b.x = W - b.r; b.dx = -Math.abs(b.dx); }
                if (b.y - b.r <= 0) { b.y = b.r; b.dy = Math.abs(b.dy); }

                Rectangle pad = new Rectangle(playerX, pY, paddleW, paddleH);
                if (b.bounds().intersects(pad) && b.dy > 0) {
                    b.y = pY - b.r; b.dy = -Math.abs(b.dy);
                    int hit = (int)(b.x - (playerX + paddleW / 2.0));
                    b.dx = Math.max(-(baseSpeed+3), Math.min(baseSpeed+3, hit / 6.0));
                    if (isPierceActive()) {
                        unstoppableHitsLeft--;
                        if (unstoppableHitsLeft <= 0) disablePierce();
                    }
                }

                collideBricks(b);
                if (b.y - b.r > H) it.remove();
            }

            // life check
            if (balls.isEmpty()){
                lives--;
                if (lives <= 0){
                    showGameOver();
                    return;
                } else {
                    spawnHeldBall(); // sinh bóng dính khi mất 1 mạng
                }
            }

            // level complete → mở overlay NEXT LEVEL
            if (totalBricks <= 0){
                levelComplete = true;
                levelLabel.setText("LEVEL " + level + " CLEARED!  Score: " + score);
                levelOverlay.setVisible(true);
            }

            repaint();
        }

        private boolean isPierceActive(){ return unstoppableTicks > 0 && unstoppableHitsLeft > 0; }
        private void disablePierce(){
            unstoppableTicks = 0; unstoppableHitsLeft = 0;
            balls.forEach(b -> b.unstoppable = false);
        }
        private void showGameOver(){
            paused = false;
            playing = false;
            levelComplete = false;
            gameOver = true;
            gameOverLabel.setText("YOUR SCORE IS " + score);
            pauseOverlay.setVisible(false);
            levelOverlay.setVisible(false);
            gameOverOverlay.setVisible(true);
            repaint();
        }

        private void resetBuffs(){
            bigBallTicks = bigPaddleTicks = slowTicks = 0;
            disablePierce();
            paddleW = basePaddleW;
            balls.forEach(b -> { b.r = baseBallR; b.explosive = false; });
            drops.clear();
        }

        private void collideBricks(Ball b){
            Rectangle br = b.bounds();
            for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) if (bricks[r][c]) {
                int bx = offsetX + c * brickW, by = offsetY + r * brickH;
                Rectangle rr = new Rectangle(bx, by, brickW - 2, brickH - 2);
                if (!br.intersects(rr)) continue;

                bricks[r][c] = false; totalBricks--; score += 10; maybeDrop(bx + brickW/2, by + brickH/2);

                if (b.explosive) { explode(bx, by); b.explosive = false; }

                if (!isPierceActive()){
                    double left = (b.x + b.r) - rr.x;
                    double right = (rr.x + rr.width) - (b.x - b.r);
                    double top = (b.y + b.r) - rr.y;
                    double bottom = (rr.y + rr.height) - (b.y - b.r);
                    double minX = Math.min(left, right);
                    double minY = Math.min(top, bottom);
                    if (minX < minY) b.dx = -b.dx; else b.dy = -b.dy;
                }
                return;
            }
        }

        private void explode(int cx, int cy){
            int R = Math.max(brickW, brickH);
            int bonus = 0;
            for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) if (bricks[r][c]) {
                int bx = offsetX + c*brickW + brickW/2, by = offsetY + r*brickH + brickH/2;
                int dx = bx - cx, dy = by - cy;
                if (dx*dx + dy*dy <= R*R) { bricks[r][c] = false; totalBricks--; bonus++; maybeDrop(bx, by); }
            }
            score += bonus * 10;
        }

        private void maybeDrop(int x, int y){
            if (rng.nextInt(100) < 10) {
                PType[] types = PType.values();
                drops.add(new PowerUp(types[rng.nextInt(types.length)], x - 12, y - 6));
            }
        }

        private void applyPowerUp(PType t){
            switch (t){
                case Coin: coins++; score += 5; break;
                case ExtraLife: lives++; break;
                case MultiBall: {
                    if (ballHeld) break;
                    int limit = 6, s = balls.size();
                    for (int i = 0; i < s && balls.size() < limit; i++){
                        Ball b = balls.get(i);
                        Ball nb = new Ball((int)b.x, (int)b.y, -b.dy, b.dx, b.r);
                        nb.unstoppable = isPierceActive();
                        balls.add(nb);
                    }
                    break;
                }
                case BigBall:
                    bigBallTicks = BUFF_TICKS;
                    balls.forEach(b -> b.r = baseBallR * 2);
                    break;
                case BigPaddle:
                    bigPaddleTicks = BUFF_TICKS;
                    paddleW = (int)(basePaddleW * 1.5);
                    if (playerX + paddleW > WIDTH) playerX = Math.max(0, WIDTH - paddleW);
                    break;
                case Slow:
                    slowTicks = BUFF_TICKS; break;
                case Unstoppable:
                    unstoppableTicks = BUFF_TICKS;
                    unstoppableHitsLeft = 2;
                    balls.forEach(b -> b.unstoppable = true);
                    break;
                case Explosive:
                    if (!balls.isEmpty()) balls.get(rng.nextInt(balls.size())).explosive = true;
                    break;
            }
        }

        // ---------- Input ----------
        @Override public void mouseDragged(MouseEvent e){}
        @Override public void mouseMoved(MouseEvent e){
            playerX = e.getX() - paddleW / 2;
            if (playerX < 0) playerX = 0;
            if (playerX > WIDTH - paddleW) playerX = WIDTH - paddleW;
            if (ballHeld && !balls.isEmpty()) {
                Ball b = balls.get(0);
                b.x = playerX + paddleW/2.0;
                b.y = HEIGHT - 60 - b.r - 1;
                repaint();
            }
        }
        @Override public void mouseClicked(MouseEvent e){
            if (paused || gameOver || levelComplete) return;
            if (ballHeld) { releaseHeldBall(); return; }
            if (balls.isEmpty() && lives > 0) { spawnHeldBall(); playing = true; return; }
            playing = true;
        }
        @Override public void mousePressed(MouseEvent e){}
        @Override public void mouseReleased(MouseEvent e){}
        @Override public void mouseEntered(MouseEvent e){}
        @Override public void mouseExited(MouseEvent e){}

        @Override public void keyTyped(KeyEvent e){}
        @Override public void keyPressed(KeyEvent e){
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                playerX -= 30; if (playerX < 0) playerX = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                playerX += 30; if (playerX > WIDTH - paddleW) playerX = WIDTH - paddleW;
            } else if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (!gameOver && !levelComplete) togglePause(!paused);
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (ballHeld) releaseHeldBall();
            }
        }
        @Override public void keyReleased(KeyEvent e){}

        private void togglePause(boolean state) {
            if (gameOver || levelComplete) return;
            paused = state;
            pauseOverlay.setVisible(paused);
            repaint();
        }
    }
}
