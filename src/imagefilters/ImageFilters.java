package imagefilters;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import org.bytedeco.javacv.FFmpegFrameGrabber;


/*
    java -cp ../../../../Downloads/javacv-bin/artoolkitplus-macosx-x86_64.jar:../../../../Downloads/javacv-bin/ffmpeg-macosx-x86_64.jar:../../../../Downloads/javacv-bin/ffmpeg.jar:../../../../Downloads/javacv-bin/flandmark-macosx-x86_64.jar:../../Downloads/javacv-bin/flandmark.jar:../../../../Downloads/javacv-bin/flycapture.jar:../../../../Downloads/javacv-bin/javacpp.jar:../../../../Downloads/javacv-bin/javacv.jar:../../../../Downloads/javacv-bin/libdc1394-macosx-x86_64.jar:../../../../Downloads/javacv-bin/libdc1394.jar:../../../../Downloads/javacv-bin/libfreenect-macosx-x86_64.jar:../../../../Downloads/javacv-bin/libfreenect.jar:../../../../Downloads/javacv-bin/opencv-macosx-x86_64.jar:../../../../Downloads/javacv-bin/opencv.jar:../../../../Downloads/javacv-bin/videoinput.jar:. imagefilters.ImageFilters
*/


public class ImageFilters extends JPanel {

    static int WINDOW_WIDTH = 1450;
    static int WINDOW_HEIGHT = 900;
    JButton LoadImageButton = new JButton("Load Image");
    JButton LoadTrainingImageButton = new JButton("Load Training Image");
    JButton LoadSetImagesButton = new JButton("Load Training Set");
    //boolean onlyOneImage = true;
    boolean onlyFirstMapping = false;

    JButton ResetButton = new JButton("Reset");
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
    JCheckBox FillInBlanksBox = new JCheckBox("Fill in gaps");
    JCheckBox FirstMappingOnly = new JCheckBox("Only First Mappings");
    JButton SplitVideoFramesButton = new JButton("Split Video Frames");
    JButton ColorizeFramesButton = new JButton("Colorize Video Frames");
    JButton CreateColorizedMovieButton = new JButton("Create Colorized Movie");
    JButton AsciiFilterButton = new JButton("Ascii Filter");
    JButton AsciiFilterVideoButton = new JButton("Create Ascii Movie");
    JButton PixelateButton = new JButton("Pixelate");
    JButton Mosaic1Button = new JButton("Mosaic 1");
    JButton Mosaic2Button = new JButton("Mosaic 2");
    JTextField numberMosaicColumnsBox = new JTextField(3);
    JLabel columnsLabel = new JLabel("Mosaic Rows");
    JButton MosaicVideoButton = new JButton("Create Mosaic Video");

    String mosaicType = "Mosaic1";
    String Current_Movie_Name;
    int totalFramesInVideo = 0;
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
    HashMap<String,HashMap<Pixel,Integer>> fourNeighborKeyDictionary = new HashMap();
    HashMap<String,Pixel> fourNeighborKeyDictionary1to1 = new HashMap();
    
    Pixel[] colorizationArray = new Pixel[6376];
    ArrayList<Pixel>[] colorizeArrayMode = new ArrayList[6376];

    int[] trainingImageContributionCountPerValue = new int[256];
    JFrame frame = new JFrame();
    BufferedImage image_pixels;
    boolean alreadyFoundModes = false;
    boolean fillInBlanks = false;
    JPanel buttons = new JPanel();
    JPanel buttons2 = new JPanel();
    static Image selected_image = null;
    File selected_file = new File("/Users/paulsoderquist/Documents/trainingImages/jimmy-stewart-rope.jpg");

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
    
