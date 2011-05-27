/*
 * This file is part of the Synyx Greetingcard module for OpenCms.
 *
 * Copyright (c) 2007 Synyx GmbH & Co.KG (http://www.synyx.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.synyx.greetingcard;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;



/**
 * This is a helper class for the greetingscard modul. With this class an image
 * becomes decorated with a text at the specified position entered by ths user.
 *
 *@author Rainer Steinegger, Synyx GmbH & Co. KG, steinegger@synyx.de
 */
public class ImageCreator implements ImageObserver {
    
    /**
     * The buffered image to paint on all parts (image, text).
     */
    private BufferedImage bufferedImage;
    
    /**
     * The graphics object of the buffered image.
     */
    private Graphics graphics;
    
    /**
     * The default width of all generated images.
     */
    private final static int HEIGHT = 600;
    
    /**
     * The default height of all generated images.
     */
    private final static int WIDTH = 800;
    
    /**
     * The URL to the new created file.
     */
    private String newFile;
    
    /**
     * This hashmap chechs the names of the new images.
     */
    private static HashMap hashMap;
    
    /**
     * A random number for the generation of the filename.
     */
    private static Random randomNumber;
    
    /**
     * The key for the hashmap.
     */
    private final static String HASHKEY = "picture name";
    
    /**
     * The symbol for line break.
     */
    public static final String LINE_BREAK = "\r\n";
    
    
    /**
     * This constructor initializes a new instace of the buffered image.
     * Creates a new instance of ImageCreator.
     * 
     * @param backgroundImage The image to paint on.
     * @param author For generating the filename, the name of the author is needed.
     * @param receiver For generating the filename, the name of the receiver is needed.
     */
    public ImageCreator(Image backgroundImage, String author, String receiver, String archiveFolder) {        
        this.bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TRANSLUCENT);        
        this.graphics = this.bufferedImage.getGraphics();       
        
