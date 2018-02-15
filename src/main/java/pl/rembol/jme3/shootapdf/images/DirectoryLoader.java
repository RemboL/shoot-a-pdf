package pl.rembol.jme3.shootapdf.images;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pl.rembol.jme3.shootapdf.slide.SlideFactory;

class DirectoryLoader implements ImageLoader {

    private final ImagesLoader imagesLoader;

    DirectoryLoader(ImagesLoader imagesLoader) {
        this.imagesLoader = imagesLoader;
    }

    @Override
    public List<SlideFactory> load(File file) {
        return Stream.of(file.listFiles()).map(Collections::singletonList).map(imagesLoader::loadImages).flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public boolean canLoad(File file) {
        return file.isDirectory();
    }
}
