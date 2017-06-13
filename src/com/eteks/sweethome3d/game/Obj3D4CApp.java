/*
 * Copyright (c) 2009-2012 jMonkeyEngine All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.eteks.sweethome3d.game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.eteks.sweethome3d.model.HomeLight;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.system.awt.AwtPanel;
import com.jme3.system.awt.AwtPanelsContext;
import com.jme3.system.awt.PaintMode;

/**
 * Sample 3 - how to load an OBJ model, and OgreXML model, a material/texture,
 * or text.
 */
public class Obj3D4CApp extends AbstractObj3DApp {
  /**
   * 初始化窗口宽度
   */
  public static final int             INIT_WINDOW_WIDTH  = 500;

  /**
   * 初始化窗口高度
   */
  public static final int             INIT_WINDOW_HEIGHT = 500;

  /**
   * 视角2
   */
  Camera                              cam2;

  /**
   * 视角3
   */
  Camera                              cam3;

  /**
   * 视角4
   */
  Camera                              cam4;

  ViewPort                            viewPort2;
  ViewPort                            viewPort3;
  ViewPort                            viewPort4;

  /**
   * 4个窗口
   */
  private static AwtPanel             panel, panel2, panel3, panel4;
  private static int                  panelsClosed       = 0;

  final private static CountDownLatch panelsAreReady     = new CountDownLatch(1);

  private Obj3D4CApp                  app;

  public Obj3D4CApp() {

  }

  public Obj3D4CApp(String objFilePath) {
    this.objFilePath = objFilePath;
  }

  public Obj3D4CApp(String objFilePath, List<HomeLight> homeLights) {
    this.objFilePath = objFilePath;
    this.homeLights = homeLights;
    this.app = this;
  }

