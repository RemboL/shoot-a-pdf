package pl.rembol.jme3.shootapdf.slide;

import com.jme3.app.Application;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture2D;

public class SimpleTextureSlideFactory extends SlideFactory {
    
    private final Texture2D texture2D;

    public SimpleTextureSlideFactory(Texture2D texture2D) {
        this.texture2D = texture2D;
    }

    @Override
    public Slide create(Application application, Vector3f position, Vector2f slideSize) {
        return new Slide(application, texture2D, position, slideSize);
    }
}