    public void TwoNeighborKeyColorize()
    {
        if (FirstMappingOnly.isSelected() == false && alreadyFoundModes == false)
        {
            System.out.println("Starting finding modes...");
            int counter = 0;
            int size = fourNeighborKeyDictionary.size();
            for (Map.Entry pair : fourNeighborKeyDictionary.entrySet()) {
                if (counter % 10000 == 0)
                {
                    System.out.println(counter + " / " + size);
                }
                counter++;
                String key = (String)(pair.getKey());
                HashMap<Pixel,Integer> value = (HashMap<Pixel,Integer>)(pair.getValue());
                Pixel mode = getModeHashMap(value);
                fourNeighborKeyDictionary1to1.put(key,mode);
                //it.remove(); // avoids a ConcurrentModificationException
            }
            System.out.println("Finished finding modes");
            alreadyFoundModes = true;
        }
        

        for (int row = 1; row < image_pixels.getHeight()-1; row++)
        {
            for (int column = 1; column < image_pixels.getWidth()-1; column++)
            {
                Pixel grayPixel = getPixel(column,row,image_copy);
                //Pixel left = getPixel(column - 1, row, image_copy);
                Pixel right = getPixel(column + 1, row, image_copy);
                Pixel down = getPixel(column, row+1, image_copy);
                //Pixel up = getPixel(column, row-1, image_copy);
                String key = grayPixel.getAverage() + "_" + right.getAverage();// + "_" + down.getAverage(); 
                        //key += "_" + left.getAverage() + "_" + up.getAverage();
                
                        
                Pixel p = null;
                if (FirstMappingOnly.isSelected() == true)
                {
                    HashMap<Pixel,Integer> pxls = fourNeighborKeyDictionary.get(key);
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
                    p = fourNeighborKeyDictionary1to1.get(key);
                }
                if (p != null)
                {
                    grayPixel.setRGB(p.red, p.green, p.blue);
                }
                else
                {
                    if (FillInBlanksBox.isSelected())
                    {
                        int average = (grayPixel.red + grayPixel.green + grayPixel.blue)/3;
                        grayPixel.setRGB(average,average,average);
                    }
                    else
                    {
                        grayPixel.setRGB(255,255,255);
                    }
                }
            }
        }
        //System.out.println("Colorized a frame");
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
                Pixel pixel = getPixel(column,row,image_pixels);
                int r = pixel.getRedValue();
                int g = pixel.getGreenValue();
                int b = pixel.getBlueValue();
                int average_value = (r + g + b) / 3;
                pixel.setRGB(average_value, average_value, average_value);
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
        //BufferedImage image_copy = getImageCopy();

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
        //BufferedImage image_copy = getImageCopy();
        
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
        
        if (selected_image != null)
        {
            g.drawImage(selected_image, 0, 0, this);            
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
    
    public static void main(String[] args) {
        
        
        
        final ImageFilters graphic = new ImageFilters();
        //graphic.assembleVideoFromFrames();
        graphic.numberMosaicColumnsBox.setText("30");

        graphic.frame = new JFrame();
        JScrollPane scroll = new JScrollPane(graphic,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        graphic.frame.getContentPane().add(scroll);

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
            graphic.ResetImage();
            graphic.repaint();
        });
        graphic.InvertButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.InvertColors();
            graphic.repaint();
        });
        graphic.GrayScaleButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.ConvertToGrayScale();
            graphic.repaint();
        });
        graphic.StepColorsButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.StepColors();
            graphic.repaint();
        });
        graphic.EmbossButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.EmbossImage();
            graphic.repaint();
        });
        graphic.BlurButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.BlurImage();
            graphic.repaint();
        });
        graphic.RedButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.ColorizeRed();
            graphic.repaint();
        });
        graphic.GreenButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.ColorizeGreen();
            graphic.repaint();
        });
        graphic.BlueButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.ColorizeBlue();
            graphic.repaint();
        });
        
        graphic.ColorizeButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.Colorize();
            graphic.repaint();
        });
        
        graphic.randomValuesButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.RandomValues();
            graphic.repaint();
        });
        
        graphic.BlackWhiteButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
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
            graphic.image_pixels = toBufferedImage(selected_image);
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
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.SumBoundingBoxColorize();
            graphic.repaint();
        });
        
        graphic.TwoNeighboKeyButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.ResetButton.setEnabled(true);
            graphic.TwoNeighborKeyColorize();
            graphic.repaint();
        });
        graphic.SplitVideoFramesButton.addActionListener((ActionEvent e) -> {
            graphic.splitVideoIntoFrames();
        });
        graphic.ColorizeFramesButton.addActionListener((ActionEvent e) -> {
            graphic.colorizeVideoFrames("Colorize");
        });
        graphic.CreateColorizedMovieButton.addActionListener((ActionEvent e) -> {
            graphic.colorizeVideoFrames("Colorize");
            graphic.assembleVideoFromFrames();
        });
        graphic.AsciiFilterButton.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
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
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.pixelate();
            graphic.ResetButton.setEnabled(true);
            graphic.repaint();
        });
        graphic.Mosaic1Button.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.mosaic1();
            graphic.mosaicType = "Mosaic1";
            graphic.ResetButton.setEnabled(true);
            graphic.repaint();
        });
        graphic.Mosaic2Button.addActionListener((ActionEvent e) -> {
            graphic.image_pixels = toBufferedImage(selected_image);
            graphic.mosaic2();
            graphic.mosaicType = "Mosaic2";
            graphic.ResetButton.setEnabled(true);
            graphic.repaint();
        });
        graphic.MosaicVideoButton.addActionListener((ActionEvent e) -> {
            //graphic.image_pixels = toBufferedImage(selected_image);
            graphic.colorizeVideoFrames(graphic.mosaicType);
            graphic.assembleVideoFromFrames();
        });        
        
        
        graphic.buttons.add(graphic.LoadImageButton);
        graphic.buttons.add(graphic.LoadTrainingImageButton);
        graphic.buttons.add(graphic.LoadSetImagesButton);
        graphic.buttons.add(graphic.ResetButton);
        graphic.buttons.add(graphic.InvertButton);
        graphic.buttons.add(graphic.GrayScaleButton);
        graphic.buttons.add(graphic.StepColorsButton);
        graphic.buttons.add(graphic.EmbossButton);
        graphic.buttons.add(graphic.BlurButton);
        //graphic.buttons.add(graphic.RedButton);
        //graphic.buttons.add(graphic.GreenButton);
        //graphic.buttons.add(graphic.BlueButton);                
        //graphic.buttons.add(graphic.ColorizeButton);
        //graphic.buttons.add(graphic.randomValuesButton);
        //graphic.buttons2.add(graphic.BlackWhiteButton);
        //graphic.buttons2.add(graphic.AlternateRGButton);
        //graphic.buttons.add(graphic.SumBoundingBoxButton);
        graphic.buttons2.add(graphic.TwoNeighboKeyButton);
        graphic.buttons2.add(graphic.FillInBlanksBox);
        //graphic.buttons2.add(graphic.FirstMappingOnly);       
        //graphic.buttons.add(graphic.SplitVideoFramesButton);
        //graphic.buttons.add(graphic.ColorizeFramesButton);
        graphic.buttons2.add(graphic.CreateColorizedMovieButton);
        graphic.buttons2.add(graphic.AsciiFilterButton);
        graphic.buttons2.add(graphic.AsciiFilterVideoButton);
        graphic.buttons2.add(graphic.PixelateButton);
        graphic.buttons2.add(graphic.Mosaic1Button);
        graphic.buttons2.add(graphic.Mosaic2Button);
        graphic.buttons2.add(graphic.columnsLabel);
        graphic.buttons2.add(graphic.numberMosaicColumnsBox);
        graphic.buttons.add(graphic.MosaicVideoButton);
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
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
        JPanel buttonPanel = new JPanel();
        GridLayout grid = new GridLayout(2,1);
        buttonPanel.setLayout(grid);
        buttonPanel.add(graphic.buttons);
        buttonPanel.add(graphic.buttons2);
        graphic.frame.add(buttonPanel, BorderLayout.SOUTH);
        graphic.revalidate();
        graphic.repaint();
        graphic.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graphic.frame.setVisible(true);
        
        //graphic.getScreenCapture();
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
        File[] files = new File(System.getProperty("user.home") 
                + File.separator + "documents" 
                + File.separator + "trainingImages"
                + File.separator + "Set2").listFiles();
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
                //Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
                continue;
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
        fc.showOpenDialog(ImageFilters.this);
        try
        {
            if (fc.getSelectedFile() == null)
            {
                return;
            }
            selected_file = fc.getSelectedFile();
            selected_image = ImageIO.read(selected_file);
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
            
            revalidate();
            repaint();
            countPixelsNotWhite();
        }
        catch (Exception e)
        {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("There was an error with the file that was selected");
        }
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
    
    private void colorizeVideoFrames(String filter) {
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
            //System.out.println(filename);
            totalFramesInVideo = getFrameCount(filename);
            lastFrame = totalFramesInVideo;
            // If I want to render part of a video give a percent (of video) for first and last frame
            //firstFrame = (int)(0.33587786259*totalFramesInVideo);
            //lastFrame = (int)(0.85877862595*totalFramesInVideo);
            FFmpegFrameGrabber g = new FFmpegFrameGrabber(filename);
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
                image_copy = getImageCopy();

                switch (filter) {
                    case "Colorize":
                        TwoNeighborKeyColorize();
                        break;
                    case "Mosaic1":
                        mosaic1();
                        break;
                    case "Mosaic2":
                        mosaic2();
                        break;
                    default:
                        break;
                }
                System.out.println("About to write frame " + (i+1) + " of " + (totalFramesInVideo-1) + " to file");
                ImageIO.write(image_pixels, "png", new File("tmp/video-frame-" + i + ".png"));
            }
        //System.out.println(System.currentTimeMillis());

            g.stop();
        }
        catch (Exception ex)
        {
            
            System.out.println("There was an error with the file that was selected");
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    
    private void createAsciiVideo() {
        try
        {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.home")
                    + File.separator + "documents"));
            fc.showOpenDialog(ImageFilters.this);
            System.out.println("Video will start in 10 seconds. Please expand/pull top of output window upward");
            Thread.sleep(10000);
            try
            {
                if (fc.getSelectedFile() == null)
                {
                    return;
                }
                Current_Movie_Name = fc.getSelectedFile().getName();
                
                String filename = fc.getSelectedFile().getAbsolutePath();
                //System.out.println(filename);
                totalFramesInVideo = getFrameCount(filename);
                lastFrame = totalFramesInVideo;
                
                // If I want to render part of a video give a percent (of video) for first and last frame
                //firstFrame = (int)(0.33587786259*totalFramesInVideo);
                //lastFrame = (int)(0.85877862595*totalFramesInVideo);
                FFmpegFrameGrabber g = new FFmpegFrameGrabber(filename);
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
                    System.out.println("About to write frame " + (i+1) + " of " + (totalFramesInVideo-1) + " to file");
                    BufferedImage buffImg = getScreenCapture();

                    // DO THIS TO SCREENSHOT OUTPUT WINDOW:
                    ImageIO.write(buffImg, "png", new File("/Users/paulsoderquist/NetBeansProjects/ImageEditor/tmp/video-frame-" + i + ".png"));
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
        }
        catch (InterruptedException ex)
        {
            
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);

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

            FFmpegFrameGrabber g = new FFmpegFrameGrabber(filename);
            g.start();

            for (int i = 0 ; i < totalFramesInVideo; i++) {
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
    
    private void assembleVideoFromFrames()
    {

        String movieName = Current_Movie_Name.substring(0, Current_Movie_Name.length()-4);
        try 
        {
            System.out.println("Assembling Frames into Video...");
            //System.out.println(System.currentTimeMillis());
            String[] command = { 
                "/usr/local/bin/ffmpeg", "-y", "-r", "24", "-f", "image2", "-s", "1280x720", "-start_number", 
                firstFrame+"",//1", 
                "-i", 
                "/Users/paulsoderquist/NetBeansProjects/ImageEditor/tmp/video-frame-%d.png", "-vframes", 
                lastFrame-firstFrame+"",//totalFramesInVideo-1+"", 
                "-vcodec", "libx264", "-crf", "24", "-pix_fmt", "yuv420p", 
                movieName+"_Colorized.mp4"
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
            System.out.println("Finished Saving Video " + movieName + "_Colorized.mp4");
            //Process chperm;

            //chperm=Runtime.getRuntime().exec("ffmpeg -r 25 -f image2 -s 1920x1080 -start_number 0 -i tmp/video-frame-%d.png -vframes 7 -vcodec libx264 -crf 25  -pix_fmt yuv420p outVideoNetbeans.mp4\n");
            /*
            DataOutputStream os = 
                  new DataOutputStream(chperm.getOutputStream());

            os.writeBytes("ffmpeg -r 25 -f image2 -s 1920x1080 -start_number 0 -i tmp/video-frame-%d.png -vframes 7 -vcodec libx264 -crf 25  -pix_fmt yuv420p netbeansTest.mp4\n");
            os.flush();

            chperm.waitFor();
            */
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
    
    static BufferedImage getImageCopy() {
        
        Image clone = selected_image.getScaledInstance(selected_image.getWidth(null), -1, Image.SCALE_DEFAULT);
        
        return toBufferedImage(clone);
    }
    
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
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
    
    private void train2NeighborKey()
    {
        image_pixels = toBufferedImage(selected_image);
        
        for (int row = 1; row < image_pixels.getHeight()-1; row++)
        {
            for (int column = 1; column < image_pixels.getWidth()-1; column++)
            {
                Pixel pixel = getPixel(column,row,image_pixels);
                //Pixel left = getPixel(column - 1, row, image_pixels);
                Pixel right = getPixel(column + 1, row, image_pixels);
                Pixel down = getPixel(column, row+1, image_pixels);
                //Pixel up = getPixel(column, row-1, image_pixels);
                String key = pixel.getAverage() + "_" + right.getAverage();// + "_" + down.getAverage(); 
                        //key += "_" + left.getAverage() + "_" + up.getAverage();
                
                
                HashMap<Pixel,Integer> pxls = fourNeighborKeyDictionary.get(key);
                if (pxls == null)
                {
                    pxls = new HashMap();
                    pxls.put(pixel,1);
                    fourNeighborKeyDictionary.put(key, pxls);
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
        int filledCounter = fourNeighborKeyDictionary.size();
        
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
                System.out.println(e.toString());
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
                System.out.println(e.toString());
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

