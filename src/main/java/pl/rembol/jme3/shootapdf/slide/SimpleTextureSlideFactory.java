package pl.rembol.jme3.shootapdf.slide;

import com.jme3.app.Application;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture2D;
import pl.rembol.jme3.shootapdf.ImageRescaler;

public class SimpleTextureSlideFactory extends SlideFactory {
    
    private final ImageRescaler imageRescaler;
    
    private final Texture2D texture2D;

    public SimpleTextureSlideFactory(ImageRescaler imageRescaler, Texture2D texture2D) {
        this.imageRescaler = imageRescaler;
        this.texture2D = texture2D;
    }

    @Override
    public Slide create(Application application, Vector3f position, Vector2f slideSize) {
        return new Slide(application, imageRescaler, texture2D, position, slideSize);
    }
}
