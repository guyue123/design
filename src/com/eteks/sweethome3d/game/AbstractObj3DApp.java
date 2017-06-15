/*
 * AbstractObj3DApp.java 2017年6月13日
 *
 * Sweet Home 3D, Copyright (c) 2017 Emmanuel PUYBARET / eTeks <info@eteks.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.eteks.sweethome3d.game;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.List;

import com.eteks.sweethome3d.model.HomeLight;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.LightSource;
import com.jme3.app.ChaseCameraAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;

/**
 * @author Emmanuel Puybaret
 */
public abstract class AbstractObj3DApp extends SimpleApplication implements ActionListener {
  
  
  protected static final int SHADOWMAP_SIZE = 512;
  /**
   * 模型路径
   */
  protected String objFilePath;
  
  /**
   * 模型灯光信息
   */
  protected List<HomeLight> homeLights;
  
  protected AppSettings appSettings;
  
  // 上下左右
  protected boolean left=false,right=false,up=false,down=false;
  
  /**
   * 游戏者
   */
  protected PhysicsCharacter player;
  
  /**
   * 游戏者位置
   */
  protected float playerPosx = 234f;
  protected float playerPosy = 200f;
  protected float playerPosz = 400f;
  
  
  protected Vector3f walkDirection = new Vector3f();
  
  private ChaseCamera chaserCamara;
  
  /**
   *  地面
   */
  protected Geometry ground;
  protected Material matGroundU;
  protected Material matGroundL;
  
  protected BulletAppState bulletAppState;
  
  // 模型
  protected Spatial objModel;
  
  /**
   * 模型缩放比例
   */
  protected float localScale = 0.1f;
  
  protected int flyCamMoveSpeed = 20;
  
  /**
   * 窗口标题
   */
  protected String appTitle = "3D";
  
  /**
   * 游戏者高度
   */
  private float playerHeight = 170f;
  
  public void start() {
    super.start();
  }
  
  public void stop() {
    super.stop(true);
  }
  
  
  /**
   * 初始化场景
   */
  private void envInit() {
      Box b = new Box(1000, 2, 1000);
      b.scaleTextureCoordinates(new Vector2f(10, 10));
      ground = new Geometry("soil", b);
      ground.setLocalTranslation(0, 0, 0);
      matGroundU = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
      matGroundU.setColor("Color", ColorRGBA.Green);

      matGroundL = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
      Texture grass = assetManager.loadTexture("resources/Textures/Terrain/splat/grass.jpeg");
      grass.setWrap(WrapMode.Repeat);
      matGroundL.setTexture("DiffuseMap", grass);

      ground.setMaterial(matGroundL);

      ground.setShadowMode(ShadowMode.Receive);
      rootNode.attachChild(ground);
      
      // 实体化地面
      getPhysicsSpace().add(ground);
      ground.addControl(new RigidBodyControl(0));

      Spatial sky = SkyFactory.createSky(assetManager, "resources/Scenes/Beach/FullskiesSunset0068.dds", false);
      sky.setLocalScale(350);
      rootNode.attachChild(sky);

  }
  
  public void onAction(String binding, boolean value, float tpf) {
      if (binding.equals("Lefts")) {
          if(value)
              left=true;
          else
              left=false;
      } else if (binding.equals("Rights")) {
          if(value)
              right=true;
          else
              right=false;
      } else if (binding.equals("Ups")) {
          if(value)
              up=true;
          else
              up=false;
      } else if (binding.equals("Downs")) {
          if(value)
              down=true;
          else
              down=false;
      } else if (binding.equals("Space")) {
          player.jump();
      }
  }
  
  protected PhysicsSpace getPhysicsSpace() {
      return bulletAppState.getPhysicsSpace();
  }

  public String getObjFilePath() {
    return this.objFilePath;
  }

  public void setObjFilePath(String objFilePath) {
    this.objFilePath = objFilePath;
  }

  public AppSettings getAppSettings() {
    return this.appSettings;
  }

  public void setAppSettings(AppSettings appSettings) {
    this.appSettings = appSettings;
  }

  public float getLocalScale() {
      return localScale;
  }

  public void setLocalScale(float localScale) {
      this.localScale = localScale;
  }
  
  public void setPlayerPosition(float x, float y, float z) {
      this.playerPosx = x;
      this.playerPosy = y;
      this.playerPosz = z;
  }
  
  /*    @Override
  public void update() {
      super.update();
      if (Display.wasResized()) {
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          int newWidth = Math.max(Display.getWidth(), screenSize.width);
          int newHeight = Math.max(Display.getHeight(), screenSize.height);
          reshape(newWidth, newHeight);
      }
  }*/
  
  
  
