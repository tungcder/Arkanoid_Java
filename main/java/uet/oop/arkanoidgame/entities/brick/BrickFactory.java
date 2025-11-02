package uet.oop.arkanoidgame.entities.brick;

public class BrickFactory {
    /**
     * Tạo brick dựa trên type.
     * @param type Mã type (1-6).
     * @param x Tọa độ x.
     * @param y Tọa độ y.
     * @param width Chiều rộng.
     * @param height Chiều cao.
     * @return Brick tương ứng.
     * @throws IllegalArgumentException Nếu type không hợp lệ.
     */
    public static Brick createBrick(int type, double x, double y, double width, double height) {
        switch (type) {
            case 1:
                return new BrickWeak(x, y, width, height);
            case 2:
                return new BrickMedium(x, y, width, height);
            case 3:
                return new BrickStrong(x, y, width, height);
            case 4:
                return new BrickUnbreakable(x, y, width, height);
            case 5:
                return new BrickPowerup(x, y, width, height);
            case 6:
                return new BrickMove(x, y, width, height);
            default:
                throw new IllegalArgumentException("Unknown brick type: " + type);
        }
    }
}