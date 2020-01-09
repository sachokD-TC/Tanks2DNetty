package client.gui;

import com.tanks2d.netty.client.gui.ClientGUI;
import com.tanks2d.netty.server.SecureServerInitializer;
import com.tanks2d.netty.server.gui.ServerGUI;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.awt.*;

import static com.tanks2d.netty.client.utils.constants.Messages.TIP_CHAT_MESSAGE;
import static com.tanks2d.netty.client.utils.constants.Messages.TIP_PLAY_GAME_MESSAGE;
import static java.awt.event.KeyEvent.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChatTest {
    private static final String EXPECTED_CHAT_MESSAGE = "You: aaaaa";
    private static final int MAX_BULLET_NUMBER = 3;
    private ClientGUI clientGUI;
    private ServerGUI serverGUI;

    @Before
    public void setUp() {
        while (SecureServerInitializer.isSomeoneRegistered.get()) {
        }
        clientGUI = new ClientGUI(MAX_BULLET_NUMBER);
        serverGUI = new ServerGUI();
        serverGUI.getStartServerButton().doClick();
    }

    public void tearDown(){
        clientGUI.disconnect();
        clientGUI.dispose();
        serverGUI.getStopServerButton().doClick();
    }

    @Test
    public void testAChatMessage() {
        Robot robot = startGame("TesterChat");
        pressKeyNumberOfTime(robot, 1, VK_F2, 100);
        pressKeyNumberOfTime(robot, 5, VK_A, 100);
        pressKeyNumberOfTime(robot, 1, VK_ENTER, 1000);
        assertEquals(TIP_CHAT_MESSAGE, clientGUI.getGameTipsLabel().getText());
        assertEquals(EXPECTED_CHAT_MESSAGE, clientGUI.getChatTextArea().getText().trim());
        tearDown();
    }

    @Test
    public void testBEscapeFromChat() {
        Robot robot = startGame("TesterEscapeChat");
        pressKeyNumberOfTime(robot, 10, VK_UP, 100);
        pressKeyNumberOfTime(robot, 1, VK_F2, 100);
        assertEquals(TIP_CHAT_MESSAGE, clientGUI.getGameTipsLabel().getText());
        pressKeyNumberOfTime(robot, 5, VK_A, 100);
        pressKeyNumberOfTime(robot, 1, VK_ENTER, 1000);
        pressKeyNumberOfTime(robot, 1, VK_F2, 100);
        assertEquals(TIP_PLAY_GAME_MESSAGE, clientGUI.getGameTipsLabel().getText());
        tearDown();
    }

    private Robot startGame(String playerName) {
        clientGUI.getNameTextField().setText(playerName);
        clientGUI.getRegisterButton().doClick();
        Robot robot = null;
        if (clientGUI.getClientTank() != null) {
            try {
                robot = new Robot();
                robot.keyPress(VK_F3);
            } catch (AWTException e) {
                e.printStackTrace();
            }
            while (!SecureServerInitializer.isSomeoneRegistered.get()) {
            }
        }
        return robot;
    }

    /**
     * @param robot
     * @param number        - key should be pressed
     * @param key           - key Code
     * @param sleepInterval - sleep mlsec
     */
    private void pressKeyNumberOfTime(Robot robot, int number, int key, int sleepInterval) {
        if (robot != null) {
            for (int i = 0; i != number; i++) {
                robot.keyPress(key);
                robot.delay(sleepInterval);
            }
        }
    }
}
