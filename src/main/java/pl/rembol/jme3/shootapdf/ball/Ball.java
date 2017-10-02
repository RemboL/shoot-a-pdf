package pl.rembol.jme3.shootapdf.ball;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

public class Ball extends Geometry {

    public Ball(Application application) {
        super("cannon ball", new Sphere(8, 12, 0.48f, true, false));
        Material material = new Material(application.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors", true);
        material.setColor("Diffuse", ColorRGBA.Brown);
        material.setColor("Specular", ColorRGBA.White);
        material.setFloat("Shininess", 64f);
        setMaterial(material);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(10f);
        addControl(rigidBodyControl);
        application.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(rigidBodyControl);
    }
}
