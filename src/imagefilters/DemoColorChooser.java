package imagefilters;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DemoColorChooser {
    final JColorChooser chooser;
    ImageFilters imageFilters;
    public DemoColorChooser(ImageFilters imageFilters)
    {
        this.imageFilters = imageFilters;
        chooser = new JColorChooser();
        chooser.setColor(imageFilters.selectedPolygon.color);
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
                imageFilters.selectedPolygon.color = chooser.getColor();
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
        dialog.setVisible(true);
    }
    
    
}