  public void start() {
    super.start();

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        /*
         * Sleep 2 seconds to ensure there's no race condition. The sleep is not
         * required for correctness.
         */
        try {
          Thread.sleep(2000);
        } catch (InterruptedException exception) {
          return;
        }

        final AwtPanelsContext ctx = (AwtPanelsContext)app.getContext();
        AppSettings settings = new AppSettings(false);
        settings.setSamples(8);
        settings.setCustomRenderer(AwtPanelsContext.class);
        // settings.setFrameRate(60);
        ctx.setSettings(settings);

        panel = ctx.createPanel(PaintMode.Accelerated);
        panel.setPreferredSize(new Dimension(INIT_WINDOW_WIDTH, INIT_WINDOW_HEIGHT));
        ctx.setInputSource(panel);

        panel2 = ctx.createPanel(PaintMode.Accelerated);
        panel2.setPreferredSize(new Dimension(INIT_WINDOW_WIDTH, INIT_WINDOW_HEIGHT));

        panel3 = ctx.createPanel(PaintMode.Accelerated);
        panel3.setPreferredSize(new Dimension(INIT_WINDOW_WIDTH, INIT_WINDOW_HEIGHT));

        panel4 = ctx.createPanel(PaintMode.Accelerated);
        panel4.setPreferredSize(new Dimension(INIT_WINDOW_WIDTH, INIT_WINDOW_HEIGHT));

        createWindowForPanel(panel, 300, "前");
        createWindowForPanel(panel2, 700, "左");
        createWindowForPanel(panel3, 100, "后");
        createWindowForPanel(panel4, 900, "右");
        /*
         * Both panels are ready.
         */
        panelsAreReady.countDown();
      }
    });
  }

  private void createWindowForPanel(AwtPanel panel, int location, String fName) {
    final JFrame frame = new JFrame(fName);
    // 全屏菜单
    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);

    JMenu listMenu = new JMenu("菜单");
    menuBar.add(listMenu);

    final JMenuItem fullScreenItem = new JMenuItem("全屏");
    listMenu.add(fullScreenItem);
    fullScreenItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (fullScreenItem.getText().equals("全屏")) {
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          Rectangle bounds = new Rectangle(screenSize);
          frame.setBounds(bounds);
          frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
          // frame.setVisible(true);

          fullScreenItem.setText("退出全屏");
        } else if (fullScreenItem.getText().equals("退出全屏")) {
          // frame.setUndecorated(false);
          frame.setSize(new Dimension(INIT_WINDOW_WIDTH, INIT_WINDOW_HEIGHT));

          fullScreenItem.setText("全屏");
        }
      }
    });

    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        if (++panelsClosed == 4) {
          app.stop();
        }
      }
    });
    frame.pack();
    frame.setLocation(location, 100);
    frame.setVisible(true);
  }

  /**
   * 
   */
  private void init4Panels() {
    /*
     * Wait until both AWT panels are ready.
     */
    try {
      panelsAreReady.await();
    } catch (InterruptedException exception) {
      throw new RuntimeException("Interrupted while waiting for panels", exception);
    }

    panel.attachTo(true, viewPort);
    guiViewPort.setClearFlags(false, false, false);
    // guiViewPort.setClearFlags(true, true, true);
    panel.attachTo(false, guiViewPort);

    ViewPort guiViewPort2 = renderManager.createPostView("Gui 2", cam2);
    guiViewPort2.setClearFlags(false, false, false);
    panel2.attachTo(true, viewPort2);
    panel2.attachTo(false, guiViewPort2);

    ViewPort guiViewPort3 = renderManager.createPostView("Gui 3", cam3);
    guiViewPort2.setClearFlags(false, false, false);
    panel3.attachTo(true, viewPort3);
    panel3.attachTo(false, guiViewPort3);

    ViewPort guiViewPort4 = renderManager.createPostView("Gui 4", cam4);
    guiViewPort2.setClearFlags(false, false, false);
    panel4.attachTo(true, viewPort4);
    panel4.attachTo(false, guiViewPort4);
  }

  /**
   * 初始化4台相机
   */
  private void init4Cameras() {
    // 设置默认相机的位置和参观者一致（基础方向：前）
    cam.setLocation(player.getPhysicsLocation());
    cam.setFrustum(1, 4000, -0.7071f, 0.7071f, 0.7071f, -0.7071f);
    viewPort.setClearFlags(true, true, true);

    // Setup second view
    cam2 = cam.clone();
    // cam2.setViewPort(0f, 0.5f, 0f, 0.5f);

    viewPort2 = renderManager.createMainView("左", cam2);
    viewPort2.setClearFlags(true, true, true);
    viewPort2.attachScene(rootNode);

    // Setup third view
    cam3 = cam.clone();
    // cam3.setViewPort(0f, .5f, .5f, 1f);

    viewPort3 = renderManager.createMainView("后", cam3);
    viewPort3.setClearFlags(true, true, true);
    viewPort3.attachScene(rootNode);

    // Setup fourth view
    cam4 = cam.clone();
    // cam4.setViewPort(.5f, 1f, .5f, 1f);

    viewPort4 = renderManager.createMainView("右", cam4);
    viewPort4.setClearFlags(true, true, true);
    viewPort4.attachScene(rootNode);

    // test multiview for gui
    // guiViewPort.getCamera().setViewPort(.5f, 1f, .5f, 1f);

    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    int numSamples = getContext().getSettings().getSamples();
    if (numSamples > 0) {
      fpp.setNumSamples(6);
    }

    /* FXAA */
    FXAAFilter FXAA = new FXAAFilter();
    FXAA.setSubPixelShift(5.0f);
    FXAA.setReduceMul(5.0f);
    FXAA.setVxOffset(5.0f);
    fpp.addFilter(FXAA);
    FXAA.setEnabled(true);
    // viewPort.addProcessor(fpp);

    // Setup second gui view
    Camera guiCam2 = guiViewPort.getCamera().clone();
    guiCam2.setViewPort(0f, 0.5f, 0f, 0.5f);
    ViewPort guiViewPort2 = renderManager.createPostView("Gui 2", guiCam2);
    guiViewPort2.setClearFlags(false, false, false);
    guiViewPort2.attachScene(guiViewPort.getScenes().get(0));
  }

  /**
   * 初始化设置
   * @return
   */
  @Override
  public AppSettings initAppSettings() {
    appSettings = new AppSettings(true);
    appSettings.setSamples(4);
    appSettings.setTitle(appTitle);
    appSettings.setCustomRenderer(AwtPanelsContext.class);
    //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    appSettings.setWidth(INIT_WINDOW_WIDTH);
    appSettings.setHeight(INIT_WINDOW_HEIGHT);
    return appSettings;
  }

  /**
   * 初始化设置
   * @param width 宽度
   * @param height 高度
   * @param samples 抗锯齿度
   * @return
   */
  @Override
  public AppSettings initAppSettings(int width, int height, int samples) {
    appSettings = new AppSettings(true);
    appSettings.setSamples(samples);
    appSettings.setCustomRenderer(AwtPanelsContext.class);
    appSettings.setWidth(width);
    appSettings.setHeight(height);
    appSettings.setTitle(appTitle);
    return appSettings;
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
    cam2.setLocation(player.getPhysicsLocation());
    cam3.setLocation(player.getPhysicsLocation());
    cam4.setLocation(player.getPhysicsLocation());

    float y = cam.getRotation().getY();
    // 计算角度
    double yAngle = this.sinAngle(y);
    // 获得X方向的值，旋转角度问题60度-90度-60度-0度 - -60度，存在重叠角度，根据X方向重新计算
    float xDirect = cam.getDirection().getX();

    // 默认计算
    double y2 = yAngle + 90 / 2;
    double y3 = yAngle + 180 / 2;
    double y4 = yAngle - 90 / 2;

    if (xDirect < 0 && yAngle <= 90 && yAngle >= 60) {
      y2 = -yAngle + 90 / 2;
      y3 = -yAngle + 180 / 2;
      y4 = -yAngle - 90 / 2;
    }

    // 计算其他相机旋转
    cam2.setRotation(new Quaternion(0.0f, (float)sin(y2), 0.0f, (float)cos(y2)));
    cam3.setRotation(new Quaternion(0.0f, (float)sin(y3), 0.0f, (float)cos(y3)));
    cam4.setRotation(new Quaternion(0.0f, (float)sin(y4), 0.0f, (float)cos(y4)));

  }

  private double sin(double angle) {
    return Math.sin(Math.PI * angle / 180);
  }

  private double cos(double angle) {
    return Math.cos(Math.PI * angle / 180);
  }

  /**
   * 通过正弦值求角度
   * 
   * @param sin
   * @return
   */
  private double sinAngle(double sin) {
    return Math.asin(sin) * 180 / Math.PI;
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
    al.setColor(ColorRGBA.White.mult(0.7f));

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
    // 4台相机
    init4Cameras();

    // 初始化面板
    init4Panels();

    // 灯光
    addLight();

    // 环境光+环境光
    addLight2();
  }

}
