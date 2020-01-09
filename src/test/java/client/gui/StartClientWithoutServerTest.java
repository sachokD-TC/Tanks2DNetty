package client.gui;

import com.tanks2d.netty.client.gui.ClientGUI;
import com.tanks2d.netty.client.worker.ParallelTasks;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

import static com.tanks2d.netty.client.utils.constants.Messages.*;
import static org.junit.Assert.assertTrue;

public class StartClientWithoutServerTest {

    private static final int MAX_BULLET_NUMBER = 3;
    private ClientGUI clientGUI;

    @Before
    public void setUp() {
        clientGUI = new ClientGUI(MAX_BULLET_NUMBER);
    }

    @After
    public void tearDown() {
        clientGUI.dispose();
    }

    @Test
    public void testNotConnectedMessage() throws InterruptedException {
        ParallelTasks tasks = new ParallelTasks();
        final Runnable waitOneSecondAndDisposeDialog = () -> {
            try {
                Thread.sleep(1000);
                JDialog frame = waitForJOptionPaneDialog(ALERT_TITLE, SERVER_NOT_RUNNING_MESSAGE);
                if (frame != null) {
                    frame.dispose();
                    assertTrue(clientGUI.getNameTextField().isEnabled());
                }
            } catch (InterruptedException e) {
            }
        };
        final Runnable runSetName = () -> {
            clientGUI.getNameTextField().setText("Tester");
            clientGUI.getRegisterButton().doClick();
        };
        tasks.add(runSetName);
        tasks.add(waitOneSecondAndDisposeDialog);
        tasks.go();
    }


    @Test
    public void testExitMessage() throws InterruptedException {
        ParallelTasks tasks = new ParallelTasks();
        final Runnable waitOneSecondAndDisposeDialog = () -> {
            try {
                Thread.sleep(1000);
                JDialog frame = waitForJOptionPaneDialog(EXIT_MESSAGE_TITLE, EXIT_QUESTION_MESSAGE);
                if (frame != null) {
                    frame.dispose();
                    assertTrue(true);
                }
            } catch (InterruptedException e) {
            }
        };
        final Runnable runPressOnExitButton = () -> {
            clientGUI.dispatchEvent(new WindowEvent(clientGUI, WindowEvent.WINDOW_CLOSING));
        };
        tasks.add(runPressOnExitButton);
        tasks.add(waitOneSecondAndDisposeDialog);
        tasks.go();
    }

    @Test
    public void testNameEnter() throws InterruptedException {
        ParallelTasks tasks = new ParallelTasks();
        final Runnable waitOneSecondAndDisposeDialog = () -> {
            try {
                Thread.sleep(1000);
                JDialog frame = waitForJOptionPaneDialog(ALERT_TITLE, ENTER_NAME_MESSAGE);
                if (frame != null) {
                    frame.dispose();
                    assertTrue(true);
                }
            } catch (InterruptedException e) {
            }
        };
        final Runnable runSetNameEmpty = () -> {
            clientGUI.getNameTextField().setText("");
            clientGUI.getRegisterButton().doClick();
        };
        tasks.add(runSetNameEmpty);
        tasks.add(waitOneSecondAndDisposeDialog);
        tasks.go();
    }


    @Test
    public void testRunWithEmptyHost() throws InterruptedException {
        ParallelTasks tasks = new ParallelTasks();
        final Runnable waitOneSecondAndDisposeDialog = () -> {
            try {
                Thread.sleep(1000);
                JDialog frame = waitForJOptionPaneDialog(ALERT_TITLE, HOST_EMPTY_MESSAGE);
                if (frame != null) {
                    assertTrue(true);
                    frame.dispose();
                }
            } catch (InterruptedException e) {
            }
        };
        final Runnable runSetHostEmpty = () -> {
            clientGUI.getHostTextField().setText("");
            clientGUI.getRegisterButton().doClick();
        };
        tasks.add(runSetHostEmpty);
        tasks.add(waitOneSecondAndDisposeDialog);
        tasks.go();
    }

    @Test
    public void testRunWithEmptyPort() throws InterruptedException {
        ParallelTasks tasks = new ParallelTasks();
        final Runnable waitOneSecondAndDisposeDialog = () -> {
            try {
                Thread.sleep(1000);
                JDialog frame = waitForJOptionPaneDialog(ALERT_TITLE, PORT_EMPTY_MESSAGE);
                if (frame != null) {
                    assertTrue(true);
                    frame.dispose();
                }
            } catch (InterruptedException e) {
            }
        };
        final Runnable runSetHostEmpty = () -> {
            clientGUI.getPortTextField().setText("");
            clientGUI.getRegisterButton().doClick();
        };
        tasks.add(runSetHostEmpty);
        tasks.add(waitOneSecondAndDisposeDialog);
        tasks.go();
    }

    @Test
    public void testRunWithPortIncorrectValue() throws InterruptedException {
        ParallelTasks tasks = new ParallelTasks();
        final Runnable waitOneSecondAndDisposeDialog = () -> {
            try {
                Thread.sleep(1000);
                JDialog frame = waitForJOptionPaneDialog(ALERT_TITLE, PORT_NOT_CORRECT_MESSAGE);
                if (frame != null) {
                    frame.dispose();
                    assertTrue(true);
                }
            } catch (InterruptedException e) {
            }
        };
        final Runnable runSetHostEmpty = () -> {
            clientGUI.getPortTextField().setText("FLKJJLKJ");
            clientGUI.getRegisterButton().doClick();
        };
        tasks.add(runSetHostEmpty);
        tasks.add(waitOneSecondAndDisposeDialog);
        tasks.go();
    }

    public static JDialog waitForJOptionPaneDialog(String title, String message) {
        JDialog win = null;
        do {
            for (Window window : Frame.getWindows()) {
                if (window instanceof JDialog) {
                    JDialog dialog = (JDialog) window;
                    if (title.equals(dialog.getTitle())) {
                        if(dialog.getContentPane().getComponents().length != 0 && dialog.getContentPane().getComponents()[0] instanceof JOptionPane) {
                            if(((JOptionPane)dialog.getContentPane().getComponents()[0]).getMessage().equals(message)) {
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
