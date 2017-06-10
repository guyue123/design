/*
 * FurnitureCatalogListPanel.java 10 janv 2010
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import com.eteks.sweethome3d.model.CatalogPieceOfFurniture;
import com.eteks.sweethome3d.model.CollectionEvent;
import com.eteks.sweethome3d.model.CollectionListener;
import com.eteks.sweethome3d.model.Content;
import com.eteks.sweethome3d.model.FurnitureCatalog;
import com.eteks.sweethome3d.model.FurnitureCategory;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.tools.OperatingSystem;
import com.eteks.sweethome3d.viewcontroller.FurnitureCatalogController;
import com.eteks.sweethome3d.viewcontroller.View;

/**
 * A furniture catalog view that displays furniture in a Tab view, with a combo and search text field.
 * @author hhx
 */
@SuppressWarnings("serial")
public class FurnitureCatalogTabbedPanel extends JPanel implements View {
  private JLabel                categoryFilterLabel;
  private JComboBox             categoryFilterComboBox;
  private JLabel                searchLabel;
  private JTextField            searchTextField;
  private JTabbedPane           tabbedPane;

  /**
   * Creates a panel that displays <code>catalog</code> furniture in a list with a filter combo box
   * and a search field.
   */
  public FurnitureCatalogTabbedPanel(FurnitureCatalog catalog,
                                   UserPreferences preferences,
                                   FurnitureCatalogController controller) {
    super(new GridBagLayout());
    createComponents(catalog, preferences, controller);
    setMnemonics(preferences);
    layoutComponents();
  }

  /**
   * Creates the components displayed by this panel.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void createComponents(FurnitureCatalog catalog,
                                final UserPreferences preferences, 
                                final FurnitureCatalogController controller) {
    
    setLayout(new BorderLayout());
    
    // create tab 
    tabbedPane = new JTabbedPane();
    add(tabbedPane, BorderLayout.CENTER);
    tabbedPane.setTabPlacement(JTabbedPane.LEFT);
      
    this.categoryFilterLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences,
        FurnitureCatalogTabbedPanel.class, "categoryFilterLabel.text"));    
    List<FurnitureCategory> categories = new ArrayList<FurnitureCategory>();
    categories.add(null);
    categories.addAll(catalog.getCategories());
    this.categoryFilterComboBox = new JComboBox(new DefaultComboBoxModel(categories.toArray())) {
        @Override
        public Dimension getMinimumSize() {
          return new Dimension(60, super.getMinimumSize().height);
        }
      };
    this.categoryFilterComboBox.setMaximumRowCount(20);
    this.categoryFilterComboBox.setRenderer(new DefaultListCellRenderer() {
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
          if (value == null) {
            return super.getListCellRendererComponent(list, 
                preferences.getLocalizedString(FurnitureCatalogTabbedPanel.class, "categoryFilterComboBox.noCategory"), 
                index, isSelected, cellHasFocus);
          } else {
            return super.getListCellRendererComponent(list, 
                ((FurnitureCategory)value).getName(), index, isSelected, cellHasFocus);
          }
        }
      });

    // --搜索框
    this.searchTextField.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "deleteContent");
    this.searchTextField.getActionMap().put("deleteContent", new AbstractAction() {
        public void actionPerformed(ActionEvent ev) {
          searchTextField.setText("");
        }
      });
    if (OperatingSystem.isMacOSXLeopardOrSuperior()) {
      this.searchTextField.putClientProperty("JTextField.variant", "search");
    } 
    
    PreferencesChangeListener preferencesChangeListener = new PreferencesChangeListener(this);
    preferences.addPropertyChangeListener(UserPreferences.Property.LANGUAGE, preferencesChangeListener);
    catalog.addFurnitureListener(preferencesChangeListener);
  }
  
  /**
   * Language and catalog listener bound to this component with a weak reference to avoid
   * strong link between preferences and this component.  
   */
  private static class PreferencesChangeListener implements PropertyChangeListener, CollectionListener<CatalogPieceOfFurniture> {
    private final WeakReference<FurnitureCatalogTabbedPanel> furnitureCatalogPanel;

    public PreferencesChangeListener(FurnitureCatalogTabbedPanel furnitureCatalogPanel) {
      this.furnitureCatalogPanel = new WeakReference<FurnitureCatalogTabbedPanel>(furnitureCatalogPanel);
    }

