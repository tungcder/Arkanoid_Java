package uet.oop.arkanoidgame.entities.brick;

public class BrickUnbreakable extends Brick {
    public BrickUnbreakable(double x, double y, double width, double height) {
        super(x, y, width, height, -1, "src/main/resourse/Sprites/PNG/07-Breakout-Tiles.png");
    }
    @Override
    public void hit() {

    }
}
