package uet.oop.arkanoidgame.entities.ball;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import uet.oop.arkanoidgame.entities.brick.Brick;
import uet.oop.arkanoidgame.entities.brick.BrickGrid;
import uet.oop.arkanoidgame.entities.item.Item;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ball {

    private static final double CANVAS_W = 800;
    private static final double CANVAS_H = 600;
    private static final int FRAME_COUNT = 8;
    private static final double FRAME_STEP  = 10;

    private static final double RESTITUTION = 1.0;   // hệ số đàn hồi
    private static final double MAX_SPEED   = 5.0;   // kẹp tốc độ theo trục
    private static final double MIN_ABS_DY  = 2.0;   // tránh trượt ngang
    private static final double PADDLE_SPIN = 5.0;   // độ bẻ ngang khi chạm paddle

    // ===== State =====
    private double x, y;              // góc trái–trên
    private double radius;
    private double dx = 1, dy = -1;
    private double prevX, prevY;      // vị trí frame trước để suy ra mặt chạm

    private final List<Image> frames = new ArrayList<>(FRAME_COUNT);
    private int currentFrame = 0, frameCounter = 0;

    public Ball(double x, double y, double radius) {
        this.x = x; this.y = y; this.radius = radius;
        this.prevX = x; this.prevY = y;
        loadFrames();
    }

    private void loadFrames() {
        for (int i = 1; i <= FRAME_COUNT; i++) {
            final String path = "/uet/oop/arkanoidgame/entities/ball/Ball_Image/ball" + i + ".png";
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

    // ---- Update có tham số (chuẩn) ----
    public void update(double canvasW, double canvasH) {
        prevX = x; prevY = y;

        x += dx; y += dy;

        double d = radius * 2;
        double cx = x + radius, cy = y + radius;

        // Tường trái/phải
        if (cx - radius <= 0) {
            x = 0;
            dx = Math.abs(dx) * RESTITUTION;
        } else if (cx + radius >= canvasW) {
            x = canvasW - d;
            dx = -Math.abs(dx) * RESTITUTION;
        }
        // Trần
        if (cy - radius <= 0) {
            y = 0;
            dy = Math.abs(dy) * RESTITUTION;
        }

        animate();
        clampSpeed();
    }

    // ---- Update tương thích code cũ (dùng kích thước mặc định) ----
    public void update() { update(CANVAS_W, CANVAS_H); }

    // ===== Collision with Paddle (normal-based + spin) =====
    public void checkCollision(Paddle paddle) {
        double rx = paddle.getX();
        double ry = paddle.getY();
        double rw = paddle.getWidth();
        double rh = paddle.getHeight();

        double cx = x + radius;
        double cy = y + radius;

        if (!circleIntersectsAABB(cx, cy, radius, rx, ry, rw, rh)) {
            return;
        }

        paddle.handleHit();

        // Đặt lại vị trí cho bóng phía trên paddle (giả sử paddle nằm ngang)
        y = ry - 2 * radius;
        dy = -Math.abs(dy) * RESTITUTION;

        // Tính vị trí chạm so với giữa paddle: -1 (rìa trái) → +1 (rìa phải)
        double hitOffset = (cx - (rx + rw / 2.0)) / (rw / 2.0);
        if (hitOffset < -1.0) hitOffset = -1.0;
        if (hitOffset >  1.0) hitOffset =  1.0;

        // Điều chỉnh vận tốc ngang tùy điểm chạm
        dx = hitOffset * MAX_SPEED;

        clampSpeed();
    }


    // ===== Collision with Bricks (normal-based) =====
    public Item checkCollision(BrickGrid grid) {
        double cx = x + radius, cy = y + radius;

        for (Brick b : grid.getBricks()) {
            if (b.isDestroyed()) continue;

            double rx = b.getX(), ry = b.getY(), rw = b.getWidth(), rh = b.getHeight();
            if (!circleIntersectsAABB(cx, cy, radius, rx, ry, rw, rh)) continue;

            CollisionSide side = resolveSideUsingPrevious(prevX, prevY, x, y, radius, rx, ry, rw, rh);

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
            if (b.hit()) {
                return b.getPowerup();
            }
            clampSpeed();
            break;
        }
        return null;
    }

    // ===== Rendering =====
    public void render(GraphicsContext gc) {
        double d = radius * 2;
        gc.setImageSmoothing(true);
        if (!frames.isEmpty()) gc.drawImage(frames.get(currentFrame), x, y, d, d);
        else { gc.setFill(Color.YELLOW); gc.fillOval(x, y, d, d); }
    }

    // ===== Helpers =====
    private void animate() {
        if (frames.isEmpty()) return;
        if (++frameCounter >= FRAME_STEP) {
            currentFrame = (currentFrame + 1) % frames.size();
            frameCounter = 0;
        }
    }

    private void reflectByNormal(double nx, double ny) {
        // Chuẩn hoá normal
        double len = Math.hypot(nx, ny);
        if (len == 0) return;
        nx /= len; ny /= len;

        // v' = v - 2*(v·n)*n
        double dot = dx * nx + dy * ny;
        dx = dx - 2 * dot * nx;
        dy = dy - 2 * dot * ny;

        dx *= RESTITUTION;
        dy *= RESTITUTION;

        // đảm bảo bóng bật lên sau khi chạm paddle/trần
        if (ny < 0) dy = -Math.abs(dy);
        if (ny > 0) dy =  Math.abs(dy);
        if (nx < 0) dx = -Math.abs(dx);
        if (nx > 0) dx =  Math.abs(dx);
    }

    private void clampSpeed() {
        if (dx >  MAX_SPEED) dx =  MAX_SPEED;
        if (dx < -MAX_SPEED) dx = -MAX_SPEED;
        if (dy >  MAX_SPEED) dy =  MAX_SPEED;
        if (dy < -MAX_SPEED) dy = -MAX_SPEED;

        if (Math.abs(dy) < MIN_ABS_DY) dy = (dy >= 0 ? MIN_ABS_DY : -MIN_ABS_DY);
    }

    private static boolean circleIntersectsAABB(double cx, double cy, double r,
                                                double rx, double ry, double rw, double rh) {
        double qx = clamp(cx, rx, rx + rw);
        double qy = clamp(cy, ry, ry + rh);
        double dx = cx - qx, dy = cy - qy;
        return dx*dx + dy*dy <= r*r;
    }

    /** Dựa vào vị trí trước đó để xác định mặt chạm đáng tin cậy hơn. */
    private static CollisionSide resolveSideUsingPrevious(double px, double py, double x, double y,
                                                          double r, double rx, double ry, double rw, double rh) {
        double d = r * 2;
        double pcx = px + r, pcy = py + r;
        double cx  = x  + r, cy  = y  + r;

        boolean wasAbove = (pcy + r) <= ry;
        boolean wasBelow = (pcy - r) >= (ry + rh);
        boolean wasLeft  = (pcx + r) <= rx;
        boolean wasRight = (pcx - r) >= (rx + rw);

        if (wasAbove && cy + r >= ry) return CollisionSide.TOP;
        if (wasBelow && cy - r <= ry + rh) return CollisionSide.BOTTOM;
        if (wasLeft  && cx + r >= rx) return CollisionSide.LEFT;
        if (wasRight && cx - r <= rx + rw) return CollisionSide.RIGHT;

        // Fallback: chọn mặt có độ chồng lấn nhỏ nhất
        double penTop    = Math.abs((cy + r) - ry);
        double penBottom = Math.abs((ry + rh) - (cy - r));
        double penLeft   = Math.abs((cx + r) - rx);
        double penRight  = Math.abs((rx + rw) - (cx - r));

        double min = penTop;
        CollisionSide side = CollisionSide.TOP;
        if (penBottom < min) { min = penBottom; side = CollisionSide.BOTTOM; }
        if (penLeft   < min) { min = penLeft;   side = CollisionSide.LEFT;   }
        if (penRight  < min) {                  side = CollisionSide.RIGHT;  }
        return side;
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(v, hi));
    }

    private enum CollisionSide { NONE, TOP, BOTTOM, LEFT, RIGHT }

    // ===== Getters =====
    public double getX() { return x; }
    public double getY() { return y; }
    public double getRadius() { return radius; }
}
