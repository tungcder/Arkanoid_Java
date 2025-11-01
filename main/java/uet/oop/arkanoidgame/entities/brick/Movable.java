// src/main/java/uet/oop/arkanoidgame/entities/brick/Movable.java
package uet.oop.arkanoidgame.entities.brick;

public interface Movable {
    void initMovementRange(BrickGrid brickGrid);
    void update();
    void reset(); // Cho phép reset trạng thái di chuyển
}