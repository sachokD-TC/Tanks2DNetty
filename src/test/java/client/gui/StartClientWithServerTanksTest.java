package client.gui;

import com.tanks2d.netty.client.gui.ClientGUI;
import com.tanks2d.netty.client.worker.ParallelTasks;
import com.tanks2d.netty.server.SecureServerInitializer;
import com.tanks2d.netty.server.gui.ServerGUI;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.swing.*;
import java.awt.*;

import static com.tanks2d.netty.client.utils.constants.Messages.*;
import static java.awt.event.KeyEvent.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StartClientWithServerTanksTest {

    private static final int EXPECTED_WALL_AT_LEFT = 70;
    private static final int EXPECTED_WALL_AT_TOP = 50;
    private static final int EXPECTED_WALL_AT_RIGHT = 536;
    private static final int EXPECTED_WALL_AT_BOTTOM = 480;
    private static final String EXPECTED_TESTER_SCORE = "$TesterScore --> 0\n";
    private static final int MAX_BULLET_NUMBER = 3;
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

    @After
    public void tearDown() {
        clientGUI.disconnect();
        clientGUI.dispose();
        serverGUI.getStopServerButton().doClick();
    }

    @Test
    public void aTestStartGame() throws InterruptedException {
        Robot robot = startGame("TesterStart");
        robot.delay(1000);
        assertEquals(TIP_PLAY_GAME_MESSAGE, clientGUI.getGameTipsLabel().getText());
    }

    @Test
    public void bTestStartGame() throws InterruptedException {
        Robot robot = startGame("TesterScore");
        robot.delay(1000);
        assertEquals(EXPECTED_TESTER_SCORE, clientGUI.getRoomScoresTextArea().getText());
    }

    @Test
    public void checkForTankPosition() throws InterruptedException {
        startGame("TesterPosition");
        clientGUI.getClientTank().setXposition(150);
        clientGUI.getClientTank().setYposition(150);
        assertEquals(150, clientGUI.getClientTank().getXposition());
        assertEquals(150, clientGUI.getClientTank().getYposition());
    }

    @Test
    public void checkCollisionWithWallLeft() {
        Robot robot = startGame("TesterLeftCollision");
        clientGUI.getClientTank().setXposition(150);
        clientGUI.getClientTank().setYposition(150);
        pressKeyNumberOfTime(robot, 25, VK_LEFT, 10);
        assertEquals(EXPECTED_WALL_AT_LEFT, clientGUI.getClientTank().getXposition());
    }

    @Test
    public void checkCollisionWithWallUp() {
        Robot robot = startGame("TesterUpCollision");
        clientGUI.getClientTank().setXposition(150);
        clientGUI.getClientTank().setYposition(150);
        pressKeyNumberOfTime(robot, 40, VK_UP, 10);
        assertEquals(EXPECTED_WALL_AT_TOP, clientGUI.getClientTank().getYposition());
    }

    @Test
    public void checkCollisionWithWallRight() {
        Robot robot = startGame("TesterRightCollision");
        clientGUI.getClientTank().setXposition(450);
        clientGUI.getClientTank().setYposition(150);
        pressKeyNumberOfTime(robot, 20, VK_RIGHT, 10);
        assertEquals(EXPECTED_WALL_AT_RIGHT, clientGUI.getClientTank().getXposition());
    }

    @Test
    public void checkCollisionWithWallDown() {
        Robot robot = startGame("TesterDownCollision");
        clientGUI.getClientTank().setXposition(150);
        clientGUI.getClientTank().setYposition(450);
        pressKeyNumberOfTime(robot, 1, VK_LEFT, 10);
        pressKeyNumberOfTime(robot, 20, VK_DOWN, 10);
        assertEquals(EXPECTED_WALL_AT_BOTTOM, clientGUI.getClientTank().getYposition());
    }

    @Test
    public void testFire() {
        Robot robot = startGame("TesterBomb");
        clientGUI.getClientTank().setXposition(150);
        clientGUI.getClientTank().setYposition(450);
        pressKeyNumberOfTime(robot, 1, VK_SPACE, 1000);
        assertNotNull(clientGUI.getClientTank().getBomb()[0]);
        assertTrue(!clientGUI.isGunLoaded().get());
    }

    @Test
    public void testGameOverMessage() throws InterruptedException {
        ParallelTasks tasks = new ParallelTasks();
        final Runnable pressFireNumberOfTimes = () -> {
            try {
                pressKeyNumberOfTime( new Robot(), 10, VK_SPACE, 1100);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        };
        final Runnable runStartGame = () -> {
            startGame("TesterGameOver");
            clientGUI.getClientTank().setXposition(150);
            clientGUI.getClientTank().setYposition(450);
            ClientGUI.runWaiterForBullet(1000, MAX_BULLET_NUMBER);
        };
        final Runnable waitOneSecondAndDisposeDialog = () -> {
            try {
                Thread.sleep(10);
                JDialog frame = waitForJOptionPaneDialog(GAMEOVER_TITLE, GAMEOVER_MESSAGE);
                if (frame != null) {
                    System.out.println(" frame is here");
                    frame.dispose();
                    assertTrue(true);
                }
            } catch (InterruptedException e) {
            }
        };
        tasks.add(runStartGame);
        tasks.add(pressFireNumberOfTimes);
        tasks.add(waitOneSecondAndDisposeDialog);
        tasks.go();
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

    /**
     * Wait for JOptionPane dialog to appear
     *
     * @param title
     * @param message
     * @return
     */
    public static JDialog waitForJOptionPaneDialog(String title, String message) {
        JDialog win = null;
        do {
            for (Window window : Frame.getWindows()) {
                if (window instanceof JDialog) {
                    JDialog dialog = (JDialog) window;
                    if (title.equals(dialog.getTitle())) {
                        if (dialog.getContentPane().getComponents().length != 0 && dialog.getContentPane().getComponents()[0] instanceof JOptionPane) {
                            if (((JOptionPane) dialog.getContentPane().getComponents()[0]).getMessage().equals(message)) {
                                win = dialog;
                                break;
                            }
                        }
                    }
                }
            }
            if (win == null) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        } while (win == null);
        return win;
    }
}
