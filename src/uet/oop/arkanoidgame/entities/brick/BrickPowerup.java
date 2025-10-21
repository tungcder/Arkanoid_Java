package uet.oop.arkanoidgame.entities.brick;

public class BrickPowerup extends Brick {
    public BrickPowerup(double x, double y, double width, double height) {
        super(x, y, width, height, 1, "src/main/resourse/Sprites/PNG/03-Breakout-Tiles.png");
    }

    @Override
    public void hit() {
        super.hit();
    }
}
