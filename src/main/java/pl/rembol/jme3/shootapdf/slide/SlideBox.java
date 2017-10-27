package pl.rembol.jme3.shootapdf.slide;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

import java.util.Optional;

public class SlideBox extends Node {

    private static Material sideMaterial;

    private final Application application;

    public SlideBox(Application application,
                    Image image, Vector3f position, Vector2f size, Vector2f texOffset,
                    Vector2f texSize) {
        this.application = application;
        Texture2D boardTexture = new Texture2D(image);
        Material textureMaterial = new Material(application.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        textureMaterial.setTexture("DiffuseMap", boardTexture);

        Quad frontQuad = new Quad(size.x, size.y);
        frontQuad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{ texOffset.x, texOffset.y,
                texOffset.x + texSize.x, texOffset.y,
                texOffset.x + texSize.x, texOffset.y + texSize.y,
                texOffset.x, texOffset.y + texSize.y });
        Geometry front = new Geometry("front", frontQuad);
        front.setLocalTranslation(-size.x / 2, -size.y / 2, .5f);
        front.setMaterial(textureMaterial);
        attachChild(front);

        Quad backQuad = new Quad(size.x, size.y);
        backQuad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{ texOffset.x + texSize.x, texOffset.y,
                texOffset.x, texOffset.y,
                texOffset.x, texOffset.y + texSize.y,
                texOffset.x + texSize.x, texOffset.y + texSize.y });
        Geometry back = new Geometry("back", backQuad);
        back.setLocalTranslation(size.x / 2, -size.y / 2, -.5f);
        back.setLocalScale(new Vector3f(-1, 1, 1));
        back.setMaterial(textureMaterial);
        attachChild(back);

        Quad side = new Quad(size.y, 1);

        Geometry right = new Geometry("right", side);
        right.setLocalTranslation(size.x / 2, -size.y / 2, -.5f);
        right.rotate(0, FastMath.HALF_PI, FastMath.HALF_PI);
        right.setMaterial(getSideMaterial(application));
        attachChild(right);

        Geometry left = new Geometry("left", side);
        left.setLocalTranslation(-size.x / 2, -size.y / 2, .5f);
        left.rotate(0, -FastMath.HALF_PI, FastMath.HALF_PI);
        left.setMaterial(getSideMaterial(application));
        attachChild(left);

        side = new Quad(size.x, 1);
        Geometry top = new Geometry("top", side);
        top.setLocalTranslation(-size.x / 2, size.y / 2, .5f);
        top.rotate(-FastMath.HALF_PI, 0, 0);
        top.setMaterial(getSideMaterial(application));
        attachChild(top);

        Geometry bottom = new Geometry("bottom", side);
        bottom.setLocalTranslation(-size.x / 2, -size.y / 2, -.5f);
        bottom.rotate(FastMath.HALF_PI, 0, 0);
        bottom.setMaterial(getSideMaterial(application));
        attachChild(bottom);

        setLocalTranslation(
                position.x + size.x / 2,
                position.y + size.y / 2,
                position.z);

        RigidBodyControl rigidBodyControl = new RigidBodyControl(
                new BoxCollisionShape(new Vector3f(size.x / 2, size.y / 2, .5f)), 1f);
        rigidBodyControl.setKinematic(true);


        addControl(rigidBodyControl);
        application.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(rigidBodyControl);
        setShadowMode(RenderQueue.ShadowMode.Cast);

        addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                if (getSpatial().getWorldTranslation().getY() < -5f) {
                    remove();
                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        });
    }

    private Material getSideMaterial(Application application) {
        if (sideMaterial == null) {
            sideMaterial = new Material(application.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
            sideMaterial.setBoolean("UseMaterialColors", true);
            sideMaterial.setColor("Diffuse", ColorRGBA.Brown);
        }
        return sideMaterial;
    }

    public void setSlidePhysical() {
        Optional.ofNullable(getParent()).filter(Slide.class::isInstance).map(Slide.class::cast).ifPresent(Slide::setSlidePhysical);
    }

    void remove() {
        application.getStateManager().getState(BulletAppState.class).getPhysicsSpace().remove(getControl(RigidBodyControl.class));
        getParent().detachChild(this);
    }
}