  @Override
  public void simpleInitApp() {
      beforeInitApp();
      bulletAppState = new BulletAppState();
      stateManager.attach(bulletAppState);
      //Display.setResizable(true);
      initChaseCameraAppState();
      
      flyCam.setEnabled(false);
      // 移动速度
      //flyCam.setMoveSpeed(flyCamMoveSpeed);
      
      
      // 初始化环境
      envInit();
      
      // 设置快捷键
      setupKeys();
      
      // 初始化模型
      initModel();
      
      rootNode.attachChild(objModel);
      // 实体化模型
      getPhysicsSpace().add(objModel);
      
      // 参观者对象
      initPlayer();
      // 实体化参观者
      getPhysicsSpace().add(player);
      
      afterInitApp();
      
  }
  
  protected abstract void beforeInitApp();

  protected abstract void afterInitApp();
  
  
  /**
   * 添加灯光
   */
  protected void addLight() {
    if (homeLights == null || homeLights.isEmpty()) {
      return;
    }

    for (HomeLight light : homeLights) {
      float lightPower = light.getPower();
      Level level = light.getLevel();
      if (light.isVisible() && lightPower > 0f && (level == null || level.isViewableAndVisible())) {
        float angle = light.getAngle();
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        for (LightSource lightSource : ((HomeLight)light).getLightSources()) {
          float lightRadius = lightSource.getDiameter() != null
              ? lightSource.getDiameter() * light.getWidth() / 2
              : 3.25f; // Default radius compatible with most lights available
                       // before version 3.0
          float power = 5 * lightPower * lightPower / (lightRadius * lightRadius);
          int lightColor = lightSource.getColor();

          LightAttribute la = new LightAttribute();
          la.setAngle(angle);

          la.setDeepth(light.getDepth());
          la.setWidth(light.getWidth());
          la.setHeight(light.getHeight());

          la.setDisplay(true);
          la.setFloor(1);

          la.setName(light.getName());
          la.setPower(lightPower);

          la.setPx(light.getX());
          la.setPy(light.getY());
          
          float hangheight = light.getElevation() + light.getLevel().getElevation();
          if (light.getLevel().getElevation() > 0) {
            hangheight += light.getLevel().getFloorThickness() + 2;
          }
          la.setHangHeight(hangheight);

          la.setRgbColor(lightSource.getColor());

          PointLight pl = new PointLight();
          ColorRGBA rgba = new ColorRGBA();

          pl.setColor(rgba.fromIntARGB(la.getRgbColor()).mult(la.getPower()));
          System.out.println(la);
          pl.setRadius(10f);
          
          Vector3f plLocation = new Vector3f(la.getPx() * localScale, la.getHangHeight() * localScale + 2.1f , la.getPy() * localScale);
          pl.setPosition(plLocation);
          rootNode.addLight(pl);
          
          // Dome b = new Dome(plLocation, 10, 100, 1);
          //new Sphere(5, 5, la.getDeepth() * localScale / 2f)
          
          Geometry lightMdl = new Geometry("Light", new Sphere(20, 100, la.getDeepth() * localScale / 2f));
          if (!la.isSphere()) {
            lightMdl = new Geometry("Light", new Box(la.getDeepth()* localScale/2, la.getHeight()* localScale/2, la.getWidth()* localScale/2));
          }
          
          Material mtl = assetManager.loadMaterial("Common/Materials/WhiteColor.j3m");
          //mtl.setColor("", rgba.fromIntARGB(la.getRgbColor()));
          lightMdl.setMaterial(mtl);
          lightMdl.getMesh().setStatic();
          lightMdl.setLocalTranslation(pl.getPosition());
          
          float y = lightMdl.getLocalRotation().getY();
          // 计算角度
          double yAngle = sinAngle(y);
          // 默认计算
          double y2 = yAngle + (la.getAngle()/Math.PI) * 180;
          
          lightMdl.setLocalRotation(new Quaternion(0.0f, -1, 0.0f, (float)cos(y2)));
          rootNode.attachChild(lightMdl);

          PointLightShadowRenderer plsr = new PointLightShadowRenderer(assetManager, SHADOWMAP_SIZE);
          plsr.setLight(pl);
          plsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
          plsr.setShadowIntensity(0.05f);
          plsr.setEdgesThickness(1);
          viewPort.addProcessor(plsr);

        }
      }
    }
  }
  
