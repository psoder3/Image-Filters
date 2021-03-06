package imagefilters;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Stack;
import javax.swing.AbstractAction;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;


/*
    java -cp ../../../../Downloads/javacv-bin/artoolkitplus-macosx-x86_64.jar:../../../../Downloads/javacv-bin/ffmpeg-macosx-x86_64.jar:../../../../Downloads/javacv-bin/ffmpeg.jar:../../../../Downloads/javacv-bin/flandmark-macosx-x86_64.jar:../../Downloads/javacv-bin/flandmark.jar:../../../../Downloads/javacv-bin/flycapture.jar:../../../../Downloads/javacv-bin/javacpp.jar:../../../../Downloads/javacv-bin/javacv.jar:../../../../Downloads/javacv-bin/libdc1394-macosx-x86_64.jar:../../../../Downloads/javacv-bin/libdc1394.jar:../../../../Downloads/javacv-bin/libfreenect-macosx-x86_64.jar:../../../../Downloads/javacv-bin/libfreenect.jar:../../../../Downloads/javacv-bin/opencv-macosx-x86_64.jar:../../../../Downloads/javacv-bin/opencv.jar:../../../../Downloads/javacv-bin/videoinput.jar:. imagefilters.ImageFilters
*/


public class ImageFilters extends JPanel implements MouseListener, KeyListener, MouseMotionListener {

    static int WINDOW_WIDTH = 1450;
    static int WINDOW_HEIGHT = 900;
    double scale = 1;
    int leftRight = 33;
    int upDown = 19;
    int scrollAmount = 10;
    
    MaskedObject emptyObject = new MaskedObject();
    
    Stack<ProjectState> UndoStack = new Stack();
    Stack<ProjectState> RedoStack = new Stack();
    
    boolean AutoCompleteBorder = false;
    boolean BorderReversed = false;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem undoItem;
    JMenuItem redoItem;
    JMenuItem saveItem;
    JMenuItem deselectItem;
    JMenuItem vertexModeItem;
    JMenuItem polygonModeItem;
    JMenuItem dragVerticesItem;
    JMenuItem dragObjectsItem;
    JMenuItem drawItem;
    JMenuItem findEdgesItem;
    JMenuItem trackMotionItem;
    JMenuItem autoBorderItem;
    JMenuItem reverseBorderDirectionItem;
    
    
    
    JButton LoadImageButton = new JButton("Load Image");
    JButton LoadTrainingImageButton = new JButton("Load Training Image");
    JButton LoadSetImagesButton = new JButton("Load Training Set");
    //boolean onlyOneImage = true;
    boolean onlyFirstMapping = false;

    JButton ResetButton = new JButton("Reset");
    JButton ResetTrainingButton = new JButton("Reset Training");
    JButton InvertButton = new JButton("Invert Colors");
    JButton GrayScaleButton = new JButton("Gray Scale");
    JButton StepColorsButton = new JButton("Step Colors");
    JButton EmbossButton = new JButton("Emboss Image");
    JButton BlurButton = new JButton("Blur Image");
    JButton RedButton = new JButton("Add Red");
    JButton GreenButton = new JButton("Add Green");
    JButton BlueButton = new JButton("Add Blue");
    JButton ColorizeButton = new JButton("Colorize");
    JButton randomValuesButton = new JButton("Random RGB");
    JButton BlackWhiteButton = new JButton("Black and White");
    JButton AlternateRGButton = new JButton("Alternate Red/Green");
    JButton SumBoundingBoxButton = new JButton("Map Bounding Box Sum");
    JButton TwoNeighboKeyButton = new JButton("Map 2 Neigbor Key");
    JCheckBox FillInBlanksBox = new JCheckBox("Grayscale Gaps");
    JCheckBox FirstMappingOnly = new JCheckBox("Only First Mappings");
    JButton SplitVideoFramesButton = new JButton("Split Video Frames");
    JButton AssembleFramesButton = new JButton("Assemble Video Frames");
    JButton ColorizeFramesButton = new JButton("Colorize Video Frames");
    JButton ApplySchemeAllFramesButton = new JButton("Apply Frame to All");
    JButton GenerateTrainingArrangements = new JButton("Generate Training Arrangements");
    //JButton CreateColorizedMovieButton = new JButton("Create Colorized Movie");
    JButton AsciiFilterButton = new JButton("Ascii Filter");
    JButton AsciiFilterVideoButton = new JButton("Create Ascii Movie");
    JButton PixelateButton = new JButton("Pixelate");
    JButton Mosaic1Button = new JButton("Mosaic 1");
    JButton Mosaic2Button = new JButton("Mosaic 2");
    JButton ApplyFilterButton = new JButton("Apply Filter");
    JCheckBox ShowOutlinesCheckbox = new JCheckBox("Show Object Outlines");
    JCheckBox InterpolateGapsCheckbox = new JCheckBox("Interpolate Gaps");
    JButton countColorsButton = new JButton("Count Colors");
    JLabel frameRangeLbl1 = new JLabel("frames");
    JLabel frameRangeLbl2 = new JLabel("to");
    JTextField firstFrameChooser = new JTextField(3);
    JTextField lastFrameChooser = new JTextField(3);
    String video_filename = "";
    FFmpegFrameGrabber g;
    IplImage currentFrame;
    int currentObjectID = 0;
    
    JButton saveVideoFrameButton = new JButton("Save Frame");
    JButton trackMotionButton = new JButton("Track Motion");
    JButton findEdgesButton = new JButton("Find Edges");
    JButton colorLayersButton = new JButton("Color By Layers");
    int drawPrecision = 2;
    int dragCounter = 0;
    
    JTextField filenameTextBox = new JTextField(10);
    JLabel extensionLbl = new JLabel(".pmoc");
    JButton saveButton = new JButton("Save");
    JButton openButton = new JButton("Open");
    JButton recolorPolygonsButton = new JButton("Recolor Selected Polygons");
    
    JButton loadVideoButton = new JButton("Load Video");
    
    Set<PointXY> ObjectSelection = new HashSet();
    
    BufferedImage[] bufferedImageFrames;

    ColorChooserButton colorChooser;

    int[] redRepresentation;
    int[] greenRepresentation;
    boolean filterPolygon = false;
    HashMap<String,Integer> patterns = new HashMap();
    int frameCounter = 0;
    
    ProjectState currentProjectState = new ProjectState();
    
    //ArrayList<MaskedObject> polygons;
    //MaskedObject selectedPolygon;
    //Point selectedVertex;
    //int selectedVertexIndex = -1;
    //int hoverVertexIndex = -1;
    boolean currentlyDragging = false;
    //MaskedObject adjacentPolygon;
    //int adjacentPolygonVertex = -1;
    Click DragDown;
    Click DragUp;
    //ArrayList<MaskedObject> selectedObjects = new ArrayList();
    //ArrayList<ArrayList<Integer>> selectedVertices = new ArrayList();
    
    Rectangle rectangle;

    JTextField numberMosaicColumnsBox = new JTextField(3);
    JLabel columnsLabel = new JLabel("Mosaic Rows");
    JButton CreateFilteredVideoButton = new JButton("Create Filtered Video");
    String[] filterStrings = { "Mosaic 1", "Mosaic 2", "Pixelate", "Emboss",
        "Grayscale", "Colorize", "Psychedelic", "Step Colors", "Invert Colors", "Increase Contrast", "Create Training Data",
        "Colorize Polygon", "De-Noise (Mean)", "De-Noise (Median)", "Gradient"};

    String[] keyStrings = { "0 Neighbors", "1 Neighbor", "2 Neighbors", "3 Neighbors",
        "4 Neighbors", "Optimized Interpolate"};
    String[] toolStrings = { "Select Polygon Mode", "Vertex Mode", "Drag Select Objects", "Drag Select Vertices", "Pen Tool"};
    ArrayList<Pixel>[][][][][] optInterpolationDictionary = new ArrayList[256][][][][];
    ArrayList<Pair>[] neighborMeanMap = new ArrayList[256];

    
    //Create the combo box, select item at index 4.
    //Indices start at 0, so 4 specifies the pig.
    JComboBox filterList = new JComboBox(filterStrings);
    JComboBox keyList = new JComboBox(keyStrings);
    JComboBox toolList = new JComboBox(toolStrings);

    HSVColorChooser hsvColorChooser;
    
    
    String mosaicType = "Mosaic1";
    String Current_Movie_Name;
    int totalFramesInVideo = 0;
    double frameRate = 24;
    
    int threadsComplete = 0;
    
    int AsciiRows = 53;
    int AsciiCols = 189;
    int firstFrame = 1;
    int lastFrame = 1;
    double cellWidth = 10;
    double cellHeight = 10;
    
    int count = 0;
    int bwcounter = 0;
    int RGCounter = 0;
    BufferedImage image_copy;
    HashMap<String,HashMap<Pixel,Integer>> neighbor_key_dictionary = new HashMap();
    HashMap<String,Pixel> neighbor_key_dictionary_1to1 = new HashMap();
    Pixel[] colorizationArray = new Pixel[6376];
    ArrayList<Pixel>[] colorizeArrayMode = new ArrayList[6376];
    
    

    int[] trainingImageContributionCountPerValue = new int[256];
    JFrame frame = new JFrame();
    BufferedImage image_pixels;
    boolean alreadyFoundModes = false;
    boolean fillInBlanks = false;
    JPanel buttons = new JPanel();
    JPanel buttons2 = new JPanel();
    public Image selected_image = null;
    //private File selected_file = new File("/Users/paulsoderquist/Documents/trainingImages/jimmy-stewart-rope.jpg");
    private File selected_file = new File("C:\\Users\\psoderquist\\Documents\\NetBeansProjects\\Image-Filters-master\\Image-Filters-master\\faveWife.png");

