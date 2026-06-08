package scene;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import java.io.IOException;

/**
 * Manager class that handles scene transitions, rendering, and input for the game.
 * Designed as a singleton so it can be accessed globally.
 */
public class SceneManager {

    // 1. Singleton instance
    private static final SceneManager instance = new SceneManager();
    
    // Tracks the currently active scene (uses polymorphism)
    private Scene currentScene;

    // Private constructor to prevent external instantiation
    private SceneManager() {}

    // 2. Global accessor (call SceneManager.getInstance() from anywhere)
    public static SceneManager getInstance() {
        return instance;
    }

    /**
     * Replaces the current screen with a new scene.
     * @param newScene the scene to transition to (e.g. new TitleScene())
     */
    public void changeScene(Scene newScene) {
        // Clean up and exit the current scene first if one exists
        if (currentScene != null) {
            currentScene.exit();
        }
        
        // Swap in the new scene
        currentScene = newScene;
        
        // Initialize the new scene
        if (currentScene != null) {
            currentScene.enter();
        }
    }

    /**
     * Forwards key input from the main loop to the current scene.
     */
    public void handleInput(KeyStroke key) throws IOException {
        if (currentScene != null && key != null) {
            currentScene.handleInput(key);
        }
    }

    /**
     * Executes the current scene's rendering logic when the main loop draws the screen.
     */
    public void render(TextGraphics graphics) {
        if (currentScene != null) {
            currentScene.render(graphics);
        }
    }

    /** Returns the current scene (used for scene saving in test mode). */
    public Scene getCurrentScene() { return currentScene; }

    /** Replaces the scene without lifecycle calls (used for player switching in test mode). */
    public void forceScene(Scene s) { currentScene = s; }
}