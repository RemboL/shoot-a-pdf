package pl.rembol.jme3.shootapdf.player;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import pl.rembol.jme3.shootapdf.ball.Ball;

public class Player extends Node {

    private static final float velocity = 50f;

    private final SimpleApplication simpleApplication;

    private boolean controlsEnabled = true;

    public Player(SimpleApplication simpleApplication) {
        super("player");
        this.simpleApplication = simpleApplication;
        setLocalTranslation(0, 0, 10);
        BetterCharacterControl player = new BetterCharacterControl(1f, 2f, 1f);
        player.setJumpForce(new Vector3f(0, 5f, 0));
        player.setGravity(new Vector3f(0, 1f, 0));
        addControl(player);
        simpleApplication.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(player);

        PlayerMovementControl playerMovementControl = new PlayerMovementControl(simpleApplication);
        addControl(playerMovementControl);
        new PlayerInputListener(simpleApplication, this);
        simpleApplication.getRootNode().attachChild(this);

        setControlsEnabled(true);
    }

    public void setControlsEnabled(boolean controlsEnabled) {
        this.controlsEnabled = controlsEnabled;
        simpleApplication.getInputManager().setCursorVisible(!controlsEnabled);
    }

    void left(float tpf) {
        if (controlsEnabled) {
            getControl(BetterCharacterControl.class).getWalkDirection()
                    .addLocal(simpleApplication.getCamera().getLeft().clone().setY(0).mult(tpf * velocity));
        }
    }

    void right(float tpf) {
        if (controlsEnabled) {
            getControl(BetterCharacterControl.class).getWalkDirection()
                    .addLocal(simpleApplication.getCamera().getLeft().clone().setY(0).mult(-tpf * velocity));
        }
    }

    void forward(float tpf) {
        if (controlsEnabled) {
            getControl(BetterCharacterControl.class).getWalkDirection()
                    .addLocal(simpleApplication.getCamera().getDirection().clone().setY(0).mult(tpf * velocity));
        }
    }

    void back(float tpf) {
        if (controlsEnabled) {
            getControl(BetterCharacterControl.class).getWalkDirection()
                    .addLocal(simpleApplication.getCamera().getDirection().clone().setY(0).mult(-tpf * velocity));
        }
    }

    void jump() {
        if (controlsEnabled) {
            getControl(BetterCharacterControl.class).jump();
        }
    }


    boolean isControlEnabled() {
        return controlsEnabled;
    }

    void lookDown(float tpf) {
        if (controlsEnabled) {
            if (simpleApplication.getCamera().getDirection().y < 0.8f) {
                simpleApplication.getCamera().setRotation(
                        simpleApplication.getCamera().getRotation().mult(new Quaternion().fromAngleAxis(tpf, Vector3f.UNIT_X))
                );
            }
        }
    }

    void lookUp(float tpf) {
        if (controlsEnabled) {
            if (simpleApplication.getCamera().getDirection().y > -0.8f) {
                simpleApplication.getCamera().setRotation(
                        simpleApplication.getCamera().getRotation().mult(new Quaternion().fromAngleAxis(-tpf, Vector3f.UNIT_X))
                );
            }
        }
    }

    void lookLeft(float tpf) {
        if (controlsEnabled) {
            simpleApplication.getCamera().setRotation(
                    new Quaternion().fromAngleAxis(-tpf, Vector3f.UNIT_Y).mult(simpleApplication.getCamera().getRotation())
            );
        }
    }

    void lookRight(float tpf) {
        if (controlsEnabled) {
            simpleApplication.getCamera().setRotation(
                    new Quaternion().fromAngleAxis(tpf, Vector3f.UNIT_Y).mult(simpleApplication.getCamera().getRotation())
            );
        }
    }

    void shootBall() {
        if (controlsEnabled) {
            Ball ball = new Ball(simpleApplication);
            simpleApplication.getRootNode().attachChild(ball);
            ball.getControl(RigidBodyControl.class).setPhysicsLocation(simpleApplication.getCamera().getLocation().add(simpleApplication.getCamera().getDirection().normalize().mult(2f)));
            ball.getControl(RigidBodyControl.class).setLinearVelocity(simpleApplication.getCamera().getDirection().normalize().mult(100f));
        }
    }

    public Vector3f getCameraPosition() {
        return getWorldTranslation().add(Vector3f.UNIT_Y.mult(1f));
    }
}
