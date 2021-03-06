/*
 * SweetHome3DBootstrap.java 2 sept. 07
 *
 * Sweet Home 3D, Copyright (c) 2007 Emmanuel PUYBARET / eTeks <info@eteks.com>
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
package com.eteks.sweethome3d;

import java.awt.Font;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.eteks.sweethome3d.tools.ExtensionsClassLoader;

/**
 * This bootstrap class loads Sweet Home 3D application classes from jars in classpath 
 * or from extension jars stored as resources.
 * @author Emmanuel Puybaret
 */
public class SweetHome3DBootstrap {
  public static void main(String [] args) throws Exception {
    // 2017/02/12
    setLookFeel();
    
    Class sweetHome3DBootstrapClass = SweetHome3DBootstrap.class;
    List<String> extensionJarsAndDlls = new ArrayList<String>(Arrays.asList(new String [] {
        "iText-2.1.7.jar", // Jars included in Sweet Home 3D executable jar file 
        "freehep-vectorgraphics-svg-2.1.1b.jar",
        "sunflow-0.07.3i.jar",
        "jmf.jar",
        "batik-svgpathparser-1.7.jar",
        "jnlp.jar"}));
    
    String operatingSystemName = System.getProperty("os.name");
    String javaVersion = System.getProperty("java.version");
    String java7Prefix = "1.7.0_";
    if (operatingSystemName.startsWith("Mac OS X")) {
      if (javaVersion.startsWith("1.6")
          && System.getProperty("com.eteks.sweethome3d.deploymentInformation", "").startsWith("Java Web Start")) {

        System.exit(1);
      } else if (javaVersion.startsWith("1.5")
          || javaVersion.startsWith("1.6")) {
        extensionJarsAndDlls.addAll(Arrays.asList(new String [] {
            "j3dcore.jar", // Main Java 3D jars
            "vecmath.jar",
            "j3dutils.jar",
            "macosx/gluegen-rt.jar", // Mac OS X jars and DLLs for Java 5 or 6
            "macosx/jogl.jar",
            "macosx/libgluegen-rt.jnilib",
            "macosx/libjogl.jnilib",
            "macosx/libjogl_awt.jnilib",
            "macosx/libjogl_cg.jnilib"}));
      } else if (javaVersion.startsWith(java7Prefix)
          && javaVersion.length() >= java7Prefix.length() + 1
          && Character.isDigit(javaVersion.charAt(java7Prefix.length()))
          && (javaVersion.length() >= java7Prefix.length() + 2 // Test version on 2 digits
          && Character.isDigit(javaVersion.charAt(java7Prefix.length() + 1))
          && Integer.parseInt(javaVersion.substring(java7Prefix.length(), java7Prefix.length() + 2)) < 40
          || javaVersion.length() == java7Prefix.length() + 1 // Test whether version is on 1 digit (i.e. < 40)
          || !Character.isDigit(javaVersion.charAt(java7Prefix.length() + 1)))) {
        System.exit(1);
      } else { // Java >= 1.7.0_40    
        extensionJarsAndDlls.addAll(Arrays.asList(new String [] {
            "java3d-1.6/j3dcore.jar", // Mac OS X Java 3D 1.6 jars and DLLs
            "java3d-1.6/vecmath.jar",
            "java3d-1.6/j3dutils.jar",
            "java3d-1.6/gluegen-rt.jar", 
            "java3d-1.6/jogl-java3d.jar",
            "java3d-1.6/macosx/libgluegen-rt.jnilib",
            "java3d-1.6/macosx/libjogl_desktop.jnilib",
            "java3d-1.6/macosx/libnativewindow_awt.jnilib",
            "java3d-1.6/macosx/libnativewindow_macosx.jnilib"}));
        // Disable JOGL library loader
        System.setProperty("jogamp.gluegen.UseTempJarCache", "false");
      }
    } else { // Other OS
      if ("1.5.2".equals(System.getProperty("com.eteks.sweethome3d.j3d.version", "1.6"))
          || "d3d".equals(System.getProperty("j3d.rend", "jogl"))) {
        extensionJarsAndDlls.addAll(Arrays.asList(new String [] {
            "j3dcore.jar", // Main Java 3D jars
            "vecmath.jar",
            "j3dutils.jar"}));
        if ("64".equals(System.getProperty("sun.arch.data.model"))) {
          extensionJarsAndDlls.addAll(Arrays.asList(new String [] {
              "linux/x64/libj3dcore-ogl.so",    // Linux 64 bits DLL for Java 3D 1.5.2
              "windows/x64/j3dcore-ogl.dll"})); // Windows 64 bits DLL for Java 3D 1.5.2
        } else {
          extensionJarsAndDlls.addAll(Arrays.asList(new String [] {
              "linux/i386/libj3dcore-ogl.so", // Linux 32 bits DLLs
              "linux/i386/libj3dcore-ogl-cg.so", 
              "windows/i386/j3dcore-d3d.dll", // Windows 32 bits DLLs
              "windows/i386/j3dcore-ogl.dll",
              "windows/i386/j3dcore-ogl-cg.dll",
              "windows/i386/j3dcore-ogl-chk.dll"}));
        }
      } else {
        extensionJarsAndDlls.addAll(Arrays.asList(new String [] {
            "java3d-1.6/j3dcore.jar", // Java 3D 1.6 jars
            "java3d-1.6/vecmath.jar",
            "java3d-1.6/j3dutils.jar",
            "java3d-1.6/gluegen-rt.jar", 
            "java3d-1.6/jogl-java3d.jar"}));
        // Disable JOGL library loader
        System.setProperty("jogamp.gluegen.UseTempJarCache", "false");
        if ("64".equals(System.getProperty("sun.arch.data.model"))) {
          extensionJarsAndDlls.addAll(Arrays.asList(new String [] {
              "java3d-1.6/linux/amd64/libgluegen-rt.so", // Linux 64 bits DLLs for Java 3D 1.6
              "java3d-1.6/linux/amd64/libjogl_desktop.so",
              "java3d-1.6/linux/amd64/libnativewindow_awt.so",
              "java3d-1.6/linux/amd64/libnativewindow_x11.so",
              "java3d-1.6/windows/amd64/gluegen-rt.dll", // Windows 64 bits DLLs for Java 3D 1.6
              "java3d-1.6/windows/amd64/jogl_desktop.dll",
              "java3d-1.6/windows/amd64/nativewindow_awt.dll",
              "java3d-1.6/windows/amd64/nativewindow_win32.dll"}));
        } else {
          extensionJarsAndDlls.addAll(Arrays.asList(new String [] {
              "java3d-1.6/linux/i586/libgluegen-rt.so", // Linux 32 bits DLLs for Java 3D 1.6
              "java3d-1.6/linux/i586/libjogl_desktop.so",
              "java3d-1.6/linux/i586/libnativewindow_awt.so",
              "java3d-1.6/linux/i586/libnativewindow_x11.so",
              "java3d-1.6/windows/i586/gluegen-rt.dll", // Windows 32 bits DLLs for Java 3D 1.6
              "java3d-1.6/windows/i586/jogl_desktop.dll",
              "java3d-1.6/windows/i586/nativewindow_awt.dll",
              "java3d-1.6/windows/i586/nativewindow_win32.dll"}));
        }
      }
    }
    
    String [] applicationPackages = {
        "com.eteks.sweethome3d",
        "javax.media",
        "javax.vecmath",
        "com.sun.j3d",
        "com.sun.opengl",
        "com.sun.gluegen.runtime",
        "com.jogamp",
        "jogamp",
        "javax.media.opengl",
        "javax.media.nativewindow",
        "com.sun.media",
        "com.ibm.media",
        "jmpapps.util",
        "com.microcrowd.loader.java3d",
        "org.sunflow",
        "org.apache.batik"};
    String applicationClassName = "com.eteks.sweethome3d.SweetHome3D";
    ClassLoader java3DClassLoader = operatingSystemName.startsWith("Windows")
        ? new ExtensionsClassLoader(
            sweetHome3DBootstrapClass.getClassLoader(), 
            sweetHome3DBootstrapClass.getProtectionDomain(),
            extensionJarsAndDlls.toArray(new String [extensionJarsAndDlls.size()]), null, applicationPackages,
            // Use cache under Windows because temporary files tagged as deleteOnExit can't 
            // be deleted if they are still opened when program exits 
            new File(System.getProperty("java.io.tmpdir")), applicationClassName + "-cache-")  
        : new ExtensionsClassLoader(
            sweetHome3DBootstrapClass.getClassLoader(), 
            sweetHome3DBootstrapClass.getProtectionDomain(),
            extensionJarsAndDlls.toArray(new String [extensionJarsAndDlls.size()]), applicationPackages);      
    Class applicationClass = java3DClassLoader.loadClass(applicationClassName);
    Method applicationClassMain = 
        applicationClass.getMethod("main", Array.newInstance(String.class, 0).getClass());
    // Call application class main method with reflection
    applicationClassMain.invoke(null, new Object [] {args});
  }
  
