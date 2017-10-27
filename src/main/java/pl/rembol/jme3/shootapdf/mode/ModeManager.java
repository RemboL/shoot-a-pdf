package pl.rembol.jme3.shootapdf.mode;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.shadow.AbstractShadowRenderer;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import org.apache.commons.lang3.tuple.Triple;
import pl.rembol.jme3.shootapdf.player.Player;
import pl.rembol.jme3.shootapdf.slide.SlideManager;

import java.util.ArrayList;
import java.util.List;

public class ModeManager {

    private final List<Triple<Float, Light, AbstractShadowRenderer>> presentationModeLights = new ArrayList<>();

    private final List<Triple<Float, Light, AbstractShadowRenderer>> fpsModeLights = new ArrayList<>();

    public final SimpleApplication simpleApplication;

    final Player player;

    final SlideManager slideManager;

    private enum Mode {
        FPS, FPS_TO_PRESENTATION, PRESENTATION, PRESENTATION_TO_FPS
    }

    private Mode currentMode = Mode.FPS;

    public ModeManager(SimpleApplication simpleApplication, Player player, SlideManager slideManager) {
        this.simpleApplication = simpleApplication;
        this.player = player;
        this.slideManager = slideManager;

        setUpKeys();

        setUpPresentationModeLights();
        setUpFpsModeLights();
    }

    private void setUpPresentationModeLights() {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(new ColorRGBA(.1f, .1f, .1f, 1f));
        simpleApplication.getRootNode().addLight(ambientLight);
        presentationModeLights.add(Triple.of(.1f, ambientLight, null));

        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setColor(new ColorRGBA(.9f, .9f, .9f, 1f));
        directionalLight.setDirection(new Vector3f(0f, -.1f, -1f).normalize());
        simpleApplication.getRootNode().addLight(directionalLight);
        DirectionalLightShadowRenderer renderer = new DirectionalLightShadowRenderer(simpleApplication.getAssetManager(), 1024, 1);
        renderer.setLight(directionalLight);
        presentationModeLights.add(Triple.of(.9f, directionalLight, renderer));
        simpleApplication.getViewPort().addProcessor(renderer);
    }

    private void setUpFpsModeLights() {
        for (int i = 1; i < 8; i+=2) {
            DirectionalLight light = new DirectionalLight();
            light.setDirection(new Vector3f(FastMath.sin(i * FastMath.TWO_PI / 8) * 20f, -20f, FastMath.cos(i * FastMath.TWO_PI / 8) * 20f).normalize());
            light.setColor(new ColorRGBA(.5f, .5f, .5f, 1f));
            simpleApplication.getRootNode().addLight(light);
            DirectionalLightShadowRenderer renderer = new DirectionalLightShadowRenderer(simpleApplication.getAssetManager(), 1024, 1);
            renderer.setLight(light);
            fpsModeLights.add(Triple.of(.5f, light, renderer));
            simpleApplication.getViewPort().addProcessor(renderer);
        }
    }

    private void setUpKeys() {

        ActionListener switchModeAction = (name, isPressed, tpf) -> {
            if (isPressed) {
                if (currentMode == Mode.FPS) {
                    startSwitchingToPresentation();
                    return;
                }
                if (currentMode == Mode.PRESENTATION) {
                    startSwitchingToFPS();
                }
            }
        };

        simpleApplication.getInputManager().addMapping("SwitchMode", new KeyTrigger(KeyInput.KEY_M));
        simpleApplication.getInputManager().addListener(switchModeAction, "SwitchMode");
    }

    void setLightDistribution(final float fps, final float presentation) {
        fpsModeLights.forEach(triple -> {
            triple.getMiddle().setColor(ColorRGBA.White.mult(triple.getLeft() * fps));
            if (triple.getRight() != null) {
                triple.getRight().setShadowIntensity(triple.getLeft() * fps);
            }
        });
        presentationModeLights.forEach(triple -> {
            triple.getMiddle().setColor(ColorRGBA.White.mult(triple.getLeft() * presentation));
            if (triple.getRight() != null) {
                triple.getRight().setShadowIntensity(triple.getLeft() * presentation);
            }
        });
    }

    private void startSwitchingToPresentation() {
        currentMode = Mode.FPS_TO_PRESENTATION;
        player.setControlsEnabled(false);
        slideManager.setControlsEnabled(false);

        simpleApplication.getStateManager().attach(new SwitchingToPresentationAppState(simpleApplication, this));
    }

    public void switchToPresentation() {

        currentMode = Mode.PRESENTATION;
        player.setControlsEnabled(false);
        slideManager.setControlsEnabled(true);
        slideManager.setCameraToSlides();
        setLightDistribution(0, 1);
    }

    private void startSwitchingToFPS() {
        currentMode = Mode.PRESENTATION_TO_FPS;
        player.setControlsEnabled(false);
        slideManager.setControlsEnabled(false);

        simpleApplication.getStateManager().attach(new SwitchingToFpsAppState(simpleApplication, this));
    }

    public void switchToFPS() {
        currentMode = Mode.FPS;
        player.setControlsEnabled(true);
        slideManager.setControlsEnabled(false);
        setLightDistribution(1, 0);
    }
}
