package scene;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import java.io.IOException;

/**
 * 게임의 화면(Scene) 전환과 렌더링, 입력을 총괄하는 매니저 클래스입니다.
 * 싱글톤(Singleton) 패턴을 적용하여 전역에서 접근할 수 있도록 설계되었습니다.
 */
public class SceneManager {

    // 1. 싱글톤 인스턴스 생성
    private static final SceneManager instance = new SceneManager();
    
    // 현재 화면에 띄워져 있는 씬을 기억하는 변수 (다형성 활용)
    private Scene currentScene;

    // 외부에서 new SceneManager()를 할 수 없도록 생성자를 private으로 막음
    private SceneManager() {}

    // 2. 전역 접근 메서드 (어디서든 SceneManager.getInstance()로 매니저를 부를 수 있음)
    public static SceneManager getInstance() {
        return instance;
    }

    /**
     * 화면을 새로운 씬으로 교체합니다.
     * @param newScene 새로 전환할 씬 객체 (예: new TitleScene())
     */
    public void changeScene(Scene newScene) {
        // 기존 씬이 존재한다면, 메모리 정리 등 종료 처리를 먼저 수행
        if (currentScene != null) {
            currentScene.exit();
        }
        
        // 새로운 씬으로 교체
        currentScene = newScene;
        
        // 새로운 씬의 초기화 작업 수행
        if (currentScene != null) {
            currentScene.enter();
        }
    }

    /**
     * 메인 루프에서 들어온 키 입력을 현재 씬으로 전달합니다.
     */
    public void handleInput(KeyStroke key) throws IOException {
        if (currentScene != null && key != null) {
            currentScene.handleInput(key);
        }
    }

    /**
     * 메인 루프에서 화면을 그릴 때 현재 씬의 렌더링 로직을 실행합니다.
     */
    public void render(TextGraphics graphics) {
        if (currentScene != null) {
            currentScene.render(graphics);
        }
    }
}