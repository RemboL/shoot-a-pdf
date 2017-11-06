package pl.rembol.jme3.shootapdf.images;

import com.jme3.texture.Texture2D;

import java.io.File;
import java.util.List;

interface ImageLoader {

    List<Texture2D> load(File file);

    boolean canLoad(File file);
}
