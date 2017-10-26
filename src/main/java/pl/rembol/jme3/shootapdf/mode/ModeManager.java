package pl.rembol.jme3.shootapdf.mode;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import pl.rembol.jme3.shootapdf.player.Player;
import pl.rembol.jme3.shootapdf.slide.SlideManager;

public class ModeManager {

    public final SimpleApplication simpleApplication;

    private final Player player;

    private final SlideManager slideManager;

    private enum Mode {
        FPS, FPS_TO_PRESENTATION, PRESENTATION, PRESENTATION_TO_FPS
    }

    private Mode currentMode = Mode.FPS;

    public ModeManager(SimpleApplication simpleApplication, Player player, SlideManager slideManager) {
        this.simpleApplication = simpleApplication;
        this.player = player;
        this.slideManager = slideManager;

        setUpKeys();
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

    private void startSwitchingToPresentation() {
        currentMode = Mode.FPS_TO_PRESENTATION;
        player.setControlsEnabled(false);

        // TODO change this into an app state that makes a fluent conversion
        switchToPresentation();
    }

    private void switchToPresentation() {

        currentMode = Mode.PRESENTATION;
        slideManager.setControlsEnabled(true);
    }

    private void startSwitchingToFPS() {
        currentMode = Mode.PRESENTATION_TO_FPS;
        slideManager.setControlsEnabled(false);

        // TODO change this into an app state that makes a fluent conversion
        switchToFPS();
    }

    private void switchToFPS() {
        currentMode = Mode.FPS;
        player.setControlsEnabled(true);
    }
}
