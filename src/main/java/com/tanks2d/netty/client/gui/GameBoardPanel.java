package com.tanks2d.netty.client.gui;

import com.tanks2d.netty.client.SecureClient;
import com.tanks2d.netty.client.control.InputManager;
import com.tanks2d.netty.client.entity.Tank;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class GameBoardPanel extends JPanel {

    private Tank clientTank;
    private int width = 609;
    private int height = 523;
    private static Map<String, Tank> tanks;
    private boolean gameStatus;
    private InputManager inputManager;
    public GameBoardPanel(Tank clientTank, boolean gameStatus) {
        this.clientTank = clientTank;
        this.gameStatus = gameStatus;
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
        g.drawString("Tanks 2D Multiplayers Game", 255, 30);
        if (gameStatus) {
            g.drawImage(clientTank.getBuffImage(), clientTank.getXposition(), clientTank.getYposition(), this);
            for (int j = 0; j < 1000; j++) {
                if (clientTank.getBomb()[j] != null) {
                    if (clientTank.getBomb()[j].stop == false) {
                        g.drawImage(clientTank.getBomb()[j].getBomBufferdImg(), clientTank.getBomb()[j].getPosiX(), clientTank.getBomb()[j].getPosiY(), this);
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
                                g.drawImage(tank.getBomb()[j].getBomBufferdImg(), tank.getBomb()[j].getPosiX(), tank.getBomb()[j].getPosiY(), this);
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

    public void removeTank(String tankName) {
        tanks.remove(tankName);
        if (tankName.equals(clientTank.getTankName())) {
            int response = JOptionPane.showConfirmDialog(this, "Sorry you were killed, would you like to try again?", "Tanks 2D Multiplayer Game!", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                this.clientTank = new Tank();
                this.clientTank.setTankName(tankName);
                inputManager.setFirstMove(true);
                inputManager.setClientTank(clientTank);
            } else {
                SecureClient.getClient().sendCommandToServer("Exit," + clientTank.getTankName());
                System.exit(0);
            }
        }
    }

    public void updateTank(String name, int x, int y, int direction) {
        if (!name.equals(this.clientTank.getTankName())) {
            Tank tank = tanks.get(name);
            tank.setXpoistion(x);
            tank.setYposition(y);
            tank.setDirection(direction);
            repaint();
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
