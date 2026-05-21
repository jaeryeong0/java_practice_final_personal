import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;

public class Util {
    
    // String 배열을 받아서 TextImage 객체로 만들어주는 마법의 함수
    public static TextImage createTextImage(String[] asciiArt) {
        if (asciiArt == null || asciiArt.length == 0) {
            return new BasicTextImage(new TerminalSize(1, 1));
        }

        // 1. 이미지의 가로(가장 긴 줄 기준), 세로 길이 구하기
        int width = 0;
        for (String line : asciiArt) {
            if (line.length() > width) {
                width = line.length();
            }
        }
        int height = asciiArt.length;

        // 2. 가로x세로 크기에 맞는 빈 이미지(도화지) 생성
        BasicTextImage image = new BasicTextImage(new TerminalSize(width, height));
        
        // 3. 도화지 전용 붓(TextGraphics) 가져오기
        TextGraphics imageGraphics = image.newTextGraphics();

        // 4. 도화지 위에 String 배열 내용 그리기
        for (int y = 0; y < height; y++) {
            imageGraphics.putString(0, y, asciiArt[y]);
        }

        // 완성된 이미지 객체 반환
        return image;
    }
}