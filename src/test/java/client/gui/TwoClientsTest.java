package client.gui;

import com.tanks2d.netty.client.entity.Tank;
import com.tanks2d.netty.client.gui.ClientGUI;
import com.tanks2d.netty.server.SecureServerInitializer;
import com.tanks2d.netty.server.gui.ServerGUI;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.Collection;

import static com.tanks2d.netty.client.utils.constants.Messages.TIP_PLAY_GAME_MESSAGE;
import static java.awt.event.KeyEvent.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TwoClientsTest {
    private static final int MAX_BULLET_NUMBER = 3;
    private static final String EXPECTED_PLAYERS_SCORES = "$PlayerTwo --> 0\nPlayerOne --> 0";
    private static final String EXPECTED_PLAYERS_AFTER_FIRE_SCORES = "$PlayerOneFire --> 50";
    private ClientGUI clientGUI;
    private ServerGUI serverGUI;

    @Before
    public void setUp() {
        clientGUI = new ClientGUI(MAX_BULLET_NUMBER);
        serverGUI = new ServerGUI();
        while (SecureServerInitializer.isSomeoneRegistered.get()) {
        }
        serverGUI.getStartServerButton().doClick();
    }


    @Test
    public void startTwoClientsGameTest() {
        Robot robot = startGame("PlayerOne");
        assertEquals(TIP_PLAY_GAME_MESSAGE, clientGUI.getGameTipsLabel().getText());
        assertEquals(EXPECTED_PLAYERS_SCORES, clientGUI.getRoomScoresTextArea().getText().trim());
        robot.delay(2000);
        clientGUI.disconnect();
        clientGUI.dispose();
        serverGUI.getStopServerButton().doClick();
    }

    @Test
    public void checkMoveUpForTwoClientsTest() {
        Robot robot = startGame("PlayerOne");
        int yCoordinate = 158;
        for (int i = 0; i != 10; i++) {
            yCoordinate -= (i + 5);
            serverGUI.getSecureServer().sendCommandToRoom(1, "1#Update,PlayerTwo,370," + yCoordinate + ",1", "PlayerTwo");
            robot.delay(100);
        }
        assertEquals(TIP_PLAY_GAME_MESSAGE, clientGUI.getGameTipsLabel().getText());
        assertEquals(EXPECTED_PLAYERS_SCORES, clientGUI.getRoomScoresTextArea().getText().trim());
        robot.delay(5000);
        clientGUI.disconnect();
        clientGUI.dispose();
        serverGUI.getStopServerButton().doClick();
    }

    @Test
    public void checkFireTest() {
        Robot robot = startGame("PlayerOneFire");
        int yCoordinate = 370;
        for (int i = 0; i != 10; i++) {
            yCoordinate -= (i + 5);
            serverGUI.getSecureServer().sendCommandToRoom(1, "1#Update,PlayerTwo,158," + yCoordinate + ",1", "PlayerTwo");
            robot.delay(100);
        }
        robot.keyPress(VK_UP);
        robot.keyPress(VK_SPACE);
        robot.delay(2000);
        assertEquals(EXPECTED_PLAYERS_AFTER_FIRE_SCORES, clientGUI.getRoomScoresTextArea().getText().trim());
        Tank foundedTank = null;
        Collection<Tank> tanksOnBoard = clientGUI.boardPanel.getTanks().values();
        for (Tank tank : tanksOnBoard) {
            if (tank.getTankName().equals("PlayerTwo")) {
                foundedTank = tank;
            }
        }
        assertNull(foundedTank);
    }

    @Test
    public void checkMoveUpAndRightForTwoClientsTest() {
        Robot robot = startGame("PlayerOne");
        int yCoordinate = 370;
        int xCoordinate = 158;
        for (int i = 0; i != 10; i++) {
            yCoordinate -= (i + 5);
            serverGUI.getSecureServer().sendCommandToRoom(1, "1#Update,PlayerTwo," + xCoordinate + "," + yCoordinate + ",1", "PlayerTwo");
            robot.delay(100);
        }
        for (int i = 0; i != 10; i++) {
            xCoordinate += (i + 5);
            serverGUI.getSecureServer().sendCommandToRoom(1, "1#Update,PlayerTwo," + xCoordinate + "," + yCoordinate + ",2", "PlayerTwo");
            robot.delay(100);
        }
        assertEquals(TIP_PLAY_GAME_MESSAGE, clientGUI.getGameTipsLabel().getText());
        assertEquals(EXPECTED_PLAYERS_SCORES, clientGUI.getRoomScoresTextArea().getText().trim());
        robot.delay(7000);
        clientGUI.disconnect();
        clientGUI.dispose();
        serverGUI.getStopServerButton().doClick();
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
                serverGUI.getSecureServer().sendCommandToRoom(1, "1#Register,PlayerTwo,158,370,1", "PlayerTwo");
                serverGUI.getSecureServer().sendCommandToRoom(1, "1#Scores$PlayerTwo --> 0&PlayerOne --> 0&", "PlayerTwo");
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
