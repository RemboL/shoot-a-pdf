package pl.rembol.jme3.shootapdf.images;

import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class PdfLoader implements ImageLoader {
    public List<Texture2D> load(File file) {
        try (PDDocument doc = PDDocument.load(file)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            List<BufferedImage> pages = new ArrayList<>();
            for (int i = 0; i < doc.getNumberOfPages(); ++i) {
                pages.add(renderer.renderImage(i));
            }

            AWTLoader awtLoader = new AWTLoader();
            return pages.stream().map(
                    awtImage -> awtLoader.load(awtImage, true))
                    .map(Texture2D::new)
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
