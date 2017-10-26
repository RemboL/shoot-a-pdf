package pl.rembol.jme3.shootapdf.slide;

import com.jme3.app.Application;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Image;
import org.omg.CORBA.PERSIST_STORE;

public class Slide extends Node {

    public static final float SIZE = 5f;

    public static final int PIECES = 8;

    public Slide(Application application, Image image, Vector3f position) {
        for (int i = 0; i < PIECES; ++i) {
            for (int j = 0; j < PIECES; ++j) {
                float offsetSize = 1.0f / PIECES;
                Vector2f offset;
                Vector2f size;
                if (i == 0 && j % 2 == 0) {
//                     left side
                    offset = new Vector2f(offsetSize * i, offsetSize * j);
                    size = new Vector2f(1.0f / PIECES, 1.0f / PIECES);
                } else if (i == (PIECES - 1) && ((i + j) % 2) == 1) {
//                     right side
                    offset = new Vector2f(offsetSize * i, offsetSize * j);
                    size = new Vector2f(1.0f / PIECES, 1.0f / PIECES);
                } else if ((i + j) % 2 == 0) {
//                     odd part - don't add
                    continue;
                } else {
                    offset = new Vector2f(offsetSize * i, offsetSize * j);
                    size = new Vector2f(2.0f / PIECES, 1.0f / PIECES);
                }
                attachChild(new SlideBox(application,
                        image,
                        position.add(new Vector3f(offset.x, offset.y, 0).multLocal(SIZE)),
                        size.mult(SIZE),
                        offset,
                        size));
            }
        }
    }

    public void setSlidePhysical() {
        getChildren().stream()
                .filter(SlideBox.class::isInstance)
                .map(SlideBox.class::cast).forEach(slideBox -> slideBox.getControl(RigidBodyControl.class).setKinematic(false));
    }
}
