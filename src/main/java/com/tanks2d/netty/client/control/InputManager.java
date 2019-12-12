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

    private Tank tank;
    private SecureClient client;

    /**
     * Creates a new instance of com.tanks2d.client.InputManager
     */
    public InputManager(Tank tank) {
        this.client = SecureClient.getClient();
        this.tank = tank;
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if(firstMove){
            client.sendCommandToServer("Register," + tank.getTankName() + "," + tank.getXposition() + "," + tank.getYposition() + "," + tank.getDirection());
            firstMove = false;
        }

        if (e.getKeyCode() == LEFT) {
            if (tank.getDirection() == 1 | tank.getDirection() == 3) {
                tank.moveLeft();
                client.sendCommandToServer("Update," + tank.getXposition() + "," +
                        tank.getYposition() + "," + tank.getTankName() + "," + tank.getDirection());
            } else if (tank.getDirection() == 4) {
                tank.moveLeft();
                client.sendCommandToServer("Update," + tank.getXposition() + "," +
                        tank.getYposition() + "," + tank.getTankName() + "," + tank.getDirection());
            }
        } else if (e.getKeyCode() == RIGHT) {
            if (tank.getDirection() == 1 | tank.getDirection() == 3) {
                tank.moveRight();
                client.sendCommandToServer("Update," + tank.getXposition() + "," +
                        tank.getYposition() + "," + tank.getTankName() + "," + tank.getDirection());

            } else if (tank.getDirection() == 2) {
                tank.moveRight();
                client.sendCommandToServer("Update," + tank.getXposition() + "," +
                        tank.getYposition() + "," + tank.getTankName() + "," + tank.getDirection());

            }
        } else if (e.getKeyCode() == UP) {
            if (tank.getDirection() == 2 | tank.getDirection() == 4) {
                tank.moveForward();
                client.sendCommandToServer("Update," + tank.getXposition() + "," +
                        tank.getYposition() + "," + tank.getTankName() + "," + tank.getDirection());
            } else if (tank.getDirection() == 1) {
                tank.moveForward();
                client.sendCommandToServer("Update," + tank.getXposition() + "," +
                        tank.getYposition() + "," + tank.getTankName() + "," + tank.getDirection());
            }
        } else if (e.getKeyCode() == DOWN) {
            if (tank.getDirection() == 2 | tank.getDirection() == 4) {
                tank.moveBackward();
                client.sendCommandToServer("Update," + tank.getXposition() + "," +
                        tank.getYposition() + "," + tank.getTankName() + "," + tank.getDirection());
            } else if (tank.getDirection() == 3) {
                tank.moveBackward();
                client.sendCommandToServer("Update," + tank.getXposition() + "," +
                        tank.getYposition() + "," + tank.getTankName() + "," + tank.getDirection());

            }
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            client.sendCommandToServer("Shot," + tank.getTankName());
            tank.shotFromKeyboard();
        }
    }

    public void keyReleased(KeyEvent e) {
    }

}
