package pl.rembol.jme3.shootapdf.player;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;

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
        simpleApplication.getInputManager().addMapping("ShootBall", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        simpleApplication.getInputManager().addMapping("LookDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        simpleApplication.getInputManager().addMapping("LookUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        simpleApplication.getInputManager().addMapping("LookLeft", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        simpleApplication.getInputManager().addMapping("LookRight", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        simpleApplication.getInputManager().addListener(this, "Left");
        simpleApplication.getInputManager().addListener(this, "Right");
        simpleApplication.getInputManager().addListener(this, "Up");
        simpleApplication.getInputManager().addListener(this, "Down");
        simpleApplication.getInputManager().addListener(this, "Jump");
        simpleApplication.getInputManager().addListener(this, "ShootBall");
        simpleApplication.getInputManager().addListener(this, "LookDown");
        simpleApplication.getInputManager().addListener(this, "LookUp");
        simpleApplication.getInputManager().addListener(this, "LookLeft");
        simpleApplication.getInputManager().addListener(this, "LookRight");
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("Left")) {
            player.left(tpf);
        } else if (name.equals("Right")) {
            player.right(tpf);
        } else if (name.equals("Up")) {
            player.forward(tpf);
        } else if (name.equals("Down")) {
            player.back(tpf);
        } else if (name.equals("LookDown")) {
            player.lookDown(value);
        } else if (name.equals("LookUp")) {
            player.lookUp(value);
        } else if (name.equals("LookLeft")) {
            player.lookLeft(value);
        } else if (name.equals("LookRight")) {
            player.lookRight(value);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            if (name.equals("Jump")) {
                player.jump();
            } else if (name.equals("ShootBall")) {
                player.shootBall();
            }
        }
    }
}