    public ImageFilters() {
        this.colorChooser = new ColorChooserButton(Color.WHITE, this);
        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("Keyboard Shortcuts");
        //menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);
        //a group of JMenuItems
        undoItem = new JMenuItem("Undo");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        undoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });
        menu.add(undoItem);
        
        redoItem = new JMenuItem("Redo");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        redoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });
        menu.add(redoItem);
        
        saveItem = new JMenuItem("Save Video Frame");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveVideoFrame();
            }
        });
        menu.add(saveItem);
        
        deselectItem = new JMenuItem("Deselect");
        deselectItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_U, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        deselectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deselect();
            }
        });
        menu.add(deselectItem);
        
        vertexModeItem = new JMenuItem("Vertex Mode");
        vertexModeItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        vertexModeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVertexMode();
            }
        });
        menu.add(vertexModeItem);
        
        polygonModeItem = new JMenuItem("Select Polygon Mode");
        polygonModeItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        polygonModeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectMode();
            }
        });
        menu.add(polygonModeItem);
        
        dragVerticesItem = new JMenuItem("Drag Select Vertices");
        dragVerticesItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        dragVerticesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDragVerticesMode();
            }
        });
        menu.add(dragVerticesItem);
        
        dragObjectsItem = new JMenuItem("Drag Select Objects");
        dragObjectsItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        dragObjectsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDragObjectsMode();
            }
        });
        menu.add(dragObjectsItem);
        
        drawItem = new JMenuItem("Pen Tool");
        drawItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        drawItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDrawingMode();
            }
        });
        menu.add(drawItem);
        
        findEdgesItem = new JMenuItem("Find Edges");
        findEdgesItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        findEdgesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findEdges();
            }
        });
        menu.add(findEdgesItem);
        
        trackMotionItem = new JMenuItem("Track Motion");
        trackMotionItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        trackMotionItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trackMotionFromPreviousFrame();
            }
        });
        menu.add(trackMotionItem);
        
        autoBorderItem = new JMenuItem("Turn Auto-Complete Border On");
        autoBorderItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        autoBorderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (AutoCompleteBorder)
                {
                    AutoCompleteBorder = false;
                    autoBorderItem.setText("Turn Auto-Complete Border On");
                }
                else
                {
                    AutoCompleteBorder = true;
                    autoBorderItem.setText("Turn Auto-Complete Border Off");
                }
            }
        });
        menu.add(autoBorderItem);
        
        reverseBorderDirectionItem = new JMenuItem("Reverse Auto-Complete Direction");
        reverseBorderDirectionItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        reverseBorderDirectionItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BorderReversed = !BorderReversed;
                repaint();
            }
        });
        menu.add(reverseBorderDirectionItem);
    }
    
    public Pixel findMedian(ArrayList<Pixel> kernal, String color)
    { 
        switch (color) {
            case "red":
                Collections.sort(kernal, new Comparator() 
                {

                    @Override
                    public int compare(Object o1, Object o2) 
                    {
                        Pixel p = (Pixel)o1;
                        Pixel p2 = (Pixel)o2;

                        if (p.red < p2.red)
                        {
                            return -1;
                        }
                        else if (p.red == p2.red)
                        {
                            return 0;
                        }
                        else
                        {
                            return 1;
                        }
                    }
                }
                );  break;
            case "green":
                Collections.sort(kernal, new Comparator() 
                {

                    @Override
                    public int compare(Object o1, Object o2) 
                    {
                        Pixel p = (Pixel)o1;
                        Pixel p2 = (Pixel)o2;

                        if (p.green < p2.green)
                        {
                            return -1;
                        }
                        else if (p.green == p2.green)
                        {
                            return 0;
                        }
                        else
                        {
                            return 1;
                        }
                    }
                }
                );  break;
            case "blue":
                Collections.sort(kernal, new Comparator() 
                {
                    
                    public int compare(Object o1, Object o2) 
                    {
                        Pixel p = (Pixel)o1;
                        Pixel p2 = (Pixel)o2;

                        if (p.blue < p2.blue)
                        {
                            return -1;
                        }
                        else if (p.blue == p2.blue)
                        {
                            return 0;
                        }
                        else
                        {
                            return 1;
                        }
                    }
                }
                );  break;
            default:
                break;
        }
        return kernal.get(kernal.size()/2);
    }
    
    public void deNoiseImageMedian()
    {
        image_copy = getImageCopy();

        int blurAmount = 3;
        for (int row = blurAmount; row < image_pixels.getHeight()-blurAmount; row++)
        {
            for (int column = blurAmount; column < image_pixels.getWidth()-blurAmount; column++)
            {
                Pixel currentPixel = getPixel(column,row,image_pixels);
                int red = 0;
                int green = 0;
                int blue = 0;
                ArrayList<Pixel> kernal = new ArrayList();
                
                for (int i = -(blurAmount/2); i < (blurAmount+1)/2; i++)
                {
                    for (int j = -(blurAmount/2); j < (blurAmount+1)/2; j++)
                    {
                        Pixel p = getPixel(column+i,row+j,image_copy);
                        kernal.add(p);
                    } 
                }
                
                int redMedian = findMedian(kernal,"red").red;
                int greenMedian = findMedian(kernal,"green").green;
                int blueMedian = findMedian(kernal,"blue").blue;
                
                currentPixel.setRGB(redMedian, greenMedian, blueMedian);
            }
        }
    }

    public void deNoiseImageMean()
    {
        image_copy = getImageCopy();

        int blurAmount = 3;
        for (int row = blurAmount; row < image_pixels.getHeight()-blurAmount; row++)
        {
            for (int column = blurAmount; column < image_pixels.getWidth()-blurAmount; column++)
            {
                Pixel currentPixel = getPixel(column,row,image_pixels);
                int red = 0;
                int green = 0;
                int blue = 0;
                for (int i = -(blurAmount/2); i < (blurAmount+1)/2; i++)
                {
                    for (int j = -(blurAmount/2); j < (blurAmount+1)/2; j++)
                    {
                        Pixel p = getPixel(column+i,row+j,image_copy);
                        red += p.getRedValue();
                        green += p.getGreenValue();
                        blue += p.getBlueValue();
                    } 
                }
                int blurSquared = blurAmount*blurAmount;
                //System.out.println(currentPixel.getRedValue() + " " + red/blurSquared);
                currentPixel.setRGB(red/blurSquared, green/blurSquared, blue/blurSquared);
            }
        }
    }
    
    public void colorizePolygonPsychedelic(Color color)
    {
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                if (currentProjectState.selectedPolygon != null && !currentProjectState.selectedPolygon.polygon.contains(column,row))
                {
                    continue;
                }
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue();
                /*
                if (r > g && r > b)
                {
                    pixel.setRGB(255, 0, 0);
                }
                if (g > b && g > r)
                {
                    pixel.setRGB(0, 255, 0);
                }
                if (b > r && b > g)
                {
                    pixel.setRGB(0, 0, 255);
                }
                */
                float[] hsvInput = new float[3];
                Color.RGBtoHSB(color.getRed(),color.getGreen(), color.getBlue(), hsvInput);
                float inputHue = hsvInput[0];
                
                double average = r+g+b/3.0;
                r = (int)average;
                g = (int)average;
                b = (int)average;
                float[] hsv = new float[3];
                Color.RGBtoHSB(r,g,b,hsv);
                float h = hsv[0];
                float s = hsv[1];
                float v = hsv[2];
                //System.out.print(hsv[0] + " ");
                v *= 1.245;
                if (v > 255) v = 255;
                int rgb = Color.HSBtoRGB(hsvInput[0],0.35f,v);
                Color newColor = new Color(rgb);
                
                pixel.setRGB(newColor.getRed(), newColor.getGreen(), newColor.getBlue());
                //System.out.print(255-average_value + ",");
                
            }
            //System.out.println();
        }
    }
    
    public boolean pointIsInsideContainedPolygon(int x, int y)
    {
        for (MaskedObject p : currentProjectState.polygons)
        {
            if (p != currentProjectState.selectedPolygon && p.polygon.contains(x,y))
            {
                for (int i = 0; i < p.polygon.npoints; i++)
                {
                    if (currentProjectState.selectedPolygon.polygon.contains(p.polygon.xpoints[i],p.polygon.ypoints[i]))
                    {
                        return true;
                    }
                            
                }
            }
        }
        return false;
    }
    
    private float getHueVariationRandom()
    {
        int hue_var = (int)(hsvColorChooser.hue_variation_spinner.getValue());
        hue_var = (int)(Math.random() * hue_var);
        if (Math.random() < .5)
        {
            hue_var *= -1;
        }
        float scaled = hue_var/360.0f;
        return scaled;
    }
    
    private float getSatVariationRandom()
    {
        int sat_var = (int)(hsvColorChooser.sat_variation_spinner.getValue());
        sat_var = (int)(Math.random() * sat_var);
        if (Math.random() < .5)
        {
            sat_var *= -1;
        }
        float scaled = sat_var/100.0f;
        return scaled;
    }
    
    private float getHueVariationFromIntensity(int intensity, int hue_var)
    {
        //int hue_var = (int)(hsvColorChooser.hue_variation_spinner.getValue());
        if (hue_var == 0) return hue_var;
        int halfway = 128;
        int difference = intensity - halfway;
        float percentageOfMaxVariation = difference/128.0f;
        if (percentageOfMaxVariation > 1) percentageOfMaxVariation = 1;
        if (percentageOfMaxVariation < -1) percentageOfMaxVariation = -1;
        float variation = 1.0f * (percentageOfMaxVariation * hue_var);
        return variation/360;
    }
    
    private float getSatVariationFromIntensity(int intensity, int sat_var)
    {
        //int sat_var = (int)(hsvColorChooser.sat_variation_spinner.getValue());
        if (sat_var == 0) return sat_var;
        int halfway = 128;
        int difference = intensity - halfway;
        float percentageOfMaxVariation = difference/128.0f;
        if (percentageOfMaxVariation > 1) percentageOfMaxVariation = 1;
        if (percentageOfMaxVariation < -1) percentageOfMaxVariation = -1;
        float variation = -1 * (1.0f * (percentageOfMaxVariation * sat_var));
        return variation/100;
    }
    
    private void getContainingObject(ArrayList<MaskedObject> borderingObjects, Pixel p)
    {
        for (MaskedObject mo : currentProjectState.polygons)
        {
            if (mo.polygon.contains(p.getPoint()))
            {
                borderingObjects.add(mo);
                return;
            }
        }
    }
    
    private boolean isEdgePixel(int column, int row, ArrayList<MaskedObject> borderingObjects,
            MaskedObject containingObject)
    {
        Polygon poly = containingObject.polygon;
        
        Pixel p1 = null;
        Pixel p2 = null;
        Pixel p3 = null;
        Pixel p4 = null;
        Pixel p5 = null;
        Pixel p6 = null;
        Pixel p7 = null;
        Pixel p8 = null;
        if (column < image_pixels.getWidth()-1)
        {
            p1 = getPixel(column+1,row,image_pixels);
        }
        if (column > 0)
        {
            p2 = getPixel(column-1,row,image_pixels);
        }
        if (row < image_pixels.getHeight()-1)
        {
            p3 = getPixel(column,row+1,image_pixels);
        }
        if (row > 0)
        {
            p4 = getPixel(column,row-1,image_pixels);
        }
        if (column < image_pixels.getWidth()-1 && row > 0)
        {
            p5 = getPixel(column+1,row-1,image_pixels);
        }
        if (column > 0 && row < image_pixels.getHeight()-1)
        {
            p6 = getPixel(column-1,row+1,image_pixels);
        }
        if (column < image_pixels.getWidth()-1 && row < image_pixels.getHeight()-1)
        {
            p7 = getPixel(column+1,row+1,image_pixels);
        }
        if (column > 0 && row > 0)
        {
            p8 = getPixel(column-1,row-1,image_pixels);
        }
        if (p1 != null && !poly.contains(p1.getPoint())) 
        {
            borderingObjects.add(containingObject);
            getContainingObject(borderingObjects,p1);
            return true;
        }
        if (p2 != null && !poly.contains(p2.getPoint())) 
        {
            borderingObjects.add(containingObject);
            getContainingObject(borderingObjects,p2);
            return true;
        }
        if (p3 != null && !poly.contains(p3.getPoint()))
        {
            borderingObjects.add(containingObject);
            getContainingObject(borderingObjects,p3); 
            return true;
        }
        if (p4 != null && !poly.contains(p4.getPoint())) 
        {
            borderingObjects.add(containingObject);
            getContainingObject(borderingObjects,p4);
            return true;
        }
        if (p5 != null && !poly.contains(p5.getPoint()))
        {
            borderingObjects.add(containingObject);
            getContainingObject(borderingObjects,p5);
            return true;
        }
        if (p6 != null && !poly.contains(p6.getPoint()))
        {
            borderingObjects.add(containingObject);
            getContainingObject(borderingObjects,p6); 
            return true;
        }
        if (p7 != null && !poly.contains(p7.getPoint())) 
        {
            borderingObjects.add(containingObject);
            getContainingObject(borderingObjects,p7); 
            return true;
        }
        if (p8 != null && !poly.contains(p8.getPoint())) 
        {
            borderingObjects.add(containingObject);
            getContainingObject(borderingObjects,p8);
            return true;
        }
        return false;
        
    }
    
    private boolean isEdgeLowContrast(int column, int row, int threshold)
    {
        if (column < 1 || row < 1 || column > image_pixels.getWidth() - 2 ||
                row > image_pixels.getHeight() - 2)
        {
            return false;
        }
        boolean isLowContrast = true;
        double pixel = getPixel(column,row,image_pixels).getAverage();
        double p1 = getPixel(column+1,row,image_pixels).getAverage();
        double p2 = getPixel(column-1,row,image_pixels).getAverage();
        double p3 = getPixel(column,row+1,image_pixels).getAverage();
        double p4 = getPixel(column,row-1,image_pixels).getAverage();
        double p5 = getPixel(column+1,row-1,image_pixels).getAverage();
        double p6 = getPixel(column-1,row+1,image_pixels).getAverage();
        double p7 = getPixel(column+1,row+1,image_pixels).getAverage();
        double p8 = getPixel(column-1,row-1,image_pixels).getAverage();
        if (Math.abs(pixel - p1) > threshold) isLowContrast = false;
        if (Math.abs(pixel - p2) > threshold) isLowContrast = false;
        if (Math.abs(pixel - p3) > threshold) isLowContrast = false;
        if (Math.abs(pixel - p4) > threshold) isLowContrast = false;
        if (Math.abs(pixel - p5) > threshold) isLowContrast = false;
        if (Math.abs(pixel - p6) > threshold) isLowContrast = false;
        if (Math.abs(pixel - p7) > threshold) isLowContrast = false;
        if (Math.abs(pixel - p8) > threshold) isLowContrast = false;
        return isLowContrast;
    }
    
    private Color blendColors(Color c1, Color c2)
    {
        return new Color((c1.getRed()+c2.getRed())/2,
                            (c1.getGreen()+c2.getGreen())/2,
                                    (c1.getRed()+c2.getBlue())/2
        );
    }
    
    private void colorPixel(int column, int row, MaskedObject containingObj)
    {
        Color color_picked;
        if (containingObj == null)
        {
            containingObj = emptyObject;
        }
        int hue_variation = containingObj.hue_variation;
        int saturation_variation = containingObj.saturation_variation;
        Pixel pixel = getPixel(column,row,image_pixels);
        int pixelR = pixel.getRedValue();
        int pixelG = pixel.getGreenValue();
        int pixelB = pixel.getBlueValue();
        int average = (int)(((pixelR+pixelG+pixelB)/3.0));
        
        pixelR = average;
        pixelG = average;
        pixelB = average;
        if (containingObj == emptyObject)
        {
            color_picked = new Color(0,0,0);
        }
        else if (average > 256-containingObj.complement_threshold)
        {
            color_picked = containingObj.secondary_color;
            hue_variation = containingObj.secondary_hue_variation;
            saturation_variation = containingObj.secondary_sat_variation;
        }
        else 
        {
            color_picked = containingObj.color;
        }
        
        if (hsvColorChooser.edgeBlendList.getSelectedIndex() == 1)
        {

            /* TESTING ONLY, FOR NOW IT WILL TURN LOW CONTRAST EDGES YELLOW */
            ArrayList<MaskedObject> borderingObjects = new ArrayList();
            boolean isEdge = isEdgePixel(column, row, borderingObjects, containingObj);
            if (isEdge)
            {
                boolean lowContrastEdge = isEdgeLowContrast(column, row, 10);
                if (lowContrastEdge && borderingObjects != null && borderingObjects.size() > 1)
                {
                    Color c1 = borderingObjects.get(0).color;
                    Color c2 = borderingObjects.get(1).color;
                    color_picked = blendColors(c1,c2);
                }
            }
            // --------------- //
        }
        
        float[] hsbInput = new float[3];
        Color.RGBtoHSB(color_picked.getRed(),color_picked.getGreen(), color_picked.getBlue(), hsbInput);
        float inputHue = hsbInput[0];
        float inputSaturation = hsbInput[1];
        
        float[] hsb = new float[3];
        Color.RGBtoHSB(pixelR,pixelG,pixelB,hsb);
        float h = hsb[0];
        float s = hsb[1];
        float b = hsb[2];
        
        inputHue = hsbInput[0] + getHueVariationFromIntensity(average,hue_variation);
        inputSaturation = hsbInput[1] + getSatVariationFromIntensity(average,saturation_variation);
        if (inputSaturation < 0) inputSaturation = 0;
        if (inputSaturation > 1) inputSaturation = 1;
        if (inputHue < 0) inputHue += 1; // Unlike saturation, hue can wrap from 
        if (inputHue > 1) inputHue -= 1; // pink/red to red/orange and vice versa
        
        //System.out.print(hsv[0] + " ");
        b *= 1.245;
        if (b > 1) b = 1;
        if (b < 0) b = 0;
        Color newColor = Color.getHSBColor(inputHue,inputSaturation,b);
        //Color newColor = new Color(rgb);
        double newAverage = (newColor.getRed() + newColor.getGreen() + newColor.getBlue()) / 3.0;
        //System.out.println("old average: " + average);
        //System.out.println("new average: " + newAverage);
        //System.out.print(255-average_value + ",");
        int difference = average - (int)newAverage;

        int newRed = newColor.getRed()+difference;
        int newGreen = newColor.getGreen()+difference;
        int newBlue = newColor.getBlue()+difference;

        
        // THIS NEXT PART ENSURES THE ORIGINAL 
        // GRAYSCALE AVERAGE VALUE IS PRESERVED
        
        
        // Red is above
        if (newRed > 255)
        {
            int surplusRed = newRed - 255;
            newRed = 255;

            newGreen += surplusRed/2;
            newBlue += surplusRed/2;
            if (newGreen > 255)
            {
                int surplusGreen = newGreen - 255;
                newGreen = 255;
                newBlue += surplusGreen;
            }
            else if (newBlue > 255)
            {
                int surplusBlue = newBlue - 255;
                newBlue = 255;
                newGreen += surplusBlue;
            }

        } 
        // Red is below
        else if (newRed < 0)
        {
            int debtRed = 0-newRed;
            newRed = 0;

            newGreen -= debtRed/2;
            newBlue -= debtRed/2;
            if (newGreen < 0)
            {
                int debtGreen = 0 - newGreen;
                newGreen = 0;
                newBlue -= debtGreen;
            }
            else if (newBlue < 0)
            {
                int debtBlue = 0 - newBlue;
                newBlue = 0;
                newGreen -= debtBlue;
            }

        }

        // Green is above
        else if (newGreen > 255)
        {
            int surplusGreen = newGreen - 255;
            newGreen = 255;

            newRed += surplusGreen/2;
            newBlue += surplusGreen/2;
            if (newRed > 255)
            {
                int surplusRed = newRed - 255;
                newRed = 255;
                newBlue += surplusRed;
            }
            else if (newBlue > 255)
            {
                int surplusBlue = newBlue - 255;
                newBlue = 255;
                newRed += surplusBlue;
            }

        }
        // Green is below
        else if (newGreen < 0)
        {
            int debtGreen = 0-newGreen;
            newGreen = 0;

            newRed -= debtGreen/2;
            newBlue -= debtGreen/2;
            if (newRed < 0)
            {
                int debtRed = 0 - newRed;
                newRed = 0;
                newBlue -= debtRed;
            }
            else if (newBlue < 0)
            {
                int debtBlue = 0 - newBlue;
                newBlue = 0;
                newRed -= debtBlue;
            }

        }


        // Blue is above
        else if (newBlue > 255)
        {
            int surplusBlue = newBlue - 255;
            newBlue = 255;

            newGreen += surplusBlue/2;
            newRed += surplusBlue/2;
            if (newGreen > 255)
            {
                int surplusGreen = newGreen - 255;
                newGreen = 255;
                newRed += surplusGreen;
            }
            else if (newRed > 255)
            {
                int surplusRed = newRed - 255;
                newRed = 255;
                newGreen += surplusRed;
            }

        }
        // Blue is below
        else if (newBlue < 0)
        {
            int debtBlue = 0-newBlue;
            newBlue = 0;

            newGreen -= debtBlue/2;
            newRed -= debtBlue/2;
            if (newGreen < 0)
            {
                int debtGreen = 0 - newGreen;
                newGreen = 0;
                newRed -= debtGreen;
            }
            else if (newRed < 0)
            {
                int debtRed = 0 - newRed;
                newRed = 0;
                newGreen -= debtRed;
            }

        }

        if (newRed > 258 || newRed < -3 || newGreen > 258 || newGreen < -3 || newBlue > 258 || newBlue < -3)
        {
            System.out.println("Something very not okay");
        }
        else if (newRed > 255)
        {
            newRed = 255;
        }
        else if (newRed < 0)
        {
            newRed = 0;
        }
        else if (newGreen > 255)
        {
            newGreen = 255;
        }
        else if (newGreen < 0)
        {
            newGreen = 0;
        }
        else if (newBlue > 255)
        {
            newBlue = 255;
        }
        else if (newBlue < 0)
        {
            newBlue = 0;
        }
        newAverage = (newRed + newGreen + newBlue)/3;
        if ((int)newAverage > average)
        {
            //System.out.println("new greater than old");
            if (newRed > 0) newRed--;
            if (newGreen > 0) newGreen--;
            if (newBlue > 0) newBlue--;

        }
        else if ((int)newAverage < average)
        {
            //System.out.println("new less than old" + (newAverage - average));
            if (newRed < 255) newRed++;
            if (newGreen < 255) newGreen++;
            if (newBlue < 255) newBlue++;

        }

        pixel.setRGB(newRed, newGreen, newBlue);

    }
    
    public void colorizePolygon(MaskedObject containingObj)
    {
        if (containingObj == null || currentProjectState.selectedPolygon == null)
        {
            return;
        }
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                
                if (!currentProjectState.selectedPolygon.polygon.contains(column,row))
                {
                    continue;
                }
                //if (pointIsInsideContainedPolygon(column,row))
                //{
                    //continue;
                //}
                ArrayList<MaskedObject> containingObjects = getAllContainingObjects(column, row);
                
                if (containingObjects.size() == 1)
                {
                    colorPixel(column,row,containingObj);
                }
                else
                {
                    boolean topLayer = true;
                    double selectedDepth = currentProjectState.selectedPolygon.depth;
                    for (MaskedObject p : containingObjects)
                    {
                        if (p.depth > selectedDepth)
                        {
                            topLayer = false;
                            break;
                        }
                    }
                    if (topLayer)
                    {
                        colorPixel(column,row,containingObj);
                    }
                }
            }
        }
    }
    
    public int constrain(int value)
    {
        if (value > 255)
        {
            value = 255;
        }
        else if (value < 0)
        {
            value = 0;
        }
        return value;
    }
    
    // the following function takes way too long if there are many objects with many vertices
    public void colorizeImageByLayers()
    {
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                ArrayList<MaskedObject> containingObjects = getAllContainingObjects(column, row);
                if (containingObjects.isEmpty()) 
                {
                    Pixel p = getPixel(column,row,image_pixels);
                    int average = p.getAverage();
                    if (average == p.getRedValue() && average == p.getBlueValue())
                    {
                        // already gray
                    }
                    else
                    {
                        colorPixel(column,row,null);
                    }
                    continue;
                }
                
                double highestDepth = containingObjects.get(0).depth;
                Color winningColor = containingObjects.get(0).color;
                MaskedObject winningObject = containingObjects.get(0);
                for (MaskedObject p : containingObjects)
                {
                    if (p.depth > highestDepth)
                    {
                        highestDepth = p.depth;
                        winningColor = p.color;
                        winningObject = p;
                    }
                }
                colorPixel(column,row,winningObject);
            }
            //System.out.println();
        }
        repaint();
    }
    
    public int getNumberColumns()
    {
        try
        {
            int cols = Integer.parseInt(numberMosaicColumnsBox.getText());
            return cols;
        }
        catch (Exception e)
        {
            return 30;
        }
    }
    
    public int getMosaicValue(int value, int x, int y, int mosaicConstant, double portionGradient)
    {
        if (x < portionGradient * cellWidth)
        {
            value += x*mosaicConstant;
        }
        if (y > (1-portionGradient) * cellHeight)
        {
            value -= y*mosaicConstant;
        }
        return constrain(value);
    }
    
    public void mosaic2()
    {
        int columns = getNumberColumns();
        
        cellWidth = (image_pixels.getWidth()/(columns+0.0));
        cellHeight = cellWidth;
        int rows = (int)(image_pixels.getHeight()/cellWidth);
        for (int i = 0 ; i < rows+1; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                int redCount = 0;
                int greenCount = 0;
                int blueCount = 0;
                
                for (int k = 0; k < cellHeight; k++)
                {
                    if (k+i*cellHeight >=image_pixels.getHeight())
                    {
                        continue;
                    }
                    for (int l = 0; l < cellWidth; l++)
                    {
                        Pixel p = getPixel((int)(j*cellWidth+l),(int)(i*cellHeight+k),image_pixels);
                        redCount += p.getRedValue();
                        greenCount += p.getGreenValue();
                        blueCount += p.getBlueValue();
                    }
                }
                int redGroupAverage;
                int greenGroupAverage;
                int blueGroupAverage;
                if (i == rows)
                {
                    redGroupAverage = (int)(redCount / ((image_pixels.getHeight()-i*cellHeight) * (int)cellWidth));
                    greenGroupAverage = (int)(greenCount / ((image_pixels.getHeight()-i*cellHeight) * (int)cellWidth));
                    blueGroupAverage = (int)(blueCount / ((image_pixels.getHeight()-i*cellHeight) * (int)cellWidth));
                }
                else
                {
                    redGroupAverage = (int)(redCount / ((int)(cellHeight) * (int)cellWidth));
                    greenGroupAverage = (int)(greenCount / ((int)(cellHeight) * (int)cellWidth));
                    blueGroupAverage = (int)(blueCount / ((int)(cellHeight) * (int)cellWidth));
                }
                
                if (redGroupAverage > 255 || redGroupAverage < 0)
                {
                    //System.out.println("error red");
                    redGroupAverage = constrain(redGroupAverage);
                }
                if (greenGroupAverage > 255 || greenGroupAverage < 0)
                {
                    //System.out.println("error green");
                    greenGroupAverage = constrain(greenGroupAverage);
                }
                if (blueGroupAverage > 255 || blueGroupAverage < 0)
                {
                    //System.out.println("error blue");
                    blueGroupAverage = constrain(blueGroupAverage);
                }
                for (int k = 0; k < cellHeight; k++)
                {
                    if (k+i*cellHeight >=image_pixels.getHeight())
                    {
                        continue;
                    }
                    for (int l = 0; l < cellWidth; l++)
                    {
                        Pixel p = getPixel((int)(j*cellWidth+l),(int)(i*cellHeight+k),image_pixels);
                        
                        //if (k == 0 || l == 0)
                        {
                            // Golden grid
                            //p.setRGB(124, 112, 64);
                        }
                        //else 
                        {
                            int red = getMosaicValue(redGroupAverage, l, k, 4, .25);
                            int green = getMosaicValue(greenGroupAverage, l, k, 4, .25);
                            int blue = getMosaicValue(blueGroupAverage, l, k, 4, .25);
                            p.setRGB(red, green, blue);
                        }
                    }
                }

                //System.out.print(getAsciiFromGrayscale(pixelGroupAverage));
            }
            //System.out.println();
        }
    }
    
    public void mosaic1()
    {
        int columns = getNumberColumns();
        
        cellWidth = (image_pixels.getWidth()/(columns+0.0));
        cellHeight = cellWidth;
        int rows = (int)(image_pixels.getHeight()/cellWidth);
        for (int i = 0 ; i < rows+1; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                int redCount = 0;
                int greenCount = 0;
                int blueCount = 0;
                
                for (int k = 0; k < cellHeight; k++)
                {
                    if (k+i*cellHeight >=image_pixels.getHeight())
                    {
                        continue;
                    }
                    for (int l = 0; l < cellWidth; l++)
                    {
                        Pixel p = getPixel((int)(j*cellWidth+l),(int)(i*cellHeight+k),image_pixels);
                        redCount += p.getRedValue();
                        greenCount += p.getGreenValue();
                        blueCount += p.getBlueValue();
                    }
                }
                int redGroupAverage;
                int greenGroupAverage;
                int blueGroupAverage;
                if (i == rows)
                {
                    redGroupAverage = (int)(redCount / ((image_pixels.getHeight()-i*cellHeight) * (int)cellWidth));
                    greenGroupAverage = (int)(greenCount / ((image_pixels.getHeight()-i*cellHeight) * (int)cellWidth));
                    blueGroupAverage = (int)(blueCount / ((image_pixels.getHeight()-i*cellHeight) * (int)cellWidth));
                }
                else
                {
                    redGroupAverage = (int)(redCount / ((int)(cellHeight) * (int)cellWidth));
                    greenGroupAverage = (int)(greenCount / ((int)(cellHeight) * (int)cellWidth));
                    blueGroupAverage = (int)(blueCount / ((int)(cellHeight) * (int)cellWidth));
                }
                
                if (redGroupAverage > 255 || redGroupAverage < 0)
                {
                    //System.out.println("error red");
                    redGroupAverage = constrain(redGroupAverage);
                }
                if (greenGroupAverage > 255 || greenGroupAverage < 0)
                {
                    //System.out.println("error green");
                    greenGroupAverage = constrain(greenGroupAverage);
                }
                if (blueGroupAverage > 255 || blueGroupAverage < 0)
                {
                    //System.out.println("error blue");
                    blueGroupAverage = constrain(blueGroupAverage);
                }
                for (int k = 0; k < cellHeight; k++)
                {
                    if (k+i*cellHeight >=image_pixels.getHeight())
                    {
                        continue;
                    }
                    for (int l = 0; l < cellWidth; l++)
                    {
                        Pixel p = getPixel((int)(j*cellWidth+l),(int)(i*cellHeight+k),image_pixels);
                        
                        if (k == 0 || l == 0)
                        {
                            // Golden grid
                            p.setRGB(124, 112, 64);
                        }
                        else 
                        {
                            int red = getMosaicValue(redGroupAverage, l, k, 2, 1);
                            int green = getMosaicValue(greenGroupAverage, l, k, 2, 1);
                            int blue = getMosaicValue(blueGroupAverage, l, k, 2, 1);
                            p.setRGB(red, green, blue);
                        }
                    }
                }

                //System.out.print(getAsciiFromGrayscale(pixelGroupAverage));
            }
            //System.out.println();
        }
    }
    
    public void pixelate()
    {
        int columns = getNumberColumns();
        
        int cellWidth = (int)(image_pixels.getWidth()/(columns+0.0));
        int cellHeight = cellWidth;
        int rows = (int)(image_pixels.getHeight()/cellWidth);
        for (int i = 0 ; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                int redCount = 0;
                int greenCount = 0;
                int blueCount = 0;
                
                for (int k = 0; k < cellHeight; k++)
                {
                    for (int l = 0; l < cellWidth; l++)
                    {
                        Pixel p = getPixel((int)(j*cellWidth+l),(int)(i*cellHeight+k),image_pixels);
                        redCount += p.getRedValue();
                        greenCount += p.getGreenValue();
                        blueCount += p.getBlueValue();
                    }
                }
                int redGroupAverage = (int)(redCount / (cellHeight * cellWidth));
                int greenGroupAverage = (int)(greenCount / (cellHeight * cellWidth));
                int blueGroupAverage = (int)(blueCount / (cellHeight * cellWidth));
                if (redGroupAverage > 255 || redGroupAverage < 0)
                {
                    System.out.println("error red");
                }
                if (greenGroupAverage > 255 || greenGroupAverage < 0)
                {
                    System.out.println("error green");
                }
                if (blueGroupAverage > 255 || blueGroupAverage < 0)
                {
                    System.out.println("error blue");
                }
                for (int k = 0; k < cellHeight; k++)
                {
                    for (int l = 0; l < cellWidth; l++)
                    {
                        Pixel p = getPixel((int)(j*cellWidth+l),(int)(i*cellHeight+k),image_pixels);
                        p.setRGB(redGroupAverage, greenGroupAverage, blueGroupAverage);

                    }
                }

                //System.out.print(getAsciiFromGrayscale(pixelGroupAverage));
            }
            //System.out.println();
        }
    }
    
    public void gradient()
    {
        
        for (int i = 0; i < image_pixels.getHeight()-1; i++)
        {
            for (int j = 0; j < image_pixels.getWidth()-1; j++)
            {
                
                Pixel p = getPixel(j,i,image_pixels);
                Pixel p2 = getPixel(j+1,i+1,image_pixels);

                int redDiff = p.getRedValue() - p2.getRedValue();
                int greenDiff = p.getGreenValue() - p2.getGreenValue();
                int blueDiff = p.getBlueValue() - p2.getBlueValue();
                
                int average = redDiff + greenDiff + blueDiff;
                average /= 3;
                average += 128;
                if (average < 0) average = 0;
                if (average > 255) average = 255;
                
                if (redDiff < 0) redDiff = 0;
                if (redDiff > 255) redDiff = 255;
                if (greenDiff < 0) greenDiff = 0;
                if (greenDiff > 255) greenDiff = 255;
                if (blueDiff < 0) blueDiff = 0;
                if (blueDiff > 255) blueDiff = 255;
                
                 
                
                p.setRGB(average, average, average);

                

                //System.out.print(getAsciiFromGrayscale(pixelGroupAverage));
            }
            //System.out.println();
        }
    }
    
    public void countPatterns()
    {
        //if (frameCounter % 100 == 0)
        {
            System.out.println("--- Frames Counted: " + frameCounter);
            int size = patterns.size();

            //if (frameCounter % 24 == 0)
            {
                System.out.println("--- Patterns seen: " + size);
            }
        
            
            int duplicateCounter = 0;
            
            Iterator<Map.Entry<String, Integer>> it = patterns.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                Integer val = entry.getValue();
                if (val > 1)
                {
                    duplicateCounter++;
                }
            }   
            System.out.println("--- Patterns that occur multiple times: " + duplicateCounter);
        }
        
        frameCounter++;

        for (int row = 1; row < image_pixels.getHeight()-1; row++)
        {
            for (int column = 1; column < image_pixels.getWidth()-1; column++)
            {
                // p1 p2 p3
                // p4 p5 p6
                // p7 p8 p9
                
                Pixel p = getPixel(column,row,image_pixels);
                int r = p.getRedValue();
                int g = p.getGreenValue();
                
                
                //Pixel p1 = getPixel(column-1,row-1,image_pixels);
                //Pixel p2 = getPixel(column,row-1,image_pixels);
                //Pixel p3 = getPixel(column+1,row-1,image_pixels);
                Pixel p4 = getPixel(column-1,row,image_pixels);
                Pixel p5 = getPixel(column,row,image_pixels);
                Pixel p6 = getPixel(column+1,row,image_pixels);
                //Pixel p7 = getPixel(column-1,row+1,image_pixels);
                Pixel p8 = getPixel(column,row+1,image_pixels);
                //Pixel p9 = getPixel(column+1,row+1,image_pixels);


                String key    
                    = //p1.getAverage() + ","
                    //+ p2.getAverage() + ","
                    //+ p3.getAverage() + ","
                    + p4.getAverage() + ","
                    + p5.getAverage() + ","
                    + p6.getAverage() + ","
                    //+ p7.getAverage() + ","
                    + p8.getAverage() + ","
                    //+ p9.getAverage()
                        ;
                
                Object value = patterns.get(key);
                if (value == null) {
                    patterns.put(key, 1);
                } else {
                    patterns.put(key,(int)value + 1);
                } 
            }
        }
        
    }
    
    public boolean redValueIsSufficientlyRepresentedInData(int r)
    {
        return redRepresentation[r] >= 100000;
    }
    
    public boolean greenValueIsSufficientlyRepresentedInData(int g)
    {
        return greenRepresentation[g] >= 100000;
    }
    
    public void createRedGreenTrainingDataFromImage(FileWriter fw, FileWriter fw2) throws IOException
    {
        for (int row = 1; row < image_pixels.getHeight()-1; row++)
        {
            for (int column = 1; column < image_pixels.getWidth()-1; column++)
            {
                // p1 p2 p3
                // p4 p5 p6
                // p7 p8 p9
                
                Pixel p = getPixel(column,row,image_pixels);
                int r = p.getRedValue();
                int g = p.getGreenValue();
                
                if (!redValueIsSufficientlyRepresentedInData(r) || !greenValueIsSufficientlyRepresentedInData(g))
                {
                    Pixel p1 = getPixel(column-1,row-1,image_pixels);
                    Pixel p2 = getPixel(column,row-1,image_pixels);
                    Pixel p3 = getPixel(column+1,row-1,image_pixels);
                    Pixel p4 = getPixel(column-1,row,image_pixels);
                    Pixel p5 = getPixel(column,row,image_pixels);
                    Pixel p6 = getPixel(column+1,row,image_pixels);
                    Pixel p7 = getPixel(column-1,row+1,image_pixels);
                    Pixel p8 = getPixel(column,row+1,image_pixels);
                    Pixel p9 = getPixel(column+1,row+1,image_pixels);

                    if (!redValueIsSufficientlyRepresentedInData(r))
                    {
                        fw.append(r + ","
                                + p1.getAverage() + ","
                                + p2.getAverage() + ","
                                + p3.getAverage() + ","
                                + p4.getAverage() + ","
                                + p5.getAverage() + ","
                                + p6.getAverage() + ","
                                + p7.getAverage() + ","
                                + p8.getAverage() + ","
                                + p9.getAverage() + "\n"
                        );
                        redRepresentation[r] += 1;

                    }
                    
                    if (!greenValueIsSufficientlyRepresentedInData(g))
                    {
                        fw2.append(g + ","
                                + p1.getAverage() + ","
                                + p2.getAverage() + ","
                                + p3.getAverage() + ","
                                + p4.getAverage() + ","
                                + p5.getAverage() + ","
                                + p6.getAverage() + ","
                                + p7.getAverage() + ","
                                + p8.getAverage() + ","
                                + p9.getAverage() + "\n"
                        );
                        greenRepresentation[g] += 1;

                    }
                }
            }
        }
    }
    
    public char getAsciiFromGrayscale(int grayscale_value)
    {
        char ascii = ' ';
        boolean dark_text_on_light = false;
        int negative_factor;
        int sign;
        if (dark_text_on_light)
        {
            negative_factor = 255;
            sign = -1;
        }
        else
        {
            negative_factor = 0;
            sign = 1;
        }
        if (negative_factor+(sign*grayscale_value) < 25)
        {
            ascii = ' ';
        }
        else if (negative_factor+(sign*grayscale_value) < 42)
        {
            // pixels in char: 14
            ascii = (char)96;
        }
        else if (negative_factor+(sign*grayscale_value) < 50)
        {
            // pixels in char: 16
            ascii = (char)183;
        }
        else if (negative_factor+(sign*grayscale_value) < 58)
        {
            // pixels in char: 21
            ascii = (char)39;
        }
        else if (negative_factor+(sign*grayscale_value) < 75)
        {
            // pixels in char: 32
            ascii = (char)58;
        }
        else if (negative_factor+(sign*grayscale_value) < 87)
        {
            // pixels in char: 39
            ascii = (char)451;
        }
        else if (negative_factor+(sign*grayscale_value) < 100)
        {
            // pixels in char: 48
            ascii = (char)34;
        }
        else if (negative_factor+(sign*grayscale_value) < 113)
        {
            // pixels in char: 56
            ascii = (char)645;
        }
        else if (negative_factor+(sign*grayscale_value) < 125)
        {
            // pixels in char: 64
            ascii = (char)247;
        }
        else if (negative_factor+(sign*grayscale_value) < 38)
        {
            // pixels in char: 72
            ascii = (char)177;
        }
        else if (negative_factor+(sign*grayscale_value) < 150)
        {
            // pixels in char: 80
            ascii = (char)70;
        }
        else if (negative_factor+(sign*grayscale_value) < 163)
        {
            // pixels in char: 88
            ascii = (char)165;
        }
        else if (negative_factor+(sign*grayscale_value) < 175)
        {
            // pixels in char: 96
            ascii = (char)80;
        }
        else if (negative_factor+(sign*grayscale_value) < 188)
        {
            // pixels in char: 104
            ascii = (char)377;
        }
        else if (negative_factor+(sign*grayscale_value) < 200)
        {
            // pixels in char: 112
            ascii = (char)439;
        }
        else if (negative_factor+(sign*grayscale_value) < 213)
        {
            // pixels in char: 120
            ascii = (char)412;
        }
        else if (negative_factor+(sign*grayscale_value) < 225)
        {
            // pixels in char: 128
            ascii = (char)557;
        }
        else if (negative_factor+(sign*grayscale_value) < 237)
        {
            // pixels in char: 136
            ascii = (char)372;
        }
        else
        {
            // pixels in char: 143
            ascii = (char)582;
        }
        return ascii;
    }
    
    public String AsciiFilter()
    {
        StringBuilder sb = new StringBuilder();
        double cellHeight = image_pixels.getHeight()/(AsciiRows+0.0);
        double cellWidth = image_pixels.getWidth()/(AsciiCols+0.0);
        for (int i = 0 ; i < AsciiRows; i++)
        {
            for (int j = 0; j < AsciiCols; j++)
            {
                int pixelGroupValueCount = 0;
                for (int k = 0; k < cellHeight; k++)
                {
                    for (int l = 0; l < cellWidth; l++)
                    {
                        Pixel p = getPixel((int)(j*cellWidth+l),(int)(i*cellHeight+k),image_pixels);
                        int average = p.getAverage();
                        pixelGroupValueCount += average;
                    }
                }
                int pixelGroupAverage = (int)(pixelGroupValueCount / (cellHeight * cellWidth));
                sb.append(getAsciiFromGrayscale(pixelGroupAverage));
                //System.out.print(getAsciiFromGrayscale(pixelGroupAverage));
            }
            //System.out.println();
            sb.append("\n");
        }
        return sb.toString();
    }
    
    
    private String getClosestPixelByNeighborMean(int grayPixAverage, double neighborMean)
    {
        ArrayList<Pair> meanMap = neighborMeanMap[grayPixAverage];
        if (meanMap == null) return "no map";
        int upperBound = meanMap.size();
        int lowerBound = 0;
        while (Math.abs(upperBound - lowerBound) > 1)
        {
            int guess = (upperBound + lowerBound) / 2;
            if (meanMap.get(guess).key > neighborMean)
            {
                upperBound = guess;
            }
            else
            {
                lowerBound = guess;
            }
        }
        
        return meanMap.get(lowerBound).value;
                
    }
    
    
    private void OptimizeInterpolationColorize()
    {
        
        image_copy = getImageCopy();

        for (int row = 1; row < image_copy.getHeight()-1; row++)
        {
            for (int column = 1; column < image_copy.getWidth()-1; column++)
            {
                Pixel grayPixel = getPixel(column,row,image_pixels);
                int grayPixAverage = grayPixel.getAverage();

                int right = getPixel(column + 1, row, image_copy).getAverage();
                int down = getPixel(column, row + 1, image_copy).getAverage();
                int left = getPixel(column - 1, row, image_copy).getAverage();
                int up = getPixel(column, row - 1, image_copy).getAverage();
                
                
                
                Pixel p = null;
                if (optInterpolationDictionary[grayPixAverage] != null)
                {
                    if (optInterpolationDictionary[grayPixAverage][right] != null)
                    {
                        if (optInterpolationDictionary[grayPixAverage][right][down] != null)
                        {
                            if (optInterpolationDictionary[grayPixAverage][right][down][left] != null)
                            {
                                if (optInterpolationDictionary[grayPixAverage][right][down][left][up] != null)
                                {
                            
                                    if (!optInterpolationDictionary[grayPixAverage][right][down][left][up].isEmpty())
                                    {
                                        if (FirstMappingOnly.isSelected())
                                        {
                                            p = optInterpolationDictionary[grayPixAverage][right][down][left][up].get(0);
                                        }
                                        else
                                        {
                                            p = getMode(optInterpolationDictionary[grayPixAverage][right][down][left][up]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (p == null)
                {
                    // Interpolate                    
                    double neighborMean = (right + down + left + up) / 4.0;
                    
                    String closestPattern = getClosestPixelByNeighborMean(grayPixAverage, neighborMean);
                    if (!closestPattern.equals("no map"))
                    {
                        // Parse pattern
                        Scanner patternParser = new Scanner(closestPattern);
                        int closestCenter = patternParser.nextInt();
                        int closestRight = patternParser.nextInt();
                        int closestDown = patternParser.nextInt();
                        int closestLeft = patternParser.nextInt();
                        int closestUp = patternParser.nextInt();
                        if (FirstMappingOnly.isSelected())
                        {
                            p = optInterpolationDictionary[closestCenter][closestRight][closestDown][closestLeft][closestUp].get(0);
                        }
                        else
                        {                        
                            p = getMode(optInterpolationDictionary[closestCenter][closestRight][closestDown][closestLeft][closestUp]);
                        }
                    }
                }
                if (p != null)
                {
                    grayPixel.setRGB(p.red, p.green, p.blue);
                
                }
                else
                {
                    grayPixel.setRGB(255,255,255);
                }
            }
        }

    }
    
    double getSurroundingMean(String key)
    {
        int index1 = key.indexOf("*");
        double mean = Double.parseDouble(key.substring(index1+1));
        return mean;
    }
    
    public BufferedImage NeighborKeyColorize(BufferedImage image_pixels)
    {
        if (keyList.getSelectedItem().equals("Optimized Interpolate"))
        {
            OptimizeInterpolationColorize();
            return null;
        }
        
        BufferedImage image_copy = getImageCopy(image_pixels);

        if (FirstMappingOnly.isSelected() == false && alreadyFoundModes == false)
        {
            System.out.println("Starting finding modes...");
            int counter = 0;
            int size = neighbor_key_dictionary.size();
            for (Map.Entry pair : neighbor_key_dictionary.entrySet()) {
                if (counter % 10000 == 0)
                {
                    System.out.println(counter + " / " + size);
                }
                counter++;
                String key = (String)(pair.getKey());
                HashMap<Pixel,Integer> value = (HashMap<Pixel,Integer>)(pair.getValue());
                Pixel mode = getModeHashMap(value);
                neighbor_key_dictionary_1to1.put(key,mode);
                //it.remove(); // avoids a ConcurrentModificationException
            }
            System.out.println("Finished finding modes");
            alreadyFoundModes = true;
        }
        

        for (int row = 1; row < image_pixels.getHeight()-1; row++)
        {
            for (int column = 1; column < image_pixels.getWidth()-1; column++)
            {
                if (currentProjectState.selectedPolygon != null 
                        && !currentProjectState.selectedPolygon.polygon.contains(column,row))
                {
                    continue;
                }
                Pixel grayPixel = getPixel(column,row,image_copy);
                String key = getColorizationKey(image_copy, row, column, grayPixel);
                        
                Pixel p = null;
                if (FirstMappingOnly.isSelected() == true)
                {
                    HashMap<Pixel,Integer> pxls = neighbor_key_dictionary.get(key);
                    if (pxls != null)
                    {
                        Set<Pixel> keys = pxls.keySet();
                        int size = pxls.size();
                        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
                        int i = 0;
                        for(Pixel obj : keys)
                        {
                            if (i == item)
                            {
                                p = obj;
                                break;
                            }
                            i++;
                        }
                    }
                    
                }
                else
                {
                    p = neighbor_key_dictionary_1to1.get(key);
                }
                if (p != null)
                {
                    grayPixel.setRGB(p.red, p.green, p.blue, image_pixels);
                }
                else
                {
                    if (InterpolateGapsCheckbox.isSelected())
                    {
                        Map.Entry closestPair = null;
                        double closestDistance = 255;
                        int grayPixAverage = grayPixel.getAverage();
                        
                        
                        double mean = getSurroundingMean(key);
                        
                        
                        for (Map.Entry pair : neighbor_key_dictionary.entrySet()) 
                        {
                            String key2 = (String)pair.getKey();
                            int index2 = key2.indexOf("_");
                            int value = Integer.parseInt(key2.substring(0,index2));
                            if (value == grayPixAverage)
                            {
                                double entrymean = getSurroundingMean((String)(pair.getKey()));
                                if (Math.abs(mean - entrymean) < closestDistance)
                                {
                                    closestDistance = Math.abs(mean - entrymean);
                                    closestPair = pair;
                                }
                            }
                        }
                        if (closestPair == null)
                        {
                            if (FillInBlanksBox.isSelected())
                            {
                                int average = (grayPixel.red + grayPixel.green + grayPixel.blue)/3;
                                grayPixel.setRGB(average,average,average,image_pixels);
                            }
                            else
                            {
                                grayPixel.setRGB(255,255,255, image_pixels);
                            }
                        }
                        else
                        {
                            Pixel p1 = getModeHashMap((HashMap)(closestPair.getValue()));
                            grayPixel.setRGB(p1.getRedValue(),p1.getGreenValue(),p1.getBlueValue(),image_pixels);
                        }
                    }
                    else if (FillInBlanksBox.isSelected())
                    {
                        int average = (grayPixel.red + grayPixel.green + grayPixel.blue)/3;
                        grayPixel.setRGB(average,average,average, image_pixels);
                    }
                    else
                    {
                        grayPixel.setRGB(255,255,255, image_pixels);
                    }
                }
            }
        }
        //System.out.println("Colorized a frame");
        return image_pixels;
    }
    
    public void SumBoundingBoxColorize()
    {
        int boxHeight = 5;
        int boxWidth = 5;
        for (int row = boxHeight/2; row < image_pixels.getHeight()-boxHeight/2; row++)
        {
            for (int column = boxWidth/2; column < image_pixels.getWidth()-boxWidth/2; column++)
            {        
                Pixel pixel = getPixel(column,row,image_pixels);
                int sum = 0;
                for (int i = -(boxHeight/2); i < (boxHeight+1)/2; i++)
                {
                    for (int j = -(boxWidth/2); j < (boxWidth+1)/2; j++)
                    {
                        Pixel p = getPixel(column+i,row+j,image_pixels);
                        int red = p.getRedValue();
                        int green = p.getGreenValue();
                        int blue = p.getBlueValue();
                        sum += (red + green + blue)/3;
                    } 
                }
                
                int r = 255;
                int g = 255;
                int b = 255;
                
                if (colorizationArray[sum] != null)
                {
                    r = colorizationArray[sum].getRedValue();
                    g = colorizationArray[sum].getGreenValue();
                    b = colorizationArray[sum].getBlueValue();
                }
                
                pixel.setRGB(r, g, b);
            }
        }
    }
    
    public void RedGreen(String rg, BufferedImage copy)
    {
        if (rg.equals("red"))
        {
            for (int row = 0; row < image_pixels.getHeight(); row++)
            {
                for (int column = 0; column < image_pixels.getWidth(); column++)
                {
                    Pixel pixel = getPixel(column,row,copy);
                    int r = pixel.getRedValue();
                    int g = pixel.getGreenValue();
                    int b = pixel.getBlueValue();
                    int average = (r+g+b)/3;
                    if (average > 255)
                    {
                        average = 255;
                    }
                    //pixel.setRGB(average, 0, 0, image_pixels);
                    pixel.setRGB(r,0,0);// also try
                }
            }
        }
        else if (rg.equals("green"))
        {
            for (int row = 0; row < image_pixels.getHeight(); row++)
            {
                for (int column = 0; column < image_pixels.getWidth(); column++)
                {
                    Pixel pixel = getPixel(column,row,copy);
                    int r = pixel.getRedValue();
                    int g = pixel.getGreenValue();
                    int b = pixel.getBlueValue();
                    int average = (r+g+b)/3 + 20;
                    if (average > 255)
                    {
                        average = 255;
                    }
                    //pixel.setRGB(0, average, 0, image_pixels);
                    pixel.setRGB(0,g,0);
                }
            }
        }
        else if (rg.equals("blue"))
        {
            for (int row = 0; row < image_pixels.getHeight(); row++)
            {
                for (int column = 0; column < image_pixels.getWidth(); column++)
                {
                    Pixel pixel = getPixel(column,row,copy);
                    int r = pixel.getRedValue();
                    int g = pixel.getGreenValue();
                    int b = pixel.getBlueValue();
                    int average = (r+g+b)/3;
                    //pixel.setRGB(0, 0, average, image_pixels);
                    pixel.setRGB(0,0,b);
                }
            }
        }
    }
    
    
    public void BlackWhite(int bwvalue) {
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel sourcePixel = getPixel(column,row,image_copy);
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = sourcePixel.getRedValue();
                int g = sourcePixel.getGreenValue();
                int b = sourcePixel.getBlueValue();                
                int averageRGB = (r + g + b)/3;
                
                int divider = bwvalue;
                int value = 0;
                if (averageRGB > divider)
                {
                    value = 255;
                }
                else
                {
                    value = 0;
                }
                
                pixel.setRGB(value, value, value);

            }
        }
    }
    
    
    public void RandomValues() {
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue();                
                int sumRGB = r + g + b;
                // my randomization technique for getting an r g and b that add 
                // up to the grayscale sum:
                // think in terms of percents. a percent of the sum will be red,
                // a percent will be green, and another blue. To make it as close
                // to random as possible, simply choose 2 random numbers between
                // 0 and 100. these will be the dividing lines between percentages
                // of the whole
                int new_red_value = 256;
                int new_green_value = 256;
                int new_blue_value = 256;
                while (new_red_value > 255 || new_green_value > 255 || new_blue_value > 255)
                {
                    double divider1 = Math.random();
                    double divider2 = Math.random();
                    double percent1 = 0;
                    double percent2 = 0;
                    double percent3 = 0;
                    if (divider1 >= divider2)
                    {
                        percent3 = 1 - divider1;
                        percent1 = divider2;
                        percent2 = divider1 - divider2;
                    }
                    else
                    {
                        percent3 = 1 - divider2;
                        percent1 = divider1;
                        percent2 = divider2 - divider1;
                    }

                    new_red_value = (int)(sumRGB * percent1);
                    new_green_value = (int)(sumRGB * percent2);
                    new_blue_value = (int)(sumRGB * percent3);
                }
                pixel.setRGB(new_red_value, new_green_value, new_blue_value);
            }
        }
    }
    
    public void Colorize() {
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue();                
                int average_value = (r + g + b) / 3;

                int new_red_value = colorizationArray[average_value].getRedValue();
                int new_green_value = colorizationArray[average_value].getGreenValue();
                int new_blue_value = colorizationArray[average_value].getBlueValue();
                pixel.setRGB(new_red_value, new_green_value, new_blue_value);
            }
        }
    }
    
    public void InvertColors() {
        
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int new_red_value = 255-pixel.getRedValue();
                int new_green_value = 255-pixel.getGreenValue();
                int new_blue_value = 255-pixel.getBlueValue();
                pixel.setRGB(new_red_value, new_green_value, new_blue_value);
            }
        }
        
    }

    public void ConvertToGrayScale() {
        
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                if (currentProjectState.selectedPolygon != null && !currentProjectState.selectedPolygon.polygon.contains(column,row))
                {
                    continue;
                }
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue();
                int average_value = (r + g + b) / 3;
                pixel.setRGB(average_value, average_value, average_value);
                //System.out.print(255-average_value + ",");
            }
        }
    }

    public void increaseBWContrast() {
        
        int max = 0;
        int min = 255;
        
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue();
                int average_value = (r + g + b) / 3;
                
                if (average_value < min)
                {
                    min = average_value;
                }
                if (average_value > max)
                {
                    max = average_value;
                }
            }
        }
        int middle = (max + min)/2;
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue();
                int average_value = (r + g + b) / 3;
                int contrastValue;
                if (average_value < middle)
                    contrastValue = 0;
                else                    
                {
                    contrastValue = 255;
                }
                //contrastValue = (int)((((average_value - min)*1.0)/(max - min))*255);
                pixel.setRGB(contrastValue, contrastValue, contrastValue);
                //System.out.print(255-average_value + ",");
            }
        }
    }
    
    public void StepColors() {
        
        int stepAmount = 60;
        
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int redRemainder = r%stepAmount;
                int g = pixel.getGreenValue();
                int greenRemainder = g%stepAmount;
                int b = pixel.getBlueValue();
                int blueRemainder = b%stepAmount; 
                int newRed = r - redRemainder;
                if (redRemainder > stepAmount / 2.0)
                {
                    newRed += stepAmount;
                }
                int newGreen = g - greenRemainder;
                if (greenRemainder > stepAmount / 2.0)
                {
                    newGreen += stepAmount;
                }
                int newBlue = b - blueRemainder;
                if (blueRemainder > stepAmount / 2.0)
                {
                    newBlue += stepAmount;
                }
                pixel.setRGB(newRed, newGreen, newBlue);
            }
        }
    }
    
    public void EmbossImage() {
                
        // in case you'd like to reference the original image without altering it
        // use this image_copy to look at values and image_pixels to set the values
        image_copy = getImageCopy();

        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth()-1; column++)
            {
                int color_value = 128;
                Pixel pixel = getPixel(column,row,image_pixels);
                if (row < image_pixels.getHeight()-1 && column < image_pixels.getWidth()-1)
                {  
                    Pixel upper_left_pixel = getPixel(column+1,row+1,image_copy);

                    int redDiff = pixel.getRedValue() - upper_left_pixel.getRedValue();
                    int greenDiff = pixel.getGreenValue() - upper_left_pixel.getGreenValue();
                    int blueDiff = pixel.getBlueValue() - upper_left_pixel.getBlueValue();

                    int maxDifference = redDiff;
                    if (Math.abs(greenDiff) > Math.abs(maxDifference)) maxDifference = greenDiff;
                    if (Math.abs(blueDiff) > Math.abs(maxDifference)) maxDifference = blueDiff;
                    color_value = maxDifference + 128;

                    if (color_value < 0) color_value = 0;
                    if (color_value > 255) color_value = 255;
                }
                pixel.setRGB(color_value, color_value, color_value);
            }
        }
    }
    
    public void BlurImage()
    {
        // in case you'd like to reference the original image without altering it
        // use this image_copy to look at values and image_pixels to set the values
        image_copy = getImageCopy();
        
        int blurAmount = 5;
        for (int row = blurAmount; row < image_pixels.getHeight()-blurAmount; row++)
        {
            for (int column = blurAmount; column < image_pixels.getWidth()-blurAmount; column++)
            {
                Pixel currentPixel = getPixel(column,row,image_pixels);
                int red = 0;
                int green = 0;
                int blue = 0;
                for (int i = -(blurAmount/2); i < (blurAmount+1)/2; i++)
                {
                    for (int j = -(blurAmount/2); j < (blurAmount+1)/2; j++)
                    {
                        Pixel p = getPixel(column+i,row+j,image_copy);
                        red += p.getRedValue();
                        green += p.getGreenValue();
                        blue += p.getBlueValue();
                    } 
                }
                int blurSquared = blurAmount*blurAmount;
                currentPixel.setRGB(red/blurSquared, green/blurSquared, blue/blurSquared);
            }
        }
    }

    public void ColorizeRed() {
        int increaseAmount = 50;
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue() + 10;//(int)(Math.random()*increaseAmount);
                if (r > 255)
                {
                    r = 255;
                }
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue();
                pixel.setRGB(r, g, b);
            }
        }
    }
    
    public void ColorizeGreen() {
        int increaseAmount = 50;
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue() + 10;//(int)(Math.random()*increaseAmount);
                if (g > 255)
                {
                    g = 255;
                }
                int b = pixel.getBlueValue();
                pixel.setRGB(r, g, b);
            }
        }
    }
    
    public void ColorizeBlue() {
        int increaseAmount = 50;
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue() + 10;//(int)(Math.random()*increaseAmount);
                if (b > 255)
                {
                    b = 255;
                }
                pixel.setRGB(r, g, b);
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform saveTransform = g2d.getTransform();
        g.setColor(Color.gray);
        g.fillRect(0, 0, 500, 300);
        try {
            AffineTransform scaleMatrix = new AffineTransform();
            scaleMatrix.scale(scale, scale);
            
            int screenWidth = frame.getBounds().width;
            int screenHeight = frame.getBounds().height;
            int imgLeftX = 0;
            int imgTopY = 0;
            g.setColor(Color.gray);
            g.fillRect(0, 0, screenWidth, screenHeight);
            if (selected_image != null)
            {
                imgLeftX = screenWidth/2 - selected_image.getWidth(null)/2;
                imgTopY = screenHeight/2 - selected_image.getHeight(null)/2;
            }
            g2d.setStroke(new BasicStroke((int)(1/scale)));

            scaleMatrix.translate(leftRight*-scrollAmount, upDown*-scrollAmount);
            scaleMatrix.translate((screenWidth/2)/scale, (screenHeight/2)/scale);

            g2d.setTransform(scaleMatrix);
        
            if (selected_image != null)
            {
                g.drawImage(selected_image, 0, 0, this);            
            }

            if (ShowOutlinesCheckbox.isSelected())
            {
                for (MaskedObject p : currentProjectState.polygons)
                {
                    g.setColor(Color.BLUE);
                    if (p.equals(currentProjectState.selectedPolygon))
                    {
                        g.setColor(Color.RED);
                    }
                    if (p.equals(currentProjectState.adjacentPolygon))
                    {
                        g.setColor(Color.GREEN);
                    }
                    if (currentProjectState.tempAutoCompletePolygon == null)
                    {
                        g.drawPolygon(p.polygon);
                    
                        if (p.equals(currentProjectState.selectedPolygon))
                        {
                            g.setColor(Color.GREEN);
                            g.drawLine(p.polygon.xpoints[currentProjectState.selectedVertexIndex], 
                                    p.polygon.ypoints[currentProjectState.selectedVertexIndex],
                                    p.polygon.xpoints[0], p.polygon.ypoints[0]);
                        }
                    }
                }
                
            }
            if (currentProjectState.tempAutoCompletePolygon != null)
            {
                g.setColor(new Color(155, 66, 244));
                Stroke currStroke = g2d.getStroke();
                g2d.setStroke(new BasicStroke((int)(1/scale)*2));
                g.drawPolygon(currentProjectState.tempAutoCompletePolygon.polygon);
                g2d.setStroke(currStroke);
            }
            if (currentProjectState.selectedVertexIndex != -1)
            {
                int dotWidth = (int)(9/(scale/2));
                if (dotWidth < 1)
                {
                    dotWidth = 2;
                }
                int dotOffset = (int)(4/(scale/2));
                if (dotOffset < 1)
                {
                    dotOffset = 1;
                }
                g.setColor(Color.RED);
                g.fillOval(currentProjectState.selectedPolygon.polygon.xpoints[currentProjectState.selectedVertexIndex] - dotOffset, 
                        currentProjectState.selectedPolygon.polygon.ypoints[currentProjectState.selectedVertexIndex] - dotOffset, dotWidth, dotWidth);
            }

            if (currentProjectState.selectedPolygon != null)
            {
                g.setColor(Color.RED);
                int dotWidth = (int)(5/(scale/2));
                if (dotWidth < 1)
                {
                    dotWidth = 1;
                }
                int dotOffset = (int)(2/(scale/2));
                if (dotOffset < 1)
                {
                    dotOffset = 1;
                }
                for (int i = 0; i < currentProjectState.selectedPolygon.polygon.npoints; i++)
                {
                    if (i == currentProjectState.selectedVertexIndex)
                    {
                        continue;
                    }
                    if (i == currentProjectState.hoverVertexIndex)
                    {
                        g.fillOval(currentProjectState.selectedPolygon.polygon.xpoints[i] - dotOffset*2, 
                                currentProjectState.selectedPolygon.polygon.ypoints[i] - dotOffset*2, dotWidth*2, dotWidth*2);
                    }
                    else
                    {
                        g.fillOval(currentProjectState.selectedPolygon.polygon.xpoints[i] - dotOffset, 
                                currentProjectState.selectedPolygon.polygon.ypoints[i] - dotOffset, dotWidth, dotWidth);
                    }
                }
            }
            if (currentProjectState.adjacentPolygon != null)
            {
                g.setColor(Color.GREEN);
                int dotWidth = (int)(5/(scale/2));
                if (dotWidth < 1)
                {
                    dotWidth = 1;
                }
                int dotOffset = (int)(2/(scale/2));
                if (dotOffset < 1)
                {
                    dotOffset = 1;
                }
                for (int i = 0; i < currentProjectState.adjacentPolygon.polygon.npoints; i++)
                {
                    if (i == currentProjectState.adjacentPolygonVertex)
                    {
                        g.fillOval(currentProjectState.adjacentPolygon.polygon.xpoints[i] - dotOffset*2, 
                                currentProjectState.adjacentPolygon.polygon.ypoints[i] - dotOffset*2, dotWidth*2, dotWidth*2);
                    }
                    else
                    {
                        g.fillOval(currentProjectState.adjacentPolygon.polygon.xpoints[i] - dotOffset, 
                                currentProjectState.adjacentPolygon.polygon.ypoints[i] - dotOffset, dotWidth, dotWidth);
                    }
                }
            }
            if (rectangle != null)
            {
                g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
            if (DragUp != null)
            {
                g2d.setColor(Color.CYAN);
                Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
                g2d.setStroke(dashed);
                g2d.drawRect(DragDown.x, DragDown.y, DragUp.x - DragDown.x, DragUp.y - DragDown.y);
            }
            if (!currentProjectState.selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Objects"))
            {
                g.setColor(Color.CYAN);
                for (MaskedObject p : currentProjectState.selectedObjects)
                {
                    g.drawPolygon(p.polygon);
                }
            }
            if (!currentProjectState.selectedVertices.isEmpty() && toolList.getSelectedItem().equals("Drag Select Vertices"))
            {
                g.setColor(Color.CYAN);
                int dotWidth = (int)(5/(scale/2));
                if (dotWidth < 1)
                {
                    dotWidth = 1;
                }
                int dotOffset = (int)(2/(scale/2));
                if (dotOffset < 1)
                {
                    dotOffset = 1;
                }
                for (int i = 0; i < currentProjectState.selectedVertices.size(); i++)
                {
                    ArrayList<Integer> vertList = currentProjectState.selectedVertices.get(i);
                    Polygon p = currentProjectState.selectedObjects.get(i).polygon;

                    for (int j = 0; j < vertList.size(); j++)
                    {
                        g.fillOval(p.xpoints[vertList.get(j)] - dotOffset, p.ypoints[vertList.get(j)] - dotOffset, dotWidth, dotWidth);
                    }
                }
            }
        } finally {
            g2d.setTransform(saveTransform);
        }
    }

    
    private void textToImage(String text, String filename)
    {
        Scanner s = new Scanner(text);
        String first_line = s.nextLine();
        int thisx = 0;
        int thisy = 0;
        /*
           Because font metrics is based on a graphics context, we need to create
           a small, temporary image so we can ascertain the width and height
           of the final image
         */
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Monospaced", Font.PLAIN, 8);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int row_width = fm.stringWidth(first_line);
        int row_height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(row_width, row_height*AsciiRows, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);
        for (String line : text.split("\n"))
        {
            g2d.drawString(line, thisx, thisy += row_height);
        }
        g2d.dispose();
        try {
            ImageIO.write(img, "png", new File(filename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    public void generateTrainingArrangements()
    {
        
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File(System.getProperty("user.home") 
                + File.separator + "documents" 
                + File.separator + "trainingImages"));
        int result = fc.showOpenDialog(ImageFilters.this);
        if (result == JFileChooser.CANCEL_OPTION)
        {
            return;
        }
        if (fc.getSelectedFile() == null)
        {
            return;
        }
        
        
        File[] files = fc.getSelectedFile().listFiles();
        int arrangements = 30;
        int images_per_arrangement = 10;
        
        int existingImages = new File("arrangements").listFiles().length;
        
        for (int i = 0; i < arrangements; i++)
        {
            FileWriter fw = null;
            try {
                File arrangementFile = new File("arrangements/arrangement" + (i+existingImages) + ".txt");
                fw = new FileWriter(arrangementFile);
                filterPolygon = false;
                currentProjectState.selectedPolygon.polygon.reset();
                neighbor_key_dictionary.clear();
                neighbor_key_dictionary_1to1.clear();
                alreadyFoundModes = false;
                ResetImage();
                ArrayList<File> arrangement = new ArrayList();
                ArrayList<Integer> indicesToInclude = new ArrayList();
                for (int j = 0; j < images_per_arrangement; j++)
                {
                    int index = (int)(Math.random()*files.length);
                    while (indicesToInclude.contains(index))
                    {
                        index = (int)(Math.random()*files.length);
                    }
                    indicesToInclude.add(index);
                    arrangement.add(files[index]);
                }   
                for (int j = 0; j < images_per_arrangement; j++) 
                {
                    
                    File file = arrangement.get(j);
                    System.out.println("File " + (j+1) + " of " + arrangement.size() + ": " + file.getName());
                    fw.append(file.getName() + "\n");
                    selected_file = file;
                    try
                    {
                        selected_image = ImageIO.read(selected_file);
                        train2NeighborKey();
                    }
                    catch (Exception ex)
                    {
                        
                        //Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
                        continue;
                    }
                }
                //selected_file = new File("/Users/paulsoderquist/Documents/trainingImages/jimmy-stewart-rope.jpg");
                selected_file = new File("C:\\Users\\psoderquist\\Documents\\NetBeansProjects\\Image-Filters-master\\Image-Filters-master\\faveWife.png");
                try {
                    selected_image = ImageIO.read(selected_file);
                } catch (IOException ex) {
                    Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
                }
                image_pixels = toBufferedImage(selected_image);
                NeighborKeyColorize(image_pixels);
                ImageIO.write(image_pixels, "png", 
                        new File("/Users/paulsoderquist/NetBeansProjects/ImageEditor/arrangementOutputImages/arrangementOutput_" + (i+existingImages) + ".png"));

                repaint();
                
            } catch (IOException ex) {
                Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fw.close();
                } catch (IOException ex) {
                    Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    
    public void applyFilter()
    {
        image_pixels = toBufferedImage(selected_image);
            
        String selected_filter = (String)filterList.getSelectedItem();

        switch (selected_filter) {
            case "Colorize":
                NeighborKeyColorize(image_pixels);
                break;
            case "Mosaic 1":
                mosaic1();
                break;
            case "Mosaic 2":
                mosaic2();
                break;
            case "Emboss":
                EmbossImage();
                break;
            case "Invert Colors":
                InvertColors();
                break;
            case "Grayscale":
                ConvertToGrayScale();
                break;
            case "Step Colors":
                StepColors();
                break;
            case "Pixelate":
                pixelate();
                break;
            case "Increase Contrast":
                increaseBWContrast();
                break;
            case "De-Noise (Mean)":
                deNoiseImageMean();
                break;
            case "De-Noise (Median)":
                deNoiseImageMedian();
                break;
            case "Gradient":
                gradient();
                break;
            default:
                break;
        }
        ResetButton.setEnabled(true);
        repaint();
    }
    
    public void smartAdd(ArrayList<MaskedObject> fakePolygons, MaskedObject real)
    {
        for (MaskedObject p : fakePolygons)
        {
            if (p.id == real.id)
            {
                p.color = real.color;
                return;
            }
        }
        fakePolygons.add(real);
    }
    
    public void saveCidsFile(File cids)
    {
        try 
        {
            ArrayList<MaskedObject> fakePolygons = new ArrayList();

            if (cids.exists())
            {
                Scanner reader = new Scanner(cids);

                while (reader.hasNext())
                {
                    int id = reader.nextInt();
                    int r = reader.nextInt();
                    int g = reader.nextInt();
                    int b = reader.nextInt();
                    MaskedObject fake = new MaskedObject();
                    fake.id = id;
                    fake.color = new Color(r,g,b);
                    fakePolygons.add(fake);
                }
                
                reader.close();
            }
            for (MaskedObject p : currentProjectState.polygons)
            {
                smartAdd(fakePolygons,p);
            }
            FileWriter fw = new FileWriter(cids);
            for (MaskedObject p : fakePolygons)
            {
                if (p.color != null)
                {
                    fw.append(p.id + " " + p.color.getRed() + " " + p.color.getGreen() + " " + p.color.getBlue() + "\n");
                }
            }
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public double getIntelligentFingerprintY(ConvolveRegionMap map, int radius, BufferedImage bi, int x, int y)
    {
        double numerator = 0;
        double totalValue = 0;
        for (int i = -radius; i <= radius; i++)
        {
            if (y+i < 0) continue;
            if (y+i > image_pixels.getHeight()-1) continue;
            for (int j = -radius; j <= radius; j++)
            {
                if (x+j < 0) continue;
                if (x+j > image_pixels.getWidth()-1) continue;
                if (map.maskMap[i+radius][j+radius] == 1)
                {
                    Pixel pixel = getPixel(x+j,y+i,bi);
                    numerator += (pixel.getAverage()*i);
                    totalValue += pixel.getAverage();
                }
            }
        }
        return numerator / totalValue;
    }
    
    
    public double getIntelligentFingerprintX(ConvolveRegionMap map, int radius, BufferedImage bi, int x, int y)
    {
        double numerator = 0;
        double totalValue = 0;
        for (int i = -radius; i <= radius; i++)
        {
            if (y+i < 0) continue;
            if (y+i > image_pixels.getHeight()-1) continue;
            for (int j = -radius; j <= radius; j++)
            {
                if (x+j < 0) continue;
                if (x+j > image_pixels.getWidth()-1) continue;
                if (map.maskMap[i+radius][j+radius] == 1)
                {
                    Pixel pixel = getPixel(x+j,y+i,bi);
                    numerator += (pixel.getAverage()*j);
                    totalValue += pixel.getAverage();
                }
                else
                {
                    //System.out.println("not in polygon");
                }
            }
        }
        return numerator / totalValue;
    }
    
    public double getFingerprint(BufferedImage bi, int x, int y)
    {
        int radius = 4;
        double totalValue = 0;
        for (int i = -radius; i < radius; i++)
        {
            for (int j = -radius; j < radius; j++)
            {
                Pixel pixel = getPixel(x+j,y+i,bi);
                totalValue += pixel.getAverage();
            }
        }
        return totalValue / ((radius*2)+1);
    }
    
    public double getContrastFromSides(BufferedImage bi, int x, int y, int polygonIndex, int vertexIndex)
    {
        if (x < 3 || y < 3 || x > bi.getWidth() - 4 || y > bi.getHeight() - 4) return .01;
        MaskedObject poly = currentProjectState.polygons.get(polygonIndex);
        int prevPointIndex = ((vertexIndex+poly.polygon.npoints)-1)%poly.polygon.npoints;
        int nextPointIndex = (vertexIndex+1)%poly.polygon.npoints;
        int prevX = poly.polygon.xpoints[prevPointIndex];
        int prevY = poly.polygon.ypoints[prevPointIndex];
        int nextX = poly.polygon.xpoints[nextPointIndex];
        int nextY = poly.polygon.ypoints[nextPointIndex];
        double slopeNumerator = (prevY - nextY + 0.0);
        double slopeDenominator = (prevX - nextX + 0.0);
        double contrast = 0;
        double slope;
        if (slopeDenominator == 0)
        {
            slope = 10000000;
        }
        else
        {
            slope = slopeNumerator/slopeDenominator;
        }
        if (slope < .414 && slope > -.414)
        {
            // slope is near 0
            double pixel1 = getPixel(x,y-1,bi).getAverage();
            double pixel2 = getPixel(x,y-2,bi).getAverage();
            double pixel3 = getPixel(x,y-3,bi).getAverage();

            double pixel4 = getPixel(x,y+1,bi).getAverage();
            double pixel5 = getPixel(x,y+2,bi).getAverage();
            double pixel6 = getPixel(x,y+3,bi).getAverage();
            
            double topLineAverage = (pixel1 + pixel2 + pixel3)/3.0;
            double bottomLineAverage = (pixel4 + pixel5 + pixel6)/3.0;
            contrast = Math.abs(topLineAverage-bottomLineAverage);
        }
        else if (slope > .414 && slope < 1.586)
        {
            // slope is near 1
            double pixel1 = getPixel(x-1,y-1,bi).getAverage();
            double pixel2 = getPixel(x-2,y-2,bi).getAverage();
            double pixel3 = getPixel(x-3,y-3,bi).getAverage();

            double pixel4 = getPixel(x+1,y+1,bi).getAverage();
            double pixel5 = getPixel(x+2,y+2,bi).getAverage();
            double pixel6 = getPixel(x+3,y+3,bi).getAverage();
            
            double topLineAverage = (pixel1 + pixel2 + pixel3)/3.0;
            double bottomLineAverage = (pixel4 + pixel5 + pixel6)/3.0;
            contrast = Math.abs(topLineAverage-bottomLineAverage);
        }
        else if (slope > 1.586 || slope < -1.586)
        {
            // slope is near infinity
            double pixel1 = getPixel(x-1,y,bi).getAverage();
            double pixel2 = getPixel(x-2,y,bi).getAverage();
            double pixel3 = getPixel(x-3,y,bi).getAverage();

            double pixel4 = getPixel(x+1,y,bi).getAverage();
            double pixel5 = getPixel(x+2,y,bi).getAverage();
            double pixel6 = getPixel(x+3,y,bi).getAverage();
            
            double leftLineAverage = (pixel1 + pixel2 + pixel3)/3.0;
            double rightLineAverage = (pixel4 + pixel5 + pixel6)/3.0;
            contrast = Math.abs(leftLineAverage-rightLineAverage);
        }
        else if (slope > -1.586 && slope < -.414)
        {
            // slope is near -1
            double pixel1 = getPixel(x+1,y-1,bi).getAverage();
            double pixel2 = getPixel(x+2,y-2,bi).getAverage();
            double pixel3 = getPixel(x+3,y-3,bi).getAverage();

            double pixel4 = getPixel(x-1,y+1,bi).getAverage();
            double pixel5 = getPixel(x-2,y+2,bi).getAverage();
            double pixel6 = getPixel(x-3,y+3,bi).getAverage();
            
            double topLineAverage = (pixel1 + pixel2 + pixel3)/3.0;
            double bottomLineAverage = (pixel4 + pixel5 + pixel6)/3.0;
            contrast = Math.abs(topLineAverage-bottomLineAverage);
        }
        if (Math.abs(contrast) < 0.01)
        {
            contrast = 0.01;
        }
        return contrast;
    }
    
    private Click findEdgeForVertex(int prevX, int prevY, int searchRadius, int polygonIndex, int vertexIndex)
    {
        double greatestContrast = getContrastFromSides(image_pixels,prevX,prevY,polygonIndex,vertexIndex);
        int bestX = prevX;
        int bestY = prevY;
        for (int j = -searchRadius; j < searchRadius; j++)
        {
            for (int k = -searchRadius; k < searchRadius; k++)
            {
                double contrastOnSide = getContrastFromSides(image_pixels,prevX+k,prevY+j,polygonIndex,vertexIndex);

                //double difference = Math.abs(prevFingerprint - currFingerprint);
                if (contrastOnSide > greatestContrast)
                {
                    greatestContrast = contrastOnSide;
                    bestX = prevX+k;
                    bestY = prevY+j;
                }
            }
        }
        Click c = new Click();
        c.x = bestX;
        c.y = bestY;
        return c;
    }
    
    public void findEdges()
    {
        addCurrentStatusToEditHistory();

        int searchRadius = 10;
        searchRadius /= scale;
        if (currentProjectState.selectedVertices.size() > 0)
        {
            for (int i = 0; i < currentProjectState.selectedVertices.size(); i++)
            {
                ArrayList<Integer> vertList = currentProjectState.selectedVertices.get(i);
                MaskedObject p = currentProjectState.selectedObjects.get(i);
                for (int j = 0; j < currentProjectState.selectedVertices.get(i).size(); j++)
                {
                    int vertIndex = vertList.get(j);
                    int prevX = p.polygon.xpoints[vertIndex];
                    int prevY = p.polygon.ypoints[vertIndex];
                    int polygonIndex = getPolygonIndex(p);
                    Click c = findEdgeForVertex(prevX,prevY,searchRadius,polygonIndex,vertIndex);

                    //int tempX = selectedPolygon.polygon.xpoints[i];
                    //int tempY = selectedPolygon.polygon.ypoints[i];
                    
                    p.polygon.xpoints[vertIndex] = c.x;
                    p.polygon.ypoints[vertIndex] = c.y; 
                    
                    /*for (MaskedObject mo : polygons)
                    {
                        boolean affected = false;
                        for (int k = 0; k < mo.polygon.npoints; k++)
                        {
                            if (mo.polygon.xpoints[k] == tempX && mo.polygon.ypoints[k] == tempY)
                            {
                                mo.polygon.xpoints[k] = c.x;
                                mo.polygon.ypoints[k] = c.y;
                                affected = true;
                            }
                        }
                        if (affected)
                        {
                            mo.polygon.invalidate();
                        }
                    }*/
                }
                p.polygon.invalidate();
            }
            
            repaint();
        }
        else if (currentProjectState.selectedPolygon != null)
        {
            for (int i = 0; i < currentProjectState.selectedPolygon.polygon.npoints; i++)
            {
                int prevX = currentProjectState.selectedPolygon.polygon.xpoints[i];
                int prevY = currentProjectState.selectedPolygon.polygon.ypoints[i];
                int selectedPolygonIndex = getPolygonIndex(currentProjectState.selectedPolygon);
                Click c = findEdgeForVertex(prevX,prevY,searchRadius,selectedPolygonIndex,i);
                
                int tempX = currentProjectState.selectedPolygon.polygon.xpoints[i];
                int tempY = currentProjectState.selectedPolygon.polygon.ypoints[i];
                currentProjectState.selectedPolygon.polygon.xpoints[i] = c.x;
                currentProjectState.selectedPolygon.polygon.ypoints[i] = c.y; 
                for (MaskedObject mo : currentProjectState.polygons)
                {
                    boolean affected = false;
                    for (int k = 0; k < mo.polygon.npoints; k++)
                    {
                        if (mo.polygon.xpoints[k] == tempX && mo.polygon.ypoints[k] == tempY)
                        {
                            mo.polygon.xpoints[k] = c.x;
                            mo.polygon.ypoints[k] = c.y;
                            affected = true;
                        }
                    }
                    if (affected)
                    {
                        mo.polygon.invalidate();
                    }
                }
            }
            currentProjectState.selectedPolygon.polygon.invalidate();
            repaint();
        }
    }
    
    private int getPolygonIndex(MaskedObject p)
    {
        for (int i = 0; i < currentProjectState.polygons.size(); i++)
        {
            if (currentProjectState.polygons.get(i).polygon == p.polygon)
            {
                return i;
            }
        }
        return -1;
    }
    
    private void undo()
    {
        if (UndoStack.empty())
        {
            return;
        }
        RedoStack.push(deepCopyState(currentProjectState));
        currentProjectState = UndoStack.pop();
        repaint();
    }
    
    private void redo()
    {
        if (RedoStack.empty())
        {
            return;
        }
        UndoStack.push(deepCopyState(currentProjectState));
        currentProjectState = RedoStack.pop();
        repaint();
    }
    
    private void addCurrentStatusToEditHistory()
    {
        
        UndoStack.push(deepCopyState(currentProjectState));
    }

    private ArrayList<ArrayList<Integer>> deepCopyVertices(ArrayList<ArrayList<Integer>> vertices)
    {
        ArrayList<ArrayList<Integer>> copiedArrayList = new ArrayList();
        for (ArrayList<Integer> vertexList : vertices)
        {
            ArrayList<Integer> copiedVertexList = new ArrayList();
            for (int index : vertexList)
            {
                copiedVertexList.add(index);
            }
            copiedArrayList.add(copiedVertexList);
        }
        return copiedArrayList;
    }
    
    private ProjectState deepCopyState(ProjectState state)
    {
        ProjectState copiedState = new ProjectState();
        ArrayList<MaskedObject> copiedPolys = new ArrayList();
        for (MaskedObject p : state.polygons)
        {
            copiedPolys.add(deepCopyPolygon(p));
        }
        copiedState.polygons = copiedPolys;
        copiedState.adjacentPolygon = deepCopyPolygon(state.adjacentPolygon);
        copiedState.adjacentPolygonVertex = state.adjacentPolygonVertex;
        copiedState.hoverVertexIndex = state.hoverVertexIndex;
        ArrayList<MaskedObject> copiedSelectedPolys = new ArrayList();
        for (MaskedObject p : state.selectedObjects)
        {
            copiedSelectedPolys.add(deepCopyPolygon(p));
        }
        copiedState.selectedObjects = copiedSelectedPolys;
        copiedState.selectedVertexIndex = state.selectedVertexIndex;
        copiedState.selectedPolygon = deepCopyPolygon(state.selectedPolygon);
        copiedState.selectedVertices = deepCopyVertices(state.selectedVertices);
        return copiedState;
    }
    
    private MaskedObject deepCopyPolygon(MaskedObject p)
    {
        if (p == null) return null;
        
        MaskedObject copy = new MaskedObject();
        if (p.color != null)
        {
            int rgb = p.color.getRGB();
            copy.color = new Color(rgb);
        }
        
        copy.depth = p.depth;
        copy.id = p.id;
        copy.hue_variation = p.hue_variation;
        copy.saturation_variation = p.saturation_variation;
        copy.complement_threshold = p.complement_threshold;
        copy.edgeBlendIndex = p.edgeBlendIndex;
        copy.polygon = new Polygon(p.polygon.xpoints,p.polygon.ypoints,p.polygon.npoints);
        return copy;
    }
    
    public double getAreaAverage(BufferedImage bi, int x, int y)
    {
        int searchRadius = 3;
        if (x < 0 || y < 0 || x > bi.getWidth()-1 || y > bi.getHeight()-1)
        {
            return -1;
        }
        double total = 0;
        double numPixels = ((searchRadius*2.0)+1.0)*((searchRadius*2.0)+1.0);
        
        for (int i = -searchRadius; i < searchRadius; i++)
        {
            if (y+i < 0 || y+i > bi.getHeight()-1)
            {
                numPixels -= ((searchRadius*2.0)+1.0);
                continue;
            }
            for (int j = -searchRadius; j < searchRadius; j++)
            {
                if (x+j < 0 || x+j > bi.getWidth()-1)
                {
                    numPixels -= 1;
                    continue;
                }
                int value = getPixel(x+j,y+i,bi).getAverage();
                total += (value +0.0);
            }
        }
        return total / numPixels;
    }
    
    
    
    public void trackMotionFromPreviousFrame()
    {
        double similarityThreshold = 10;
        addCurrentStatusToEditHistory();
        int fingerprintRadius = 3;
        int searchRadius = 16;
        //searchRadius /= scale;
        int tweakableScalar = 0;
        if (currentProjectState.selectedPolygon == null)
        {
            return;
        }
        try {
            int frameNumber = hsvColorChooser.video_current_value;
            File prevImageFile = new File("Video Frame PMOCs" + File.separator + "video-frame-" + (frameNumber-1) + ".png");
            File prevPMOCFile = new File("Video Frame PMOCs" + File.separator + "video-frame-" + (frameNumber-1) + ".pmoc");
            ArrayList<MaskedObject> prevFrameObjects = getDataFromPMOC(prevPMOCFile);
            int prevFramePolygonIndex = getPolygonIndex(currentProjectState.selectedPolygon);
            MaskedObject prevPolygon = prevFrameObjects.get(prevFramePolygonIndex);
            BufferedImage previousImg = ImageIO.read(prevImageFile);
            for (int i = 0; i < prevPolygon.polygon.npoints; i++)
            {     
                int vertIndex = i;
                int prevX = prevPolygon.polygon.xpoints[vertIndex];
                int prevY = prevPolygon.polygon.ypoints[vertIndex];
                ConvolveRegionMap maskMap = new ConvolveRegionMap(fingerprintRadius);
                maskMap.initMaskMap(prevPolygon,prevX,prevY);
                double prevAreaAverage = getAreaAverage(previousImg,prevX,prevY);
                double currAreaAverage = getAreaAverage(image_pixels,prevX,prevY);
                int mostSimilarX;
                int mostSimilarY;
                if (Math.abs(prevAreaAverage-currAreaAverage) < similarityThreshold
                        || prevAreaAverage == -1 || currAreaAverage == -1)
                {
                    // The current position is similar enough to the value in the previous
                    // frame so just keep it the same vertex location
                    mostSimilarX = prevX;
                    mostSimilarY = prevY;
                }
                else
                {
                    
                
                    double prevFingerprintX = getIntelligentFingerprintX(maskMap,fingerprintRadius,previousImg,prevX,prevY);
                    double prevFingerprintY = getIntelligentFingerprintY(maskMap,fingerprintRadius,previousImg,prevX,prevY);
                    double currX = getIntelligentFingerprintX(maskMap,fingerprintRadius,image_pixels,prevX,prevY);
                    double currY = getIntelligentFingerprintY(maskMap,fingerprintRadius,image_pixels,prevX,prevY);
                    double mostSimilarFingerprint = Math.sqrt(Math.pow(currX - prevFingerprintX,2)+Math.pow(currY - prevFingerprintY,2));
                    int selectedPolygonIndex = getPolygonIndex(currentProjectState.selectedPolygon);
                    double contrastOnSide = getContrastFromSides(image_pixels,prevX,prevY,selectedPolygonIndex,vertIndex);
                    mostSimilarFingerprint += (tweakableScalar)*(1.0/contrastOnSide);
                    mostSimilarX = prevX;
                    mostSimilarY = prevY;
                    for (int j = -searchRadius; j < searchRadius; j++)
                    {
                        if (prevX+j < 0) continue;
                        if (prevX+j > image_pixels.getHeight()-1) continue;
                        for (int k = -searchRadius; k < searchRadius; k++)
                        {
                            if (prevX+k < 0) continue;
                            if (prevX+k > image_pixels.getWidth()-1) continue;
                            double currFingerprintX = getIntelligentFingerprintX(maskMap,fingerprintRadius,image_pixels,prevX+k,prevY+j);
                            double currFingerprintY = getIntelligentFingerprintY(maskMap,fingerprintRadius,image_pixels,prevX+k,prevY+j);
                            double distance = Math.sqrt(Math.pow(currFingerprintX - prevFingerprintX,2)+Math.pow(currFingerprintY - prevFingerprintY,2));
                            contrastOnSide = getContrastFromSides(image_pixels,prevX+k,prevY+j,selectedPolygonIndex,vertIndex);
                            distance += (tweakableScalar)*(1.0/contrastOnSide);
                            //double difference = Math.abs(prevFingerprint - currFingerprint);
                            if (distance < mostSimilarFingerprint)
                            {
                                mostSimilarFingerprint = distance;
                                mostSimilarX = prevX+k;
                                mostSimilarY = prevY+j;
                            }
                        }
                    }
                }
                int tempX = currentProjectState.selectedPolygon.polygon.xpoints[i];
                int tempY = currentProjectState.selectedPolygon.polygon.ypoints[i];
                
                currentProjectState.selectedPolygon.polygon.xpoints[i] = mostSimilarX;
                currentProjectState.selectedPolygon.polygon.ypoints[i] = mostSimilarY; 
                
                for (MaskedObject p : currentProjectState.polygons)
                {
                    boolean affected = false;
                    for (int j = 0; j < p.polygon.npoints; j++)
                    {
                        if (p.polygon.xpoints[j] == tempX && p.polygon.ypoints[j] == tempY)
                        {
                            p.polygon.xpoints[j] = mostSimilarX;
                            p.polygon.ypoints[j] = mostSimilarY;
                            affected = true;
                        }
                    }
                    if (affected)
                    {
                        p.polygon.invalidate();
                    }
                }
            }
            currentProjectState.selectedPolygon.polygon.invalidate();
            repaint();
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void copyPolygonScheme(MaskedObject source, MaskedObject destination)
    {
        destination.color = source.color;
        destination.complement_threshold = source.complement_threshold;
        destination.depth = source.depth;
        destination.edgeBlendIndex = source.edgeBlendIndex;
        destination.hue_variation = source.hue_variation;
        destination.saturation_variation = source.saturation_variation;
        destination.secondary_color = source.secondary_color;
        destination.secondary_hue_variation = source.secondary_hue_variation;
        destination.secondary_sat_variation = source.secondary_sat_variation;
    }
    
    private void applyCurrentFrameToPMOC(String pmocFileName)
    {
        try {
            File pmocFile = new File(pmocFileName);
            ArrayList<MaskedObject> polys = getDataFromPMOC(pmocFile);
            for (int i = 0; i < polys.size(); i++)
            {
                MaskedObject schemeP = currentProjectState.polygons.get(i);
                MaskedObject p = polys.get(i);
                copyPolygonScheme(schemeP,p);
            }
            if (polys.size() > 0)
            {
                writeObjectsToPMOCFile(polys,pmocFileName);
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeImageFile(BufferedImage imgPixels, String filename, String type)
    {
        try {
            File outputfile = new File("PMOCs"+File.separator+filename+"."+type);
            ImageIO.write(imgPixels, type, outputfile);
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void ApplyFrameToAllFrames()
    {
        File folder = new File("Video Frame PMOCs"+File.separator);
        for (final File fileEntry : folder.listFiles()) 
        {
            if (!fileEntry.isDirectory()) {
                if ((fileEntry.getName().substring(fileEntry.getName().length()-4)).equals("pmoc"))
                {
                    String filename = "Video Frame PMOCs" + File.separator + fileEntry.getName();
                    applyCurrentFrameToPMOC(filename);
                }
                    
            }
        }
    }
    
    private void writeObjectsToPMOCFile(ArrayList<MaskedObject> objects, String filename)
    {
        try {
            FileWriter fw = new FileWriter(new File(filename));
            fw.append(scale + "\n");
            fw.append(objects.size()+"\n");
            for (MaskedObject p : objects)
            {
                if (p.color != null)
                {
                    fw.append("rgb " + p.color.getRed() + " " + p.color.getGreen() + " " + p.color.getBlue() + "\n");
                }
                fw.append("hVar " + p.hue_variation + "\n");
                fw.append("sVar " + p.saturation_variation + "\n");
                if (p.secondary_color != null)
                {
                    fw.append("rgb2 " + p.secondary_color.getRed() + " " + p.secondary_color.getGreen() + " " + p.secondary_color.getBlue() + "\n");
                }
                fw.append("hVar2 " + p.secondary_hue_variation + "\n");
                fw.append("sVar2 " + p.secondary_sat_variation + "\n");
                fw.append("shadowComplement " + p.complement_threshold + "\n");
                fw.append("edgeBlendIndex " + p.edgeBlendIndex + "\n");
                fw.append("depth " + p.depth + "\n");
                fw.append(p.polygon.npoints+"\n");
                for (int i = 0; i < p.polygon.npoints; i++)
                {
                    fw.append(p.polygon.xpoints[i] + " ");
                    fw.append(p.polygon.ypoints[i] + " ");
                }
                fw.append("\n");
            }
            fw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public static void main(String[] args) {
        
        
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Paul\'s Coloring Studio");
        

        final ImageFilters graphic = new ImageFilters();
        graphic.currentProjectState.polygons = new ArrayList();
        graphic.ShowOutlinesCheckbox.setSelected(true);
        //graphic.assembleVideoFromFrames();
        graphic.numberMosaicColumnsBox.setText("30");
        graphic.addMouseListener(graphic);
        graphic.addMouseMotionListener(graphic);
        graphic.frame = new JFrame();
        graphic.filterList.setSelectedIndex(5);
        graphic.keyList.setSelectedIndex(0);
        graphic.addKeyListener(graphic);
        graphic.setFocusable(true);
        graphic.requestFocus();
        graphic.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                graphic.currentProjectState.adjacentPolygon = null;
                graphic.currentProjectState.adjacentPolygonVertex = -1;
                graphic.repaint();
            }
        });
        //JScrollPane scroll = new JScrollPane(graphic,
        //    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
        //    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //graphic.frame.getContentPane().add(scroll);
        
        // I substituted above code with below code when I made my own scrolling functionality
        graphic.frame.getContentPane().add(graphic);

        graphic.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graphic.frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT + 100);
        graphic.frame.setTitle("Paul's Coloring Studio");
        graphic.LoadImageButton.addActionListener((ActionEvent e) -> {
            graphic.LoadImage();
            
        });
        graphic.LoadTrainingImageButton.addActionListener((ActionEvent e) -> {
            graphic.LoadImage();
            
            graphic.repaint();
            
            graphic.count++;
            //graphic.trainColorizeArrayMode();
            //graphic.train24NeighborSumMode();
            graphic.train2NeighborKey();
            for (int i = 0; i < 256; i++)
            {
                if (graphic.neighborMeanMap[i] != null)
                {
                    Collections.sort(graphic.neighborMeanMap[i]);
                }
            }
            
        });
        graphic.LoadSetImagesButton.addActionListener((ActionEvent e) -> {
            graphic.LoadImageSet();
            
            graphic.repaint();
            
            graphic.count++;
            //graphic.trainColorizeArrayMode();
            //graphic.train24NeighborSumMode();
            //graphic.train4NeighborKey();
            
        });
        graphic.ResetButton.addActionListener((ActionEvent e) -> {
            graphic.filterPolygon = false;
            //graphic.selectedPolygon.reset();        
            graphic.ResetImage();
            graphic.repaint();
        });
        graphic.ResetTrainingButton.addActionListener((ActionEvent e) -> {
            graphic.filterPolygon = false;
            //graphic.selectedPolygon.reset();
            graphic.neighbor_key_dictionary.clear();
            graphic.neighbor_key_dictionary_1to1.clear();
            graphic.alreadyFoundModes = false;
            graphic.ResetImage();
            graphic.repaint();
        });
        graphic.InvertButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.InvertColors();
            graphic.repaint();
        });
        graphic.GrayScaleButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.ConvertToGrayScale();
            graphic.repaint();
        });
        graphic.StepColorsButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.StepColors();
            graphic.repaint();
        });
        graphic.EmbossButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.EmbossImage();
            graphic.repaint();
        });
        graphic.BlurButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.BlurImage();
            graphic.repaint();
        });
        graphic.RedButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.ColorizeRed();
            graphic.repaint();
        });
        graphic.GreenButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.ColorizeGreen();
            graphic.repaint();
        });
        graphic.BlueButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.ColorizeBlue();
            graphic.repaint();
        });
        
        graphic.ColorizeButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.Colorize();
            graphic.repaint();
        });
        
        graphic.randomValuesButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.RandomValues();
            graphic.repaint();
        });
        
        graphic.BlackWhiteButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            //graphic.image_copy = getImageCopy();
            graphic.BlackWhite(90);

            Timer timer=new Timer(50, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ev) {

                    if(graphic.bwcounter < 256){
                        graphic.BlackWhite(graphic.bwcounter++);
                        graphic.repaint();
                    }
                }
            });
            timer.start();
                
            graphic.repaint();
        });
        graphic.AlternateRGButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            //graphic.image_copy = getImageCopy();

            
            graphic.ResetButton.setEnabled(true);
            
            Timer timer=new Timer(3, (ActionEvent ev) -> {
                if (graphic.RGCounter % 3 == 0)
                {
                    graphic.RedGreen("green", graphic.image_copy);
                }
                else if (graphic.RGCounter % 3 == 1)
                {
                    graphic.RedGreen("red", graphic.image_copy);
                }
                else
                {
                    graphic.RedGreen("blue", graphic.image_copy);
                }
                graphic.RGCounter++;
                graphic.repaint();
            });
            timer.start();
                
            graphic.repaint();
        });
        
        graphic.SumBoundingBoxButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.SumBoundingBoxColorize();
            graphic.repaint();
        });
        
        graphic.TwoNeighboKeyButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.NeighborKeyColorize(graphic.image_pixels);
            graphic.repaint();
        });
        
        graphic.SplitVideoFramesButton.addActionListener((ActionEvent e) -> {
            graphic.splitVideoIntoFrames();
        });
        /*
        graphic.ColorizeFramesButton.addActionListener((ActionEvent e) -> {
            graphic.colorizeVideoFrames("Colorize");
        });
        graphic.CreateColorizedMovieButton.addActionListener((ActionEvent e) -> {
            graphic.colorizeVideoFrames("Colorize");
            graphic.assembleVideoFromFrames();
        });*/
        graphic.AssembleFramesButton.addActionListener((ActionEvent e) -> {
            graphic.assembleVideoFromFrames();
        });
        graphic.AsciiFilterButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            String textImage = graphic.AsciiFilter();
            graphic.textToImage(textImage, "testImage.png");

            graphic.repaint();
        });
       
        graphic.AsciiFilterVideoButton.addActionListener((ActionEvent e) -> {
            
            graphic.createAsciiVideo();
            graphic.assembleVideoFromFrames();
            graphic.repaint();
        });
        
        graphic.PixelateButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.pixelate();
            graphic.ResetButton.setEnabled(true);
            graphic.repaint();
            
        });
        graphic.Mosaic1Button.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.mosaic1();
            graphic.mosaicType = "Mosaic1";
            graphic.ResetButton.setEnabled(true);
            graphic.repaint();
        });
        graphic.Mosaic2Button.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
            graphic.mosaic2();
            graphic.mosaicType = "Mosaic2";
            graphic.ResetButton.setEnabled(true);
            graphic.repaint();
        });
        
        graphic.trackMotionButton.addActionListener((ActionEvent e) -> {
            if (graphic.currentProjectState.selectedPolygon != null)
            {
                graphic.trackMotionFromPreviousFrame();
            }
            else
            {
                for (MaskedObject p : graphic.currentProjectState.selectedObjects)
                {
                    graphic.currentProjectState.selectedPolygon = p;
                    graphic.currentProjectState.selectedVertexIndex = 0;
                    graphic.trackMotionFromPreviousFrame();
                }
            }
        });
        
        graphic.findEdgesButton.addActionListener((ActionEvent e) -> {
            graphic.findEdges();
        });
        
        graphic.colorLayersButton.addActionListener((ActionEvent e) -> {
            graphic.colorizeImageByLayers();
        });
        
        graphic.GenerateTrainingArrangements.addActionListener((ActionEvent e) -> {
            graphic.generateTrainingArrangements();
        });
        
        graphic.ApplyFilterButton.addActionListener((ActionEvent e) -> {
            graphic.applyFilter();

        }); 
        
        graphic.loadVideoButton.addActionListener((ActionEvent e) -> {
            graphic.loadVideo();

        }); 
        
        graphic.CreateFilteredVideoButton.addActionListener((ActionEvent e) -> {
            try {
                //graphic.image_pixels = toBufferedImage(selected_image);
                String selected_filter = (String)graphic.filterList.getSelectedItem();
                if (!graphic.filterVideoFrames(selected_filter)) return;
                if (!selected_filter.equals("Create Training Data") 
                        && !selected_filter.equals("Count Number of 9px Patterns"))
                {
                    graphic.assembleVideoFromFrames();
                    graphic.addSoundToVideo();
                }
            } catch (IOException ex) {
                //Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
            }
        }); 
        
        graphic.countColorsButton.addActionListener((ActionEvent e) -> {
            int roundFactor = 10;
            int currentIndex = 0;
            int totalPixels = graphic.image_pixels.getWidth() * graphic.image_pixels.getHeight();
            ArrayList<ColorFrequency> freqs = new ArrayList();
            try {
                graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
                
                for (int i = 0; i < graphic.image_pixels.getHeight(); i++)
                {
                    for (int j = 0; j < graphic.image_pixels.getWidth(); j++)
                    {
                        int currentCount = (i*graphic.image_pixels.getWidth() + j);
                        if (currentCount % 1000 == 0)
                        {
                            System.out.println(currentCount + " / " + totalPixels);
                        }
                        Pixel p = graphic.getPixel(j,i,graphic.image_pixels);
                        int r = ((p.red + (roundFactor/2)) / roundFactor) * roundFactor;
                        int g = ((p.green + (roundFactor/2)) / roundFactor) * roundFactor;
                        int b = ((p.blue + (roundFactor/2)) / roundFactor) * roundFactor;
                        if (r > 255) 
                        {
                            r = 255;
                        }
                        if (r < 0)
                        {
                            r = 0;
                        }
                        if (g > 255)
                        {
                            g = 255;
                        }
                        if (g < 0)
                        {
                            g = 0;
                        }
                        if (b > 255)
                        {
                            b = 255;
                        }
                        if (b < 0)
                        {
                            b = 0;
                        }
                        Color c = new Color(r,g,b);
                        boolean found = false;
                        for (ColorFrequency col : freqs)
                        {
                            if (col.color.equals(c))
                            {
                                col.frequency++;
                                found = true;
                            }
                        }
                        if (!found)
                        {
                            ColorFrequency freq = new ColorFrequency();
                            freq.color = c;
                            freq.frequency = 1;
                            freqs.add(freq);
                        }
                    }
                }
                Collections.sort(freqs);
                for (ColorFrequency col : freqs)
                {
                    //System.out.println(col.frequency + " " + col.color);
                } 
                int freqCounter = 0;
                for (int i = 0; i < graphic.image_pixels.getHeight(); i++)
                {
                    for (int j = 0; j < graphic.image_pixels.getWidth(); j++)
                    {
                        
                        Pixel p = graphic.getPixel(j,i,graphic.image_pixels);
                        ColorFrequency current = freqs.get(currentIndex);
                        int red = current.color.getRed();
                        int green = current.color.getGreen();
                        int blue = current.color.getBlue();
                        p.setRGB(red, green, blue);
                        freqCounter++;
                        if (freqCounter >= current.frequency)
                        {
                            freqCounter = 0;
                            currentIndex++;
                        }
                    }
                }
                graphic.repaint();
            } catch (Exception ex) {
                Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
            }
        }); 
        
        
        
        graphic.buttons.add(graphic.LoadImageButton);
        //graphic.buttons.add(graphic.LoadTrainingImageButton);
        graphic.buttons.add(graphic.countColorsButton);
        graphic.buttons.add(graphic.loadVideoButton);
        graphic.buttons.add(graphic.saveVideoFrameButton);
        graphic.buttons.add(graphic.trackMotionButton);
        graphic.buttons.add(graphic.findEdgesButton);
        
        //graphic.buttons.add(graphic.AssembleFramesButton);
        //graphic.buttons.add(graphic.frameRangeLbl1);
        //graphic.buttons.add(graphic.firstFrameChooser);
        //graphic.buttons.add(graphic.frameRangeLbl2);
        //graphic.buttons.add(graphic.lastFrameChooser);
        //graphic.buttons.add(graphic.LoadSetImagesButton);
        //graphic.buttons.add(graphic.ResetButton);
        graphic.buttons.add(graphic.ResetTrainingButton);
        
        graphic.buttons.add(new JLabel("Filter"));
        graphic.buttons.add(graphic.filterList);
        graphic.buttons.add(graphic.keyList);
        graphic.buttons.add(graphic.ApplyFilterButton);
        //graphic.buttons.add(graphic.InvertButton);
        //graphic.buttons.add(graphic.GrayScaleButton);
        //graphic.buttons.add(graphic.StepColorsButton);
        //graphic.buttons.add(graphic.EmbossButton);
        //graphic.buttons.add(graphic.BlurButton);
        //graphic.buttons.add(graphic.RedButton);
        //graphic.buttons.add(graphic.GreenButton);
        //graphic.buttons.add(graphic.BlueButton);                
        //graphic.buttons.add(graphic.ColorizeButton);
        //graphic.buttons.add(graphic.randomValuesButton);
        //graphic.buttons2.add(graphic.BlackWhiteButton);
        //graphic.buttons2.add(graphic.AlternateRGButton);
        //graphic.buttons.add(graphic.SumBoundingBoxButton);
        //graphic.buttons2.add(graphic.TwoNeighboKeyButton);
        //graphic.buttons2.add(graphic.InterpolateGapsCheckbox);
        //graphic.buttons2.add(graphic.FillInBlanksBox);
        
        graphic.saveVideoFrameButton.addActionListener((ActionEvent e) -> {
            graphic.saveVideoFrame();
            
            
        }); 
        
        
        graphic.saveButton.addActionListener((ActionEvent e) -> {
            String filename = graphic.filenameTextBox.getText();

            graphic.writeObjectsToPMOCFile(graphic.currentProjectState.polygons, "PMOCs"
                    + File.separator + filename+".pmoc"); 
            graphic.writeImageFile(graphic.image_pixels,filename,"png");
            
        }); 
        
        graphic.openButton.addActionListener((ActionEvent e) -> {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.home") 
                    + File.separator + "NetBeansProjects" 
                    + File.separator + "ImageEditor"
                    + File.separator + "PMOCs"));
            int result = fc.showOpenDialog(graphic);
            try
            {
                if (result == JFileChooser.CANCEL_OPTION)
                {
                    return;
                }
                if (fc.getSelectedFile() == null)
                {
                    return;
                }
                File selected_pmoc_file = fc.getSelectedFile();
                graphic.loadPMOC(selected_pmoc_file);
                graphic.loadPMOCImage(selected_pmoc_file);
                graphic.repaint();
            }
            catch (Exception ex)
            {
                Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, e);
                System.out.println("There was an error with the file that was selected (was it not a .pmoc?)");
            }
        });
        
        graphic.buttons2.add(graphic.filenameTextBox);
        graphic.buttons2.add(graphic.extensionLbl);
        graphic.buttons2.add(graphic.saveButton);
        graphic.buttons2.add(graphic.openButton);
        
        
        graphic.buttons2.add(graphic.FirstMappingOnly);    
        
        graphic.toolList.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                graphic.requestFocus();
                graphic.currentProjectState.adjacentPolygon = null;
                graphic.currentProjectState.adjacentPolygonVertex = -1;
                graphic.repaint();
            }
        });
        
        graphic.ApplySchemeAllFramesButton.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                graphic.ApplyFrameToAllFrames();
            }
        });
        
        graphic.recolorPolygonsButton.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                graphic.recolorSelectedPolygons();
                graphic.requestFocus();
            }
        });
        
        graphic.buttons2.add(graphic.toolList);
        //graphic.buttons.add(graphic.SplitVideoFramesButton);
        //graphic.buttons.add(graphic.ColorizeFramesButton);
        //graphic.buttons2.add(graphic.CreateColorizedMovieButton);
        //graphic.buttons2.add(graphic.AsciiFilterButton);
        //graphic.buttons2.add(graphic.AsciiFilterVideoButton);
        graphic.buttons2.add(graphic.ShowOutlinesCheckbox);
        /*graphic.colorChooser.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                    
                    if (graphic.selectedPolygon == null)
                    {
                        return;
                    }
                    
                    graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);
                    //graphic.colorChooser.setSelectedColor(newColor);
                    graphic.selectedPolygon.color = newColor;
                    // do something with newColor ...
                    String selected_filter = (String)graphic.filterList.getSelectedItem();

                    if (selected_filter.equals("Psychedelic"))
                    {
                        graphic.colorizePolygonPsychedelic(newColor);
                    }
                    else
                    {
                        graphic.colorizePolygon(newColor);
                    }
                    graphic.repaint();
            }
        });
        */
        graphic.buttons2.add(graphic.colorChooser);
        graphic.buttons2.add(graphic.recolorPolygonsButton);
        
        
        graphic.buttons2.add(graphic.colorLayersButton);
        graphic.buttons2.add(graphic.ApplySchemeAllFramesButton);
        
        //graphic.buttons2.add(graphic.PixelateButton);
        //graphic.buttons2.add(graphic.Mosaic1Button);
        //graphic.buttons2.add(graphic.Mosaic2Button);
        //graphic.buttons2.add(graphic.columnsLabel);
        
        //graphic.buttons2.add(graphic.numberMosaicColumnsBox);

        //graphic.buttons2.add(graphic.GenerateTrainingArrangements);
        //graphic.buttons.add(graphic.CreateFilteredVideoButton);
        graphic.ResetButton.setEnabled(false);
                
        /*
        graphic.InvertButton.setEnabled(false);
        graphic.GrayScaleButton.setEnabled(false);
        graphic.StepColorsButton.setEnabled(false);
        graphic.EmbossButton.setEnabled(false);
        graphic.BlurButton.setEnabled(false);
        graphic.RedButton.setEnabled(false);
        graphic.GreenButton.setEnabled(false);        
        graphic.BlueButton.setEnabled(false);
        */
        
        try {
            graphic.selected_image = ImageIO.read(graphic.selected_file);
            graphic.image_pixels = graphic.toBufferedImage(graphic.selected_image);

        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
        graphic.hsvColorChooser = new HSVColorChooser(graphic);
        JPanel buttonPanel = new JPanel();
        GridLayout grid = new GridLayout(2,1);
        buttonPanel.setLayout(grid);
        buttonPanel.add(graphic.buttons);
        buttonPanel.add(graphic.buttons2);
        graphic.frame.add(buttonPanel, BorderLayout.SOUTH);
        graphic.frame.add(graphic.hsvColorChooser, BorderLayout.EAST);
        
        graphic.frame.setJMenuBar(graphic.menuBar);
        
        graphic.revalidate();
        graphic.repaint();
        graphic.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graphic.frame.setExtendedState(graphic.frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        graphic.frame.setVisible(true);
        
        graphic.setupShortcutKeys();
        
        try {
            //graphic.getScreenCapture();

            graphic.g.stop();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void saveVideoFrame()
    {
        if (g == null)
        {
            //return;
        }
        File cids = new File("Video Frame PMOCs" + File.separator + "ids.cids");
        saveCidsFile(cids);
        String filename = filenameTextBox.getText();
        int frameNumber = hsvColorChooser.video_current_value;
        try {
            FileWriter fw = new FileWriter(new File("Video Frame PMOCs"+File.separator+"video-frame-" + frameNumber + ".pmoc"));
            fw.append(scale + "\n");
            fw.append(currentProjectState.polygons.size()+"\n");
            for (MaskedObject p : currentProjectState.polygons)
            {
                //fw.append("ID: " + p.id + "\n");
                if (p.color == null)
                {
                    fw.append("rgb 0 0 0\n");
                }
                else
                {
                    fw.append("rgb " + p.color.getRed() + " " + p.color.getGreen() + " " + p.color.getBlue() + "\n");
                }
                fw.append("hVar " + p.hue_variation + "\n");
                fw.append("sVar " + p.saturation_variation + "\n");
                
                if (p.secondary_color == null)
                {
                    fw.append("rgb2 0 0 0\n");
                }
                else
                {
                    fw.append("rgb2 " + p.secondary_color.getRed() + " " + p.secondary_color.getGreen() + " " + p.secondary_color.getBlue() + "\n");
                }
                fw.append("hVar2 " + p.secondary_hue_variation + "\n");
                fw.append("sVar2 " + p.secondary_sat_variation + "\n");
                
                fw.append("shadowComplement " + p.complement_threshold + "\n");
                fw.append("edgeBlendIndex " + p.edgeBlendIndex + "\n");
                fw.append("depth " + p.depth + "\n");
                fw.append(p.polygon.npoints+"\n");
                for (int i = 0; i < p.polygon.npoints; i++)
                {
                    fw.append(p.polygon.xpoints[i] + " ");
                    fw.append(p.polygon.ypoints[i] + " ");
                }
                fw.append("\n");
            }
            fw.close();
            File outputfile = new File("Video Frame PMOCs"+File.separator+"video-frame-" + frameNumber + ".png");
            ImageIO.write(image_pixels, "png", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void deselect()
    {
        currentProjectState.selectedPolygon = null;
        currentProjectState.selectedVertexIndex = -1;
        currentProjectState.selectedObjects.clear();
        currentProjectState.selectedVertices.clear();
        repaint();
    }
    
    private void setDrawingMode()
    {
        toolList.setSelectedItem("Pen Tool");
    }
    
    private void setDragVerticesMode()
    {
        toolList.setSelectedItem("Drag Select Vertices");
    }
    
    private void setDragObjectsMode()
    {
        toolList.setSelectedItem("Drag Select Objects");
    }
    
    private void setVertexMode()
    {
        toolList.setSelectedItem("Vertex Mode");
    }
    
    private void setSelectMode()
    {
        toolList.setSelectedItem("Select Polygon Mode");
    }
    
    private void setupShortcutKeys()
    {
        KeyStroke cmdZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        getInputMap().put(cmdZ,
                    "cmdZ");
        getActionMap().put("cmdZ", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed cmd Z");
                undo();
            }
        });
        
        KeyStroke cmdY = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        getInputMap().put(cmdY,
                    "cmdY");
        getActionMap().put("cmdY", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed cmd Y");
                redo();
            }
        });
        
        KeyStroke cmdS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        getInputMap().put(cmdS,
                    "cmdS");
        getActionMap().put("cmdS", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed cmd S");
                saveVideoFrame();
            }
        });
        
        KeyStroke cmdU = KeyStroke.getKeyStroke(KeyEvent.VK_U, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        getInputMap().put(cmdU,
                    "cmdU");
        getActionMap().put("cmdU", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed cmd U");
                deselect();
            }
        });
        
        KeyStroke cmdD = KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        getInputMap().put(cmdD,
                    "cmdD");
        getActionMap().put("cmdD", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed cmd D");
                setDrawingMode();
            }
        });
        
        KeyStroke cmdI = KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        getInputMap().put(cmdI,
                    "cmdI");
        getActionMap().put("cmdI", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed cmd I");
                setDragVerticesMode();
            }
        });
        
        KeyStroke cmdV = KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        getInputMap().put(cmdV,
                    "cmdV");
        getActionMap().put("cmdV", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed cmd V");
                setVertexMode();
            }
        });
        
        KeyStroke cmdB = KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        getInputMap().put(cmdB,
                    "cmdB");
        getActionMap().put("cmdB", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed cmd B");
                setSelectMode();
            }
        });
        
        KeyStroke cmdO = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        getInputMap().put(cmdO,
                    "cmdO");
        getActionMap().put("cmdO", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed cmd O");
                setDragObjectsMode();
            }
        });
        
        KeyStroke cmdE = KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        getInputMap().put(cmdE,
                    "cmdE");
        getActionMap().put("cmdE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed cmd E");
                findEdges();
            }
        });
        
        KeyStroke cmdT = KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        getInputMap().put(cmdT,
                    "cmdT");
        getActionMap().put("cmdT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed cmd T");
                trackMotionFromPreviousFrame();
            }
        });
        
    }
    
    private BufferedImage getScreenCapture()
    {
        boolean using_terminal = true;
        try {
            Robot robot = new Robot();
            String format = "jpg";
            String fileName = "FullScreenshot." + format;
             
            
            
            Rectangle screenRect;
            if (using_terminal)
            {
                // WIDTH AND HEIGHT MUST BE DIVISIBLE BY TWO (for encoder to work)!!!
                screenRect = new Rectangle(0,45,1312,746);
                //screenRect = new Rectangle(0,45,982,746); 
            }
            else
            {
                screenRect = new Rectangle(270,221,756,552);

            }
            BufferedImage screenShot = robot.createScreenCapture(screenRect);
            //ImageIO.write(screenFullImage, format, new File(fileName));
            return screenShot; 
            //System.out.println("A full screenshot saved!");
        } 
        catch (AWTException ex) {
            System.err.println(ex);
        }
        return null;
    }
    
    private void LoadImageSet()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File(System.getProperty("user.home") 
                + File.separator + "documents" 
                + File.separator + "trainingImages"));
        int result = fc.showOpenDialog(ImageFilters.this);
        if (result == JFileChooser.CANCEL_OPTION)
        {
            return;
        }
        if (fc.getSelectedFile() == null)
        {
            return;
        }
        File[] files = fc.getSelectedFile().listFiles();
        int file_counter = 1;
        for (File file : files) {
            System.out.println("File " + file_counter + " of " + files.length + ": " + file.getName());
            file_counter++;
            selected_file = file;
            try 
            {
                selected_image = ImageIO.read(selected_file);
                train2NeighborKey();
            } 
            catch (Exception ex) 
            {
                
                Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
        }
        for (int i = 0; i < 256; i++)
        {
            if (neighborMeanMap[i] != null)
            {
                Collections.sort(neighborMeanMap[i]);
            }
        }
    }
    
    private void countPixelsNotWhite()
    {
        ArrayList<ASCIIChar> chars = new ArrayList();
        image_pixels = toBufferedImage(selected_image);
        double Column_Increment = (int)(image_pixels.getWidth() / 31.0+.5);
        double Row_Increment = image_pixels.getHeight() / 20;
        for (int row = 0; row < image_pixels.getHeight()-20; row++)
        {
            for (int column = 0; column < image_pixels.getWidth()-20; column++)
            {
                Pixel p1 = getPixel(column,row,image_pixels);

                if (column % Column_Increment == 0 && row % Row_Increment == 0)
                {

                    int countPixels = 0;
                    for (int i = 0; i < Row_Increment; i++)
                    {
                        for (int j = 0; j < Column_Increment; j++)
                        {
                            if (column+j < image_pixels.getWidth() && row+i< image_pixels.getHeight())
                            {
                                Pixel p = getPixel(column+j,row+i,image_pixels);
                                int average = p.getAverage();
                                if (average != 255)
                                {
                                    countPixels ++;//= average;
                                }
                            }
                        }
                    }
                    
                    int ascii = (int)(((row+0.0)/Row_Increment)*(31)+(column+0.0)/Column_Increment);
                    ascii+= 33;
                    if (ascii >= 124)
                    {
                        ascii += 37;
                    }
                    char asciiChar = (char)ascii;
                    //System.out.print(ascii + " ");
                    //System.out.print(asciiChar);
                    //System.out.print(" ");
                    //System.out.println(countPixels);
                    ASCIIChar a = new ASCIIChar();
                    a.value = asciiChar;
                    a.pixels = countPixels;
                    a.number = ascii;
                    chars.add(a);
                    
                    //p1.setRGB(255, 0, 0);

                }
                
            }
        }
        Collections.sort(chars);
        for (ASCIIChar a : chars)
        {
            System.out.println(a.toString());
        }
    }
    
    private void LoadImage() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home") 
                + File.separator + "NetBeansProjects"
                + File.separator + "ImageEditor"
                + File.separator + "PMOCs"));
        int result = fc.showOpenDialog(ImageFilters.this);
        try
        {
            if (result == JFileChooser.CANCEL_OPTION)
            {
                return;
            }
            if (fc.getSelectedFile() == null)
            {
                return;
            }
            selected_file = fc.getSelectedFile();
            selected_image = ImageIO.read(selected_file);
            image_pixels = toBufferedImage(selected_image);
            this.setPreferredSize(new Dimension(selected_image.getWidth(null),selected_image.getHeight(null)));
            InvertButton.setEnabled(true);
            GrayScaleButton.setEnabled(true);
            StepColorsButton.setEnabled(true);
            EmbossButton.setEnabled(true);
            BlurButton.setEnabled(true);
            RedButton.setEnabled(true);
            GreenButton.setEnabled(true);
            BlueButton.setEnabled(true);
            ColorizeButton.setEnabled(true);
            randomValuesButton.setEnabled(true);
            BlackWhiteButton.setEnabled(true);
            
            ResetButton.setEnabled(false);
            image_copy = getImageCopy();
            
            
            //countPixelsNotWhite();
            revalidate();
            repaint();
        }
        catch (Exception e)
        {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("There was an error with the file that was selected");
        }
    }
    
    void updateProgressBar(int i)
    {
        if (i % 1 == 0)
        {
            System.out.println("About to write frame " + (i) + " of " + (lastFrame) + " to file");
            double percent = ((i+0.0))/(lastFrame);
            percent*=100;
            if (percent > 100)
            {
                percent = 100;
            }
            if (percent < 0)
            {
                percent = 0;
            }
        }
        //progressBar.repaint();
    }
    
    int getFrameCount(String filename)
    {
        try {
            String[] command = { 
                "/usr/local/bin/ffprobe", "-v", "error", "-count_frames", "-select_streams", "v:0", "-show_entries", "stream=nb_read_frames", "-of", "default=nokey=1:noprint_wrappers=1",
                filename
            };
            
            Process proc = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(proc.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            String outputFromCommand = "";
            while ((s = stdInput.readLine()) != null) {
                outputFromCommand += s;
                System.out.println(s);

            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);

            }
            totalFramesInVideo = Integer.parseInt(outputFromCommand);
            System.out.println(totalFramesInVideo);
            return totalFramesInVideo;
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;

    }
    
    double getFrameRate(String filename)
    {
        try {
            String[] command = { 
                "/usr/local/bin/ffmpeg","-i",filename
            };
            
            Process proc = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(proc.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the information on the video:\n");
            String s = null;
            String outputFromCommand = "";
            while ((s = stdInput.readLine()) != null) {
                outputFromCommand += s;
                System.out.println(s);

            }

            // read any errors from the attempted command
            String outputInfoFromCommand = "";
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {    
                outputInfoFromCommand += s;
                System.out.println(s);

            }
            Scanner inputScanner = new Scanner(outputInfoFromCommand);
            while (inputScanner.hasNext())
            {
                
                String word = inputScanner.next();
                if (word.equals("kb/s,"))
                {
                    frameRate = inputScanner.nextDouble();
                    return frameRate;
                }
            }
            //totalFramesInVideo = Integer.parseInt(outputFromCommand);
            //System.out.println(totalFramesInVideo);
            return frameRate;
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
        return frameRate;

    }
    
    double getDuration(String filename)
    {
        double total_seconds = 0.0;
        try {
            String[] command = { 
                "/usr/local/bin/ffmpeg","-i",filename
            };
            
            Process proc = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(proc.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the information on the video:\n");
            String s = null;
            String outputFromCommand = "";
            while ((s = stdInput.readLine()) != null) {
                outputFromCommand += s;
                System.out.println(s);

            }

            // read any errors from the attempted command
            String outputInfoFromCommand = "";
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {    
                outputInfoFromCommand += s;
                System.out.println(s);

            }
            Scanner inputScanner = new Scanner(outputInfoFromCommand);
            String durationStr = "";
            while (inputScanner.hasNext())
            {
                
                String word = inputScanner.next();
                if (word.equals("Duration:"))
                {
                    durationStr = inputScanner.next();
                    break;
                }
            }
            char[] chars = durationStr.toCharArray(); 
            int hours = Integer.parseInt(chars[0]+""+chars[1]);
            int minutes = Integer.parseInt(chars[3]+""+chars[4]);
            int seconds = Integer.parseInt(chars[6]+""+chars[7]);
            int millis = Integer.parseInt(chars[9]+""+chars[10]);
            //totalFramesInVideo = Integer.parseInt(outputFromCommand);
            //System.out.println(totalFramesInVideo);
            total_seconds += hours*3600;
            total_seconds += minutes*60;
            total_seconds += seconds;
            total_seconds += millis/100.0;
            return total_seconds;
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
        return frameRate;

    }
    
    
    private void extractAndSaveAudioToFile(String video_filename)
    {
        String audio_destination = "/Users/paulsoderquist/NetBeansProjects/ImageEditor/" + "output-audio.aac";
        //String audio_destination = "/Volumes/PAUL_SODERQ 1/MIDIs/" + "output-audio.aac";
        try {
            String[] command = { 
                "/usr/local/bin/ffmpeg", "-i", video_filename, "-vn", "-codec", "copy", "-write_xing", "0", audio_destination, "-y"
            };
            
            Process proc = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(proc.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of extracting audio from the video:\n");
            String s = null;
            String outputFromCommand = "";
            while ((s = stdInput.readLine()) != null) {
                outputFromCommand += s;
                System.out.println(s);

            }

            // read any errors from the attempted command
            String outputInfoFromCommand = "";
            System.out.println("Here is the standard error of extracting audio from the video (if any):\n");
            while ((s = stdError.readLine()) != null) {    
                outputInfoFromCommand += s;
                System.out.println(s);

            }
            
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    private void filterSection(Image selected_image, BufferedImage image_pixels, String filename, String filter, int start, int end)
    {
        
            //System.out.println(System.currentTimeMillis());

            String images_source = "/Users/paulsoderquist/NetBeansProjects/ImageEditor/";
            //String images_source = "/Volumes/PAUL_SODERQ 1/MIDIs/";

            for (int i = start; i < end; i++) 
            {
                BufferedImage bi = bufferedImageFrames[i];
                
                selected_image = bi;
                image_pixels = toBufferedImage(selected_image);
                //image_copy = getImageCopy();

                switch (filter) {
                    case "Colorize":
                        image_pixels = NeighborKeyColorize(image_pixels);
                        break;
                    case "Mosaic 1":
                        mosaic1();
                        break;
                    case "Mosaic 2":
                        mosaic2();
                        break;
                    case "Emboss":
                        EmbossImage();
                        break;
                    case "Invert Colors":
                        InvertColors();
                        break;
                    case "Grayscale":
                        ConvertToGrayScale();
                        break;
                    case "Step Colors":
                        StepColors();
                        break;
                    case "Pixelate":
                        pixelate();
                        break;
                    case "Create Training Data":
                        //createRedGreenTrainingDataFromImage(fw,fw2);
                        break;
                    case "Count Number of 9px Patterns":
                        countPatterns();
                    default:
                        break;
                }
                updateProgressBar(i);
                if (!filter.equals("Create Training Data") && !filter.equals("Count Number of 9px Patterns"))
                {
                    try {
                        ImageIO.write(image_pixels, "png", new File(images_source + "tmp/video-frame-" + i + ".png"));
                    } catch (IOException ex) {
                        Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            //System.out.println(System.currentTimeMillis());

        
    }
    
    private boolean filterVideoFrames(String filter) throws IOException {
        frameCounter = 0;
        FileWriter fw = null;
        FileWriter fw2 = null;
        if (filter.equals("Create Training Data"))
        {
            redRepresentation = new int[256];
            greenRepresentation = new int[256];
            File f = new File("bw_color_train_reds_25600000.csv");
            File f2 = new File("bw_color_train_greens_25600000.csv");
            fw = new FileWriter(f);
            fw2 = new FileWriter(f2);
        }
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")
                + File.separator + "documents"));
        int result = fc.showOpenDialog(ImageFilters.this);
        

        //progressBar = new JProgressBar(0, 500);
        if (result == JFileChooser.CANCEL_OPTION)
        {
            return false;
        }
        if (fc.getSelectedFile() == null)
        {
            return false;
        }
        Current_Movie_Name = fc.getSelectedFile().getName();

        String filename = fc.getSelectedFile().getAbsolutePath();
        extractAndSaveAudioToFile(filename);
        //System.out.println(filename);
        frameRate = getFrameRate(filename);
        totalFramesInVideo = getFrameCount(filename);
        lastFrame = totalFramesInVideo-1;
        // If I want to render part of a video give a percent (of video) for first and last frame
        //firstFrame = (int)(0.33587786259*totalFramesInVideo);
        //lastFrame = (int)(0.85877862595*totalFramesInVideo);
        firstFrame = 0;
        
        int numThreads = 20;
        int numFramesPerThread = (lastFrame/numThreads)+1;
        threadsComplete = 0;
        /*
        try
        {
            FFmpegFrameGrabber g = new FFmpegFrameGrabber(filename);
            g.start();
            bufferedImageFrames = new BufferedImage[totalFramesInVideo];
            
            for (int i = 0; i < totalFramesInVideo; i++) 
            {
                BufferedImage bi = getImageCopy(g.grab().getBufferedImage());
                bufferedImageFrames[i] = bi;
            }
            
            for (int i = 0; i < numThreads; i++)
            {
                int start = i*numFramesPerThread;
                int end = (i+1)*numFramesPerThread;
                if (end > totalFramesInVideo)
                {
                    end = totalFramesInVideo;
                }
                SectionProcessor sp = new SectionProcessor(filename, filter, start, end);
                Thread t = new Thread(sp);
                t.start();
            }
            g.stop();
        }
        catch (Exception ex)
        {

            System.out.println("There was an error with the file that was selected");
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);

        }
        */
        while (threadsComplete < numThreads)
        {
            try {
                //System.out.println("threads complete: " + threadsComplete);
                // Sleep while threads each process part of the movie
                // Wake up 10 times per second to see if they are done
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (filter.equals("Create Training Data"))
        {
            fw.close();
            fw2.close();
            for (int i = 0; i < 256; i++)
            {
                System.out.println(i + ": " + redRepresentation[i]);
                System.out.println(i + ": " + greenRepresentation[i]);   
            }
        }
        
        return true;
    }
    
    private void createAsciiVideo() {
        try
        {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            fc.setCurrentDirectory(new File(System.getProperty("user.home")
                    + File.separator + "documents"));
            fc.showOpenDialog(ImageFilters.this);
            
            try {
                String filename1 = fc.getSelectedFile().getAbsolutePath();
                //String filename1 = "/Volumes/PAUL_SODERQ 1/Podcasts/tmp/";
                ImageIO.write(image_pixels, "png", new File(filename1 + "/aaatest.png"));
            } catch (IOException ex) {
                Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
            }
            String images_source = "/Users/paulsoderquist/NetBeansProjects/ImageEditor/";
            //String images_source = "/Volumes/PAUL_SODERQ 1/MIDIs/";
            
            System.out.println("Video will start in 10 seconds. Please expand/pull top of output window upward");
            Thread.sleep(10000);
            /*
            try
            {
                if (fc.getSelectedFile() == null)
                {
                    return;
                }
                Current_Movie_Name = fc.getSelectedFile().getName();
                
                String input_filename = fc.getSelectedFile().getAbsolutePath();
                extractAndSaveAudioToFile(input_filename);
                //System.out.println(filename);
                totalFramesInVideo = getFrameCount(input_filename);
                frameRate = getFrameRate(input_filename);
                lastFrame = totalFramesInVideo;
                
                // If I want to render part of a video give a percent (of video) for first and last frame
                //firstFrame = (int)(0.33587786259*totalFramesInVideo);
                //lastFrame = (int)(0.85877862595*totalFramesInVideo);
                FFmpegFrameGrabber g = new FFmpegFrameGrabber(input_filename);
                g.start();
                //System.out.println(System.currentTimeMillis());
                
                
                for (int i = 0 ; i < totalFramesInVideo; i++) {
                    BufferedImage bi = g.grab().getBufferedImage();
                    if (i < firstFrame || i > lastFrame)
                    {
                        continue;
                    }
                    selected_image = bi;
                    image_pixels = toBufferedImage(selected_image);
                    
                    String text = AsciiFilter();
                    System.out.print(text);
                    //Thread.sleep(250);
                    //image_copy = getImageCopy();
                    //Thread.sleep(250);
                    //TwoNeighborKeyColorize();
                    updateProgressBar(i);
                    BufferedImage buffImg = getScreenCapture();

                    // DO THIS TO SCREENSHOT OUTPUT WINDOW:
                    ImageIO.write(buffImg, "png", new File(images_source + "tmp/video-frame-" + i + ".png"));
                    // DO THIS TO SAVE DIRECTLY TO IMAGE:
                    //textToImage(text, "tmp/video-frame-" + i + ".png");
                }
                //System.out.println(System.currentTimeMillis());
                
                g.stop();
                
            }
            catch (Exception ex)
            {
                
                System.out.println("There was an error with the file that was selected");
                Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
                
            }
            */
        }
        catch (InterruptedException ex)
        {
            
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    
    public void openCidsFile()
    {
        try {
            File cidsFile = new File("ids.cids");
            Scanner file_in = new Scanner(cidsFile);
            while (file_in.hasNextLine())
            {
                int id = file_in.nextInt();
                int r = file_in.nextInt();
                int g = file_in.nextInt();
                int b = file_in.nextInt();
                
                for (MaskedObject p : currentProjectState.polygons)
                {
                    if (p.id == id)
                    {
                        p.color = new Color(r,g,b);
                        break;
                    }
                }
                currentObjectID = id+1;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private ArrayList<MaskedObject> getDataFromPMOC(File pmoc) throws FileNotFoundException
    {
        ArrayList<MaskedObject> tempPolygons = new ArrayList();

        Scanner reader = new Scanner(pmoc);
        double scale = reader.nextDouble();
        int numPolygons = reader.nextInt();

        for (int i = 0; i < numPolygons; i++)
        {
            MaskedObject poly = new MaskedObject();
            String next = reader.next();
            int numVertices;
            double depth = 0;
            if (next.equals("rgb"))
            {
                int r = reader.nextInt();
                int g = reader.nextInt();
                int b = reader.nextInt();
                poly.color = new Color(r,g,b);
                next = reader.next();
            }
            
            if (next.equals("hVar"))
            {
                int hueVariation = reader.nextInt();
                poly.hue_variation = hueVariation;
                next = reader.next();
            }
            
            if (next.equals("sVar"))
            {
                int saturationVariation = reader.nextInt();
                poly.saturation_variation = saturationVariation;
                next = reader.next();
            }
            
            if (next.equals("rgb2"))
            {
                int r2 = reader.nextInt();
                int g2 = reader.nextInt();
                int b2 = reader.nextInt();
                poly.secondary_color = new Color(r2,g2,b2);
                next = reader.next();
            }
            
            if (next.equals("hVar2"))
            {
                int hueVariation2 = reader.nextInt();
                poly.secondary_hue_variation = hueVariation2;
                next = reader.next();
            }
            
            if (next.equals("sVar2"))
            {
                int saturationVariation2 = reader.nextInt();
                poly.secondary_sat_variation = saturationVariation2;
                next = reader.next();
            }
            
            if (next.equals("shadowComplement"))
            {
                int shadowComplementThreshold = reader.nextInt();
                poly.complement_threshold = shadowComplementThreshold;
                next = reader.next();
            }
            
            if (next.equals("edgeBlendIndex"))
            {
                int index = reader.nextInt();
                poly.edgeBlendIndex = index;
                next = reader.next();
            }
            
            if (next.equals("depth"))
            {
                depth = reader.nextDouble();
                next = reader.next();
                numVertices = Integer.parseInt(next);
            }
            else
            {
                numVertices = Integer.parseInt(next);
            }

            for (int j = 0; j < numVertices; j++)
            {
                int xcoord = reader.nextInt();
                int ycoord = reader.nextInt();
                poly.polygon.addPoint(xcoord, ycoord);
            }
            poly.id = tempPolygons.size();
            poly.depth = depth;
            tempPolygons.add(poly);
        }
        return tempPolygons;
    }
    
    public void openVideoPMOC(String filepath, File pmoc)
    {
        currentProjectState.polygons.clear();
        try {
            
            currentProjectState.polygons = getDataFromPMOC(pmoc);
            openCidsFile();
            
            selected_file = new File(filepath + ".png");
            selected_image = ImageIO.read(selected_file);
            image_pixels = toBufferedImage(selected_image);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean tryAlreadyHaveImage()
    {
        String filename = filenameTextBox.getText();
        int frameNumber = hsvColorChooser.video_current_value;
        String filepath = "Video Frame PMOCs"+File.separator+filename+"-" + frameNumber;    
        File pmoc = new File(filepath + ".pmoc");
        if(pmoc.exists() && !pmoc.isDirectory()) {
            openVideoPMOC(filepath, pmoc);
            return true;
        }
        return false;
    }
    
    public void advanceFrame()
    {
        try {
            if (!hsvColorChooser.lastPressedWasBackward)
            {
                currentFrame = g.grab();
            }
            BufferedImage bi = currentFrame.getBufferedImage();
            
            //if (tryAlreadyHaveImage())
            {
                // do nothing
            }
            //else
            {
                selected_image = bi;
                image_pixels = toBufferedImage(selected_image);
            }
            repaint();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean imagesAreEqual(BufferedImage bi1, BufferedImage bi2)
    {
        for (int row = 0; row < bi1.getHeight(); row++)
        {
            for (int column = 0; column < bi1.getWidth(); column++)
            {
                Pixel currentPixel1 = getPixel(column,row,bi1);
                Pixel currentPixel2 = getPixel(column,row,bi2);
                if (currentPixel1.getRedValue() != currentPixel2.getRedValue() ||
                    currentPixel1.getGreenValue() != currentPixel2.getGreenValue() ||
                    currentPixel1.getBlueValue() != currentPixel2.getBlueValue()    
                   )
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void goToPreviousFrame()
    {
        /*
            PreviousFrameHack:
            save current image as currFrame
            get image stepBack at position currentIndex - 1 until stepBack != currFrame
            from stepBack grab stepForward (keeping prevImage) until stepForward == currFrame
            display prevImage
        */
        
        try {
            BufferedImage currFrame = getImageCopy(image_pixels);
            int steps = 0;
            BufferedImage stepBack;
            do 
            {
                steps++;
                g.setFrameNumber((int)(hsvColorChooser.video_frame_spinner.getValue())-steps);
                stepBack = g.grab().getBufferedImage();
            } while (imagesAreEqual(currFrame,stepBack) && steps < 30);
            if (steps > 25)
            {
                System.out.println("Got stuck in first loop. Never found different previous frame");
            }
            BufferedImage stepForward = stepBack;
            BufferedImage prevImage;
            steps = 0;
            do
            {
                steps++;
                prevImage = getImageCopy(stepForward);
                stepForward = g.grab().getBufferedImage();
            } while (!imagesAreEqual(stepForward,currFrame) && steps < 30);
            if (steps > 25)
            {
                System.out.println("Got stuck in second loop. Never found same frame ahead");
            }
            selected_image = prevImage;
            image_pixels = toBufferedImage(selected_image);
            //tryAlreadyHaveImage();
            repaint();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setCurrentFrame(int frame_number)
    {
        try {
            System.out.println(g.getFrameRate());
            g.setFrameNumber(frame_number);
            //System.out.println("Number hopefully setting frame to: " + frame_number);
            frame_number = g.getFrameNumber();            

            //System.out.println("changed our frame number to: " + frame_number);
            //System.out.println("Number before grabbing: " + g.getFrameNumber());

            BufferedImage bi = g.grab().getBufferedImage();
            //System.out.println("Number after grabbing: " + g.getFrameNumber());
            selected_image = bi;
            image_pixels = toBufferedImage(selected_image);
            repaint();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadPMOCImage(File selected_pmoc_file)
    {
        try {
            String name = selected_pmoc_file.getName();
            selected_file = new File("PMOCs" + File.separator + name.substring(0,name.length()-5) + ".png");
            selected_image = ImageIO.read(selected_file);
            image_pixels = toBufferedImage(selected_image);
            repaint();
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadPMOC(File selected_pmoc_file)
    {
        try {
            currentProjectState.polygons = getDataFromPMOC(selected_pmoc_file);

            repaint();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadVideo()
    {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home") 
                + File.separator + "documents"));
        fc.showOpenDialog(ImageFilters.this);
        
        try
        {
            if (fc.getSelectedFile() == null)
            {
                return;
            }
            Current_Movie_Name = fc.getSelectedFile().getName();
            String filename = fc.getSelectedFile().getAbsolutePath();
            video_filename = filename;
            System.out.println(filename);
            totalFramesInVideo = 8;//getFrameCount(filename);
            frameRate = getFrameRate(filename);
            double duration = getDuration(filename);
            totalFramesInVideo = (int)(duration*frameRate);
            hsvColorChooser.video_frame_max = totalFramesInVideo;
            hsvColorChooser.video_frame_slider.setMaximum(totalFramesInVideo);
            hsvColorChooser.video_frame_spinner.setModel(new SpinnerNumberModel(0, //initial value
                               0, //min
                               totalFramesInVideo, //max
                               1));
            File f = new File("Video Frame PMOCs" + File.separator + Current_Movie_Name + ".vmoc");
            if (f.exists() && !f.isDirectory())
            {
                Scanner file_in = new Scanner(f);
                int fFrame = file_in.nextInt();
                int numFrames = file_in.nextInt();
                switchToFramesEditor(numFrames, fFrame);
            }
            else
            {
                g = new FFmpegFrameGrabber(video_filename);
                g.start();

                if (totalFramesInVideo > 101788)
                {
                    hsvColorChooser.video_frame_slider.setValue(101788);
                    hsvColorChooser.video_frame_spinner.setValue(101788);
                }
            }
            //setCurrentFrame(0);
            /*
            firstFrame = Integer.parseInt(firstFrameChooser.getText());
            lastFrame = Integer.parseInt(lastFrameChooser.getText());
            */
            /*
            FFmpegFrameGrabber g = new FFmpegFrameGrabber(filename);
            g.start();
            g.setFrameNumber(firstFrame);
            for (int i = firstFrame ; i < totalFramesInVideo-1; i++) {
                BufferedImage bi = g.grab().getBufferedImage();
                if (i < firstFrame)
                {
                    continue;
                }
                if (i > lastFrame)
                {
                    break;
                }
                selected_image = bi;
                image_pixels = toBufferedImage(selected_image);
                ImageIO.write(image_pixels, "png", new File("/Users/paulsoderquist/NetBeansProjects/ImageEditor/tmp/video-frame-" + i + ".png"));

                //ImageIO.write(g.grab().getBufferedImage(), "png", 
                //        new File(Current_Movie_Name.substring(0, Current_Movie_Name.length()-4)
                //                +"/video-frame-" + i + ".png"));
            }
        System.out.println(System.currentTimeMillis());

            g.stop();
            */
        }
        catch (Exception ex)
        {
            
            System.out.println("There was an error with the file that was selected");
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    
    void changeSpinnerActionListeners()
    {
        hsvColorChooser.swapToFrameChangeListener();
        hsvColorChooser.beginFramesButton.setEnabled(false);
        hsvColorChooser.numFramesField.setEnabled(false);
        hsvColorChooser.repaint();
        repaint();
    }
    
    void setVideoFrameAlreadySaved(int frameNumber)
    {
        try {
            String filename = "Video Frame PMOCs" + File.separator + "video-frame-" + frameNumber + ".png";
            String pmocfilename = "Video Frame PMOCs" + File.separator + "video-frame-" + frameNumber + ".pmoc";
            selected_file = new File(filename);
            selected_image = ImageIO.read(selected_file);
            image_pixels = toBufferedImage(selected_image);
            File pmoc = new File(pmocfilename);
            if (pmoc.exists())
            {
                loadPMOC(pmoc);
                currentProjectState.selectedPolygon = null;
                currentProjectState.selectedVertexIndex = -1;
            }
            repaint();
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void switchToFramesEditor(int numberFrames, int firstFrame)
    {
        changeSpinnerActionListeners();
        hsvColorChooser.video_frame_slider.setMaximum(firstFrame+numberFrames);
        hsvColorChooser.video_frame_slider.setMinimum(firstFrame+1);
        hsvColorChooser.video_frame_slider.setValue(firstFrame+1);
        hsvColorChooser.video_frame_spinner.setModel(new SpinnerNumberModel(firstFrame+1, //initial value
                           firstFrame+1, //min
                           firstFrame+numberFrames, //max
                           1));
        hsvColorChooser.repaint();
    }
    
    void grabNextFrames(int numberFrames, int firstFrame) {
        FileWriter fw = null;
        try {
            File f = new File("Video Frame PMOCs" + File.separator + Current_Movie_Name + ".vmoc");
            fw = new FileWriter(f);
            fw.append(firstFrame + " " + numberFrames);
            fw.close();
            for (int i = 0 ; i < numberFrames; i++) {
                try {
                    BufferedImage bi = g.grab().getBufferedImage();
                    
                    selected_image = bi;
                    image_pixels = toBufferedImage(selected_image);
                    ImageIO.write(image_pixels, "png", new File("Video Frame PMOCs" + File.separator + "video-frame-" + (i+1+firstFrame) + ".png"));
                    
                    //ImageIO.write(g.grab().getBufferedImage(), "png",
                    //        new File(Current_Movie_Name.substring(0, Current_Movie_Name.length()-4)
                    //                +"/video-frame-" + i + ".png"));
                } catch (IOException | FrameGrabber.Exception ex) {
                    Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   
            switchToFramesEditor(numberFrames, firstFrame);
            hsvColorChooser.video_frame_slider.setMaximum(firstFrame+numberFrames);
            hsvColorChooser.video_frame_slider.setMinimum(firstFrame+1);
            hsvColorChooser.video_frame_slider.setValue(firstFrame+1);
            hsvColorChooser.video_frame_spinner.setModel(new SpinnerNumberModel(firstFrame+1, //initial value
                    firstFrame+1, //min
                    firstFrame+numberFrames, //max
                    1));
            changeSpinnerActionListeners();
            hsvColorChooser.repaint();
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    private void splitVideoIntoFrames() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home") 
                + File.separator + "documents"));
        fc.showOpenDialog(ImageFilters.this);
        
        try
        {
            if (fc.getSelectedFile() == null)
            {
                return;
            }
            Current_Movie_Name = fc.getSelectedFile().getName();
            String filename = fc.getSelectedFile().getAbsolutePath();
            System.out.println(filename);
            totalFramesInVideo = getFrameCount(filename);
            firstFrame = 0;
            lastFrame = totalFramesInVideo +6;
            FFmpegFrameGrabber g = new FFmpegFrameGrabber(filename);
            g.start();

            for (int i = 0 ; i < totalFramesInVideo; i++) {
                BufferedImage bi = g.grab().getBufferedImage();
                if (i < firstFrame || i > lastFrame)
                {
                    continue;
                }
                selected_image = bi;
                image_pixels = toBufferedImage(selected_image);
                ImageIO.write(image_pixels, "png", new File("/Users/paulsoderquist/NetBeansProjects/ImageEditor/tmp/video-frame-" + i + ".png"));

                //ImageIO.write(g.grab().getBufferedImage(), "png", 
                //        new File(Current_Movie_Name.substring(0, Current_Movie_Name.length()-4)
                //                +"/video-frame-" + i + ".png"));
            }
        System.out.println(System.currentTimeMillis());

            g.stop();
        }
        catch (Exception ex)
        {
            
            System.out.println("There was an error with the file that was selected");
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);

        }
        
    }
    
    private void addSoundToVideo()
    {

        String movieName = Current_Movie_Name.substring(0, Current_Movie_Name.length()-4);
        try 
        {
            String video_source = "/Users/paulsoderquist/NetBeansProjects/ImageEditor/";
            //String video_source = "/Volumes/PAUL_SODERQ 1/Podcasts/";
            
            String audio_source = "/Users/paulsoderquist/NetBeansProjects/ImageEditor/";
            //String audio_source = "/Volumes/PAUL_SODERQ 1/Podcasts/";            
            
            System.out.println("Adding sound to video...");
            //System.out.println(System.currentTimeMillis());
            String[] command = { 
                "/usr/local/bin/ffmpeg", "-y", 
                "-i", 
                video_source + movieName + "_Colorized.mp4", 
                "-i",
                audio_source + "output-audio.aac",
                "-vcodec", "copy",
                "-acodec", "copy",
                "-write_xing", "0",
                "-shortest",
                video_source + movieName+"_ColorizedWithAudio.mp4"
                
                /*
                JUST RUN THIS COMMAND DIRECTLY ON THE COMMAND LINE. IT'S EASIER TO SEE THE PROGRESS ANYWAY
                /usr/local/bin/ffmpeg -i "/Volumes/PAUL_SODERQ 1/MIDIs/Harvey_ColorizedThroughCommandLine.mp4" -i "/Volumes/PAUL_SODERQ 1/MIDIs/output-audio.aac" -vcodec copy -acodec copy -write_xing 0 -shortest "/Volumes/PAUL_SODERQ 1/MIDIs/Harvey_ColorizedWithAudio.mp4"
                */    
                    
                    
            };
            Process proc = Runtime.getRuntime().exec(command);
            
            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(proc.getErrorStream()));
            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            String outputFromCommand = "";
            while ((s = stdInput.readLine()) != null) {
                outputFromCommand += s;
                System.out.println(s);

            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);

            }
            System.out.println(System.currentTimeMillis());
            System.out.println("Finished adding sound to video " + movieName + "_Colorized.mp4");
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    private void assembleVideoFromFrames()
    {
        
        //String img_source = "/Volumes/PAUL_SODERQ 1/Podcasts/";
        String img_source = "/Users/paulsoderquist/NetBeansProjects/ImageEditor/";
        String movieName = Current_Movie_Name.substring(0, Current_Movie_Name.length()-4);
        //firstFrame = 1;
        //lastFrame = 150019;
        try 
        {
            //1520952083694
            //1520952084528
                        System.out.println(System.currentTimeMillis());

            System.out.println("Assembling Frames into Video...");
            //System.out.println(System.currentTimeMillis());
            String[] command = { 
                "/usr/local/bin/ffmpeg", "-y", 
                "-r", frameRate+"", "-f", "image2", 
                "-start_number", firstFrame+"",//1", 
                "-i", 
                img_source + "tmp/video-frame-%d.png", 
                //"-i",
                //"/Users/paulsoderquist/NetBeansProjects/ImageEditor/output-audio.aac",
                "-vframes",
                lastFrame-firstFrame+"",//totalFramesInVideo-1+"",       
                "-c:v", "libx264", "-crf", "25", 
                //"-acodec", "copy",
                "-pix_fmt", "yuv420p",
                "-write_xing", "0",
                img_source + movieName+"_Colorized.mp4"
                    
                /*
                    JUST RUN THIS COMMAND DIRECTLY ON THE COMMAND LINE. IT'S EASIER TO SEE THE PROGRESS ANYWAY
                    /usr/local/bin/ffmpeg -r 24 -f image2 -start_number 1 -i /Volumes/PAUL_SODERQ 1/MIDIs/tmp/video-frame-%d.png" -vframes 150019 -c:v libx264 -crf 25 -pix_fmt yuv420p -write_xing 0 "/Volumes/PAUL_SODERQ 1/MIDIs/testCommand_Colorized.mp4"
                
                    or more recently this is the command:
                    /usr/local/bin/ffmpeg -r 24 -f image2 -start_number 101789 -i "Desktop/Video Frame PMOCs/video-frame-%d.png" -vframes 78 -c:v libx264 -crf 25 -pix_fmt yuv420p -write_xing 0 "Desktop/Video Frame PMOCs/testCommand_Colorized.mp4"
                    
                */
                    
            };
            Process proc = Runtime.getRuntime().exec(command);
            
            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(proc.getErrorStream()));
            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            String outputFromCommand = "";
            while ((s = stdInput.readLine()) != null) {
                outputFromCommand += s;
                System.out.println(s);

            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);

            }
            System.out.println("Finished assembling images to video " + movieName + "_Colorized.mp4");
            System.out.println(System.currentTimeMillis());

            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    private void ResetImage() {
        try
        {
            selected_image = ImageIO.read(selected_file);
            this.setPreferredSize(new Dimension(selected_image.getWidth(null),selected_image.getHeight(null)));
            ResetButton.setEnabled(false);
            revalidate();
            repaint();
        }
        catch (Exception e)
        {
            System.out.println("There was an error with the file that was selected");
        }
    }
    
    
    private BufferedImage getImageCopy(BufferedImage bi) {
        
        Image clone = bi.getScaledInstance(bi.getWidth(null), -1, Image.SCALE_DEFAULT);
        
        return toBufferedImage(clone);
    }
    
    private BufferedImage getImageCopy() {
        
        Image clone = selected_image.getScaledInstance(selected_image.getWidth(null), -1, Image.SCALE_DEFAULT);
        
        return toBufferedImage(clone);
    }
    
    public BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        int screenWidth = frame.getBounds().width;
        int screenHeight = frame.getBounds().height;
        int imgX = screenWidth/2 - img.getWidth(null)/2;
        int imgY = screenHeight/2 - img.getHeight(null)/2;
        //bGr.drawImage(img, imgX, imgY, null);
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
    
    private Pixel getModeHashMap(HashMap<Pixel,Integer> frequencyMap)
    {
        Pixel modePixel = null;
        int highestFrequency = 0;
        for (Map.Entry pair : frequencyMap.entrySet()) {
            
            Pixel keyPixel = (Pixel)(pair.getKey());
            int value = (int)(pair.getValue());
            if (value > highestFrequency)
            {
                highestFrequency = value;
                modePixel = keyPixel;
            }
        }
        return modePixel;
    }


    private static Pixel getMode(ArrayList<Pixel> array)
    {
        HashMap<Pixel,Integer> hm = new HashMap();
        int max  = 1;
        Pixel temp = array.get(0);

        for(int i = 0; i < array.size(); i++) {

            if (hm.get(array.get(i)) != null) {

                int count = hm.get(array.get(i));
                count++;
                hm.put(array.get(i), count);

                if(count > max) {
                    max  = count;
                    temp = array.get(i);
                }
            }

            else 
                hm.put(array.get(i),1);
        }
        return temp;
    }
    
    private Pixel getPixel(int column, int row, BufferedImage image) {
        Color currentPixelColor = new Color(image.getRGB(column, row));        
        int red = currentPixelColor.getRed();
        int green = currentPixelColor.getGreen();
        int blue = currentPixelColor.getBlue();
        return new Pixel(red,green,blue,column,row);
    }
    
    
    private void trainForOptimizedInterpolation()
    {
        for (int row = 1; row < image_pixels.getHeight()-1; row+=2)
        {
            for (int column = 2; column < image_pixels.getWidth()-1; column++)
            {
                String indexConcat = "";
                Pixel pixel = getPixel(column,row,image_pixels);

                int average = pixel.getAverage();
                int right = getPixel(column + 1, row, image_pixels).getAverage();
                int down = getPixel(column, row + 1, image_pixels).getAverage();
                int left = getPixel(column - 1, row, image_pixels).getAverage();
                int up = getPixel(column, row - 1, image_pixels).getAverage();

                indexConcat += average + " " + right + " " + down + " " + left + " " + up;
                double neighborMean = (right + down + left + up) / 4.0;
                Pair pair = new Pair();
                pair.key = neighborMean;
                pair.value = indexConcat;
                if (neighborMeanMap[average] == null)
                {
                    neighborMeanMap[average] = new ArrayList();
                }
                neighborMeanMap[average].add(pair);
                
                
                if (optInterpolationDictionary[average] == null)
                {
                    optInterpolationDictionary[average] = new ArrayList[256][][][];
                }
                if (optInterpolationDictionary[average][right] == null)
                {
                    optInterpolationDictionary[average][right] = new ArrayList[256][][];
                }
                if (optInterpolationDictionary[average][right][down] == null)
                {
                    optInterpolationDictionary[average][right][down] = new ArrayList[256][];
                }
                if (optInterpolationDictionary[average][right][down][left] == null)
                {
                    optInterpolationDictionary[average][right][down][left] = new ArrayList[256];
                }
                if (optInterpolationDictionary[average][right][down][left][up] == null)
                {
                    optInterpolationDictionary[average][right][down][left][up] = new ArrayList();
                }
                if (!FirstMappingOnly.isSelected() 
                    || optInterpolationDictionary[average][right][down][left][up].isEmpty())
                {
                    optInterpolationDictionary[average][right][down][left][up].add(pixel);

                }
                
            }
        }
        
    }
    
    private String getColorizationKey(BufferedImage image_pixels, int row, int column, Pixel pixel)
    {
        String key = pixel.getAverage() + "";
        int numNeighbors = 0;
        int sumNeighbors = 0;
        if (keyList.getSelectedItem().equals("Optimize Interpolation"))
        {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, 
                    "Tried to apply optimized interpolation key in the wrong data structure");
        }
        else
        {
            if (keyList.getSelectedIndex() > 0)
            {
                int right = getPixel(column + 1, row, image_pixels).getAverage();
                key += "_" + right;
                numNeighbors++;
                sumNeighbors += right;
            } 
            if (keyList.getSelectedIndex() > 1)
            {
                int down = getPixel(column, row+1, image_pixels).getAverage();
                key += "_" + down;
                numNeighbors++;
                sumNeighbors += down;
            }
            if (keyList.getSelectedIndex() > 2)
            {
                int left = getPixel(column - 1, row, image_pixels).getAverage();
                key += "_" + left;
                numNeighbors++;
                sumNeighbors += left;
            }
            if (keyList.getSelectedIndex() > 3)
            {
                int up = getPixel(column, row-1, image_pixels).getAverage();
                key += "_" + up;
                numNeighbors++;
                sumNeighbors += up;
            }
        
            if (InterpolateGapsCheckbox.isSelected())
            {
                double neighbor_mean = sumNeighbors / numNeighbors;
                key += "*" + (double)Math.round(neighbor_mean * 100000d) / 100000d;
            }
        }
        return key;
    }
    
    private void train2NeighborKey()
    {        
        image_pixels = toBufferedImage(selected_image);
        //System.out.println(keyList.getSelectedIndex());
        if (keyList.getSelectedItem().equals("Optimized Interpolate"))
        {
            trainForOptimizedInterpolation();
            return;
        }
        
        for (int row = 1; row < image_pixels.getHeight()-1; row++)
        {
            for (int column = 1; column < image_pixels.getWidth()-1; column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);

                String key = getColorizationKey(image_pixels,row,column,pixel);
                
                
                
                HashMap<Pixel,Integer> pxls = neighbor_key_dictionary.get(key);
                if (pxls == null)
                {
                    pxls = new HashMap();
                    pxls.put(pixel,1);
                    neighbor_key_dictionary.put(key, pxls);
                }
                else if (FirstMappingOnly.isSelected() == false)
                {
                    Integer value = pxls.get(pixel);
                    if (value == null)
                    {
                        pxls.put(pixel,1);
                    }
                    else
                    {
                        value += 1;
                        pxls.put(pixel,value);  
                    }
                }
                
            }
        }
        int filledCounter = neighbor_key_dictionary.size();
        
        System.out.println(filledCounter + "   /   16,777,216 containers filled");//"1,099,511,600,000 containers filled");
    }
    
    private void train24NeighborSumMode() {
        image_pixels = toBufferedImage(selected_image);
        int boxHeight = 5;
        int boxWidth = 5;
        for (int row = boxHeight/2; row < image_pixels.getHeight()-boxHeight/2; row++)
        {
            for (int column = boxWidth/2; column < image_pixels.getWidth()-boxWidth/2; column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int sum = 0;
                for (int i = -(boxHeight/2); i < (boxHeight+1)/2; i++)
                {
                    for (int j = -(boxWidth/2); j < (boxWidth+1)/2; j++)
                    {
                        Pixel p = getPixel(column+i,row+j,image_pixels);
                        int red = p.getRedValue();
                        int green = p.getGreenValue();
                        int blue = p.getBlueValue();
                        sum += (red + green + blue)/3;
                    } 
                }
                
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue();
                if (colorizeArrayMode[sum] == null)
                {
                    colorizeArrayMode[sum] = new ArrayList();
                }
                colorizeArrayMode[sum].add(new Pixel(r,g,b,column,row));
                
            }
        }
        int filledCounter = 0;

        for (int i = 0; i < 6376; i++)
        {
            if (colorizeArrayMode[i] != null)
            {
                Pixel mode = getMode(colorizeArrayMode[i]);

                if (colorizationArray[i] == null)
                {
                    colorizationArray[i] = new Pixel(0,0,0,0,0);
                }
                colorizationArray[i].red = mode.red;
                colorizationArray[i].green = mode.green;
                colorizationArray[i].blue = mode.blue;
            }
            if (colorizationArray[i] != null)
            {
                filledCounter ++;
            }
        }
        System.out.println(filledCounter + "/6376 containers filled");
    }
    
    private void trainColorizeArrayMode() {
        image_pixels = toBufferedImage(selected_image);

        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue();
                int average_value = (r + g + b) / 3;
                if (colorizeArrayMode[average_value] == null)
                {
                    colorizeArrayMode[average_value] = new ArrayList();
                }
                colorizeArrayMode[average_value].add(new Pixel(r,g,b,column,row));
                
            }
        }
        int filledCounter = 0;

        for (int i = 0; i < 256; i++)
        {
            if (colorizeArrayMode[i] != null)
            {
                Pixel mode = getMode(colorizeArrayMode[i]);

                if (colorizationArray[i] == null)
                {
                    colorizationArray[i] = new Pixel(0,0,0,0,0);
                }
                colorizationArray[i].red = mode.red;
                colorizationArray[i].green = mode.green;
                colorizationArray[i].blue = mode.blue;
            }
            if (colorizationArray[i] != null)
            {
                filledCounter ++;
            }
        }
        System.out.println(filledCounter + "/256 containers filled");
    }

    private void trainColorizeArrayMean() {
        image_pixels = toBufferedImage(selected_image);

        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue();
                int average_value = (r + g + b) / 3;
                trainingImageContributionCountPerValue[average_value]++;
                if (colorizationArray[average_value]==null)
                {
                    colorizationArray[average_value] = new Pixel(r,g,b,column,row);
                }
                else
                {
                    int numTimes = trainingImageContributionCountPerValue[average_value];
                    colorizationArray[average_value].red = ((colorizationArray[average_value].red*(numTimes-1))+r)/numTimes;
                    colorizationArray[average_value].green = ((colorizationArray[average_value].green*(numTimes-1))+g)/numTimes;
                    colorizationArray[average_value].blue = ((colorizationArray[average_value].blue*(numTimes-1))+b)/numTimes;
                }
            }
        }
        int filledCounter = 0;
        for (int i = 0; i < 256; i++)
        {
            if (colorizationArray[i] != null)
            {
                filledCounter ++;
            }
        }
        System.out.println(filledCounter + "/256 containers filled");
    }

    public Color getColorById(int id)
    {
        for (MaskedObject p : currentProjectState.polygons)
        {
            if (p.id == id)
            {
                return p.color;
            }
        }
        return null;
    }
    
    
    private void validateDraggingAdjacentObject(int x, int y)
    {
        // Check if they're trying to click along a border of another polygon
        if (currentProjectState.selectedPolygon != null)
        {
            currentProjectState.adjacentPolygonVertex = -1;
            currentProjectState.adjacentPolygon = null;
            for (MaskedObject p : currentProjectState.polygons)
            {
                if (p == currentProjectState.selectedPolygon)
                {
                    continue;
                }
                int possible_index = clickNearVertexOfPolygon(p.polygon,x,y);
                if (possible_index != -1)
                {
                    currentProjectState.adjacentPolygon = p;
                    currentProjectState.adjacentPolygonVertex = possible_index;
                    break;
                }
            }
            
        }
    }
    
    private boolean perimeterContains(MaskedObject m, int x, int y)
    {
        for (int i = 0; i < m.polygon.npoints; i++)
        {
            if (m.polygon.xpoints[i] == x && m.polygon.ypoints[i] == y)
            {
                return true;
            }
        }
        return false;
    }
    
    private int getNearestCorrespondingPointIndex(Polygon p1, int x, int y)
    {
        for (int i = 0; i < p1.npoints; i++)
        {
            if (p1.xpoints[i] == x && p1.ypoints[i] == y)
            {
                return i;
            }
        }
        return -1;
    }
        
    private void validateHoveringAdjacentObject(int x, int y)
    {
        currentProjectState.tempAutoCompletePolygon = null;
        // Check if they're trying to click along a border of another polygon
        if (currentProjectState.selectedPolygon != null && currentProjectState.hoverVertexIndex == -1)
        {
            currentProjectState.adjacentPolygonVertex = -1;
            currentProjectState.adjacentPolygon = null;
            for (MaskedObject p : currentProjectState.polygons)
            {
                if (p == currentProjectState.selectedPolygon)
                {
                    continue;
                }
                int possible_index = clickNearVertexOfPolygon(p.polygon,x,y);
                if (possible_index != -1)
                {
                    currentProjectState.adjacentPolygon = p;
                    currentProjectState.adjacentPolygonVertex = possible_index;
                    if (AutoCompleteBorder)
                    {
                        int x2 = currentProjectState.selectedPolygon.polygon.xpoints[currentProjectState.selectedVertexIndex];
                        int y2 = currentProjectState.selectedPolygon.polygon.ypoints[currentProjectState.selectedVertexIndex];
                        if (perimeterContains(p,x2,y2))
                        {
                            int anchorPointIndex = getNearestCorrespondingPointIndex(p.polygon,x2,y2);
                            currentProjectState.tempAutoCompletePolygon = deepCopyPolygon(currentProjectState.selectedPolygon);
                            if (BorderReversed)
                            {
                                for (int i = anchorPointIndex - 1; i != possible_index-1; i--)
                                {
                                    if (i < 0) i += p.polygon.npoints;
                                    int x3 = p.polygon.xpoints[i];
                                    int y3 = p.polygon.ypoints[i];
                                    currentProjectState.tempAutoCompletePolygon.polygon.addPoint(x3, y3);
                                }
                            }
                            else
                            {
                                for (int i = anchorPointIndex + 1; i != possible_index+1; i++)
                                {
                                    if (i >= p.polygon.npoints) i -= p.polygon.npoints;
                                    int x3 = p.polygon.xpoints[i];
                                    int y3 = p.polygon.ypoints[i];
                                    currentProjectState.tempAutoCompletePolygon.polygon.addPoint(x3, y3);
                                }
                            }
                            repaint();
                        }
                    }
                    break;
                }
            }
            
        }
    }
    
    private ArrayList<MaskedObject> getAllContainingObjects(int x, int y)
    {
        ArrayList<MaskedObject> PolygonsThatContainPoint = new ArrayList();
        for (MaskedObject p : currentProjectState.polygons)
        {
            if (p.polygon.contains(x,y))
            {
                PolygonsThatContainPoint.add(p);
            }
        }
        return PolygonsThatContainPoint;
    }
    
    private MaskedObject pointIsContainedByObject(int x, int y) {
        ArrayList<MaskedObject> PolygonsThatContainPoint = getAllContainingObjects(x,y);
        
        if (PolygonsThatContainPoint.isEmpty())
        {
            return null;
        }
        else if (PolygonsThatContainPoint.size() == 1)
        {
            return PolygonsThatContainPoint.get(0);
        }
        else
        {
            for (int i = 0; i < PolygonsThatContainPoint.size(); i++)
            {
                MaskedObject p = PolygonsThatContainPoint.get(i);
                for (int j = 0; j < PolygonsThatContainPoint.size(); j++)
                {
                    if (i == j)
                    {
                        continue;
                    }
                    boolean p2CompletelyContainedByP = true;
                    MaskedObject p2 = PolygonsThatContainPoint.get(j);
                    for (int k = 0; k < p2.polygon.npoints; k++)
                    {
                        if (!p.polygon.contains(p2.polygon.xpoints[k],p2.polygon.ypoints[k]))
                        {
                            p2CompletelyContainedByP = false;
                            break;
                        }
                    }
                    if (p2CompletelyContainedByP)
                    {
                        PolygonsThatContainPoint.remove(p);
                        i = -1;
                        break;
                    }
                } 
            }
            return PolygonsThatContainPoint.get(0);
        }
    }

    private int clickNearVertex(int x, int y) {
        if (currentProjectState.selectedPolygon == null)
        {
            return -1;
        }
        double minDistance = 100;
        int minIndex = -1;
        for (int i = 0; i < currentProjectState.selectedPolygon.polygon.npoints; i++)
        {
            double distance = Math.hypot(currentProjectState.selectedPolygon.polygon.xpoints[i]-x,
                    currentProjectState.selectedPolygon.polygon.ypoints[i]-y);
            if (distance <= 20.0/scale )
            {
                if (distance < minDistance)
                {
                    minDistance = distance;
                    minIndex = i;
                }
            }
        }
        
        return minIndex;
    }
    
    private int clickNearVertexOfPolygon(Polygon p, int x, int y) {
        if (p == null)
        {
            return -1;
        }
        double minDistance = 100;
        int minIndex = -1;
        for (int i = 0; i < p.npoints; i++)
        {
            double distance = Math.hypot(p.xpoints[i]-x, p.ypoints[i]-y);
            if (distance <= 20.0/scale )
            {
                if (distance < minDistance)
                {
                    minDistance = distance;
                    minIndex = i;
                }
            }
        }
        return minIndex;
    }
    
       
    @Override
    public void mouseClicked(MouseEvent e) {
        Click click = convertClick(e);

        if (toolList.getSelectedItem().equals("Magic Select"))
        {
            magicSelect(click.x, click.y);
            //QuickFill.quickFillSelect(e.getX(),e.getY(), image_pixels);
            repaint();
            return;
        }
        else if (toolList.getSelectedItem().equals("Select Polygon Mode"))
        {
            MaskedObject clickedObject = pointIsContainedByObject(click.x, click.y);
            if (clickedObject != null)
            {
                currentProjectState.selectedPolygon = clickedObject;
                currentProjectState.selectedVertexIndex = clickedObject.polygon.npoints-1;
                if (hsvColorChooser.whichColorList.getSelectedItem().equals("Primary Color"))
                {
                    hsvColorChooser.setSelectedColor(currentProjectState.selectedPolygon.color);
                    hsvColorChooser.hue_variation_spinner.setValue(clickedObject.hue_variation);
                    hsvColorChooser.sat_variation_spinner.setValue(clickedObject.saturation_variation);
                    hsvColorChooser.hue_var_slider.setValue(clickedObject.hue_variation);
                    hsvColorChooser.sat_var_slider.setValue(clickedObject.saturation_variation);
                }
                else if (hsvColorChooser.whichColorList.getSelectedItem().equals("Secondary Color"))
                {
                    hsvColorChooser.setSelectedColor(currentProjectState.selectedPolygon.secondary_color);
                    hsvColorChooser.hue_variation_spinner.setValue(clickedObject.secondary_hue_variation);
                    hsvColorChooser.sat_variation_spinner.setValue(clickedObject.secondary_sat_variation);
                    hsvColorChooser.hue_var_slider.setValue(clickedObject.secondary_hue_variation);
                    hsvColorChooser.sat_var_slider.setValue(clickedObject.secondary_sat_variation);
                }
                hsvColorChooser.depthField.setText(clickedObject.depth+"");
                
                hsvColorChooser.complement_spinner.setValue(clickedObject.complement_threshold);
                hsvColorChooser.complement_slider.setValue(clickedObject.complement_threshold);
                hsvColorChooser.edgeBlendList.setSelectedIndex(clickedObject.edgeBlendIndex);
                hsvColorChooser.idField.setText(clickedObject.id+"");

            }
            else
            {
                currentProjectState.selectedVertexIndex = -1;
                currentProjectState.selectedPolygon = null;
                hsvColorChooser.setSelectedColor(Color.white);
                hsvColorChooser.depthField.setText("0");
                hsvColorChooser.idField.setText("NULL");
            }
            repaint();
        }
        else if (toolList.getSelectedItem().equals("Vertex Mode"))
        {
            if (currentProjectState.tempAutoCompletePolygon != null)
            {
                for (int i = 0; i < currentProjectState.polygons.size(); i++)
                {
                    if (currentProjectState.polygons.get(i).polygon.equals(currentProjectState.selectedPolygon.polygon))
                    {
                        currentProjectState.polygons.remove(i);
                        currentProjectState.polygons.add(i,currentProjectState.tempAutoCompletePolygon);
                    }
                }
                currentProjectState.selectedPolygon = currentProjectState.tempAutoCompletePolygon;
                currentProjectState.selectedVertexIndex = currentProjectState.selectedPolygon.polygon.npoints-1;
                currentProjectState.tempAutoCompletePolygon = null;
                repaint();
                return;
            }
            
            int index = clickNearVertex(click.x, click.y);
            if (index != -1)
            {
                currentProjectState.selectedVertexIndex = index;
                cycleToSelected();
            }
            else
            {
                if (currentProjectState.selectedPolygon == null)
                {
                    currentProjectState.selectedPolygon = new MaskedObject();
                    currentProjectState.selectedPolygon.id = ++currentObjectID;
                    currentProjectState.polygons.add(currentProjectState.selectedPolygon);
                }
                if (currentProjectState.adjacentPolygon != null && currentProjectState.adjacentPolygonVertex != -1)
                {
                    currentProjectState.selectedPolygon.polygon.addPoint(
                            currentProjectState.adjacentPolygon.polygon.xpoints[currentProjectState.adjacentPolygonVertex],
                            currentProjectState.adjacentPolygon.polygon.ypoints[currentProjectState.adjacentPolygonVertex]);
                    
                }
                else
                {
                    currentProjectState.selectedPolygon.polygon.addPoint(click.x, click.y);
                }
                currentProjectState.selectedVertexIndex = currentProjectState.selectedPolygon.polygon.npoints-1;
                
                
                
            }
            
            
            
            repaint();
        }
        /*
        if (selectedPolygon != null)
        {
            System.out.println(e.getX() + ", " + e.getY());
            selectedPolygon.addPoint(e.getX(),e.getY());
            for (int i = 0; i < selectedPolygon.xpoints.length; i++)
            {
                int x1 = selectedPolygon.xpoints[i];
                int y1 = selectedPolygon.ypoints[i];
                int x2 = selectedPolygon.xpoints[(i+1)%selectedPolygon.xpoints.length];
                int y2 = selectedPolygon.ypoints[(i+1)%selectedPolygon.xpoints.length];
                System.out.println("Polygon point: " + x1 + "," + y1 + " " + x2 + "," + y2);
            }
            repaint();
        }
        */
        this.requestFocus();
    }

    public void cycleToSelected()
    {
        int numPoints = currentProjectState.selectedPolygon.polygon.npoints;
        while (currentProjectState.selectedVertexIndex != numPoints - 1)
        {
            int tempX = currentProjectState.selectedPolygon.polygon.xpoints[numPoints-1];
            int tempY = currentProjectState.selectedPolygon.polygon.ypoints[numPoints-1];
            for (int i = numPoints-2; i >= 0; i--)
            {
                currentProjectState.selectedPolygon.polygon.xpoints[i+1] = currentProjectState.selectedPolygon.polygon.xpoints[i];
                currentProjectState.selectedPolygon.polygon.ypoints[i+1] = currentProjectState.selectedPolygon.polygon.ypoints[i];
            }
            currentProjectState.selectedPolygon.polygon.xpoints[0] = tempX;
            currentProjectState.selectedPolygon.polygon.ypoints[0] = tempY;
            currentProjectState.selectedVertexIndex++;
        }
        currentProjectState.hoverVertexIndex = -1;
        currentProjectState.selectedPolygon.polygon.invalidate();
        repaint();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (!toolList.getSelectedItem().equals("Vertex Mode") 
                && !toolList.getSelectedItem().equals("Drag Select Objects")
                && !toolList.getSelectedItem().equals("Drag Select Vertices")
                && !toolList.getSelectedItem().equals("Pen Tool"))
        {
            return;
        }
        if (toolList.getSelectedItem().equals("Drag Select Objects") || 
                toolList.getSelectedItem().equals("Drag Select Vertices"))
        {
            deselect();            
            DragDown = convertClick(e);
            currentProjectState.selectedObjects.clear();
            currentProjectState.selectedVertices.clear();
            repaint();
        }
        else if (toolList.getSelectedItem().equals("Pen Tool"))
        {
            dragCounter = 0;
            Click start = convertClick(e);
            currentProjectState.selectedPolygon = new MaskedObject();
            currentProjectState.selectedPolygon.polygon.addPoint(start.x, start.y);
            currentProjectState.selectedVertexIndex = 0;
            currentProjectState.polygons.add(currentProjectState.selectedPolygon);
            repaint();
        }
        else
        {
            if (currentProjectState.hoverVertexIndex != -1)
            {
                currentProjectState.selectedVertexIndex = currentProjectState.hoverVertexIndex;
                currentlyDragging = true;
            }
        }
    }

    public void selectObjects()
    {
        int width = DragUp.x - DragDown.x;
        int height = DragUp.y - DragDown.y;
        Rectangle rect = new Rectangle(DragDown.x, DragDown.y, width, height);
        for (MaskedObject p : currentProjectState.polygons)
        {
            for (int i = 0; i < p.polygon.npoints; i++)
            {
                PointXY p2 = new PointXY(p.polygon.xpoints[i], p.polygon.ypoints[i]);
                if (rect.contains(p2.x,p2.y))
                {
                    currentProjectState.selectedObjects.add(p);
                    break;
                }
            }
        }
    }
    
    public void selectVertices()
    {
        int width = DragUp.x - DragDown.x;
        int height = DragUp.y - DragDown.y;
        Rectangle rect = new Rectangle(DragDown.x, DragDown.y, width, height);
        for (MaskedObject p : currentProjectState.polygons)
        {
            ArrayList<Integer> vertexList = new ArrayList();

            for (int i = 0; i < p.polygon.npoints; i++)
            {
                PointXY p2 = new PointXY(p.polygon.xpoints[i], p.polygon.ypoints[i]);
                if (rect.contains(p2.x,p2.y))
                {
                    if (!currentProjectState.selectedObjects.contains(p))
                    {
                        currentProjectState.selectedObjects.add(p);
                    }
                    vertexList.add(i);
                }
            }
            if (vertexList.size() > 0)
            {
                currentProjectState.selectedVertices.add(vertexList);
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        currentlyDragging = false;
        if (toolList.getSelectedItem().equals("Drag Select Objects"))
        {
            //lisa falls .5 miles rappel optional 60 ft 
            selectObjects();
            DragUp = null;
        }
        else if (toolList.getSelectedItem().equals("Drag Select Vertices"))
        {
            selectVertices();
            DragUp = null;
        }
        
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.currentProjectState.adjacentPolygon = null;
        this.currentProjectState.adjacentPolygonVertex = -1;
        repaint();
    }
    
    public Click convertClick(MouseEvent e) {
        Click click = new Click();
        int screenWidth = frame.getBounds().width;
        int screenHeight = frame.getBounds().height;
        int imgLeftX = 0;
        int imgTopY = 0;
        if (selected_image != null)
        {
            imgLeftX = screenWidth/2 - selected_image.getWidth(null)/2;
            imgTopY = screenHeight/2 - selected_image.getHeight(null)/2;
        }
        
        int clickedX = 0;
        int clickedY = 0;
        
        clickedX += (int)((e.getX()+leftRight*scrollAmount*scale)/scale);
        clickedY += (int)((e.getY()+upDown*scrollAmount*scale)/scale);
        
        clickedX -= (screenWidth/2)/scale;
        clickedY -= (screenHeight/2)/scale;
        click.x = clickedX;
        click.y = clickedY;
        return click;
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
       //System.out.println("Mouse moved");
       if (!hasFocus())
       {
           requestFocus();
       }
       if (!toolList.getSelectedItem().equals("Vertex Mode"))
        {
            return;
        }
       Click click = convertClick(e);
       
        int oldHover = currentProjectState.hoverVertexIndex;
        currentProjectState.hoverVertexIndex = clickNearVertex(click.x,click.y);
        
        if (oldHover != currentProjectState.hoverVertexIndex)
        {
            repaint();
        }
        
        int oldHoverAdjacent = currentProjectState.adjacentPolygonVertex;
        validateHoveringAdjacentObject(click.x,click.y);
        if (oldHoverAdjacent != currentProjectState.adjacentPolygonVertex)
        {
            repaint();
        }
       
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Click click = convertClick(e);
        if (toolList.getSelectedItem().equals("Pen Tool"))
        {
            if (dragCounter % drawPrecision == 0)
            {
                currentProjectState.selectedPolygon.polygon.addPoint(click.x, click.y);
            }
            dragCounter++;
            repaint();
        }
        else if (currentlyDragging)
        {     
            validateDraggingAdjacentObject(click.x,click.y);
            if (currentProjectState.adjacentPolygonVertex != -1)
            {               
                currentProjectState.selectedPolygon.polygon.xpoints[currentProjectState.selectedVertexIndex]
                        = currentProjectState.adjacentPolygon.polygon.xpoints[currentProjectState.adjacentPolygonVertex];
                currentProjectState.selectedPolygon.polygon.ypoints[currentProjectState.selectedVertexIndex]
                        = currentProjectState.adjacentPolygon.polygon.ypoints[currentProjectState.adjacentPolygonVertex];
            }
            else
            {
                currentProjectState.selectedPolygon.polygon.xpoints[currentProjectState.selectedVertexIndex] = click.x;
                currentProjectState.selectedPolygon.polygon.ypoints[currentProjectState.selectedVertexIndex] = click.y;
            }
            // This code below fixes the problem of the points being directly 
            // manipulated and the bounding box not updated
            currentProjectState.selectedPolygon.polygon.invalidate();
        }
        // int oldHoverAdjacent = adjacentPolygonVertex;
        //if (oldHoverAdjacent != adjacentPolygonVertex)
        //{
        //    repaint();
        //}
        
        
        if (toolList.getSelectedItem().equals("Drag Select Objects") ||
                toolList.getSelectedItem().equals("Drag Select Vertices"))
        {
            DragUp = convertClick(e);
            
        }
        
        repaint();
        
    }
    

    private void magicSelect(int x, int y) {
        ObjectSelection = new HashSet();
        image_pixels = toBufferedImage(selected_image);
        rectangle = new Rectangle();
        int gradientThreshold = 5;
        Pixel pixel = getPixel(x,y,image_pixels);
        
        ObjectSelection.add(new PointXY(pixel.column, pixel.row));
        
        int firstAverage = pixel.getAverage();
        int rightX = x;
        int leftX = x;
        int upY = y;
        int downY = y;
        int lastAverage = firstAverage;
        HashSet<PointXY> EdgesSelection = new HashSet();
        EdgesSelection.add(new PointXY(pixel.column, pixel.row));
        int lastSize = 0;
        
        do 
        {
            Iterator itr = EdgesSelection.iterator();
            HashSet<PointXY> toAdd = new HashSet();
            HashSet<PointXY> toRemove = new HashSet();
            
            while(itr.hasNext())
            {
                lastSize = ObjectSelection.size();
                PointXY p = (PointXY)(itr.next());
                if (!p.isObjectEdgePixel)
                {
                    continue;
                }
                Pixel pix = getPixel(p.x,p.y,image_pixels);
                
                int averageP = pix.getAverage();
                
                int neighborCounter = 0;
                
                if (pix.row > 0)
                {
                    Pixel topPixel = getPixel(pix.column,pix.row-1,image_pixels);
                    int topP = topPixel.getAverage();
                    if (Math.abs(averageP - topP) < gradientThreshold)
                    {
                        neighborCounter++;
                        toAdd.add(new PointXY(topPixel.column, topPixel.row));
                    }
                }
                if (pix.row < image_pixels.getHeight() - 1)
                {
                    Pixel bottomPixel = getPixel(pix.column,pix.row+1,image_pixels);
                    int bottomP = bottomPixel.getAverage();
                    if (Math.abs(averageP - bottomP) < gradientThreshold)
                    {
                        neighborCounter++;
                        toAdd.add(new PointXY(bottomPixel.column, bottomPixel.row));
                    }
                }
                if (pix.column > 0)
                {
                    Pixel leftPixel = getPixel(pix.column-1,pix.row,image_pixels);
                    int leftP = leftPixel.getAverage();
                    if (Math.abs(averageP - leftP) < gradientThreshold)
                    {
                        neighborCounter++;
                        toAdd.add(new PointXY(leftPixel.column, leftPixel.row));
                    }
                }
                if (pix.column < image_pixels.getWidth() - 1)
                {
                    Pixel rightPixel = getPixel(pix.column+1,pix.row,image_pixels);
                    int rightP = rightPixel.getAverage();
                    if (Math.abs(averageP - rightP) < gradientThreshold )
                    {
                        neighborCounter++;
                        toAdd.add(new PointXY(rightPixel.column, rightPixel.row));
                    }
                }
                if (neighborCounter == 4)
                {
                    p.isObjectEdgePixel = false;
                    toRemove.add(p);
                }
                
                
            }
            for(PointXY p : toAdd) 
            {
                ObjectSelection.add(p);
                if (p.isObjectEdgePixel)
                {
                    EdgesSelection.add(p);
                }
            }
            for(PointXY p : toRemove) 
            {
                if (!p.isObjectEdgePixel)
                {
                    EdgesSelection.remove(p);
                }
            }
        } while (lastSize < ObjectSelection.size());
        /*
        while (Math.abs(pixel.getAverage() - lastAverage) < gradientThreshold)
        {
        lastAverage = pixel.getAverage();
        // go right
        rightX++;
        pixel = getPixel(rightX,y,image_pixels);
        if (rightX + 1 > image_pixels.getWidth() - 1)
        {
        break;
        }
        }
        pixel = getPixel(x,y,image_pixels);
        lastAverage = firstAverage;
        while (Math.abs(pixel.getAverage() - lastAverage) < gradientThreshold)
        {
        lastAverage = pixel.getAverage();
        // go left
        leftX--;
        pixel = getPixel(leftX,y,image_pixels);
        if (leftX - 1 < 0)
        {
        break;
        }
        }
        pixel = getPixel(x,y,image_pixels);
        lastAverage = firstAverage;
        while (Math.abs(pixel.getAverage() - lastAverage) < gradientThreshold)
        {
        lastAverage = pixel.getAverage();
        // go up
        upY--;
        pixel = getPixel(x,upY,image_pixels);
        if (upY - 1 < 0)
        {
        break;
        }
        }
        pixel = getPixel(x,y,image_pixels);
        lastAverage = firstAverage;
        while (Math.abs(pixel.getAverage() - lastAverage) < gradientThreshold)
        {
        lastAverage = pixel.getAverage();
        // go down
        downY++;
        pixel = getPixel(x,downY,image_pixels);
        if (downY + 1 > image_pixels.getHeight() - 1)
        {
        break;
        }
        }
        rectangle.x = leftX;
        rectangle.y = upY;
        rectangle.width = rightX - leftX;
        rectangle.height = downY - upY;
         */
        for (PointXY p : ObjectSelection) {
            Pixel pix = getPixel(p.x,p.y,image_pixels);
            pix.setRGB(0,0,255);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_EQUALS)
        {
            // zoom in
            System.out.println("Zoom in");
            scale *= 2;
            if (scale > 8)
            {
                scale = 8;
            }
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_MINUS)
        {
            // zoom out
            System.out.println("Zoom out");
            scale /= 2;
            if (scale < .125)
            {
                scale = .125;
            }
            repaint();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            //System.out.println("Pressed UP");
            upDown--;
            repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            //System.out.println("Pressed DOWN");
            upDown++;
            repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            //System.out.println("Pressed RIGHT");
            leftRight++;
            repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            //System.out.println("Pressed LEFT");
            leftRight--;
            repaint();
            return;
        }
        
        
        
        
        
        if (e.getKeyCode() == KeyEvent.VK_W)
        {
            if (!currentProjectState.selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Objects"))
            {
                for (MaskedObject p : currentProjectState.selectedObjects)
                {
                    for (int i = 0; i < p.polygon.npoints; i++)
                    {
                        p.polygon.ypoints[i]--;
                    }
                    p.polygon.invalidate();
                }
            }
            else if (!currentProjectState.selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Vertices"))
            {
                for (int i = 0; i < currentProjectState.selectedVertices.size(); i++)
                {
                    ArrayList<Integer> vertList = currentProjectState.selectedVertices.get(i);
                    Polygon p = currentProjectState.selectedObjects.get(i).polygon;
                    for (int j = 0; j < currentProjectState.selectedVertices.get(i).size(); j++)
                    {
                        p.ypoints[vertList.get(j)]--;
                    }
                    p.invalidate();
                }
            }
            else if (currentProjectState.selectedPolygon != null)
            {
                //System.out.println("Pressed UP");
                currentProjectState.selectedPolygon.polygon.ypoints[currentProjectState.selectedVertexIndex]--;
                //selectedVertex.y = selectedPolygon.ypoints[selectedVertexIndex];
                currentProjectState.selectedPolygon.polygon.invalidate();
            }
            
            repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_S)
        {
            
            if (!currentProjectState.selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Objects"))
            {
                for (MaskedObject p : currentProjectState.selectedObjects)
                {
                    for (int i = 0; i < p.polygon.npoints; i++)
                    {
                        p.polygon.ypoints[i]++;
                    }
                    p.polygon.invalidate();
                }
            }
            else if (!currentProjectState.selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Vertices"))
            {
                for (int i = 0; i < currentProjectState.selectedVertices.size(); i++)
                {
                    ArrayList<Integer> vertList = currentProjectState.selectedVertices.get(i);
                    Polygon p = currentProjectState.selectedObjects.get(i).polygon;
                    for (int j = 0; j < currentProjectState.selectedVertices.get(i).size(); j++)
                    {
                        p.ypoints[vertList.get(j)]++;
                    }
                    p.invalidate();
                }
            }
            else if (currentProjectState.selectedPolygon != null)
            {
                //System.out.println("Pressed DOWN");
                currentProjectState.selectedPolygon.polygon.ypoints[currentProjectState.selectedVertexIndex]++;
                //selectedVertex.y = selectedPolygon.ypoints[selectedVertexIndex];
                currentProjectState.selectedPolygon.polygon.invalidate();
            }
            
            repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_D)
        {
            
            if (!currentProjectState.selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Objects"))
            {
                for (MaskedObject p : currentProjectState.selectedObjects)
                {
                    for (int i = 0; i < p.polygon.npoints; i++)
                    {
                        p.polygon.xpoints[i]++;
                    }
                    p.polygon.invalidate();
                }
            }
            else if (!currentProjectState.selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Vertices"))
            {
                for (int i = 0; i < currentProjectState.selectedVertices.size(); i++)
                {
                    ArrayList<Integer> vertList = currentProjectState.selectedVertices.get(i);
                    Polygon p = currentProjectState.selectedObjects.get(i).polygon;
                    for (int j = 0; j < currentProjectState.selectedVertices.get(i).size(); j++)
                    {
                        p.xpoints[vertList.get(j)]++;
                    }
                    p.invalidate();
                }
            }
            else if (currentProjectState.selectedPolygon != null)
            {
                //System.out.println("Pressed RIGHT");
                currentProjectState.selectedPolygon.polygon.xpoints[currentProjectState.selectedVertexIndex]++;
                //selectedVertex.x = selectedPolygon.xpoints[selectedVertexIndex];
                currentProjectState.selectedPolygon.polygon.invalidate();
            }
            
            repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_A)
        {
            
            if (!currentProjectState.selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Objects"))
            {
                for (MaskedObject p : currentProjectState.selectedObjects)
                {
                    for (int i = 0; i < p.polygon.npoints; i++)
                    {
                        p.polygon.xpoints[i]--;
                    }
                    p.polygon.invalidate();
                }
            }
            else if (!currentProjectState.selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Vertices"))
            {
                for (int i = 0; i < currentProjectState.selectedVertices.size(); i++)
                {
                    ArrayList<Integer> vertList = currentProjectState.selectedVertices.get(i);
                    Polygon p = currentProjectState.selectedObjects.get(i).polygon;
                    for (int j = 0; j < currentProjectState.selectedVertices.get(i).size(); j++)
                    {
                        p.xpoints[vertList.get(j)]--;
                    }
                    p.invalidate();
                }
            }
            else if (currentProjectState.selectedPolygon != null)
            {
                //System.out.println("Pressed LEFT");
                currentProjectState.selectedPolygon.polygon.xpoints[currentProjectState.selectedVertexIndex]--;
                //selectedVertex.x = selectedPolygon.xpoints[selectedVertexIndex];
                currentProjectState.selectedPolygon.polygon.invalidate();
            }
            
            repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
        {
            System.out.println("Pressed Delete");
            if (currentProjectState.selectedPolygon.polygon.npoints == 1)
            {
                currentProjectState.polygons.remove(currentProjectState.selectedPolygon);
                currentProjectState.selectedPolygon = null;
                currentProjectState.selectedVertexIndex = -1;
                repaint();
                return;
            }
            if (currentProjectState.selectedPolygon == null)
            {
                return;
            }
            for (int i = currentProjectState.selectedVertexIndex+1; i < currentProjectState.selectedPolygon.polygon.npoints; i++)
            {
                currentProjectState.selectedPolygon.polygon.xpoints[i-1] = currentProjectState.selectedPolygon.polygon.xpoints[i];
                currentProjectState.selectedPolygon.polygon.ypoints[i-1] = currentProjectState.selectedPolygon.polygon.ypoints[i];
            }
            currentProjectState.selectedPolygon.polygon.npoints--;

            if (currentProjectState.selectedPolygon.polygon.npoints-1 < currentProjectState.selectedVertexIndex)
            {
                currentProjectState.selectedVertexIndex--;
            }
            currentProjectState.selectedPolygon.polygon.invalidate();
            
            repaint();
        }
        
        
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void recolorSelectedPolygons() {
        for (MaskedObject p : currentProjectState.selectedObjects)
        {
            currentProjectState.selectedPolygon = p;
            currentProjectState.selectedVertexIndex = 0;
            colorizePolygon(p);
        }
        repaint();
    }
    
    
    class Pair implements Comparable {
        
        public double key;
        public String value;

        @Override
        public int compareTo(Object o) {
            Pair p = (Pair)o;
            return new Double(this.key).compareTo(p.key);
        }
        
    }
    
    
    
    public class SectionProcessor implements Runnable {

        int start;
        int end;
        String filename;
        String filter;
        Image img = null;
        BufferedImage bi = null;
        
        public SectionProcessor(String filename, String filter, int start, int end) {
            this.start = start;
            this.end = end;
            this.filename = filename;
            this.filter = filter;
            
        }

        public void run() {
            filterSection(img,bi,filename,filter,start,end);
            threadsComplete += 1;
        }
    }
    
    class PointXY {
        int x;
        int y;
        boolean isObjectEdgePixel = true;
        
        PointXY(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public boolean equals(Object o) {

            // If the object is compared with itself then return true  
            if (o == this) {
                return true;
            }

            /* Check if o is an instance of Complex or not
              "null instanceof [type]" also returns false */
            if (!(o instanceof PointXY)) {
                return false;
            }

            // typecast o to Complex so that we can compare data members 
            PointXY p = (PointXY) o;

            // Compare the data members and return accordingly 
            return p.x == this.x && p.y == this.y;
        }
        
        
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 59 * hash + this.x;
            hash = 59 * hash + this.y;
            return hash;
        }
        
    }
    
    class Pixel {
        
        private int red;
        private int green;
        private int blue;
        final private int column;
        final private int row;
        
        Pixel (int r, int g, int b, int column, int row)
        {
            red = r;
            green = g;
            blue = b;
            this.column = column;
            this.row = row;
            
        }
        
        public int getRedValue()
        {
            return red;
        }
        
        public int getGreenValue()
        {
            return green;
        }
        
        public int getBlueValue()
        {
            return blue;
        }
        
        public int getAverage()
        {
            return (red + green + blue)/3;
        }
        
        public java.awt.Point getPoint()
        {
            return new java.awt.Point(column,row);
        }
        
        public void setRGB(int r, int g, int b)
        {
            //System.out.println(r + " " + g + " " + b);
            try
            {
                Color c = new Color(r,g,b);
                image_pixels.setRGB(column, row, c.getRGB());

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        public void setRGB(int r, int g, int b, BufferedImage bi)
        {
            //System.out.println(r + " " + g + " " + b);
            try
            {
                Color c = new Color(r,g,b);
                bi.setRGB(column, row, c.getRGB());

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        
        @Override
        public boolean equals(Object o) {

            // If the object is compared with itself then return true  
            if (o == this) {
                return true;
            }

            /* Check if o is an instance of Complex or not
              "null instanceof [type]" also returns false */
            if (!(o instanceof Pixel)) {
                return false;
            }

            // typecast o to Complex so that we can compare data members 
            Pixel p = (Pixel) o;

            // Compare the data members and return accordingly 
            return p.red == this.red && p.green == this.green && p.blue == this.blue;
        }
        
        

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 59 * hash + this.red;
            hash = 59 * hash + this.green;
            hash = 59 * hash + this.blue;
            return hash;
        }
    }

    private class ASCIIChar implements Comparable
    {
        char value;
        int pixels;
        int number;

        @Override
        public int compareTo(Object o) {
            ASCIIChar a = (ASCIIChar)o;
            if (this.pixels < a.pixels)
            {
                return -1;
            }
            else if (this.pixels > a.pixels)
            {
                return 1;
            }
            else if (this.pixels == a.pixels)
            {
                return 0;
            }
            return 0;
            
        }
        @Override
        public String toString()
        {
            return number + " " + value + " " + pixels;
        }
    }

}


class MaskedObject
{
    
    Polygon polygon;
    Color color;
    Color secondary_color;
    int id;
    double depth;
    int hue_variation;
    int saturation_variation;
    
    int secondary_hue_variation;
    int secondary_sat_variation;
    
    int complement_threshold;
    int edgeBlendIndex;
    
    public MaskedObject()
    {
        polygon = new Polygon();
        secondary_color = new Color(128,128,128);
        depth = 0;
    }
    
}

class Click {
    int x;
    int y;
}

class ProjectState {
    
    ArrayList<MaskedObject> polygons;
    MaskedObject selectedPolygon;
    int selectedVertexIndex = -1;
    int hoverVertexIndex = -1;
    MaskedObject adjacentPolygon;
    int adjacentPolygonVertex = -1;
    MaskedObject tempAutoCompletePolygon = null;
    ArrayList<MaskedObject> selectedObjects = new ArrayList();
    ArrayList<ArrayList<Integer>> selectedVertices = new ArrayList();
    
}

class ConvolveRegionMap {
    
    int maskMap[][];
    int length;
    int centerX;
    int centerY;
    
    ConvolveRegionMap(int radius)
    {
        this.length = (radius*2)+1;
        maskMap = new int[this.length][this.length];
    }
    
    public void initMaskMap(MaskedObject p, int centerX, int centerY)
    {
        for (int i = -length/2; i <= length/2; i++)
        {
            for (int j = -length/2; j <= length/2; j++)
            {
                if (p.polygon.contains(centerX + j, centerY + i))
                {
                    maskMap[i+length/2][j+length/2] = 1;
                }
                else
                {
                    maskMap[i+length/2][j+length/2] = 0;
                }
            }
        }
    }
    
}


class ColorFrequency implements Comparable {
    
    Color color;
    int frequency = 0;

    @Override
    public int compareTo(Object o) {
        ColorFrequency cf = (ColorFrequency)o;
        if (this.frequency == cf.frequency) return 0;
        if (this.frequency < cf.frequency) return 1;
        return -1;
    }
    
}