package pl.rembol.jme3.shootapdf.images;

import com.jme3.texture.Texture2D;

import java.util.List;

interface ImageLoader {

    List<Texture2D> load(String fileLocation);

    boolean canLoad(String fileLocation);
}
