package pl.rembol.jme3.shootapdf.slide;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Image;

public class Slide extends Node {

    public static final float SIZE = 5f;

    public static final int PIECES = 8;

    public Slide(Application application, Image image, Vector3f position) {
        for (int i = 0; i < PIECES; ++i) {
            for (int j = 0; j < PIECES; ++j) {
                float offsetSize = 1.0f / PIECES;
                attachChild(new SlideBox(application, image, position, SIZE / PIECES, offsetSize * i, offsetSize * j, offsetSize));
            }
        }
    }

    public void setKinematic(boolean kinematic) {
        getChildren().stream()
                .filter(SlideBox.class::isInstance)
                .map(SlideBox.class::cast).forEach(slideBox -> slideBox.setKinematic(kinematic));
    }
}
