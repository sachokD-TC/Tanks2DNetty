package server;

import com.tanks2d.netty.client.gui.ClientGUI;
import com.tanks2d.netty.server.SecureServerInitializer;
import com.tanks2d.netty.server.gui.ServerGUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.awt.*;

import static java.awt.event.KeyEvent.VK_F3;
import static junit.framework.TestCase.assertTrue;

public class ServerCommandTest {
    private ClientGUI clientGUI;
    private ServerGUI serverGUI;
    private static final int MAX_BULLET_NUMBER = 3;

    @Before
    public void setUp() {
        clientGUI = new ClientGUI(MAX_BULLET_NUMBER);
        serverGUI = new ServerGUI();
        while (SecureServerInitializer.isSomeoneRegistered.get()) {
        }
        serverGUI.getStartServerButton().doClick();
    }

    @After
    public void tearDown() {
        clientGUI.disconnect();
        clientGUI.dispose();
        serverGUI.getStopServerButton().doClick();
    }

    @Test
    public void sendWrongCommandTest() throws Exception {
        startGame("sendWrongCommandTest");
        serverGUI.getSecureServer().sendCommandToRoom(1, "1#WrongCommand,sendWrongCommandTest,dfd,3d,d", "sendWrongCommandTest");
    }

    @Test
    public void sendFakeCommandTest() throws Exception {
        startGame("sendWrongCommandTest");
        serverGUI.getSecureServer().sendCommandToRoom(1, "dsjklh[eeee]#ddkljek,h", "sendFakeCommandTest");
    }

    @Test
    public void sendWrongRoomIdCommand() {
        startGame("sendWrongRoomId");
        serverGUI.getSecureServer().sendCommandToRoom(1, "134r5#Update,sendWrongCommandTest,dfd,3d,d", "sendWrongCommandTest");
    }

    @Test
    public void sendCrapCommand() {
        startGame("sendCrapCommand");
        serverGUI.getSecureServer().sendCommandToRoom(1, "frekevkjechkljehe;", "sendWrongCommandTest");
    }

    @Test
    public void sendWrongPlayerName() {
        startGame("sendWrongCommandTest");
        serverGUI.getSecureServer().sendCommandToRoom(1, "1#WrongCommand,PlayerWWW,dfd,3d,d", "PlayerTwADAD");
    }

    @Test
    public void sendCorrectUpdate() {
        try {
            Robot robot = startGame("CorrectUpdate");
            serverGUI.getSecureServer().sendCommandToRoom(1, "1#Register,PlayerTwo,158,370,1", "PlayerTwo");
            int y = 150;
            for (int i = 0; i != 20; i++) {
                y += (i + 5);
                serverGUI.getSecureServer().sendCommandToRoom(1, "1#Update,PlayerTwo,172," + y + ",1", "PlayerTwo");
                robot.delay(100);
            }
        } catch (Exception ex) {
            assertTrue(false);
        }
    }

    private Robot startGame(String playerName) {
        clientGUI.getNameTextField().setText(playerName);
        clientGUI.getRegisterButton().doClick();
        clientGUI.getClientTank().setXposition(158);
        clientGUI.getClientTank().setYposition(420);
        Robot robot = null;
        if (clientGUI.getClientTank() != null) {
            try {
                robot = new Robot();
                robot.keyPress(VK_F3);
                robot.delay(1000);
            } catch (AWTException e) {
                e.printStackTrace();
            }
            while (!SecureServerInitializer.isSomeoneRegistered.get()) {
            }
        }
        return robot;
    }
}
