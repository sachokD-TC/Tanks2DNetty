package com.tanks2d.netty.client.control;

import com.tanks2d.netty.client.SecureClient;
import com.tanks2d.netty.client.entity.Tank;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static com.tanks2d.netty.client.utils.constants.Commands.*;
import static com.tanks2d.netty.client.utils.constants.Messages.TIP_CHAT_MESSAGE;
import static com.tanks2d.netty.client.utils.constants.Messages.TIP_PLAY_GAME_MESSAGE;
import static java.awt.event.KeyEvent.*;

public class InputManager implements KeyListener {
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
        int keyCode = e.getKeyCode();
        if(client.getClientGUI().isGunLoaded().get()){
            client.getClientGUI().setGunLoaded();
        }
        if (firstMove && keyCode == VK_F3) {
            client.registerTank(REGISTER + DELIMITER + clientTank.getTankName() + DELIMITER + clientTank.getXposition() + DELIMITER + clientTank.getYposition() + DELIMITER + clientTank.getDirection());
            client.getClientGUI().setTipsText(TIP_PLAY_GAME_MESSAGE);
            firstMove = false;
        }
        if (!firstMove) {
            if (keyCode == VK_LEFT) {
                if (clientTank.getDirection() == 1 | clientTank.getDirection() == 3) {
                    clientTank.moveLeft();
                } else if (clientTank.getDirection() == 4) {
                    clientTank.moveLeft();
                }
            } else if (keyCode == VK_RIGHT) {
                if (clientTank.getDirection() == 1 | clientTank.getDirection() == 3) {
                    clientTank.moveRight();
                } else if (clientTank.getDirection() == 2) {
                    clientTank.moveRight();
                }
            } else if (keyCode == VK_UP) {
                clientTank.moveForward();
            } else if (keyCode == VK_DOWN) {
                if (clientTank.getDirection() == 2 | clientTank.getDirection() == 4) {
                    clientTank.moveBackward();
                } else if (clientTank.getDirection() == 3) {
                    clientTank.moveBackward();
                }
            } else if (keyCode == KeyEvent.VK_SPACE) {
                if(client.getClientGUI().isGunLoaded().get()) {
                    client.sendCommandToServer(SHOT + DELIMITER + clientTank.getTankName());
                    clientTank.shotFromKeyboard();
                    client.getClientGUI().loadGun();
                }
            }
            if (keyCode == VK_RIGHT || keyCode == VK_LEFT || keyCode == VK_UP || keyCode == VK_DOWN) {
                client.sendCommandToServer(UPDATE + DELIMITER + clientTank.getTankName() + DELIMITER + clientTank.getXposition() + DELIMITER +
                        clientTank.getYposition() + "," + clientTank.getDirection());
            } else if (keyCode == VK_F2) {
                client.getClientGUI().activateChat();
                client.getClientGUI().setTipsText(TIP_CHAT_MESSAGE);
            }
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
