package uet.oop.arkanoidgame.entities.ball;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import uet.oop.arkanoidgame.entities.brick.Brick;
import uet.oop.arkanoidgame.entities.brick.BrickGrid;
import uet.oop.arkanoidgame.entities.item.Item;
import uet.oop.arkanoidgame.entities.paddle.Paddle;
import uet.oop.arkanoidgame.SoundManager;
import uet.oop.arkanoidgame.ThemeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ball {
    private static final double CANVAS_W = 600;
    private static final double CANVAS_H = 600;
    private static final int FRAME_COUNT = 8;
    private static final double FRAME_STEP = 10;
    private static final double RESTITUTION = 1.0;
    private static final double MAX_SPEED = 8.0;
    private static final double MIN_ABS_DY = 2.0;
    private static final double EPS = 1e-6;

    private double x, y;
    private double radius;
    private double dx = 6, dy = -6;
    private double prevX, prevY;

    private final List<Image> frames = new ArrayList<>(FRAME_COUNT);
    private int currentFrame = 0, frameCounter = 0;

    // === Ball attach/release ===
    private boolean attachedToPaddle = true;

    // === Buff tốc độ ===
    private double speedMultiplier = 1.0;
    private double baseSpeed;
    private Timeline speedBuffTimer;

    // === Buff kích thước ===
    private double sizeMultiplier = 1.0;
    private double baseRadius;
    private Timeline sizeBuffTimer;

    // === Buff nổ (Explosive) ===
    private boolean explosive = false;
    private double explosionRadius = 0.0;
    private Timeline explosiveTimer;

    private final SoundManager soundManager;

    public Ball(double x, double y, double radius, SoundManager soundManager) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.soundManager = soundManager;
        this.prevX = x;
        this.prevY = y;
        this.baseRadius = radius;

        loadFrames();

        // Lưu tốc độ gốc
        baseSpeed = Math.hypot(dx, dy);
        if (baseSpeed < EPS) {
            baseSpeed = 5.0;
            dx = 0;
            dy = -baseSpeed;
        }
    }

    private void loadFrames() {
        for (int i = 1; i <= FRAME_COUNT; i++) {
            final String assetPath = "ball/ball" + i + ".png";
            final String path = ThemeManager.getImagePath(assetPath);
            try {
                frames.add(new Image(Objects.requireNonNull(
                        Ball.class.getResourceAsStream(path),
                        "Không tìm thấy ảnh: " + path
                )));
            } catch (Exception e) {
                System.out.println("Lỗi load " + path + ": " + e.getMessage());
            }
        }
    }

    // =======================
    // BALL ATTACH/RELEASE
    // =======================
    public void attachToPaddle(Paddle paddle) {
        attachedToPaddle = true;
        // đặt bóng ở giữa paddle, ngay phía trên
        x = paddle.getX() + paddle.getWidth() / 2.0 - radius;
        y = paddle.getY() - radius * 2.0;
        dx = 0;
        dy = 0;
    }

    public void releaseFromPaddle() {
        if (!attachedToPaddle) return;
        attachedToPaddle = false;
        // phóng thẳng lên với tốc độ gốc (có MIN_ABS_DY đảm bảo không quá phẳng)
        dx = 0;
        dy = -baseSpeed;
        clampSpeed();
    }

    public boolean isAttachedToPaddle() {
        return attachedToPaddle;
    }

    // =======================
    // QUẢN LÝ BUFF TỐC ĐỘ
    // =======================
    public void applySpeedBuff(double multiplier, double durationSeconds) {
        if (speedBuffTimer != null) speedBuffTimer.stop();

        speedMultiplier = multiplier;
        renormalizeSpeed();

        speedBuffTimer = new Timeline(new KeyFrame(
                Duration.seconds(durationSeconds),
                e -> {
                    speedMultiplier = 1.0;
                    renormalizeSpeed();
                }
        ));
        speedBuffTimer.play();
    }

    private void renormalizeSpeed() {
        double currentSpeed = Math.hypot(dx, dy);
        double targetSpeed = baseSpeed * speedMultiplier;

        if (currentSpeed < EPS) {
            dx = 0;
            dy = -targetSpeed;
            return;
        }

        double scale = targetSpeed / currentSpeed;
        dx *= scale;
        dy *= scale;
    }

    // =======================
    // QUẢN LÝ BUFF KÍCH THƯỚC
    // =======================
    public void applySizeBuff(double multiplier, double durationSeconds) {
        if (sizeBuffTimer != null) sizeBuffTimer.stop();

        sizeMultiplier = multiplier;
        radius = baseRadius * sizeMultiplier;

        sizeBuffTimer = new Timeline(new KeyFrame(
                Duration.seconds(durationSeconds),
                e -> {
                    sizeMultiplier = 1.0;
                    radius = baseRadius;
                }
        ));
        sizeBuffTimer.play();
    }

    // =======================
    // QUẢN LÝ BUFF NỔ (EXPLOSIVE)
    // =======================
    public void applyExplosiveBuff(double radius, double durationSeconds) {
        if (explosiveTimer != null) explosiveTimer.stop();

        this.explosive = true;
        this.explosionRadius = Math.max(0.0, radius);

        explosiveTimer = new Timeline(new KeyFrame(
                Duration.seconds(durationSeconds),
                e -> {
                    this.explosive = false;
                    this.explosionRadius = 0.0;
                }
        ));
        explosiveTimer.play();
    }

    public boolean isExplosive() { return explosive; }
    public double getExplosionRadius() { return explosionRadius; }

    // =======================
    // UPDATE / RENDER
    // =======================
    /** Update theo paddle (dùng cho trạng thái dính bóng) */
    public void update(Paddle paddle, double canvasW, double canvasH) {
        if (attachedToPaddle) {
            // Bóng dính và đi theo paddle
            x = paddle.getX() + paddle.getWidth() / 2.0 - radius;
            y = paddle.getY() - radius * 2.0;
            // hoạt ảnh khung hình vẫn chạy để bóng sống động
            animate();
        } else {
            update(canvasW, canvasH);
        }
    }

    /** Update theo paddle với kích thước canvas mặc định */
    public void update(Paddle paddle) {
        update(paddle, CANVAS_W, CANVAS_H);
    }

    public void update(double canvasW, double canvasH) {
        prevX = x;
        prevY = y;

        x += dx;
        y += dy;

        double d = radius * 2;
        double cx = x + radius, cy = y + radius;

        if (cx - radius <= 0) {
            x = 0;
            dx = Math.abs(dx) * RESTITUTION;
        } else if (cx + radius >= canvasW) {
            x = canvasW - d;
            dx = -Math.abs(dx) * RESTITUTION;
        }
        if (cy - radius <= 0) {
            y = 0;
            dy = Math.abs(dy) * RESTITUTION;
        }

        animate();
        clampSpeed();
    }

    public void update() {
        update(CANVAS_W, CANVAS_H);
    }

    /** Kiểm tra bóng đã rơi khỏi màn hình chưa (để trừ mạng & respawn) */
    public boolean isOutOfScreen(double canvasH) {
        return (y + radius * 2.0) >= canvasH;
    }

    public void checkCollision(Paddle paddle) {
        if (attachedToPaddle) return;

        double rx = paddle.getX();
        double ry = paddle.getY();
        double rw = paddle.getWidth();
        double rh = paddle.getHeight();

        double cx = x + radius;
        double cy = y + radius;

        if (!circleIntersectsAABB(cx, cy, radius, rx, ry, rw, rh)) {
            return;
        }

        soundManager.playSfx("PaddleHit");
        paddle.handleHit();

        y = ry - 2 * radius;
        dy = -Math.abs(dy) * RESTITUTION;

        double hitOffset = (cx - (rx + rw / 2.0)) / (rw / 2.0);
        hitOffset = clamp(hitOffset, -1.0, 1.0);

        dx = hitOffset * MAX_SPEED;
        clampSpeed();
    }


    public Item checkCollision(BrickGrid grid) {
        if (attachedToPaddle) return null; // đang dính thì chưa va gạch

        double cx = x + radius, cy = y + radius;

        for (Brick b : grid.getBricks()) {
            if (b.isDestroyed()) continue;

            double rx = b.getX(), ry = b.getY(), rw = b.getWidth(), rh = b.getHeight();
            if (!circleIntersectsAABB(cx, cy, radius, rx, ry, rw, rh)) continue;

            soundManager.playSfx("BrickHit");

            CollisionSide side = resolveSideUsingPrevious(prevX, prevY, x, y, radius, rx, ry, rw, rh);

            // Tính điểm va chạm gần nhất để làm tâm vụ nổ (nếu có)
            double impactX = clamp(cx, rx, rx + rw);
            double impactY = clamp(cy, ry, ry + rh);

            switch (side) {
                case TOP -> { y = ry - 2 * radius; reflectByNormal(0, -1); }
                case BOTTOM -> { y = ry + rh; reflectByNormal(0, 1); }
                case LEFT -> { x = rx - 2 * radius; reflectByNormal(-1, 0); }
                case RIGHT -> { x = rx + rw; reflectByNormal(1, 0); }
                default -> {
                    double nx = clamp(cx, rx, rx + rw);
                    double ny = clamp(cy, ry, ry + rh);
                    reflectByNormal(cx - nx, cy - ny);
                    double nlen = Math.hypot(cx - nx, cy - ny);
                    if (nlen > 0) {
                        double push = (radius - nlen) + 0.1;
                        x += (cx - nx) / nlen * push;
                        y += (cy - ny) / nlen * push;
                    }
                }
            }

            // Viên trúng trực tiếp
            Item directDrop = null;
            if (b.hit()) {
                directDrop = b.getPowerup();
            }

            // Nếu có buff nổ → phá thêm các viên trong bán kính quanh điểm va chạm
            Item splashDrop = null;
            if (explosive && explosionRadius > 0.0) {
                splashDrop = explodeAt(grid, impactX, impactY);
            }

            clampSpeed();

            // Trả về item rơi: ưu tiên viên trúng trực tiếp, nếu không có thì viên do nổ
            return (directDrop != null) ? directDrop : splashDrop;
        }
        return null;
    }

    public void render(GraphicsContext gc) {
        double d = radius * 2;
        gc.setImageSmoothing(true);
        if (!frames.isEmpty()) gc.drawImage(frames.get(currentFrame), x, y, d, d);
        else {
            gc.setFill(Color.YELLOW);
            gc.fillOval(x, y, d, d);
        }
    }

    private void animate() {
        if (frames.isEmpty()) return;
        if (++frameCounter >= FRAME_STEP) {
            currentFrame = (currentFrame + 1) % frames.size();
            frameCounter = 0;
        }
    }

    private void reflectByNormal(double nx, double ny) {
        double len = Math.hypot(nx, ny);
        if (len == 0) return;
        nx /= len;
        ny /= len;

        double dot = dx * nx + dy * ny;
        dx = dx - 2 * dot * nx;
        dy = dy - 2 * dot * ny;

        dx *= RESTITUTION;
        dy *= RESTITUTION;

        if (ny < 0) dy = -Math.abs(dy);
        if (ny > 0) dy = Math.abs(dy);
        if (nx < 0) dx = -Math.abs(dx);
        if (nx > 0) dx = Math.abs(dx);
    }

    private void clampSpeed() {
        if (dx > MAX_SPEED) dx = MAX_SPEED;
        if (dx < -MAX_SPEED) dx = -MAX_SPEED;
        if (dy > MAX_SPEED) dy = MAX_SPEED;
        if (dy < -MAX_SPEED) dy = -MAX_SPEED;
        if (Math.abs(dy) < MIN_ABS_DY) dy = (dy >= 0 ? MIN_ABS_DY : -MIN_ABS_DY);
    }

    private static boolean circleIntersectsAABB(double cx, double cy, double r,
                                                double rx, double ry, double rw, double rh) {
        double qx = clamp(cx, rx, rx + rw);
        double qy = clamp(cy, ry, ry + rh);
        double dx = cx - qx, dy = cy - qy;
        return dx * dx + dy * dy <= r * r;
    }

    private static CollisionSide resolveSideUsingPrevious(double px, double py, double x, double y,
                                                          double r, double rx, double ry, double rw, double rh) {
        double pcx = px + r, pcy = py + r;
        double cx = x + r, cy = y + r;

        boolean wasAbove = (pcy + r) <= ry;
        boolean wasBelow = (pcy - r) >= (ry + rh);
        boolean wasLeft = (pcx + r) <= rx;
        boolean wasRight = (pcx - r) >= (rx + rw);

        if (wasAbove && cy + r >= ry) return CollisionSide.TOP;
        if (wasBelow && cy - r <= ry + rh) return CollisionSide.BOTTOM;
        if (wasLeft && cx + r >= rx) return CollisionSide.LEFT;
        if (wasRight && cx - r <= rx + rw) return CollisionSide.RIGHT;

        double penTop = Math.abs((cy + r) - ry);
        double penBottom = Math.abs((ry + rh) - (cy - r));
        double penLeft = Math.abs((cx + r) - rx);
        double penRight = Math.abs((rx + rw) - (cx - r));

        double min = penTop;
        CollisionSide side = CollisionSide.TOP;
        if (penBottom < min) { min = penBottom; side = CollisionSide.BOTTOM; }
        if (penLeft < min) { min = penLeft; side = CollisionSide.LEFT; }
        if (penRight < min) { side = CollisionSide.RIGHT; }
        return side;
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(v, hi));
    }

    private enum CollisionSide { NONE, TOP, BOTTOM, LEFT, RIGHT }

    // ======= HỖ TRỢ NỔ =======
    private Item explodeAt(BrickGrid grid, double ix, double iy) {
        Item firstDrop = null;
        if (!explosive || explosionRadius <= 0) return null;

        for (Brick nb : grid.getBricks()) {
            if (nb.isDestroyed()) continue;

            double bx = nb.getX() + nb.getWidth()  / 2.0;
            double by = nb.getY() + nb.getHeight() / 2.0;
            double dist = Math.hypot(bx - ix, by - iy);

            if (dist <= explosionRadius) {
                if (nb.hit()) {
                    Item drop = nb.getPowerup();
                    if (firstDrop == null) firstDrop = drop;
                }
            }
        }
        return firstDrop;
    }

    // === Getter/Setter ===
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // ✅ NEW: Setter cho vị trí (để support save/load)
    public void setX(double x) {
        this.x = x;
        this.prevX = x; // Cập nhật cả prevX để tránh lỗi collision
    }

    public void setY(double y) {
        this.y = y;
        this.prevY = y; // Cập nhật cả prevY để tránh lỗi collision
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setSpeedMultiplier(double multiplier) {
        this.speedMultiplier = multiplier;
        renormalizeSpeed();
    }

    // Thêm getter/setter cho speed (dx, dy) để support save/load
    public double getSpeedX() {
        return dx;
    }

    public double getSpeedY() {
        return dy;
    }

    public void setSpeedX(double speedX) {
        this.dx = speedX;
    }

    public void setSpeedY(double speedY) {
        this.dy = speedY;
    }

    // Alias methods (backward compatibility)
    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }
}