package com.eteks.sweethome3d.swing;
 
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
 
public class BeyeTranslucenceJPanel extends JPanel {
     
  float alphaValue = 0.1f;
  int compositeRule = AlphaComposite.SRC_OVER;
  AlphaComposite ac;

  BeyeTranslucenceJPanel() {
    setSize(300, 300);
    setBackground(Color.white);
  }

  public void paintComponent(Graphics g) {
    Graphics2D g2D = (Graphics2D) g;

    int w = getSize().width;
    int h = getSize().height;

    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D big = bi.createGraphics();

    ac = AlphaComposite.getInstance(compositeRule, alphaValue);

    big.setColor(Color.black);
    big.setComposite(ac);
    big.fill(new Rectangle2D.Double(0, 0, w, h));
    g2D.drawImage(bi, null, 0, 0);
    big.dispose();
  }
    
    public static void main(String[] args) {
      JFrame frame = new JFrame();
      frame.setSize(new Dimension(800, 600));
      frame.setPreferredSize(new Dimension(800, 600));
      
      JPanel jp = new JPanel();
      jp.setBackground(Color.red);
      frame.setContentPane(jp);
      
      JPanel jp1 = new JPanel(new GridBagLayout());
      jp1.setSize(200, 200);
      jp1.setOpaque(false);
      frame.add(jp1);
      
      BeyeTranslucenceJPanel tjp = new BeyeTranslucenceJPanel();
     // jp1.add(new JLabel("999999999999"));
      //jp1.add(new JButton("999999999999"));
      
      BeyeTranslucenceJPanel trasJpanel = new BeyeTranslucenceJPanel();
      trasJpanel.setBorder(BorderFactory.createLineBorder(Color.RED));
      trasJpanel.setPreferredSize(new Dimension(200, 500));
      trasJpanel.setSize(200, 500);
      trasJpanel.setOpaque(false);
      
      jp1.add(trasJpanel);
      
      
      
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
  }
}