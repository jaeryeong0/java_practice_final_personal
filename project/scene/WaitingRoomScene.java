package scene;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.io.IOException;

public class WaitingRoomScene implements Scene {

    @Override
    public void enter() {}

    @Override
    public void handleInput(KeyStroke key) throws IOException {
        if (key.getKeyType() == KeyType.Enter) {
            SceneManager.getInstance().changeScene(new GamePlayScene());
        }
    }

    @Override
    public void render(TextGraphics graphics) {}

    @Override
    public void exit() {}
}
