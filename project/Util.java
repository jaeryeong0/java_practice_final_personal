import java.lang.reflect.Field;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;

public class Util {
    
    // String array --> TextImage 
    public static TextImage createTextImage(String[] asciiArt) {
        if (asciiArt == null || asciiArt.length == 0) {
            return new BasicTextImage(new TerminalSize(1, 1));
        }

        // get size of String array
        int width = 0;
        for (String line : asciiArt) {
            if (line.length() > width) {
                width = line.length();
            }
        }
        int height = asciiArt.length;

        // create empty TextImage
        BasicTextImage image = new BasicTextImage(new TerminalSize(width, height));
        
        // initializing the drawing tool
        TextGraphics tg = image.newTextGraphics();

        // draw
        for (int y = 0; y < height; y++) {
            tg.putString(0, y, asciiArt[y]);
        }

        // return processed variable
        return image;
    }

    // change color of TextImage
    public static TextImage colorizeTextImage(TextImage originalImage, TextColor color) {
        if (originalImage == null) return null;

        TerminalSize size = originalImage.getSize();
        BasicTextImage newImage = new BasicTextImage(size);

        for (int row = 0; row < size.getRows(); row++) {
            for (int col = 0; col < size.getColumns(); col++) {
                TextCharacter character = originalImage.getCharacterAt(col, row);
                if (character != null) {
                    newImage.setCharacterAt(col, row, character.withForegroundColor(color));
                }
            }
        }

        return newImage;
    }

    // colorize by color name string (e.g. "RED", "GREEN", "BLUE")
    public static TextImage colorizeTextImage(TextImage originalImage, String colorName) {
        TextColor color = TextColor.ANSI.valueOf(colorName.toUpperCase());
        return colorizeTextImage(originalImage, color);
    }

    // colorize by RGB values
    public static TextImage colorizeTextImage(TextImage originalImage, int r, int g, int b) {
        TextColor color = new TextColor.RGB(r, g, b);
        return colorizeTextImage(originalImage, color);
    }


    public static void placeImage(TextGraphics tg, TerminalPosition position, String assetName) {
        placeImage(tg, position, assetName, "white");
    }

    public static void placeImage(TextGraphics tg, TerminalPosition position, String assetName, String colorName) {
        try {
            Field field = Assets.class.getField(assetName.toUpperCase());
            TextImage originalImage = (TextImage) field.get(null);

            TextImage coloredImage = colorizeTextImage(originalImage, colorName);

            tg.drawImage(position, coloredImage);
        } catch (NoSuchFieldException e) {
            return;
        } catch (IllegalAccessException e) {
            return;
        } catch (Exception e) {
            return;
        }
    }
}