  /**
   * 2017/02/12
   * @throws Exception
   */
  private static void setLookFeel() throws Exception {
    //设置本属性将改变窗口边框样式定义
    BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
    org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
    
    //改变InsetsUIResource参数的值即可实现
    UIManager.put("ToggleButton.margin", new javax.swing.plaf.InsetsUIResource(5,5,5,5));
        
    /** UIManager中UI字体相关的key */
    String[] DEFAULT_FONT  = new String[]{
        "Table.font"
        ,"TableHeader.font"
        ,"CheckBox.font"
        ,"Tree.font"
        ,"Viewport.font"
        ,"ProgressBar.font"
        ,"RadioButtonMenuItem.font"
        ,"ToolBar.font"
        ,"ColorChooser.font"
        ,"ToggleButton.font"
        ,"Panel.font"
        ,"TextArea.font"
        ,"Menu.font"
        ,"TableHeader.font"
        // ,"TextField.font"
        ,"OptionPane.font"
        ,"MenuBar.font"
        ,"Button.font"
        ,"Label.font"
        ,"PasswordField.font"
        ,"ScrollPane.font"
        ,"MenuItem.font"
        ,"ToolTip.font"
        ,"List.font"
        ,"EditorPane.font"
        ,"Table.font"
        ,"TabbedPane.font"
        ,"RadioButton.font"
        ,"CheckBoxMenuItem.font"
        ,"TextPane.font"
        ,"PopupMenu.font"
        ,"TitledBorder.font"
        ,"ComboBox.font"
    };
    // 调整默认字体
    for (int i = 0; i < DEFAULT_FONT.length; i++)
        UIManager.put(DEFAULT_FONT[i],new Font("微软雅黑", Font.PLAIN,12));
  }
}