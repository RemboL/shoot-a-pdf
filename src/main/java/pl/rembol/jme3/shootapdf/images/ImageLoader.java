package pl.rembol.jme3.shootapdf.images;

import java.io.File;
import java.util.List;

import pl.rembol.jme3.shootapdf.slide.SlideFactory;

interface ImageLoader {

    List<SlideFactory> load(File file);

    boolean canLoad(File file);
}
