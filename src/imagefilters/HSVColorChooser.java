/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author paulsoderquist
 */
public class HSVColorChooser extends JPanel {
    
    
    JSlider hue_slider;
    int hue_min = 0;
    int hue_max = 360;
    int hue_initial = 0;
    
    JSlider sat_slider;
    int sat_min = 0;
    int sat_max = 100;
    int sat_initial = 0;
    
    JSlider val_slider;
    int val_min = 0;
    int val_max = 100;
    int val_initial = 100;
    
    JSpinner hue_spinner;
    JSpinner sat_spinner;
    JSpinner val_spinner;
    JSpinner video_frame_spinner;
    
    JSlider video_frame_slider;
    int video_frame_min = 0;
    int video_frame_max = 1;
    int video_frame_initial = 0;
    int video_current_value = 0;
    
    boolean lastPressedWasBackward = false;
    /*
    JSlider red_slider;
    int red_min = 0;
    int red_max = 255;
    int red_initial = 255;
    
    JSlider green_slider;
    int green_min = 0;
    int green_max = 255;
    int green_initial = 255;
    
    JSlider blue_slider;
    int blue_min = 0;
    int blue_max = 255;
    int blue_initial = 255;
    
    JSpinner red_spinner;
    JSpinner green_spinner;
    JSpinner blue_spinner;
    */
    
    
    private int red = 0;
    private int green = 0;
    private int blue = 0;
    
    private ImageFilters imageFilters;
    
