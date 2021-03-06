package pl.rembol.jme3.shootapdf.images;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.jme3.app.SimpleApplication;
import pl.rembol.jme3.shootapdf.ImageRescaler;
import pl.rembol.jme3.shootapdf.slide.SlideFactory;

public class ImagesLoader {

    private List<ImageLoader> imageLoaders;

    public ImagesLoader(SimpleApplication simpleApplication) {
        imageLoaders = new ArrayList<>();
        
        ImageRescaler imageRescaler = new ImageRescaler(simpleApplication);

        imageLoaders.add(new PdfLoader(imageRescaler));
        imageLoaders.add(new ImageIOLoader(imageRescaler));
        imageLoaders.add(new GifLoader(imageRescaler));
        imageLoaders.add(new DirectoryLoader(this));
        imageLoaders.add(new VideoLoader(imageRescaler));
    }

    public List<SlideFactory> loadImages(List<File> files) {
        System.out.println("loading images: "+files.stream().map(File::getName).collect(Collectors.toList()));
        return files.stream()
                .map(this::loadImages)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<SlideFactory> loadImages(File file) {
        return imageLoaders
                .stream()
                .filter(imageLoader -> imageLoader.canLoad(file))
                .findFirst()
                .map(imageLoader -> imageLoader.load(file))
                .orElse(Collections.emptyList());
    }

}