    public void propertyChange(PropertyChangeEvent ev) {
      // If panel was garbage collected, remove this listener from preferences
      FurnitureCatalogTabbedPanel furnitureCatalogPanel = this.furnitureCatalogPanel.get();
      UserPreferences preferences = (UserPreferences)ev.getSource();
      if (furnitureCatalogPanel == null) {
        preferences.removePropertyChangeListener(UserPreferences.Property.LANGUAGE, this);
      } else {
        furnitureCatalogPanel.categoryFilterLabel.setText(SwingTools.getLocalizedLabelText(preferences,
            FurnitureCatalogTabbedPanel.class, "categoryFilterLabel.text"));
        furnitureCatalogPanel.searchLabel.setText(SwingTools.getLocalizedLabelText(preferences,
            FurnitureCatalogTabbedPanel.class, "searchLabel.text"));
        furnitureCatalogPanel.setMnemonics(preferences);
        // Categories listed in combo box are updated through collectionChanged
      }
    }
    
    public void collectionChanged(CollectionEvent<CatalogPieceOfFurniture> ev) {
      // If panel was garbage collected, remove this listener from catalog
      FurnitureCatalogTabbedPanel furnitureCatalogPanel = this.furnitureCatalogPanel.get();
      FurnitureCatalog catalog = (FurnitureCatalog)ev.getSource();
      if (furnitureCatalogPanel == null) {
        catalog.removeFurnitureListener(this);
      } else {
        DefaultComboBoxModel model = 
            (DefaultComboBoxModel)furnitureCatalogPanel.categoryFilterComboBox.getModel();
        FurnitureCategory category = ev.getItem().getCategory();
        List<FurnitureCategory> categories = catalog.getCategories();
        if (!categories.contains(category)) {
          model.removeElement(category);
          furnitureCatalogPanel.categoryFilterComboBox.setSelectedIndex(0);
        } else if (model.getIndexOf(category) == -1) {
          model.insertElementAt(category, categories.indexOf(category) + 1);
        }
      }
    }
  }

  /**
   * Adds mouse listeners that will select only the piece under mouse cursor in the furniture list 
   * before the start of a drag operation, ensuring only one piece can be dragged at a time.
   */
  private void addDragListener(final JList catalogFurnitureList) {
    MouseInputAdapter mouseListener = new MouseInputAdapter() {
        private CatalogPieceOfFurniture exportedPiece;
  
        @Override
        public void mousePressed(MouseEvent ev) {
          this.exportedPiece = null;
          if (SwingUtilities.isLeftMouseButton(ev)
              && catalogFurnitureList.getSelectedValue() != null
              && catalogFurnitureList.getTransferHandler() != null) {
            int index = catalogFurnitureList.locationToIndex(ev.getPoint());
            if (index != -1) {
              this.exportedPiece = (CatalogPieceOfFurniture)catalogFurnitureList.getModel().getElementAt(index);
            }
          }
        }
        
        public void mouseDragged(MouseEvent ev) {
          if (this.exportedPiece != null) {
            if (catalogFurnitureList.getSelectedIndices().length > 1) {
              catalogFurnitureList.clearSelection();
              catalogFurnitureList.setSelectedValue(this.exportedPiece, false);
            }
            if (!OperatingSystem.isJavaVersionGreaterOrEqual("1.6")) {
              catalogFurnitureList.getTransferHandler().exportAsDrag(catalogFurnitureList, ev, DnDConstants.ACTION_COPY);
            }
            this.exportedPiece = null;
          }
        }
      };
    
    catalogFurnitureList.addMouseListener(mouseListener);
    catalogFurnitureList.addMouseMotionListener(mouseListener);
  }

