package pl.rembol.jme3.shootapdf.slide;

import com.jme3.app.Application;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public abstract class SlideFactory {
    
    public abstract Slide create(Application application, Vector3f position, Vector2f slideSize);
}
