/*
 * VideoPanel.java 15 fevr. 2010
 *
 * Sweet Home 3D, Copyright (c) 2010 Emmanuel PUYBARET / eTeks <info@eteks.com>
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
package com.eteks.sweethome3d.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InterruptedIOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.ObserverCamera;
import com.eteks.sweethome3d.model.Selectable;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.tools.OperatingSystem;

/**
 * A panel used for Navigation creation. 
 * @author Emmanuel Puybaret
 */
public class NavigationPlanPanel extends JPanel{
  private PlanComponent         planComponent; 

  public NavigationPlanPanel(Home home, UserPreferences preferences) {
    super(new GridBagLayout());
    createComponents(home, preferences);
    layoutComponents();    
  }

  /**
   * Creates and initializes components.
   */
  private void createComponents(final Home home, 
                                final UserPreferences preferences) {
    final Dimension preferredSize = new Dimension(getToolkit().getScreenSize().width <= 1024 ? 324 : 404, 404);
    home.getCompass().setVisible(false);
    this.planComponent = new PlanComponent(home, preferences, null) {
        private void updateScale() {
          if (getWidth() > 0 && getHeight() > 0) {
            // Adapt scale to always view the home  
            float oldScale = getScale();
            Dimension preferredSize = super.getPreferredSize();
            Insets insets = getInsets();
            float planWidth = (preferredSize.width - insets.left - insets.right) / oldScale;
            float planHeight = (preferredSize.height - insets.top - insets.bottom) / oldScale;          
            setScale(Math.min((getWidth() - insets.left - insets.right) / planWidth, 
                (getHeight() - insets.top - insets.bottom) / planHeight));
          }
        }
        
        @Override
        public Dimension getPreferredSize() {
          return preferredSize;
        }
        
        @Override
        public void revalidate() {
          super.revalidate();
          updateScale();
        }

        @Override
        public void setBounds(int x, int y, int width, int height) {
          super.setBounds(x, y, width, height);
          updateScale();
        }
        
        
        @Override
        protected Rectangle2D getItemBounds(Graphics g, Selectable item) {
          if (item instanceof ObserverCamera) {
            return new Rectangle2D.Float(((ObserverCamera)item).getX() - 1, ((ObserverCamera)item).getY() - 1, 2, 2);
          } else {
            return super.getItemBounds(g, item);
          }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
          Graphics2D g2D = (Graphics2D)g;
          g2D.setColor(new Color(255, 255, 255, 233));
          g2D.fillRect(0, 0, getWidth(), getHeight());
          super.paintComponent(g);
        }

        @Override
        protected void paintHomeItems(Graphics g, float planScale, Color backgroundColor, Color foregroundColor,
                                      PaintMode paintMode) throws InterruptedIOException {
          Graphics2D g2D = (Graphics2D)g;
          Composite oldComposite = g2D.getComposite();
          g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
          super.paintHomeItems(g, planScale, backgroundColor, foregroundColor, paintMode);
          
          // Paint recorded camera path
          g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
          g2D.setColor(getSelectionColor());
          g2D.setComposite(oldComposite);
        }
      };
    this.planComponent.setSelectedItemsOutlinePainted(false);
    this.planComponent.setBackgroundPainted(false);
    this.planComponent.setBorder(BorderFactory.createEtchedBorder());
  }
  

  /**
   * Preferences property listener bound to this panel with a weak reference to avoid
   * strong link between user preferences and this panel.  
   */
  public static class LanguageChangeListener implements PropertyChangeListener {
    private final WeakReference<NavigationPlanPanel> videoPanel;

    public LanguageChangeListener(NavigationPlanPanel videoPanel) {
      this.videoPanel = new WeakReference<NavigationPlanPanel>(videoPanel);
    }

    public void propertyChange(PropertyChangeEvent ev) {
      // If video panel was garbage collected, remove this listener from preferences
      NavigationPlanPanel videoPanel = this.videoPanel.get();
      UserPreferences preferences = (UserPreferences)ev.getSource();
      if (videoPanel == null) {
        preferences.removePropertyChangeListener(UserPreferences.Property.LANGUAGE, this);
      } else {
        videoPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
      }
    }
  }

  /**
   * Layouts panel components in panel with their labels. 
   */
  private void layoutComponents() {
    int labelAlignment = OperatingSystem.isMacOSX() 
        ? GridBagConstraints.LINE_END
        : GridBagConstraints.LINE_START;
    
    JPanel panel = new JPanel(new GridBagLayout());
    // First row
    add(panel, new GridBagConstraints(
        0, 0, 4, 1, 2, 1, labelAlignment, 
        GridBagConstraints.BOTH, new Insets(0, 0, 1, 0), 0, 0));
    
    // second row
    add(this.planComponent, new GridBagConstraints(
        0, 1, 4, 1, 1, 1, labelAlignment, 
        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
  }
}
