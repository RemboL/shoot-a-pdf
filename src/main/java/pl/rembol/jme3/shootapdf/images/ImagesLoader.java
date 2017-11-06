package pl.rembol.jme3.shootapdf.images;

import com.jme3.app.SimpleApplication;
import com.jme3.texture.Texture2D;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ImagesLoader {

    private List<ImageLoader> imageLoaders;

    public ImagesLoader(SimpleApplication simpleApplication) {
        imageLoaders = new ArrayList<>();

        imageLoaders.add(new PdfLoader());
        imageLoaders.add(new ImageIOLoader());
        imageLoaders.add(new GifLoader(simpleApplication));
        imageLoaders.add(new DirectoryLoader(this));
    }

    public List<Texture2D> loadImages(List<File> files) {
        return files.stream()
                .map(this::loadImages)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Texture2D> loadImages(File file) {
        return imageLoaders
                .stream()
                .filter(imageLoader -> imageLoader.canLoad(file))
                .findFirst()
                .map(imageLoader -> imageLoader.load(file))
                .orElse(Collections.emptyList());
    }

}
