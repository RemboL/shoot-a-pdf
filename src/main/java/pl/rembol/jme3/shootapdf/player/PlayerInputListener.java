package pl.rembol.jme3.shootapdf.player;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;

public class PlayerInputListener implements AnalogListener, ActionListener {

    private final SimpleApplication simpleApplication;

    private final Player player;

    public PlayerInputListener(SimpleApplication simpleApplication, Player player) {
        this.simpleApplication = simpleApplication;
        this.player = player;

        setUpKeys();
    }

    private void setUpKeys() {
        simpleApplication.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        simpleApplication.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        simpleApplication.getInputManager().addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        simpleApplication.getInputManager().addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        simpleApplication.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        simpleApplication.getInputManager().addListener(this, "Left");
        simpleApplication.getInputManager().addListener(this, "Right");
        simpleApplication.getInputManager().addListener(this, "Up");
        simpleApplication.getInputManager().addListener(this, "Down");
        simpleApplication.getInputManager().addListener(this, "Jump");
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("Left")) {
            player.getControl(PlayerMovementControl.class).left(tpf);
        } else if (name.equals("Right")) {
            player.getControl(PlayerMovementControl.class).right(tpf);
        } else if (name.equals("Up")) {
            player.getControl(PlayerMovementControl.class).up(tpf);
        } else if (name.equals("Down")) {
            player.getControl(PlayerMovementControl.class).down(tpf);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Jump")) {
            if (isPressed) {
                player.getControl(PlayerMovementControl.class).jump();
            }
        }
    }
}
