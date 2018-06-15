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
import imagefilters.ColorChooserButton.ColorChangedListener;
import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorConvertOp;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
    
    int drawPrecision = 10;
    int dragCounter = 0;
    
    JTextField filenameTextBox = new JTextField(10);
    JLabel extensionLbl = new JLabel(".pmoc");
    JButton saveButton = new JButton("Save");
    JButton openButton = new JButton("Open");
    JButton recolorPolygonsButton = new JButton("Recolor Selected Polygons");
    
    JButton loadVideoButton = new JButton("Load Video");
    
    Set<Point> ObjectSelection = new HashSet();
    
    BufferedImage[] bufferedImageFrames;

    ColorChooserButton colorChooser;

    int[] redRepresentation;
    int[] greenRepresentation;
    boolean filterPolygon = false;
    HashMap<String,Integer> patterns = new HashMap();
    int frameCounter = 0;
    
    
    ArrayList<MaskedObject> polygons;
    MaskedObject selectedPolygon;
    //Point selectedVertex;
    int selectedVertexIndex = -1;
    int hoverVertexIndex = -1;
    boolean currentlyDragging = false;
    MaskedObject adjacentPolygon;
    int adjacentPolygonVertex = -1;
    Click DragDown;
    Click DragUp;
    ArrayList<MaskedObject> selectedObjects = new ArrayList();
    ArrayList<ArrayList<Integer>> selectedVertices = new ArrayList();
    
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
                if (selectedPolygon != null && !selectedPolygon.polygon.contains(column,row))
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
        for (MaskedObject p : polygons)
        {
            if (p != selectedPolygon && p.polygon.contains(x,y))
            {
                for (int i = 0; i < p.polygon.npoints; i++)
                {
                    if (selectedPolygon.polygon.contains(p.polygon.xpoints[i],p.polygon.ypoints[i]))
                    {
                        return true;
                    }
                            
                }
            }
        }
        return false;
    }
    
    
    public void colorizePolygon(Color color_picked)
    {
        if (color_picked == null)
        {
            return;
        }
        int counterOff = 0;
        for (int row = 0; row < image_pixels.getHeight(); row++)
        {
            for (int column = 0; column < image_pixels.getWidth(); column++)
            {
                if (selectedPolygon == null)
                {
                    continue;
                }
                if (!selectedPolygon.polygon.contains(column,row))
                {
                    continue;
                }
                if (pointIsInsideContainedPolygon(column,row))
                {
                    continue;
                }
                float[] hsbInput = new float[3];
                Color.RGBtoHSB(color_picked.getRed(),color_picked.getGreen(), color_picked.getBlue(), hsbInput);
                float inputHue = hsbInput[0];
                float inputSaturation = hsbInput[1];
                Pixel pixel = getPixel(column,row,image_pixels);
                int pixelR = pixel.getRedValue();
                int pixelG = pixel.getGreenValue();
                int pixelB = pixel.getBlueValue();
                int average = (int)(((pixelR+pixelG+pixelB)/3.0));
                pixelR = average;
                pixelG = average;
                pixelB = average;
                float[] hsb = new float[3];
                Color.RGBtoHSB(pixelR,pixelG,pixelB,hsb);
                float h = hsb[0];
                float s = hsb[1];
                float b = hsb[2];
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
                
                if (row > 200)
                {
                    //System.out.println("Row big");
                }

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
                newAverage = (newRed + newGreen + newBlue)/3;
                if ((int)newAverage > average)
                {
                    //System.out.println("new greater than old");
                    
                    counterOff++;
                }
                else if ((int)newAverage < average)
                {
                    //System.out.println("new less than old" + (newAverage - average));
                    
                    counterOff++;                    
                }
                pixel.setRGB(newRed, newGreen, newBlue);

                
                
            }
            //System.out.println();
        }
        System.out.println(counterOff);
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
                if (selectedPolygon != null && !selectedPolygon.polygon.contains(column,row))
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
                if (selectedPolygon != null && !selectedPolygon.polygon.contains(column,row))
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
                for (MaskedObject p : polygons)
                {
                    g.setColor(Color.BLUE);
                    if (p.equals(selectedPolygon))
                    {
                        g.setColor(Color.RED);
                    }
                    if (p.equals(adjacentPolygon))
                    {
                        g.setColor(Color.GREEN);
                    }
                    g.drawPolygon(p.polygon);
                    if (p.equals(selectedPolygon))
                    {
                        g.setColor(Color.GREEN);
                        g.drawLine(p.polygon.xpoints[selectedVertexIndex], p.polygon.ypoints[selectedVertexIndex],
                                p.polygon.xpoints[0], p.polygon.ypoints[0]);
                    }
                }
                
            }
            if (selectedVertexIndex != -1)
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
                g.fillOval(selectedPolygon.polygon.xpoints[selectedVertexIndex] - dotOffset, selectedPolygon.polygon.ypoints[selectedVertexIndex] - dotOffset, dotWidth, dotWidth);
            }

            if (selectedPolygon != null)
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
                for (int i = 0; i < selectedPolygon.polygon.npoints; i++)
                {
                    if (i == selectedVertexIndex)
                    {
                        continue;
                    }
                    if (i == hoverVertexIndex)
                    {
                        g.fillOval(selectedPolygon.polygon.xpoints[i] - dotOffset*2, selectedPolygon.polygon.ypoints[i] - dotOffset*2, dotWidth*2, dotWidth*2);
                    }
                    else
                    {
                        g.fillOval(selectedPolygon.polygon.xpoints[i] - dotOffset, selectedPolygon.polygon.ypoints[i] - dotOffset, dotWidth, dotWidth);
                    }
                }
            }
            if (adjacentPolygon != null)
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
                for (int i = 0; i < adjacentPolygon.polygon.npoints; i++)
                {
                    if (i == adjacentPolygonVertex)
                    {
                        g.fillOval(adjacentPolygon.polygon.xpoints[i] - dotOffset*2, adjacentPolygon.polygon.ypoints[i] - dotOffset*2, dotWidth*2, dotWidth*2);
                    }
                    else
                    {
                        g.fillOval(adjacentPolygon.polygon.xpoints[i] - dotOffset, adjacentPolygon.polygon.ypoints[i] - dotOffset, dotWidth, dotWidth);
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
            if (!selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Objects"))
            {
                g.setColor(Color.CYAN);
                for (MaskedObject p : selectedObjects)
                {
                    g.drawPolygon(p.polygon);
                }
            }
            if (!selectedVertices.isEmpty() && toolList.getSelectedItem().equals("Drag Select Vertices"))
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
                for (int i = 0; i < selectedVertices.size(); i++)
                {
                    ArrayList<Integer> vertList = selectedVertices.get(i);
                    Polygon p = selectedObjects.get(i).polygon;

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
                selectedPolygon.polygon.reset();
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
            for (MaskedObject p : polygons)
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
    
    public double getIntelligentFingerprintY(BufferedImage bi, int x, int y)
    {
        int radius = 3;
        double numerator = 0;
        double totalValue = 0;
        for (int i = -radius; i < radius; i++)
        {
            for (int j = -radius; j < radius; j++)
            {
                Pixel pixel = getPixel(x+j,y+i,bi);
                numerator += (pixel.getAverage()*i);
                totalValue += pixel.getAverage();
            }
        }
        return numerator / totalValue;
    }
    
    
    public double getIntelligentFingerprintX(BufferedImage bi, int x, int y)
    {
        int radius = 3;
        double numerator = 0;
        double totalValue = 0;
        for (int i = -radius; i < radius; i++)
        {
            for (int j = -radius; j < radius; j++)
            {
                Pixel pixel = getPixel(x+j,y+i,bi);
                numerator += (pixel.getAverage()*j);
                totalValue += pixel.getAverage();
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
        MaskedObject poly = polygons.get(polygonIndex);
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
        int searchRadius = 10;
        searchRadius /= scale;
        if (selectedVertices.size() > 0)
        {
            for (int i = 0; i < selectedVertices.size(); i++)
            {
                ArrayList<Integer> vertList = selectedVertices.get(i);
                MaskedObject p = selectedObjects.get(i);
                for (int j = 0; j < selectedVertices.get(i).size(); j++)
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
        else if (selectedPolygon != null)
        {
            for (int i = 0; i < selectedPolygon.polygon.npoints; i++)
            {
                int prevX = selectedPolygon.polygon.xpoints[i];
                int prevY = selectedPolygon.polygon.ypoints[i];
                int selectedPolygonIndex = getPolygonIndex(selectedPolygon);
                Click c = findEdgeForVertex(prevX,prevY,searchRadius,selectedPolygonIndex,i);
                
                int tempX = selectedPolygon.polygon.xpoints[i];
                int tempY = selectedPolygon.polygon.ypoints[i];
                selectedPolygon.polygon.xpoints[i] = c.x;
                selectedPolygon.polygon.ypoints[i] = c.y; 
                for (MaskedObject mo : polygons)
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
            selectedPolygon.polygon.invalidate();
            repaint();
        }
    }
    
    private int getPolygonIndex(MaskedObject p)
    {
        for (int i = 0; i < polygons.size(); i++)
        {
            if (polygons.get(i).polygon == p.polygon)
            {
                return i;
            }
        }
        return -1;
    }
    
    public void trackMotionFromPreviousFrame()
    {
        int searchRadius = 16;
        searchRadius /= scale;
        int tweakableScalar = 0;
        if (selectedPolygon == null)
        {
            return;
        }
        try {
            int frameNumber = hsvColorChooser.video_current_value;
            File prevImageFile = new File("Video Frame PMOCs" + File.separator + "video-frame-" + (frameNumber-1) + ".png");
            File prevPMOCFile = new File("Video Frame PMOCs" + File.separator + "video-frame-" + (frameNumber-1) + ".pmoc");
            ArrayList<MaskedObject> prevFrameObjects = getDataFromPMOC(prevPMOCFile);
            int prevFramePolygonIndex = getPolygonIndex(selectedPolygon);
            MaskedObject prevPolygon = prevFrameObjects.get(prevFramePolygonIndex);
            BufferedImage previousImg = ImageIO.read(prevImageFile);
            for (int i = 0; i < prevPolygon.polygon.npoints; i++)
            {
                int vertIndex = i;
                int prevX = prevPolygon.polygon.xpoints[vertIndex];
                int prevY = prevPolygon.polygon.ypoints[vertIndex];
                double prevFingerprintX = getIntelligentFingerprintX(previousImg,prevX,prevY);
                double prevFingerprintY = getIntelligentFingerprintY(previousImg,prevX,prevY);
                double currX = getIntelligentFingerprintX(image_pixels,prevX,prevY);
                double currY = getIntelligentFingerprintY(image_pixels,prevX,prevY);
                double mostSimilarFingerprint = Math.sqrt(Math.pow(currX - prevFingerprintX,2)+Math.pow(currY - prevFingerprintY,2));
                int selectedPolygonIndex = getPolygonIndex(selectedPolygon);
                double contrastOnSide = getContrastFromSides(image_pixels,prevX,prevY,selectedPolygonIndex,vertIndex);
                mostSimilarFingerprint += (tweakableScalar)*(1.0/contrastOnSide);
                int mostSimilarX = prevX;
                int mostSimilarY = prevY;
                for (int j = -searchRadius; j < searchRadius; j++)
                {
                    for (int k = -searchRadius; k < searchRadius; k++)
                    {
                        double currFingerprintX = getIntelligentFingerprintX(image_pixels,prevX+k,prevY+j);
                        double currFingerprintY = getIntelligentFingerprintY(image_pixels,prevX+k,prevY+j);
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
                int tempX = selectedPolygon.polygon.xpoints[i];
                int tempY = selectedPolygon.polygon.ypoints[i];
                
                selectedPolygon.polygon.xpoints[i] = mostSimilarX;
                selectedPolygon.polygon.ypoints[i] = mostSimilarY; 
                
                for (MaskedObject p : polygons)
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
            selectedPolygon.polygon.invalidate();
            repaint();
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        
        
        
        final ImageFilters graphic = new ImageFilters();
        graphic.polygons = new ArrayList();
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
        //JScrollPane scroll = new JScrollPane(graphic,
        //    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
        //    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //graphic.frame.getContentPane().add(scroll);
        
        // I substituted above code with below code when I made my own scrolling functionality
        graphic.frame.getContentPane().add(graphic);

        graphic.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graphic.frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT + 100);
        
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
            graphic.trackMotionFromPreviousFrame();
        });
        
        graphic.findEdgesButton.addActionListener((ActionEvent e) -> {
            graphic.findEdges();
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
        
         
        
        
        graphic.buttons.add(graphic.LoadImageButton);
        //graphic.buttons.add(graphic.LoadTrainingImageButton);
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
            if (graphic.g == null)
            {
                //return;
            }
            File cids = new File("Video Frame PMOCs" + File.separator + "ids.cids");
            graphic.saveCidsFile(cids);
            String filename = graphic.filenameTextBox.getText();
            int frameNumber = graphic.hsvColorChooser.video_current_value;
            try {
                FileWriter fw = new FileWriter(new File("Video Frame PMOCs"+File.separator+"video-frame-" + frameNumber + ".pmoc"));
                fw.append(graphic.scale + "\n");
                fw.append(graphic.polygons.size()+"\n");
                for (MaskedObject p : graphic.polygons)
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
                ImageIO.write(graphic.image_pixels, "png", outputfile);
            } catch (IOException ex) {
                Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }); 
        
        
        graphic.saveButton.addActionListener((ActionEvent e) -> {
            String filename = graphic.filenameTextBox.getText();
            try {
                FileWriter fw = new FileWriter(new File("PMOCs"+File.separator+filename+".pmoc"));
                fw.append(graphic.scale + "\n");
                fw.append(graphic.polygons.size()+"\n");
                for (MaskedObject p : graphic.polygons)
                {
                    if (p.color != null)
                    {
                        fw.append("rgb " + p.color.getRed() + " " + p.color.getGreen() + " " + p.color.getBlue() + "\n");
                    }
                    fw.append(p.polygon.npoints+"\n");
                    for (int i = 0; i < p.polygon.npoints; i++)
                    {
                        fw.append(p.polygon.xpoints[i] + " ");
                        fw.append(p.polygon.ypoints[i] + " ");
                    }
                    fw.append("\n");
                }
                fw.close();
                File outputfile = new File("PMOCs"+File.separator+filename+".png");
                ImageIO.write(graphic.image_pixels, "png", outputfile);
            } catch (IOException ex) {
                Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }); 
        
        graphic.openButton.addActionListener((ActionEvent e) -> {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.home") 
                    + File.separator + "documents" 
                    + File.separator + "trainingImages"));
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
        graphic.revalidate();
        graphic.repaint();
        graphic.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graphic.frame.setExtendedState(graphic.frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        graphic.frame.setVisible(true);
        
        try {
            //graphic.getScreenCapture();

            graphic.g.stop();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }

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
                + File.separator + "documents" 
                + File.separator + "trainingImages"));
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
                
                for (MaskedObject p : polygons)
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
            if (next.equals("rgb"))
            {
                int r = reader.nextInt();
                int g = reader.nextInt();
                int b = reader.nextInt();
                poly.color = new Color(r,g,b);
                numVertices = reader.nextInt();
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
            tempPolygons.add(poly);
        }
        return tempPolygons;
    }
    
    public void openVideoPMOC(String filepath, File pmoc)
    {
        polygons.clear();
        try {
            
            polygons = getDataFromPMOC(pmoc);
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
            polygons = getDataFromPMOC(selected_pmoc_file);

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
                selectedPolygon = null;
                selectedVertexIndex = -1;
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
        for (MaskedObject p : polygons)
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
        if (selectedPolygon != null)
        {
            adjacentPolygonVertex = -1;
            adjacentPolygon = null;
            for (MaskedObject p : polygons)
            {
                if (p == selectedPolygon)
                {
                    continue;
                }
                int possible_index = clickNearVertexOfPolygon(p.polygon,x,y);
                if (possible_index != -1)
                {
                    adjacentPolygon = p;
                    adjacentPolygonVertex = possible_index;
                    break;
                }
            }
            
        }
    }
    
    private void validateHoveringAdjacentObject(int x, int y)
    {
        // Check if they're trying to click along a border of another polygon
        if (selectedPolygon != null && hoverVertexIndex == -1)
        {
            adjacentPolygonVertex = -1;
            adjacentPolygon = null;
            for (MaskedObject p : polygons)
            {
                if (p == selectedPolygon)
                {
                    continue;
                }
                int possible_index = clickNearVertexOfPolygon(p.polygon,x,y);
                if (possible_index != -1)
                {
                    adjacentPolygon = p;
                    adjacentPolygonVertex = possible_index;
                    break;
                }
            }
            
        }
    }
    
    private MaskedObject pointIsContainedByObject(int x, int y) {
        ArrayList<MaskedObject> PolygonsThatContainPoint = new ArrayList();
        for (MaskedObject p : polygons)
        {
            if (p.polygon.contains(x,y))
            {
                PolygonsThatContainPoint.add(p);
            }
        }
        if (PolygonsThatContainPoint.size() == 0)
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
        if (selectedPolygon == null)
        {
            return -1;
        }
        double minDistance = 100;
        int minIndex = -1;
        for (int i = 0; i < selectedPolygon.polygon.npoints; i++)
        {
            double distance = Math.hypot(selectedPolygon.polygon.xpoints[i]-x, selectedPolygon.polygon.ypoints[i]-y);
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
                selectedPolygon = clickedObject;
                int x = clickedObject.polygon.xpoints[clickedObject.polygon.npoints-1];
                int y = clickedObject.polygon.ypoints[clickedObject.polygon.npoints-1];
                //selectedVertex = new Point(x,y);
                selectedVertexIndex = clickedObject.polygon.npoints-1;
                colorChooser.setSelectedColor(selectedPolygon.color);
                hsvColorChooser.setSelectedColor(selectedPolygon.color);
            }
            else
            {
                selectedVertexIndex = -1;
                //selectedVertex = null;
                selectedPolygon = null;
                colorChooser.setSelectedColor(Color.white);
            }
            repaint();
        }
        else if (toolList.getSelectedItem().equals("Vertex Mode"))
        {
            int index = clickNearVertex(click.x, click.y);
            if (index != -1)
            {
                int x = selectedPolygon.polygon.xpoints[index];
                int y = selectedPolygon.polygon.ypoints[index];
                //selectedVertex = new Point(x,y);
                selectedVertexIndex = index;
                cycleToSelected();
            }
            else
            {
                if (selectedPolygon == null)
                {
                    selectedPolygon = new MaskedObject();
                    selectedPolygon.id = ++currentObjectID;
                    polygons.add(selectedPolygon);
                }
                if (adjacentPolygon != null && adjacentPolygonVertex != -1)
                {
                    selectedPolygon.polygon.addPoint(adjacentPolygon.polygon.xpoints[adjacentPolygonVertex],
                            adjacentPolygon.polygon.ypoints[adjacentPolygonVertex]);
                    
                }
                else
                {
                    selectedPolygon.polygon.addPoint(click.x, click.y);
                    //selectedVertex = new Point(e.getX(), e.getY());
                }
                selectedVertexIndex = selectedPolygon.polygon.npoints-1;
                
                
                
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
        int numPoints = selectedPolygon.polygon.npoints;
        while (selectedVertexIndex != numPoints - 1)
        {
            int tempX = selectedPolygon.polygon.xpoints[numPoints-1];
            int tempY = selectedPolygon.polygon.ypoints[numPoints-1];
            for (int i = numPoints-2; i >= 0; i--)
            {
                selectedPolygon.polygon.xpoints[i+1] = selectedPolygon.polygon.xpoints[i];
                selectedPolygon.polygon.ypoints[i+1] = selectedPolygon.polygon.ypoints[i];
            }
            selectedPolygon.polygon.xpoints[0] = tempX;
            selectedPolygon.polygon.ypoints[0] = tempY;
            selectedVertexIndex++;
        }
        hoverVertexIndex = -1;
        selectedPolygon.polygon.invalidate();
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
            DragDown = convertClick(e);
            selectedObjects.clear();
            selectedVertices.clear();
            
        }
        else if (toolList.getSelectedItem().equals("Pen Tool"))
        {
            dragCounter = 0;
            Click start = convertClick(e);
            selectedPolygon = new MaskedObject();
            selectedPolygon.polygon.addPoint(start.x, start.y);
            selectedVertexIndex = 0;
            polygons.add(selectedPolygon);
            repaint();
        }
        else
        {
            if (hoverVertexIndex != -1)
            {
                selectedVertexIndex = hoverVertexIndex;
                currentlyDragging = true;
            }
        }
    }

    public void selectObjects()
    {
        int width = DragUp.x - DragDown.x;
        int height = DragUp.y - DragDown.y;
        Rectangle rect = new Rectangle(DragDown.x, DragDown.y, width, height);
        for (MaskedObject p : polygons)
        {
            for (int i = 0; i < p.polygon.npoints; i++)
            {
                Point p2 = new Point(p.polygon.xpoints[i], p.polygon.ypoints[i]);
                if (rect.contains(p2.x,p2.y))
                {
                    selectedObjects.add(p);
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
        for (MaskedObject p : polygons)
        {
            ArrayList<Integer> vertexList = new ArrayList();

            for (int i = 0; i < p.polygon.npoints; i++)
            {
                Point p2 = new Point(p.polygon.xpoints[i], p.polygon.ypoints[i]);
                if (rect.contains(p2.x,p2.y))
                {
                    if (!selectedObjects.contains(p))
                    {
                        selectedObjects.add(p);
                    }
                    vertexList.add(i);
                }
            }
            if (vertexList.size() > 0)
            {
                selectedVertices.add(vertexList);
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
       
        int oldHover = hoverVertexIndex;
        hoverVertexIndex = clickNearVertex(click.x,click.y);
        
        if (oldHover != hoverVertexIndex)
        {
            repaint();
        }
        
        int oldHoverAdjacent = adjacentPolygonVertex;
        validateHoveringAdjacentObject(click.x,click.y);
        if (oldHoverAdjacent != adjacentPolygonVertex)
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
                selectedPolygon.polygon.addPoint(click.x, click.y);
            }
            dragCounter++;
            repaint();
        }
        else if (currentlyDragging)
        {     
            validateDraggingAdjacentObject(click.x,click.y);
            if (adjacentPolygonVertex != -1)
            {               
                selectedPolygon.polygon.xpoints[selectedVertexIndex] = adjacentPolygon.polygon.xpoints[adjacentPolygonVertex];
                selectedPolygon.polygon.ypoints[selectedVertexIndex] = adjacentPolygon.polygon.ypoints[adjacentPolygonVertex];
            }
            else
            {
                selectedPolygon.polygon.xpoints[selectedVertexIndex] = click.x;
                selectedPolygon.polygon.ypoints[selectedVertexIndex] = click.y;
            }
            // This code below fixes the problem of the points being directly 
            // manipulated and the bounding box not updated
            selectedPolygon.polygon.invalidate();
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
        
        ObjectSelection.add(new Point(pixel.column, pixel.row));
        
        int firstAverage = pixel.getAverage();
        int rightX = x;
        int leftX = x;
        int upY = y;
        int downY = y;
        int lastAverage = firstAverage;
        HashSet<Point> EdgesSelection = new HashSet();
        EdgesSelection.add(new Point(pixel.column, pixel.row));
        int lastSize = 0;
        
        do 
        {
            Iterator itr = EdgesSelection.iterator();
            HashSet<Point> toAdd = new HashSet();
            HashSet<Point> toRemove = new HashSet();
            
            while(itr.hasNext())
            {
                lastSize = ObjectSelection.size();
                Point p = (Point)(itr.next());
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
                        toAdd.add(new Point(topPixel.column, topPixel.row));
                    }
                }
                if (pix.row < image_pixels.getHeight() - 1)
                {
                    Pixel bottomPixel = getPixel(pix.column,pix.row+1,image_pixels);
                    int bottomP = bottomPixel.getAverage();
                    if (Math.abs(averageP - bottomP) < gradientThreshold)
                    {
                        neighborCounter++;
                        toAdd.add(new Point(bottomPixel.column, bottomPixel.row));
                    }
                }
                if (pix.column > 0)
                {
                    Pixel leftPixel = getPixel(pix.column-1,pix.row,image_pixels);
                    int leftP = leftPixel.getAverage();
                    if (Math.abs(averageP - leftP) < gradientThreshold)
                    {
                        neighborCounter++;
                        toAdd.add(new Point(leftPixel.column, leftPixel.row));
                    }
                }
                if (pix.column < image_pixels.getWidth() - 1)
                {
                    Pixel rightPixel = getPixel(pix.column+1,pix.row,image_pixels);
                    int rightP = rightPixel.getAverage();
                    if (Math.abs(averageP - rightP) < gradientThreshold )
                    {
                        neighborCounter++;
                        toAdd.add(new Point(rightPixel.column, rightPixel.row));
                    }
                }
                if (neighborCounter == 4)
                {
                    p.isObjectEdgePixel = false;
                    toRemove.add(p);
                }
                
                
            }
            for(Point p : toAdd) 
            {
                ObjectSelection.add(p);
                if (p.isObjectEdgePixel)
                {
                    EdgesSelection.add(p);
                }
            }
            for(Point p : toRemove) 
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
        for (Point p : ObjectSelection) {
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
            
            if (!selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Objects"))
            {
                for (MaskedObject p : selectedObjects)
                {
                    for (int i = 0; i < p.polygon.npoints; i++)
                    {
                        p.polygon.ypoints[i]--;
                    }
                    p.polygon.invalidate();
                }
            }
            else if (!selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Vertices"))
            {
                for (int i = 0; i < selectedVertices.size(); i++)
                {
                    ArrayList<Integer> vertList = selectedVertices.get(i);
                    Polygon p = selectedObjects.get(i).polygon;
                    for (int j = 0; j < selectedVertices.get(i).size(); j++)
                    {
                        p.ypoints[vertList.get(j)]--;
                    }
                    p.invalidate();
                }
            }
            else if (selectedPolygon != null)
            {
                //System.out.println("Pressed UP");
                selectedPolygon.polygon.ypoints[selectedVertexIndex]--;
                //selectedVertex.y = selectedPolygon.ypoints[selectedVertexIndex];
                selectedPolygon.polygon.invalidate();
            }
            repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_S)
        {
            
            if (!selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Objects"))
            {
                for (MaskedObject p : selectedObjects)
                {
                    for (int i = 0; i < p.polygon.npoints; i++)
                    {
                        p.polygon.ypoints[i]++;
                    }
                    p.polygon.invalidate();
                }
            }
            else if (!selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Vertices"))
            {
                for (int i = 0; i < selectedVertices.size(); i++)
                {
                    ArrayList<Integer> vertList = selectedVertices.get(i);
                    Polygon p = selectedObjects.get(i).polygon;
                    for (int j = 0; j < selectedVertices.get(i).size(); j++)
                    {
                        p.ypoints[vertList.get(j)]++;
                    }
                    p.invalidate();
                }
            }
            else if (selectedPolygon != null)
            {
                //System.out.println("Pressed DOWN");
                selectedPolygon.polygon.ypoints[selectedVertexIndex]++;
                //selectedVertex.y = selectedPolygon.ypoints[selectedVertexIndex];
                selectedPolygon.polygon.invalidate();
            }
            repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_D)
        {
            
            if (!selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Objects"))
            {
                for (MaskedObject p : selectedObjects)
                {
                    for (int i = 0; i < p.polygon.npoints; i++)
                    {
                        p.polygon.xpoints[i]++;
                    }
                    p.polygon.invalidate();
                }
            }
            else if (!selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Vertices"))
            {
                for (int i = 0; i < selectedVertices.size(); i++)
                {
                    ArrayList<Integer> vertList = selectedVertices.get(i);
                    Polygon p = selectedObjects.get(i).polygon;
                    for (int j = 0; j < selectedVertices.get(i).size(); j++)
                    {
                        p.xpoints[vertList.get(j)]++;
                    }
                    p.invalidate();
                }
            }
            else if (selectedPolygon != null)
            {
                //System.out.println("Pressed RIGHT");
                selectedPolygon.polygon.xpoints[selectedVertexIndex]++;
                //selectedVertex.x = selectedPolygon.xpoints[selectedVertexIndex];
                selectedPolygon.polygon.invalidate();
            }
            repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_A)
        {
            
            if (!selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Objects"))
            {
                for (MaskedObject p : selectedObjects)
                {
                    for (int i = 0; i < p.polygon.npoints; i++)
                    {
                        p.polygon.xpoints[i]--;
                    }
                    p.polygon.invalidate();
                }
            }
            else if (!selectedObjects.isEmpty() && toolList.getSelectedItem().equals("Drag Select Vertices"))
            {
                for (int i = 0; i < selectedVertices.size(); i++)
                {
                    ArrayList<Integer> vertList = selectedVertices.get(i);
                    Polygon p = selectedObjects.get(i).polygon;
                    for (int j = 0; j < selectedVertices.get(i).size(); j++)
                    {
                        p.xpoints[vertList.get(j)]--;
                    }
                    p.invalidate();
                }
            }
            else if (selectedPolygon != null)
            {
                //System.out.println("Pressed LEFT");
                selectedPolygon.polygon.xpoints[selectedVertexIndex]--;
                //selectedVertex.x = selectedPolygon.xpoints[selectedVertexIndex];
                selectedPolygon.polygon.invalidate();
            }
            repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
        {
            System.out.println("Pressed Delete");
            if (selectedPolygon.polygon.npoints == 1)
            {
                polygons.remove(selectedPolygon);
                selectedPolygon = null;
                selectedVertexIndex = -1;
                repaint();
                return;
            }
            if (selectedPolygon == null)
            {
                return;
            }
            for (int i = selectedVertexIndex+1; i < selectedPolygon.polygon.npoints; i++)
            {
                selectedPolygon.polygon.xpoints[i-1] = selectedPolygon.polygon.xpoints[i];
                selectedPolygon.polygon.ypoints[i-1] = selectedPolygon.polygon.ypoints[i];
            }
            selectedPolygon.polygon.npoints--;

            if (selectedPolygon.polygon.npoints-1 < selectedVertexIndex)
            {
                selectedVertexIndex--;
            }
            selectedPolygon.polygon.invalidate();
            repaint();
            return;
        }
        
        
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void recolorSelectedPolygons() {
        for (MaskedObject p : selectedObjects)
        {
            selectedPolygon = p;
            selectedVertexIndex = 0;
            colorizePolygon(p.color);
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
    
    class Point {
        int x;
        int y;
        boolean isObjectEdgePixel = true;
        
        Point(int x, int y)
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
            if (!(o instanceof Point)) {
                return false;
            }

            // typecast o to Complex so that we can compare data members 
            Point p = (Point) o;

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
    int id;
    
    public MaskedObject()
    {
        polygon = new Polygon();
    }
    
}

class Click {
    int x;
    int y;
}
