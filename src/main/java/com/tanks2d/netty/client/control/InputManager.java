package com.tanks2d.netty.client.control;

import com.tanks2d.netty.client.SecureClient;
import com.tanks2d.netty.client.entity.Tank;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputManager implements KeyListener {
    private final int LEFT = 37;
    private final int RIGHT = 39;
    private final int UP = 38;
    private final int DOWN = 40;
    private static int status = 0;
    private boolean firstMove = true;
    private Tank clientTank;
    private SecureClient client;

    /**
     * Creates a new instance of com.tanks2d.client.InputManager
     */
    public InputManager(Tank clientTank) {
        this.client = SecureClient.getClient();
        this.clientTank = clientTank;
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if(firstMove){
            client.sendCommandToServer("Register," + clientTank.getTankName() + "," + clientTank.getXposition() + "," + clientTank.getYposition() + "," + clientTank.getDirection());
            firstMove = false;
        }

        if (e.getKeyCode() == LEFT) {
            if (clientTank.getDirection() == 1 | clientTank.getDirection() == 3) {
                clientTank.moveLeft();
                client.sendCommandToServer("Update," + clientTank.getXposition() + "," +
                        clientTank.getYposition() + "," + clientTank.getTankName() + "," + clientTank.getDirection());
            } else if (clientTank.getDirection() == 4) {
                clientTank.moveLeft();
                client.sendCommandToServer("Update," + clientTank.getXposition() + "," +
                        clientTank.getYposition() + "," + clientTank.getTankName() + "," + clientTank.getDirection());
            }
        } else if (e.getKeyCode() == RIGHT) {
            if (clientTank.getDirection() == 1 | clientTank.getDirection() == 3) {
                clientTank.moveRight();
                client.sendCommandToServer("Update," + clientTank.getXposition() + "," +
                        clientTank.getYposition() + "," + clientTank.getTankName() + "," + clientTank.getDirection());

            } else if (clientTank.getDirection() == 2) {
                clientTank.moveRight();
                client.sendCommandToServer("Update," + clientTank.getXposition() + "," +
                        clientTank.getYposition() + "," + clientTank.getTankName() + "," + clientTank.getDirection());

            }
        } else if (e.getKeyCode() == UP) {
            if (clientTank.getDirection() == 2 | clientTank.getDirection() == 4) {
                clientTank.moveForward();
                client.sendCommandToServer("Update," + clientTank.getXposition() + "," +
                        clientTank.getYposition() + "," + clientTank.getTankName() + "," + clientTank.getDirection());
            } else if (clientTank.getDirection() == 1) {
                clientTank.moveForward();
                client.sendCommandToServer("Update," + clientTank.getXposition() + "," +
                        clientTank.getYposition() + "," + clientTank.getTankName() + "," + clientTank.getDirection());
            }
        } else if (e.getKeyCode() == DOWN) {
            if (clientTank.getDirection() == 2 | clientTank.getDirection() == 4) {
                clientTank.moveBackward();
                client.sendCommandToServer("Update," + clientTank.getXposition() + "," +
                        clientTank.getYposition() + "," + clientTank.getTankName() + "," + clientTank.getDirection());
            } else if (clientTank.getDirection() == 3) {
                clientTank.moveBackward();
                client.sendCommandToServer("Update," + clientTank.getXposition() + "," +
                        clientTank.getYposition() + "," + clientTank.getTankName() + "," + clientTank.getDirection());

            }
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            client.sendCommandToServer("Shot," + clientTank.getTankName());
            clientTank.shotFromKeyboard();
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public void setClientTank(Tank clientTank) {
        this.clientTank = clientTank;
    }

}
