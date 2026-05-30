package tools;
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
    public static TextImage colorizeTextImage(TextImage asset, String colorName) {
        TextColor color = TextColor.ANSI.valueOf(colorName.toUpperCase());
        return colorizeTextImage(asset, color);
    }

    // colorize by RGB values
    public static TextImage colorizeTextImage(TextImage asset, int r, int g, int b) {
        TextColor color = new TextColor.RGB(r, g, b);
        return colorizeTextImage(asset, color);
    }

    // colorName의 기본값을 white로 하기 위함.
    public static void placeImage(TextGraphics tg, TerminalPosition position, TextImage asset) {
        placeImage(tg, position, asset, "white");
    }
    // 이미지 그릴 때 이 함수로 깔끔하게 적으려고 만듦
    public static void placeImage(TextGraphics tg, TerminalPosition position, TextImage asset, String colorName) {
        try {

            TextImage coloredImage = colorizeTextImage(asset, colorName);

            tg.drawImage(position, coloredImage);
        } catch (Exception e) {
            return;
        }
    }

    public static void changeBackgroundColor(TextGraphics tg, TerminalPosition topLeft, int width, int height, TextColor bgColor) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                TerminalPosition pos = new TerminalPosition(topLeft.getColumn() + col, topLeft.getRow() + row);
                TextCharacter existing = tg.getCharacter(pos);
                if (existing != null) {
                    tg.setCharacter(pos, existing.withBackgroundColor(bgColor));
                } else {
                    tg.setCharacter(pos, TextCharacter.DEFAULT_CHARACTER.withBackgroundColor(bgColor));
                }
            }
        }
    }

    // 깔려 있는 카드, 내 손패를 배치하기 위한 함수
    public static void placeCards(TextGraphics tg, TextImage[] cards, TerminalPosition startPos, int maxWidth) {
        placeCards(tg, cards, startPos, maxWidth, -1);
    }

    public static void placeCards(TextGraphics tg, TextImage[] cards, TerminalPosition startPos, int maxWidth, int highlightIndex) {
        if (cards == null || cards.length == 0) {
            return;
        }

        int numCards = cards.length;
        int cardWidth = cards[0].getSize().getColumns();

        int spacing;

        if (numCards == 1) {
            spacing = 0;
        } else {
            int totalNormalWidth = numCards * cardWidth;

            if (totalNormalWidth <= maxWidth) {
                spacing = cardWidth;
            } else {
                spacing = (maxWidth - cardWidth) / (numCards - 1);
                spacing = Math.max(1, spacing);
            }
        }

        int clampedHighlight = (highlightIndex < 0 || highlightIndex >= numCards) ? -1
                : highlightIndex;

        for (int i = numCards - 1; i >= 0; i--) {
            if (i == clampedHighlight) continue;

            int currentX = startPos.getColumn() + (i * spacing);
            int currentY = startPos.getRow();

            tg.drawImage(new TerminalPosition(currentX, currentY), cards[i]);
        }

        if (clampedHighlight != -1) {
            int hx = startPos.getColumn() + (clampedHighlight * spacing);
            int hy = startPos.getRow() - 1;
            tg.drawImage(new TerminalPosition(hx, hy), cards[clampedHighlight]);
        }
    }
}