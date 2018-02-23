package pl.rembol.jme3.shootapdf.slide;

import com.jme3.app.Application;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import pl.rembol.jme3.shootapdf.ImageRescaler;

public class Slide extends Node {
    
    private final ImageRescaler imageRescaler;
    
    private final Texture2D rescaledTexture;

    public static final int PIECES = 6;

    public Slide(Application application, ImageRescaler imageRescaler, Texture2D texture, Vector3f position, Vector2f slideSize) {
        this.imageRescaler = imageRescaler;
        this.rescaledTexture = imageRescaler.rescale(texture);
        
        // building a brick wall
        for (int i = 0; i < PIECES; ++i) {
            for (int j = 0; j < PIECES; ++j) {
                float offsetSize = 1.0f / PIECES;
                Vector2f offset;
                Vector2f brickSize;
                if (i == 0 && j % 2 == 0) {
                    // left side
                    offset = new Vector2f(offsetSize * i, offsetSize * j);
                    brickSize = new Vector2f(1.0f / PIECES, 1.0f / PIECES);
                } else if (i == (PIECES - 1) && ((i + j) % 2) == 1) {
                    // right side
                    offset = new Vector2f(offsetSize * i, offsetSize * j);
                    brickSize = new Vector2f(1.0f / PIECES, 1.0f / PIECES);
                } else if ((i + j) % 2 == 0) {
                    // odd part - don't add
                    continue;
                } else {
                    offset = new Vector2f(offsetSize * i, offsetSize * j);
                    brickSize = new Vector2f(2.0f / PIECES, 1.0f / PIECES);
                }
                attachChild(new SlideBox(application,
                        rescaledTexture,
                        position.add(new Vector3f((offset.x - .5f) * slideSize.x, offset.y * slideSize.y, 0)),
                        new Vector2f(brickSize.x * slideSize.x, brickSize.y * slideSize.y),
                        offset,
                        brickSize));
            }
        }
    }

    public void remove() {
        getChildren().stream()
                .filter(SlideBox.class::isInstance)
                .map(SlideBox.class::cast)
                .forEach(SlideBox::remove);

        getParent().detachChild(this);
        imageRescaler.dropViewPort(rescaledTexture);
    }

    public void setSlidePhysical() {
        getChildren().stream()
                .filter(SlideBox.class::isInstance)
                .map(SlideBox.class::cast).forEach(slideBox -> slideBox.getControl(RigidBodyControl.class).setKinematic(false));
    }
}
