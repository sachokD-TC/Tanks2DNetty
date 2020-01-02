package com.tanks2d.netty.client.entity;

import com.tanks2d.netty.client.gui.GameBoardPanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

public class Tank {

    private Image[] tankImg;
    private BufferedImage ImageBuff;
    private Bomb bomb[] = new Bomb[1000];
    private int curBomb = 0;
    private String tankName;
    private int posiX = -1, posiY = -1;
    private int direction = 1;
    private float velocityX = 0.03125f, velocityY = 0.03125f;
    private int width = 559, height = 473;

    public int getDirection() {
        return direction;
    }

    /**
     * Creates a new instance of Tank - random one
     */
    public Tank() {
        while (posiX < 70 | posiY < 50 | posiY > height - 43 | posiX > width - 43) {
            posiX = (int) (Math.random() * width);
            posiY = (int) (Math.random() * height);
        }
        loadImage(4);
    }

    /**
     *  Creates a new instance of Tank with parameters
     * @param x
     * @param y
     * @param dir
     * @param name
     */
    public Tank(int x, int y, int dir, String name) {
        posiX = x;
        posiY = y;
        tankName = name;
        direction = dir;
        loadImage(0);
    }

    /**
     * Loader for images
     * @param a = number of picture to be loaded
     */
    public void loadImage(int a) {
        tankImg = new Image[4];
        for (int i = a; i < tankImg.length + a; i++) {
            try {
                tankImg[i - a] = ImageIO.read(getClass().getResource("/Images/" + i + ".PNG"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ImageBuff = new BufferedImage(tankImg[direction - 1].getWidth(null), tankImg[direction - 1].getHeight(null), BufferedImage.TYPE_INT_RGB);
        ImageBuff.createGraphics().drawImage(tankImg[direction - 1], 0, 0, null);
    }

    public BufferedImage getBuffImage() {
        return ImageBuff;
    }

    public int getXposition() {
        return posiX;
    }

    public int getYposition() {
        return posiY;
    }

    public void setXpoistion(int x) {
        posiX = x;
    }

    public void setYposition(int y) {
        posiY = y;
    }

    /**
     *  Move tank left
     */
    public void moveLeft() {
        if (direction == 1 | direction == 3) {
            ImageBuff = new BufferedImage(tankImg[3].getWidth(null), tankImg[3].getHeight(null), BufferedImage.TYPE_INT_RGB);
            ImageBuff.createGraphics().drawImage(tankImg[3], 0, 0, null);
            direction = 4;
        } else {

            int temp;

            temp = (int) (posiX - velocityX * posiX);
            if (checkCollision(temp, posiY) == false && temp < 70) {
                posiX = 70;
            } else if (checkCollision(temp, posiY) == false) {
                posiX = temp;
            }

        }

    }

    /**
     *  Move tank right
     */
    public void moveRight() {
        if (direction == 1 | direction == 3) {
            ImageBuff = new BufferedImage(tankImg[1].getWidth(null), tankImg[1].getHeight(null), BufferedImage.TYPE_INT_RGB);
            ImageBuff.createGraphics().drawImage(tankImg[1], 0, 0, null);
            direction = 2;
        } else {
            int temp;
            temp = (int) (posiX + velocityX * posiX);
            if (checkCollision(temp, posiY) == false && temp > width - 43 + 20) {

                posiX = width - 43 + 20;
            } else if (checkCollision(temp, posiY) == false) {
                posiX = temp;
            }
        }

    }

    /**
     *  Move tank forward
     */
    public void moveForward() {
        if (direction == 2 | direction == 4) {
            ImageBuff = new BufferedImage(tankImg[0].getWidth(null), tankImg[0].getHeight(null), BufferedImage.TYPE_INT_RGB);
            ImageBuff.createGraphics().drawImage(tankImg[0], 0, 0, null);
            direction = 1;
        } else {

            int temp;
            temp = (int) (posiY - velocityY * posiY);
            if (checkCollision(posiX, temp) == false && temp < 50) {
                posiY = 50;
            } else if (checkCollision(posiX, temp) == false) {
                posiY = temp;
            }

        }
    }

    /**
     *  Move tank backward
     */
    public void moveBackward() {
        if (direction == 2 | direction == 4) {
            ImageBuff = new BufferedImage(tankImg[2].getWidth(null), tankImg[2].getHeight(null), BufferedImage.TYPE_INT_RGB);
            ImageBuff.createGraphics().drawImage(tankImg[2], 0, 0, null);
            direction = 3;
        } else {

            int temp;
            temp = (int) (posiY + velocityY * posiY);
            if (checkCollision(posiX, temp) == false && temp > height - 43 + 50) {
                posiY = height - 43 + 50;
            } else if (checkCollision(posiX, temp) == false) {
                posiY = temp;
            }
        }
    }

    /**
     *  Process fire of tank
     */
    public void shotFromKeyboard() {
        bomb[curBomb] = new Bomb(this.getXposition(), this.getYposition(), direction);
        bomb[curBomb].startBombThread(true, this.getTankName());
        curBomb++;

    }

    public Bomb[] getBomb() {
        return bomb;
    }

    public void setTankName(String name) {
        tankName = name;
    }

    public String getTankName() {
        return tankName;
    }

    /**
     * Set tank direction
     * @param dir
     */
    public void setDirection(int dir) {
        ImageBuff = new BufferedImage(tankImg[dir - 1].getWidth(null), tankImg[dir - 1].getHeight(null), BufferedImage.TYPE_INT_RGB);
        ImageBuff.createGraphics().drawImage(tankImg[dir - 1], 0, 0, null);
        direction = dir;
    }

    /**
     * Process bomb fire
     */
    public void shot() {
        bomb[curBomb] = new Bomb(this.getXposition(), this.getYposition(), direction);
        if (bomb[curBomb] != null) {
            bomb[curBomb].startBombThread(false, this.getTankName());
            curBomb++;
        }
    }

    /**
     *  Check of collision for tank
     * @param xP
     * @param yP
     * @return - true if tank hit another tank, or reach the border of play room
     */
    public boolean checkCollision(int xP, int yP) {
        Collection<Tank> clientTanks = GameBoardPanel.getTanks().values();
        int x, y;
        for (Tank tank : clientTanks) {
            x = tank.getXposition();
            y = tank.getYposition();
            if (direction == 1) {
                if (((yP <= y + 43) && yP >= y) && ((xP <= x + 43 && xP >= x) || (xP + 43 >= x && xP + 43 <= x + 43))) {
                    return true;
                }
            } else if (direction == 2) {
                if (((xP + 43 >= x) && xP + 43 <= x + 43) && ((yP <= y + 43 & yP >= y) || (yP + 43 >= y && yP + 43 <= y + 43))) {
                    return true;
                }
            } else if (direction == 3) {
                if (((yP + 43 >= y) && yP + 43 <= y + 43) && ((xP <= x + 43 && xP >= x) || (xP + 43 >= x && xP + 43 <= x + 43))) {
                    return true;
                }
            } else if (direction == 4) {
                if (((xP <= x + 43) && xP >= x) && ((yP <= y + 43 && yP >= y) || (yP + 43 >= y && yP + 43 <= y + 43))) {
                    return true;
                }
            }
        }
        return false;
    }
}
