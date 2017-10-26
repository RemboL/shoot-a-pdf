package pl.rembol.jme3.shootapdf.player;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class PlayerMovementControl extends AbstractControl {

    private static final float velocity = 50f;

    private SimpleApplication simpleApplication;

    public PlayerMovementControl(SimpleApplication simpleApplication) {
        this.simpleApplication = simpleApplication;
    }

    @Override
    protected void controlUpdate(float tpf) {
        getSpatial().getControl(BetterCharacterControl.class).setViewDirection(simpleApplication.getCamera().getDirection());
        getSpatial().getControl(BetterCharacterControl.class).getWalkDirection().multLocal(.9f);

        simpleApplication.getCamera().setLocation(getSpatial().getWorldTranslation().add(Vector3f.UNIT_Y.mult(2f)));
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    void left(float tpf) {
        getSpatial().getControl(BetterCharacterControl.class).getWalkDirection()
                .addLocal(simpleApplication.getCamera().getLeft().clone().setY(0).mult(tpf * velocity));
    }

    void right(float tpf) {
        getSpatial().getControl(BetterCharacterControl.class).getWalkDirection()
                .addLocal(simpleApplication.getCamera().getLeft().clone().setY(0).mult(tpf * velocity * -1));
    }

    void up(float tpf) {
        getSpatial().getControl(BetterCharacterControl.class).getWalkDirection()
                .addLocal(simpleApplication.getCamera().getDirection().clone().setY(0).mult(tpf * velocity));
    }

    void down(float tpf) {
        getSpatial().getControl(BetterCharacterControl.class).getWalkDirection()
                .addLocal(simpleApplication.getCamera().getDirection().clone().setY(0).mult(tpf * velocity * -1));
    }

    void jump() {
        getSpatial().getControl(BetterCharacterControl.class).jump();
    }
}
