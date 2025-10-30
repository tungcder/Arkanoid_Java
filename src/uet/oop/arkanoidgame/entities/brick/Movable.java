// src/main/java/uet/oop/arkanoidgame/entities/brick/Movable.java
package uet.oop.arkanoidgame.entities.brick;

public interface Movable {
    void initMovementRange(BrickGrid brickGrid);
    void update(); // Để override riêng
}