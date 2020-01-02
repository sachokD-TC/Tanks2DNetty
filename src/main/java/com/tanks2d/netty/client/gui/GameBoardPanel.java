package com.tanks2d.netty.client.gui;

import com.tanks2d.netty.client.control.InputManager;
import com.tanks2d.netty.client.entity.Tank;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.tanks2d.netty.client.utils.constants.Messages.GAME_TITLE;
import static com.tanks2d.netty.client.utils.constants.Messages.TIP_KILLED_MESSAGE;


public class GameBoardPanel extends JPanel {

    private Tank clientTank;
    private int width = 609;
    private int height = 523;
    private static Map<String, Tank> tanks;
    private boolean gameStatus;
    private InputManager inputManager;
    private ClientGUI clientGUI;

    public GameBoardPanel(Tank clientTank, ClientGUI clientGUI) {
        this.clientTank = clientTank;
        this.clientGUI = clientGUI;
        setSize(width, height);
        setBounds(-50, 0, width, height);
        this.inputManager = new InputManager(clientTank);
        addKeyListener(inputManager);
        setFocusable(true);
        tanks = new HashMap<>();
    }

    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D) gr;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.GREEN);
        g.fillRect(70, 50, getWidth() - 100, getHeight());
        try {
            g.drawImage(ImageIO.read(getClass().getResourceAsStream("/Images/bg.JPG")), 70, 50, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        g.drawString(GAME_TITLE, 255, 30);
        if (gameStatus) {
            g.drawImage(clientTank.getBuffImage(), clientTank.getXposition(), clientTank.getYposition(), this);
            for (int j = 0; j < 1000; j++) {
                if (clientTank.getBomb()[j] != null) {
                    if (clientTank.getBomb()[j].stop == false) {
                        g.drawImage(clientTank.getBomb()[j].getBombBufferdImg(), clientTank.getBomb()[j].getPosiX(), clientTank.getBomb()[j].getPosiY(), this);
                    }
                }
            }
            Collection<Tank> tanksOnBoard = tanks.values();
            for (Tank tank : tanksOnBoard) {
                if (!tank.getTankName().equals(this.clientTank.getTankName())) {
                    g.drawImage(tank.getBuffImage(), tank.getXposition(), tank.getYposition(), this);
                    for (int j = 0; j < 1000; j++) {
                        if (tank.getBomb()[j] != null) {
                            if (tank.getBomb()[j].stop == false) {
                                g.drawImage(tank.getBomb()[j].getBombBufferdImg(), tank.getBomb()[j].getPosiX(), tank.getBomb()[j].getPosiY(), this);
                            }
                        }
                    }
                }
            }
        }
        repaint();
    }

    public void registerNewTank(Tank newTank) {
        tanks.put(newTank.getTankName(), newTank);
    }

    public void removeTank(String tankName, String killerName) {
        tanks.remove(tankName);
        if (tankName.equals(clientTank.getTankName())) {
            clientGUI.setTipsText(TIP_KILLED_MESSAGE.replace("&NAME&", killerName));
            this.clientTank = new Tank();
            this.clientTank.setTankName(tankName);
            inputManager.setFirstMove(true);
            inputManager.setClientTank(clientTank);
        }
    }

    public void updateTank(String name, int x, int y, int direction) {
        if (tanks.get(name) == null) {
            Tank tank = new Tank(x, y, direction, name);
            tanks.put(name, tank);
        } else {
            if (!name.equals(this.clientTank.getTankName())) {
                Tank tank = tanks.get(name);
                tank.setXpoistion(x);
                tank.setYposition(y);
                tank.setDirection(direction);
                repaint();
            }
        }
    }

    public Tank getTank(String name) {
        return tanks.get(name);
    }

    public boolean isTankOnBoard(String name) {
        return tanks.containsKey(name);
    }

    public void setGameStatus(boolean status) {
        gameStatus = status;
    }

    public static Map<String, Tank> getTanks() {
        return tanks;
    }
}
