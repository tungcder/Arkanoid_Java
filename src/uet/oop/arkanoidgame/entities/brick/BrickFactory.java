package uet.oop.arkanoidgame.entities.brick;

/**
 * Factory for creating different types of bricks based on type code from CSV.
 */
public class BrickFactory {
    public static Brick createBrick(int type, double x, double y, double width, double height) {
        switch (type) {
            case 5:
                return new BrickWeak(x, y, width, height);
            case 2:
                return new BrickMedium(x, y, width, height);
            case 3:
                return new BrickStrong(x, y, width, height);
            case 4:
                return new BrickUnbreakable(x, y, width, height);
            case 1:
                return new BrickPowerup(x, y, width, height);
            default:
                throw new IllegalArgumentException("Unknown brick type: " + type);
        }
    }
}