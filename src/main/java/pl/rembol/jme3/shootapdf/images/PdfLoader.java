package pl.rembol.jme3.shootapdf.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import pl.rembol.jme3.shootapdf.ImageRescaler;
import pl.rembol.jme3.shootapdf.slide.SimpleTextureSlideFactory;
import pl.rembol.jme3.shootapdf.slide.SlideFactory;

class PdfLoader implements ImageLoader {
    
    private final ImageRescaler imageRescaler;

    PdfLoader(ImageRescaler imageRescaler) {
        this.imageRescaler = imageRescaler;
    }

    public List<SlideFactory> load(File file) {
        try (PDDocument doc = PDDocument.load(file)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            List<BufferedImage> pages = new ArrayList<>();
            for (int i = 0; i < doc.getNumberOfPages(); ++i) {
                pages.add(renderer.renderImage(i, 2f));
                System.out.println(String.format("pdf[%s/%d].res = %dx%d", 
                        file.getName(), i, pages.get(i).getWidth(), pages.get(i).getHeight()));
            }

            AWTLoader awtLoader = new AWTLoader();
            return pages.stream().map(
                    awtImage -> awtLoader.load(awtImage, true))
                    .map(Texture2D::new)
                    .map(texture2D -> new SimpleTextureSlideFactory(imageRescaler, texture2D))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean canLoad(File file) {
        return file.isFile() && file.getName().toLowerCase().endsWith(".pdf");
    }
}