        if (randomNumber == null) {
            randomNumber = new Random(System.currentTimeMillis());
        }
        if (hashMap == null) {
            hashMap = new HashMap(1000);
        }      
        String filename = "" + Math.abs(randomNumber.nextLong());
        while (hashMap.put(filename, HASHKEY) != null) {
            filename = author + receiver + Math.abs(randomNumber.nextDouble());
        }
        this.newFile = archiveFolder + filename; 
        this.graphics.drawImage(backgroundImage, 0, 0, this);        
    }        
    
    /**
     * This method generates a rectangle and inserts the given text with the attributes.
     * 
     * @param x The x-coordinate on the upper left side.
     * @param y The y-coordinate on the upper left side.
     * @param x2 The x-coordinate on the right side at the bottom.
     * @param y2 The y-coordinate on the right side at the bottom.
     * @param text The text which should be drawn.
     * @param fontSize The font size of the text.
     * @param fontColor The font color of the text.
     * @param fontStyle The font style of the text.
     */
    public void addText(int x, int y, int x2, int y2, String text, int fontSize, String fontColor,
            boolean italic, boolean underline, boolean bold, boolean plain, String fontType) throws IOException {
        // generate the buffered image and get the graphics object to paint on        
        BufferedImage tempBuffer  = new BufferedImage(x2 - x, y2 - y, BufferedImage.TRANSLUCENT);        
        Graphics tempGraphics = tempBuffer.createGraphics();
        
        // get the color
        Color color = new Color(hexToInt(fontColor));
        tempGraphics.setColor(color);
        
        // get the styles and convert it to an int for the font
        int fontStyleParam = 0;
        if (italic) {
            // is italic
            fontStyleParam |= Font.ITALIC;
        }
        if (bold) {
            // is bold
            fontStyleParam |= Font.BOLD;
        }
        if (plain) {
            // is plain
            fontStyleParam |= Font.PLAIN;
        }
        
        tempGraphics.setFont(new Font(fontType, fontStyleParam, fontSize));
        
                
        List linesOfText = (List) splitTextIntoLines(text, x2 - x, tempGraphics);
        Iterator textIterator = linesOfText.iterator();
        int line = 1;
        while (textIterator.hasNext()) {
            String thisLine = (String)textIterator.next();
            tempGraphics.drawString(thisLine, 0, line * fontSize);
            if (underline) {
                // draw a line under the text
                tempGraphics.drawLine(0, line * fontSize
                        , getWidthOfTheWord(thisLine, tempGraphics), line * fontSize);
            }
            // the next line
            line++;
        }        
        graphics.drawImage(tempBuffer, x, y, this);
    }
    
    
   /**
    * This method gets a hex value as a String and returns the int value.
    *
    * @param hexValue The hex Value as a String.
    * @return The int value of the String.
    */
   private static int hexToInt (final String hexValue) {
       int intValue = 0;
       int tempValue = 0;       
       String hexValueTemp = hexValue.substring(1);               
       for (int i = 0; i < hexValueTemp.length(); i++) {
           char subChar = hexValueTemp.charAt(hexValueTemp.length() - 1 - i);                      
           if (subChar == 'A' || subChar == 'a') {
               tempValue = 10;
           } else if (subChar == 'B' || subChar == 'b') {
               tempValue = 11;
           } else if (subChar == 'C' || subChar == 'c') {
               tempValue = 12;
           } else if (subChar == 'D' || subChar == 'd') {
               tempValue = 13;
           } else if (subChar == 'E' || subChar == 'e') {
               tempValue = 14;
           } else if (subChar == 'F' || subChar == 'f') {
               tempValue = 15;
           } else {
               tempValue = Integer.parseInt(String.valueOf(subChar));
           }
           intValue += (Math.pow(16, i) * tempValue);
       }
       return intValue;
   } 
   
    /**
     * This method creates a new png file at the archive folder.
     *
     * @return The new image.  
     */
    public Image getFile() throws IOException {        
        return (Image)bufferedImage;
    }
    
    /**
     * This class asigns for every image a new filename and returns this.
     *      
     * @return The URL of the new file.
     */
    public String getNewLocation() {
        return newFile;
    }

    /**
     * @see java.awt.image.ImageObserver
     */
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return true;
    }

    /**
     * This method becomes a text as a string and the width of the box in which 
     * this string should be displayed. Width this variables the method calculates 
     * which words can stand in one line without a line break. The list with the 
     * seperated words are returned in a List.
     * 
     * @param text The text to be added into the box.
     * @param boxWidth The possible width of the box
     * @param tempGraphics The current graphics object.
     * @return A list with the string of a line per entry.
     */
    private List splitTextIntoLines(String text, int boxWidth, Graphics tempGraphics) throws IOException {
        ArrayList lines = new ArrayList();
        // replace the line break so that it can be found in any case
        text = text.replaceAll(ImageCreator.LINE_BREAK, " " + ImageCreator.LINE_BREAK + " ");
        String [] splittedText = text.split(" ");        
        int widthOfSpacer = getWidthOfTheWord(" ", tempGraphics);
        
        int usedSpacePerLine = 0;
        StringBuffer nextLine = new StringBuffer();
        for (int i = 0; i < splittedText.length; i++) { 
            // first check if this is the line break
            if (ImageCreator.LINE_BREAK.equals(splittedText[i])) {
                // add the saved text
                if (nextLine.length() != 0) {
                    lines.add(nextLine.toString());
                    // delete the saved word
                    nextLine.delete(0, nextLine.length());    
                    // reset
                    usedSpacePerLine = 0;
                }
                // just add a blank line
                lines.add("");
                continue;
            }
            int widthOfWord = getWidthOfTheWord(splittedText[i], tempGraphics);
            if (widthOfWord + usedSpacePerLine < boxWidth) {                 
                // add to the current line the length of the word and the spcer
                usedSpacePerLine += widthOfWord + widthOfSpacer; 
                nextLine.append(splittedText[i] + " ");
            } else if (nextLine.length() > 0){  
                nextLine.deleteCharAt(nextLine.length() - 1);
                // add to the list the next line
                lines.add(nextLine.toString());
                // delete the saved word
                nextLine.delete(0, nextLine.length());    
                // add to the current line the length of the word and the spcer
                usedSpacePerLine = widthOfWord + widthOfSpacer;
                nextLine.append(splittedText[i] + " ");
            } else {
                // failed to add the text
                throw new IOException("Failed to add text " + splittedText[i]);
            }
        }
        if (usedSpacePerLine > 0) {
            // this case is needed, if there are no more words and the current line is not added to the returend list
            nextLine.deleteCharAt(nextLine.length() - 1);
            lines.add(nextLine.toString());
        }
        return lines;
    }
    
    /**
     * This method calculates the width of the
     * 
     * @param word The word for which the width is needed.
     * @param graphics The graphics object needed to get the font metrics and the current font.
     * @return The width of the word.
     */
    private int getWidthOfTheWord (String word, Graphics graphics) {      
        return graphics.getFontMetrics(graphics.getFont()).stringWidth(word);
    }
    
    /**
     * This method gets a string and returns the string without any spacer signs.
     *
     * @param input The origin string.
     * @return The same string without spacer.
     */
    private String removeSpacer (String input) {
        int position = 0;
        StringBuffer toReturn = new StringBuffer();
        while (position < input.length()) {
            if (input.charAt(position) != ' ') {
                toReturn.append(input.charAt(position));
            }
            position++;
        }
        return toReturn.toString();
    }

}
