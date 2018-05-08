package imagefilters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DemoColorChooser {
    final JColorChooser chooser;
    ImageFilters imageFilters;
    public DemoColorChooser(ImageFilters imageFilters)
    {
        this.imageFilters = imageFilters;
        chooser = new JColorChooser();
        AbstractColorChooserPanel[] oldPanels = chooser.getChooserPanels();
        for (AbstractColorChooserPanel p: oldPanels) {
            String display_name = p.getDisplayName();
            if (display_name.equals("Swatches")) 
            {
                chooser.removeChooserPanel(p);
            } 
            else if (display_name.equals("HSL")) 
            {
                chooser.removeChooserPanel(p);
            } 
            else if (display_name.equals("CMYK")) 
            {
                chooser.removeChooserPanel(p);
            }
        }
        chooser.setPreviewPanel(new MyPreviewPane(chooser));
        if (imageFilters.selectedPolygon != null)
        {
            chooser.setColor(imageFilters.selectedPolygon.color);
        }
        chooser.getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                if (imageFilters.selectedPolygon == null)
                {
                    return;
                }
                Color color = chooser.getColor();
                //System.out.println(color);
                imageFilters.image_pixels = imageFilters.toBufferedImage(imageFilters.selected_image);
                imageFilters.colorizePolygon(color);
                imageFilters.repaint();
            }
        });
        
        ActionListener okListener = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                
                System.out.println("Clicked OK");
                if (imageFilters.selectedPolygon != null)
                {
                    imageFilters.selectedPolygon.color = chooser.getColor();
                }
            }
        };

        ActionListener cancelListener = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.out.println("Canceled");
                Color c = null;
                if (imageFilters.selectedPolygon != null)
                {
                    c = imageFilters.selectedPolygon.color;
                }
                
                imageFilters.colorChooser.setSelectedColor(c);
            }
        };
        
        
        JDialog dialog = JColorChooser.createDialog(null, "Color Chooser",
                true, chooser, okListener, cancelListener);

        dialog.setLocation(0, 0);
        dialog.setVisible(true);
    }
    
    
}

class MyPreviewPane extends JLabel{
  Color curColor;
  public MyPreviewPane(JColorChooser chooser) {
    curColor = chooser.getColor();
    ColorSelectionModel model = chooser.getSelectionModel();
    model.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        ColorSelectionModel model = (ColorSelectionModel) evt.getSource();
        curColor = model.getSelectedColor();
      }
    });
    setPreferredSize(new Dimension(50, 50));
  }
  public void paint(Graphics g) {
    g.setColor(curColor);
    g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
  }
}
