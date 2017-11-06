package pl.rembol.jme3.shootapdf.images;

import com.jme3.texture.Texture2D;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DirectoryLoader implements ImageLoader {

    private final ImagesLoader imagesLoader;

    DirectoryLoader(ImagesLoader imagesLoader) {
        this.imagesLoader = imagesLoader;
    }

    @Override
    public List<Texture2D> load(File file) {
        return Stream.of(file.listFiles()).map(Collections::singletonList).map(imagesLoader::loadImages).flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public boolean canLoad(File file) {
        return file.isDirectory();
    }
}
