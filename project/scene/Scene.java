package scene;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import java.io.IOException;

/**
 * 게임의 모든 화면(타이틀, 대기방, 인게임 등)이 공통적으로 구현해야 하는 인터페이스입니다.
 * SceneManager는 이 인터페이스의 규격을 통해 각 화면을 제어합니다.
 */
public interface Scene {

    /**
     * 해당 씬(화면)으로 전환될 때 최초 1회 호출됩니다.
     * 씬을 시작하기 전 필요한 데이터 초기화, 리소스 로딩, 네트워크 연결 확인 등을 수행합니다.
     */
    void enter();

    /**
     * 메인 게임 루프에서 키보드 입력이 발생했을 때 호출됩니다.
     * * @param key 플레이어가 누른 키 정보 (Lanterna KeyStroke 객체)
     * @throws IOException 네트워크 통신(클라이언트-호스트) 중 예외 발생 시 처리를 위함
     */
    void handleInput(KeyStroke key) throws IOException;

    /**
     * 화면을 터미널에 렌더링(그리기)할 때 호출됩니다.
     * 메인 루프에서 매 프레임마다 지속적으로 호출됩니다.
     * * @param graphics 화면에 텍스트나 도형을 그리기 위한 Lanterna 그래픽 객체
     */
    void render(TextGraphics graphics);

    /**
     * 다른 씬으로 전환되어 현재 씬이 종료될 때 마지막으로 1회 호출됩니다.
     * 메모리 정리, 스레드 종료, 불필요한 네트워크 소켓 닫기 등을 수행합니다.
     */
    void exit();
}