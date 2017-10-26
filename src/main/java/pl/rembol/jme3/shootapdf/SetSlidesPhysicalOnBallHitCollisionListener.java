package pl.rembol.jme3.shootapdf;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import pl.rembol.jme3.shootapdf.ball.Ball;
import pl.rembol.jme3.shootapdf.slide.SlideBox;

public class SetSlidesPhysicalOnBallHitCollisionListener implements PhysicsCollisionListener {
    @Override
    public void collision(PhysicsCollisionEvent event) {
        SlideBox slideBox = null;
        Ball ball = null;
        if (event.getObjectA().getUserObject() instanceof SlideBox) {
            slideBox = (SlideBox) event.getObjectA().getUserObject();
        }
        if (event.getObjectB().getUserObject() instanceof SlideBox) {
            slideBox = (SlideBox) event.getObjectB().getUserObject();
        }
        if (event.getObjectA().getUserObject() instanceof Ball) {
            ball = (Ball) event.getObjectA().getUserObject();
        }
        if (event.getObjectB().getUserObject() instanceof Ball) {
            ball = (Ball) event.getObjectB().getUserObject();
        }

        if (slideBox != null && ball != null) {
            slideBox.setSlidePhysical();
        }
    }
}