    public HSVColorChooser(ImageFilters imgFilters)
    {
        this.imageFilters = imgFilters;
        this.setLayout(new GridLayout(11,1));
        
        // -----------------------
        // H S V
        // -----------------------
        
        // -----------------------
        // Hue
        // -----------------------
        JPanel hue_panel = new JPanel();
        JLabel hue_label = new JLabel("H");
        hue_slider = new JSlider(JSlider.HORIZONTAL,hue_min,hue_max,hue_initial);
        SpinnerModel hue_model =
        new SpinnerNumberModel(hue_initial, //initial value
                               hue_min, //min
                               hue_max, //max
                               1);                //step
        hue_spinner = new JSpinner(hue_model);
        hue_slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                hue_spinner.setValue((int)(hue_slider.getValue()));
                //calculateRGB();
                repaint();
            }
        });
        hue_spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                hue_slider.setValue((int)(hue_spinner.getValue()));
                calculateRGB();
                repaint();
            }
        });
        hue_panel.add(hue_label);
        hue_panel.add(hue_slider);
        hue_panel.add(hue_spinner);
        
        // -----------------------
        // Saturation
        // -----------------------
        JPanel sat_panel = new JPanel();
        JLabel sat_label = new JLabel("S");
        sat_slider = new JSlider(JSlider.HORIZONTAL,sat_min,sat_max,sat_initial);
        SpinnerModel sat_model =
        new SpinnerNumberModel(sat_initial, //initial value
                               sat_min, //min
                               sat_max, //max
                               1);                //step
        sat_spinner = new JSpinner(sat_model);
        sat_slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sat_spinner.setValue((int)(sat_slider.getValue()));
                //calculateRGB();
                repaint();
            }
        });
        sat_spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sat_slider.setValue((int)(sat_spinner.getValue()));
                calculateRGB();
                repaint();
            }
        });
        sat_panel.add(sat_label);
        sat_panel.add(sat_slider);
        sat_panel.add(sat_spinner);
        
        // -----------------------
        // Value
        // -----------------------
        JPanel val_panel = new JPanel();
        JLabel val_label = new JLabel("V");
        val_slider = new JSlider(JSlider.HORIZONTAL,val_min,val_max,val_initial);
        SpinnerModel val_model =
        new SpinnerNumberModel(val_initial, //initial value
                               val_min, //min
                               val_max, //max
                               1);                //step
        val_spinner = new JSpinner(val_model);
        val_slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                val_spinner.setValue((int)(val_slider.getValue()));
                //calculateRGB();
                repaint();
            }
        });
        val_spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                val_slider.setValue((int)(val_spinner.getValue()));
                calculateRGB();
                repaint();
            }
        });
        val_panel.add(val_label);
        val_panel.add(val_slider);
        val_panel.add(val_spinner);
        
        
        // -----------------------
        // Video Frame
        // -----------------------
        JPanel video_frame_panel = new JPanel();
        //JLabel video_frame_label = new JLabel("Frame");
        video_frame_slider = new JSlider(JSlider.HORIZONTAL,video_frame_min,video_frame_max,video_frame_initial);
        SpinnerModel video_frame_model =
        new SpinnerNumberModel(video_frame_initial, //initial value
                               video_frame_min, //min
                               video_frame_max, //max
                               1);                //step
        video_frame_spinner = new JSpinner(video_frame_model);
        video_frame_slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                video_frame_spinner.setValue((int)(video_frame_slider.getValue()));
                repaint();
            }
        });
        video_frame_spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (int)(video_frame_spinner.getValue());
                if (value == video_current_value+1)
                {
                    imgFilters.advanceFrame();
                    lastPressedWasBackward = false;
                }
                else if (value == video_current_value-1)
                {
                    imgFilters.goToPreviousFrame();
                    lastPressedWasBackward = true;
                }
                else
                {
                    video_frame_slider.setValue(value);
                    imgFilters.setCurrentFrame(value);
                }
                video_current_value = value;
                repaint();
            }
        });
        //video_frame_panel.add(video_frame_label);
        video_frame_panel.add(video_frame_slider);
        video_frame_panel.add(video_frame_spinner);
        
        
        /*
        // -----------------------
        // R G B
        // -----------------------
        
        // -----------------------
        // Red
        // -----------------------
        JPanel red_panel = new JPanel();
        JLabel red_label = new JLabel("R");
        red_slider = new JSlider(JSlider.HORIZONTAL,red_min,red_max,red_initial);
        SpinnerModel red_model =
        new SpinnerNumberModel(red_initial, //initial value
                               red_min, //min
                               red_max, //max
                               1);                //step
        red_spinner = new JSpinner(red_model);
        red_slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                red_spinner.setValue((int)(red_slider.getValue()));
                calculateHSV();
                repaint();
            }
        });
        red_spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                red_slider.setValue((int)(red_spinner.getValue()));
                calculateHSV();
                repaint();
            }
        });
        red_panel.add(red_label);
        red_panel.add(red_slider);
        red_panel.add(red_spinner);
        
        // -----------------------
        // Green
        // -----------------------
        JPanel green_panel = new JPanel();
        JLabel green_label = new JLabel("G");
        green_slider = new JSlider(JSlider.HORIZONTAL,green_min,green_max,green_initial);
        SpinnerModel green_model =
        new SpinnerNumberModel(green_initial, //initial value
                               green_min, //min
                               green_max, //max
                               1);                //step
        green_spinner = new JSpinner(green_model);
        green_slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                green_spinner.setValue((int)(green_slider.getValue()));
                calculateHSV();
                repaint();
            }
        });
        green_spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                green_slider.setValue((int)(green_spinner.getValue()));
                calculateHSV();
                repaint();
            }
        });
        green_panel.add(green_label);
        green_panel.add(green_slider);
        green_panel.add(green_spinner);
        
        // -----------------------
        // Blue
        // -----------------------
        JPanel blue_panel = new JPanel();
        JLabel blue_label = new JLabel("B");
        blue_slider = new JSlider(JSlider.HORIZONTAL,blue_min,blue_max,blue_initial);
        SpinnerModel blue_model =
        new SpinnerNumberModel(blue_initial, //initial value
                               blue_min, //min
                               blue_max, //max
                               1);                //step
        blue_spinner = new JSpinner(blue_model);
        blue_slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                blue_spinner.setValue((int)(blue_slider.getValue()));
                calculateHSV();
                repaint();
            }
        });
        blue_spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                blue_slider.setValue((int)(blue_spinner.getValue()));
                calculateHSV();
                repaint();
            }
        });
        blue_panel.add(blue_label);
        blue_panel.add(blue_slider);
        blue_panel.add(blue_spinner);
        
        */
        
        this.add(hue_panel);
        this.add(sat_panel);
        this.add(val_panel);
        this.add(new Rectangle());
        this.add(new JLabel("   Video Frame"));
        this.add(video_frame_panel);
        //this.add(red_panel);
        //this.add(green_panel);
        //this.add(blue_panel);
    }
    
    public Color getColor()
    {
        return new Color(red,green,blue);
    }
    
    public void setSelectedColor(Color c)
    {
        if (c == null)
        {
            red = 255;
            green = 255;
            blue = 255;
        }
        else
        {
            red = c.getRed();
            green = c.getGreen();
            blue = c.getBlue();
        }
        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
        float hue = hsb[0]; 
        float saturation = hsb[1];
        float brightness = hsb[2];
        
        hue_slider.setValue((int)(hue*hue_max+.5));
        hue_spinner.setValue((int)(hue*hue_max+.5));
        sat_slider.setValue((int)(saturation*sat_max+.5));
        sat_spinner.setValue((int)(saturation*sat_max+.5));
        val_slider.setValue((int)(brightness*val_max+.5));
        val_spinner.setValue((int)(brightness*val_max+.5));
    }
    
    public void calculateRGB()
    {
        float h = hue_slider.getValue()/360.0f;
        float s = sat_slider.getValue()/100.0f;
        float v = val_slider.getValue()/100.0f;
        
        int rgb = Color.HSBtoRGB(h, s, v);
        red = (rgb >> 16) & 0xFF;
        green = (rgb >> 8) & 0xFF;
        blue = rgb & 0xFF;
        
        /*
        red_slider.setValue(red);
        red_spinner.setValue(red);
        green_slider.setValue(green);
        green_spinner.setValue(green);
        blue_slider.setValue(blue);
        blue_spinner.setValue(blue);
        */
        if (imageFilters.selectedPolygon != null)
        {
            Color color = getColor();
            imageFilters.selectedPolygon.color = color;
            imageFilters.colorizePolygon(color);
            imageFilters.repaint();
        }
    }
    
    /*
    public void calculateHSV()
    {
        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
        float hue = hsb[0]; 
        float saturation = hsb[1];
        float brightness = hsb[2];
        
        red = red_slider.getValue();
        green = green_slider.getValue();
        blue = blue_slider.getValue();
        
        hue_slider.setValue((int)(hue*hue_max+.5));
        hue_spinner.setValue((int)(hue*hue_max+.5));
        sat_slider.setValue((int)(saturation*sat_max+.5));
        sat_spinner.setValue((int)(saturation*sat_max+.5));
        val_slider.setValue((int)(brightness*val_max+.5));
        val_spinner.setValue((int)(brightness*val_max+.5));
        
    }
    */
    
    public static void main(String[] args)
    {
        JFrame f = new JFrame();
        f.add(new HSVColorChooser(null));
        f.setVisible(true);
        f.pack();
    }

    class Rectangle extends JPanel {

        public void paintComponent(Graphics g) {
          super.paintComponent(g);
          Graphics2D g2d = (Graphics2D) g;

          g2d.setColor(new Color(red, green, blue));
          g2d.fillRect(10, 5, 240, 25);
          g2d.setColor(new Color(0, 0, 0));
          g2d.drawRect(10, 5, 240, 25);

        }

  }
}


