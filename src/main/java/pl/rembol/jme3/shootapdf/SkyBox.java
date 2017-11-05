package pl.rembol.jme3.shootapdf;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Sphere;

public class SkyBox extends Node {

    private final SimpleApplication application;

    public SkyBox(SimpleApplication application) {
        super("skybox");

        this.application = application;

        Sphere sphere = new Sphere(72, 72, -500f);
        Geometry skyboxGeometry = new Geometry("Box", sphere);
        Material skyboxMaterial = new Material(application.getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");
        skyboxMaterial.setTexture("ColorMap", application.getAssetManager().loadTexture("skybox.png"));
//        skyboxMaterial.setBoolean("UseMaterialColors", true);
        skyboxGeometry.setMaterial(skyboxMaterial);
        attachChild(skyboxGeometry);
        skyboxGeometry.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));

        application.getRootNode().attachChild(this);

        addControl(new FollowCameraControl());
    }

    private class FollowCameraControl extends AbstractControl {

        @Override
        protected void controlUpdate(float tpf) {
            this.getSpatial().setLocalTranslation(application.getCamera().getLocation());
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
        }

    }

}
