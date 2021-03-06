package pl.rembol.jme3.shootapdf.slide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.shootapdf.player.Player;

public class SlideManager {

    private static final int CACHE_SIZE = 5;

    private final SimpleApplication simpleApplication;

    private final List<SlideFactory> slideFactories;

    private final Player player;

    private Map<Integer, Slide> slides = new HashMap<>();

    private int currentSlide = 0;

    private boolean controlsEnabled = true;

    private Vector3f viewPosition = Vector3f.ZERO;

    private final Vector2f slideSize;

    public SlideManager(SimpleApplication simpleApplication, List<SlideFactory> slideFactories, Player player) {
        this.simpleApplication = simpleApplication;
        this.slideFactories = slideFactories;
        this.player = player;

        slideSize = new Vector2f(simpleApplication.getCamera().getWidth(), simpleApplication.getCamera().getHeight()).normalize().mult(5f);

        if (!slideFactories.isEmpty()) {
            initSlide(0);
        }

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
        simpleApplication.getInputManager().addMapping("NextSlide", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        simpleApplication.getInputManager().addMapping("PrevSlide", new KeyTrigger(KeyInput.KEY_LEFT));
        simpleApplication.getInputManager().addListener(nextSlideAction, "NextSlide");
        simpleApplication.getInputManager().addListener(prevSlideAction, "PrevSlide");
    }

    private void showNextSlide() {
        if (currentSlide < slideFactories.size() - 1) {
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
        Slide slide = slideFactories.get(id).create(simpleApplication, new Vector3f(0f, 0f, (id - slideFactories.size()) * 4f), slideSize);
        slides.put(id, slide);

        Set<Integer> slidesToBeRemoved = slides.keySet().stream().filter(i -> i > currentSlide).collect(Collectors.toSet());
        if (slides.size() > CACHE_SIZE) {
            slidesToBeRemoved.addAll(slides.keySet().stream().filter(i -> Math.abs(i - currentSlide) > CACHE_SIZE).collect(Collectors.toSet()));
            slidesToBeRemoved.forEach(this::clearSlide);
        }
        slidesToBeRemoved.forEach(this::clearSlide);
        simpleApplication.getRootNode().attachChild(slide);
        viewPosition = new Vector3f(0, slideSize.y / 2, (id - slideFactories.size()) * 4f + .5f + (slideSize.x / simpleApplication.getCamera().getFrustumRight() / 4 * 0.468f * 4.25f)); // ratio achieved experimentally, 0.468 is somehow connected to atan(45deg) but 4.25 - no idea...
        if (controlsEnabled) {
            simpleApplication.getCamera().setLocation(viewPosition);
            player.getControl(BetterCharacterControl.class).warp(getViewPosition().clone().setY(0f));
            player.getControl(BetterCharacterControl.class).setViewDirection(Vector3f.UNIT_Z.negate());
        }
    }

    private void clearSlide(Integer id) {
        if (slides.get(id) != null) {
            slides.get(id).remove();
        }
        slides.remove(id);
    }

    public void setCameraToSlides() {
        simpleApplication.getCamera().setLocation(viewPosition);
        simpleApplication.getCamera().lookAtDirection(Vector3f.UNIT_Z.negate(), Vector3f.UNIT_Y);
    }

    public Vector3f getViewPosition() {
        return viewPosition;
    }
}