  /**
   * Adds mouse listeners to the furniture list to modify selected furniture 
   * and manage links in piece information.
   */
  private void addMouseListeners(final JList catalogFurnitureList,
                                 final FurnitureCatalogController controller) {
    final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    MouseInputAdapter mouseListener = new MouseInputAdapter () {
        @Override
        public void mouseClicked(MouseEvent ev) {
          if (SwingUtilities.isLeftMouseButton(ev)) {
            if (ev.getClickCount() == 2) {
              int clickedPieceIndex = catalogFurnitureList.locationToIndex(ev.getPoint());
              if (clickedPieceIndex != -1) {
                controller.modifySelectedFurniture();
              }
            } else {
              URL url = getURLAt(ev.getPoint(), catalogFurnitureList);
              if (url != null) {
                SwingTools.showDocumentInBrowser(url);
              }
            }
          }
        }
        
        @Override
        public void mouseMoved(MouseEvent ev) {
          final URL url = getURLAt(ev.getPoint(), catalogFurnitureList);
          EventQueue.invokeLater(new Runnable() {                  
              public void run() {
                if (url != null) {
                  setCursor(handCursor);
                } else {
                  setCursor(Cursor.getDefaultCursor());
                }
              }
            });
        }

        private URL getURLAt(Point point, JList list) {
          int pieceIndex = list.locationToIndex(point);
          if (pieceIndex != -1) {
            CatalogPieceOfFurniture piece = (CatalogPieceOfFurniture)list.getModel().getElementAt(pieceIndex);
            String information = piece.getInformation();
            if (information != null) {
              JComponent rendererComponent = (JComponent)list.getCellRenderer().
                  getListCellRendererComponent(list, piece, pieceIndex, list.isSelectedIndex(pieceIndex), false);
              for (JEditorPane pane : SwingTools.findChildren(rendererComponent, JEditorPane.class)) {
                Rectangle cellBounds = list.getCellBounds(pieceIndex, pieceIndex);
                point.x -= cellBounds.x; 
                point.y -= cellBounds.y + pane.getY(); 
                if (point.x > 0 && point.y > 0) {
                  // Search in information pane if point is over a HTML link
                  int position = pane.viewToModel(point);
                  if (position > 1
                      && pane.getDocument() instanceof HTMLDocument) {
                    HTMLDocument hdoc = (HTMLDocument)pane.getDocument();
                    Element element = hdoc.getCharacterElement(position);
                    AttributeSet a = element.getAttributes();
                    AttributeSet anchor = (AttributeSet)a.getAttribute(HTML.Tag.A);
                    if (anchor != null) {
                      String href = (String)anchor.getAttribute(HTML.Attribute.HREF);
                      if (href != null) {
                        try {
                          return new URL(href);
                        } catch (MalformedURLException ex) {
                          // Ignore malformed URL
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          return null;
        }
      };
    catalogFurnitureList.addMouseListener(mouseListener);
    catalogFurnitureList.addMouseMotionListener(mouseListener);
  }

  /**
   * Sets components mnemonics and label / component associations.
   */
  private void setMnemonics(UserPreferences preferences) {
    if (!OperatingSystem.isMacOSX()) {
      this.categoryFilterLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(preferences.getLocalizedString(
          FurnitureCatalogTabbedPanel.class, "categoryFilterLabel.mnemonic")).getKeyCode());
      this.categoryFilterLabel.setLabelFor(this.categoryFilterComboBox);
      this.searchLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(preferences.getLocalizedString(
          FurnitureCatalogTabbedPanel.class, "searchLabel.mnemonic")).getKeyCode());
      this.searchLabel.setLabelFor(this.searchTextField);
    }
  }

  /**
   * Layouts the components displayed by this panel.
   */
  private void layoutComponents() {
    int labelAlignment = OperatingSystem.isMacOSX() 
        ? GridBagConstraints.LINE_END
        : GridBagConstraints.LINE_START;
    // First row
    Insets labelInsets = new Insets(0, 2, 5, 3);
    Insets componentInsets = new Insets(0, 2, 3, 0);
    if (!OperatingSystem.isMacOSX()) {
      labelInsets.top = 2;
      componentInsets.top = 2;
      componentInsets.right = 2;
    }
    add(this.categoryFilterLabel, new GridBagConstraints(
        0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, 
        GridBagConstraints.HORIZONTAL, labelInsets, 0, 0));
    add(this.categoryFilterComboBox, new GridBagConstraints(
        1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, 
        GridBagConstraints.HORIZONTAL, componentInsets, 0, 0));
    // Second row
    if (OperatingSystem.isMacOSXLeopardOrSuperior()) {
      add(this.searchTextField, new GridBagConstraints(
          0, 1, 2, 1, 0, 0, GridBagConstraints.LINE_START, 
          GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));
    } else { 
      add(this.searchLabel, new GridBagConstraints(
          0, 1, 1, 1, 0, 0, labelAlignment, 
          GridBagConstraints.NONE, labelInsets, 0, 0));
      add(this.searchTextField, new GridBagConstraints(
          1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, 
          GridBagConstraints.HORIZONTAL, componentInsets, 0, 0));
    }
    // Last row
    JScrollPane listScrollPane = new JScrollPane(null);
    listScrollPane.getVerticalScrollBar().addAdjustmentListener(
        SwingTools.createAdjustmentListenerUpdatingScrollPaneViewToolTip(listScrollPane));
    listScrollPane.setPreferredSize(new Dimension(250, 250));
    listScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    add(listScrollPane, 
        new GridBagConstraints(
        0, 2, 2, 1, 1, 1, GridBagConstraints.CENTER, 
        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    SwingTools.installFocusBorder(null);
    
    setFocusTraversalPolicyProvider(true);
    setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
        @Override
        public Component getDefaultComponent(Container aContainer) {
            EventQueue.invokeLater(new Runnable() {
              public void run() {
                // Return furniture list only at the first request  
                setFocusTraversalPolicyProvider(false);
              }
            });
          return null;
        }
      });
  }
  
  /**
   * Cell renderer for the furniture list.
   */
  private static class CatalogCellCompnent extends JComponent {
    private static final int DEFAULT_ICON_HEIGHT = Math.round(48 * SwingTools.getResolutionScale());
    private Font                    defaultFont;
    private Font                    modifiablePieceFont;
    private DefaultListCellRenderer nameLabel;
    private JEditorPane             informationPane;
    
    public CatalogCellCompnent() {
      setLayout(null);
      this.nameLabel = new DefaultListCellRenderer() {
          @Override
          public Dimension getPreferredSize() {
            return new Dimension(DEFAULT_ICON_HEIGHT * 3 / 2 + 5, super.getPreferredSize().height);
          }
        };
      this.nameLabel.setHorizontalTextPosition(JLabel.CENTER);
      this.nameLabel.setVerticalTextPosition(JLabel.BOTTOM);
      this.nameLabel.setHorizontalAlignment(JLabel.CENTER);
      this.nameLabel.setText("-");
      this.nameLabel.setIcon(IconManager.getInstance().getWaitIcon(DEFAULT_ICON_HEIGHT));
      this.defaultFont = UIManager.getFont("ToolTip.font");
      this.modifiablePieceFont = new Font(this.defaultFont.getFontName(), Font.ITALIC, this.defaultFont.getSize());
      this.nameLabel.setFont(this.defaultFont);
      
      this.informationPane = new JEditorPane("text/html", "-");
      this.informationPane.setOpaque(false);
      this.informationPane.setEditable(false);
      String bodyRule = "body { font-family: " + this.defaultFont.getFamily() + "; " 
          + "font-size: " + this.defaultFont.getSize() + "pt; " 
          + "text-align: center; }";
      ((HTMLDocument)this.informationPane.getDocument()).getStyleSheet().addRule(bodyRule);
      
      add(this.nameLabel);
      add(this.informationPane);
    }

    @Override
    public void doLayout() {
      Dimension namePreferredSize = this.nameLabel.getPreferredSize();
      this.nameLabel.setSize(getWidth(), namePreferredSize.height);
      this.informationPane.setBounds(0, namePreferredSize.height,
          getWidth(), getHeight() - namePreferredSize.height);
    }
    
    @Override
    public Dimension getPreferredSize() {
      Dimension preferredSize = this.nameLabel.getPreferredSize();
      preferredSize.height += this.informationPane.getPreferredSize().height + 2;
      return preferredSize;
    }
    
    /**
     * The following methods are overridden for performance reasons.
     */
    @Override
    public void revalidate() {      
    }
    
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {      
    }

    @Override
    public void repaint(Rectangle r) {      
    }

    @Override
    public void repaint() {      
    }

    private Icon getLabelIcon(JList list, Content content) {
      return IconManager.getInstance().getIcon(content, DEFAULT_ICON_HEIGHT, list);
    }
    
    @Override
    protected void paintChildren(Graphics g) {
      // Force text anti aliasing on texts
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintChildren(g);
    }
  }
}
