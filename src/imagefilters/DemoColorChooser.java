package imagefilters;

import static imagefilters.ImageFilters.toBufferedImage;
import java.awt.Color;
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
        chooser.setColor(Color.BLUE);
        chooser.getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                //Color color = chooser.getColor();
                //System.out.println(color);
                //imageFilters.image_pixels = toBufferedImage(imageFilters.selected_image);
                //imageFilters.colorizePolygon(color);
                //imageFilters.repaint();
            }
        });
        JDialog dialog = JColorChooser.createDialog(null, "Color Chooser",
                true, chooser, null, null);
        dialog.setVisible(true);
    }
    
    
}