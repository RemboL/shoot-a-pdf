package pl.rembol.jme3.shootapdf.slide;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.texture.Image;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlideManager {

    private final SimpleApplication simpleApplication;

    private final List<Image> images;

    private Map<Integer, Slide> slides = new HashMap<>();

    private int currentSlide = 0;

    private boolean controlsEnabled = false;

    public SlideManager(SimpleApplication simpleApplication, List<Image> images) {
        this.simpleApplication = simpleApplication;
        this.images = images;
        initSlide(0);

        setUpKeys();
    }

    public void setControlsEnabled(boolean controlsEnabled) {
        this.controlsEnabled = controlsEnabled;
    }

    private void setUpKeys() {

        ActionListener nextSlideAction = (name, isPressed, tpf) -> {
            if (isPressed && controlsEnabled) {
                showNextSlide();
            }
        };

        ActionListener prevSlideAction = (name, isPressed, tpf) -> {
            if (isPressed && controlsEnabled) {
                showPreviousSlide();
            }
        };

        simpleApplication.getInputManager().addMapping("NextSlide", new KeyTrigger(KeyInput.KEY_RIGHT));
        simpleApplication.getInputManager().addMapping("PrevSlide", new KeyTrigger(KeyInput.KEY_LEFT));
        simpleApplication.getInputManager().addListener(nextSlideAction, "NextSlide");
        simpleApplication.getInputManager().addListener(prevSlideAction, "PrevSlide");
    }

    private void showNextSlide() {
        if (currentSlide < images.size() - 1) {
            currentSlide++;
            initSlide(currentSlide);
        }
    }

    private void showPreviousSlide() {
        if (currentSlide > 0) {
            currentSlide--;
            initSlide(currentSlide);
        }
    }

    private void initSlide(Integer id) {
        clearSlide(id);
        Slide slide = new Slide(simpleApplication, images.get(id), new Vector3f(0f, 0.5f, (id - images.size()) * 4f));
        slides.put(id, slide);
        simpleApplication.getRootNode().attachChild(slide);
    }

    private void clearSlide(Integer id) {
        if (slides.get(id) != null) {
            slides.get(id).remove();
        }
        slides.remove(id);
    }
}
