package pl.rembol.jme3.shootapdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PDFLoader {

    public List<BufferedImage> load(String fileLocation) {
        File file = new File(fileLocation);

        try (PDDocument doc = PDDocument.load(file)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            List<BufferedImage> pages = new ArrayList<>();
            for (int i = 0; i < doc.getNumberOfPages(); ++i) {
                pages.add(renderer.renderImage(i));
            }
            return pages;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
