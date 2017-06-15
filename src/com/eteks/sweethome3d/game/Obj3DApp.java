package com.eteks.sweethome3d.game;

import java.util.List;

import com.eteks.sweethome3d.model.HomeLight;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;

/**
 * Sample 3 - how to load an OBJ model, and OgreXML model, a material/texture,
 * or text.
 */
public class Obj3DApp extends AbstractObj3DApp {

  public Obj3DApp() {

  }

  public Obj3DApp(String objFilePath) {
    this.objFilePath = objFilePath;
  }

  public Obj3DApp(String objFilePath, List<HomeLight> homeLights) {
    this.objFilePath = objFilePath;
    this.homeLights = homeLights;
  }

  @Override
  public void simpleUpdate(float tpf) {
    Vector3f camDir = cam.getDirection().clone().multLocal(0.6f);
    Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
    walkDirection.set(0, 0, 0);
    if (left)
      walkDirection.addLocal(camLeft);
    if (right)
      walkDirection.addLocal(camLeft.negate());
    if (up)
      walkDirection.addLocal(camDir);
    if (down)
      walkDirection.addLocal(camDir.negate());
    player.setWalkDirection(walkDirection);
    cam.setLocation(player.getPhysicsLocation());
  }

  /**
   * 光线和阴影
   */
  private void addLight2() {
    // 太阳光
    DirectionalLight l = new DirectionalLight();
    l.setDirection(new Vector3f(-1, -2, -1));

    // 太阳光阴影
    DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 512 * 2, 4);
    dlsr.setLight(l);
    dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);

    rootNode.addLight(l);

    // 环境光
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(1f));

    rootNode.addLight(al);
  }

  @Override
  protected void beforeInitApp() {
    flyCam.setEnabled(false);
    // 移动速度
    // flyCam.setMoveSpeed(flyCamMoveSpeed);
  }

  @Override
  protected void afterInitApp() {
    // 灯光
    addLight();

    // 环境光+环境光
    addLight2();
  }

}