  /**
   * 触发鼠标旋转
   */
  private void initChaseCameraAppState() {
      final Node camTarget = new Node("CamTarget");
      rootNode.attachChild(camTarget);

      ChaseCameraAppState chaser = new ChaseCameraAppState();
      chaser.setTarget(camTarget);
      chaser.setMaxDistance(150);
      chaser.setDefaultDistance(70);
      chaser.setDefaultHorizontalRotation(FastMath.HALF_PI);
      chaser.setMinVerticalRotation(-FastMath.PI);
      chaser.setMaxVerticalRotation(FastMath.PI * 2);
      chaser.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
      stateManager.attach(chaser);
      
      chaserCamara = new ChaseCamera(cam, inputManager);
      chaserCamara.registerWithInput(inputManager);
      chaserCamara.setSmoothMotion(true);
      chaserCamara.setMaxDistance(50);
      chaserCamara.setDefaultDistance(50);
  }
  
  /**
   * 设置快捷键
   */
  private void setupKeys() {
      inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_A));
      inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
      inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_W));
      inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_S));
      inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
      inputManager.addListener(this, "Lefts");
      inputManager.addListener(this, "Rights");
      inputManager.addListener(this, "Ups");
      inputManager.addListener(this, "Downs");
      inputManager.addListener(this, "Space");
  }
  
  /**
   * 初始化模型
   */
  private void initModel() {
      File f = new File(objFilePath);
      assetManager.registerLocator(f.getParent(), FileLocator.class);
      objModel = assetManager.loadModel(f.getName());
      objModel.setLocalScale(localScale);
      objModel.setLocalTranslation(0, 2.1f, 0);
      objModel.setShadowMode(ShadowMode.CastAndReceive);
      objModel.addControl(new RigidBodyControl(0));
      rootNode.attachChild(objModel);
  }
  
  /**
   * 初始化参观者
   */
  private void initPlayer() {
      CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.1f, playerPosz * this.localScale, 1);
      player = new CharacterControl(capsuleShape, 0.5f);
      
      /*player = new PhysicsCharacter(new SphereCollisionShape(0.1f), .01f);*/
      player.setJumpSpeed(20);
      player.setFallSpeed(20);
      player.setGravity(30);

      player.setPhysicsLocation(new Vector3f(playerPosx * this.localScale, playerPosy * this.localScale, playerPosz * this.localScale));
  }
  
  /**
   * 运行
   */
  public void init() {
      this.setShowSettings(false);
      if (appSettings == null) {
          appSettings = initAppSettings();
      }
      this.setSettings(appSettings);
  }

  /**
   * 初始化设置
   * @return
   */
  public AppSettings initAppSettings() {
      appSettings = new AppSettings(true);
      appSettings.setSamples(4);
      appSettings.setTitle(appTitle);
      
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      appSettings.setWidth(screenSize.width);
      appSettings.setHeight(screenSize.height);
      return appSettings;
  }
  
  /**
   *  初始化设置
   * @param width 宽度
   * @param height 高度
   * @param samples 抗锯齿度
   * @return
   */
  public AppSettings initAppSettings(int width, int height, int samples) {
      appSettings = new AppSettings(true);
      appSettings.setSamples(samples);
      appSettings.setWidth(width);
      appSettings.setHeight(height);
      appSettings.setTitle(appTitle);
    return appSettings;
  }

  public float getPlayerPosx() {
      return playerPosx;
  }

  public void setPlayerPosx(float playerPosx) {
      this.playerPosx = playerPosx;
  }

  public float getPlayerPosy() {
      return playerPosy;
  }

  public void setPlayerPosy(float playerPosy) {
      this.playerPosy = playerPosy;
  }

  public float getPlayerPosz() {
      return playerPosz;
  }

  public void setPlayerPosz(float playerPosz) {
      this.playerPosz = playerPosz;
  }

  public int getFlyCamMoveSpeed() {
      return flyCamMoveSpeed;
  }

  public void setFlyCamMoveSpeed(int flyCamMoveSpeed) {
      this.flyCamMoveSpeed = flyCamMoveSpeed;
  }

  public List<HomeLight> getHomeLights() {
    return this.homeLights;
  }

  public void setHomeLights(List<HomeLight> homeLights) {
    this.homeLights = homeLights;
  }

  public String getAppTitle() {
    return this.appTitle;
  }

  public void setAppTitle(String appTitle) {
    this.appTitle = appTitle;
  }
  

  public static double sin(double angle) {
    return Math.sin(Math.PI * angle / 180);
  }

  public static double cos(double angle) {
    return Math.cos(Math.PI * angle / 180);
  }

  /**
   * 通过正弦值求角度
   * 
   * @param sin
   * @return
   */
  public static double sinAngle(double sin) {
    return Math.asin(sin) * 180 / Math.PI;
  }

  public float getPlayerHeight() {
    return this.playerHeight;
  }

  public void setPlayerHeight(float playerHeight) {
    this.playerHeight = playerHeight;
  }
}
