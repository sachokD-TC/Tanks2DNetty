package com.tanks2d.netty.client.entity;

import com.tanks2d.netty.client.SecureClient;
import com.tanks2d.netty.client.gui.ClientGUI;
import com.tanks2d.netty.client.gui.GameBoardPanel;
import com.tanks2d.netty.client.sound.SimpleSoundPlayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
/*
 * com.tanks2d.client.Bomb.java
 *
 * Created on 29 ����, 2008, 06:20 �
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * @author Mohamed Talaat Saad
 */
public class Bomb {

    /**
     * Creates a new instance of com.tanks2d.client.Bomb
     */

    private Image bombImg;
    private BufferedImage bombBuffImage;

    private int xPosi;
    private int yPosi;
    private int direction;
    public boolean stop = false;
    private float velocityX = 0.05f, velocityY = 0.05f;

    public Bomb(int x, int y, int direction) {
        final SimpleSoundPlayer soundBoom = new SimpleSoundPlayer("/sounds/boom.wav");
        final InputStream streamBoom = new ByteArrayInputStream(soundBoom.getSamples());
        xPosi = x;
        yPosi = y;
        this.direction = direction;
        stop = false;
        try {
            bombImg = ImageIO.read(getClass().getResource("/Images/bomb.PNG"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        bombBuffImage = new BufferedImage(bombImg.getWidth(null), bombImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
        bombBuffImage.createGraphics().drawImage(bombImg, 0, 0, null);
        Thread t = new Thread(new Runnable() {
            public void run() {
                soundBoom.play(streamBoom);
            }
        });
        t.start();
    }

    public int getPosiX() {
        return xPosi;
    }

    public int getPosiY() {
        return yPosi;
    }

    public void setPosiX(int x) {
        xPosi = x;
    }

    public void setPosiY(int y) {
        yPosi = y;
    }

    public BufferedImage getBomBufferdImg() {
        return bombBuffImage;
    }

    public BufferedImage getBombBuffImage() {
        return bombBuffImage;
    }

    public boolean checkCollision() {
        Collection<Tank> clientTanks = GameBoardPanel.getTanks().values();
        int x, y;
        for (Tank tank : clientTanks) {
            x = tank.getXposition();
            y = tank.getYposition();
            if ((yPosi >= y && yPosi <= y + 43) && (xPosi >= x && xPosi <= x + 43)) {
                ClientGUI.setScore(50);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                SecureClient.getClient().sendCommandToServer("Remove," + tank.getTankName());
                return true;
            }
        }
        return false;
    }


    public void startBombThread(boolean chekCollision) {
        new BombShotThread(chekCollision).start();
    }

    private class BombShotThread extends Thread {
        boolean checkCollis;

        public BombShotThread(boolean chCollision) {
            checkCollis = chCollision;
        }

        public void run() {
            if (checkCollis) {

                if (direction == 1) {
                    xPosi = 17 + xPosi;
                    while (yPosi > 50) {
                        yPosi = (int) (yPosi - yPosi * velocityY);
                        if (checkCollision()) {
                            break;
                        }
                        try {

                            Thread.sleep(40);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }

                } else if (direction == 2) {
                    yPosi = 17 + yPosi;
                    xPosi += 30;
                    while (xPosi < 564) {
                        xPosi = (int) (xPosi + xPosi * velocityX);
                        if (checkCollision()) {
                            break;
                        }
                        try {

                            Thread.sleep(40);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }
                } else if (direction == 3) {
                    yPosi += 30;
                    xPosi += 20;
                    while (yPosi < 505) {
                        yPosi = (int) (yPosi + yPosi * velocityY);
                        if (checkCollision()) {
                            break;
                        }
                        try {

                            Thread.sleep(40);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }
                } else if (direction == 4) {
                    yPosi = 21 + yPosi;

                    while (xPosi > 70) {
                        xPosi = (int) (xPosi - xPosi * velocityX);
                        if (checkCollision()) {
                            break;
                        }
                        try {

                            Thread.sleep(40);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }
                }

                stop = true;
            } else {
                if (direction == 1) {
                    xPosi = 17 + xPosi;
                    while (yPosi > 50) {
                        yPosi = (int) (yPosi - yPosi * velocityY);

                        try {

                            Thread.sleep(40);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }

                } else if (direction == 2) {
                    yPosi = 17 + yPosi;
                    xPosi += 30;
                    while (xPosi < 564) {
                        xPosi = (int) (xPosi + xPosi * velocityX);

                        try {

                            Thread.sleep(40);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }
                } else if (direction == 3) {
                    yPosi += 30;
                    xPosi += 20;
                    while (yPosi < 505) {
                        yPosi = (int) (yPosi + yPosi * velocityY);

                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if (direction == 4) {
                    yPosi = 21 + yPosi;
                    while (xPosi > 70) {
                        xPosi = (int) (xPosi - xPosi * velocityX);
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }
                }
                stop = true;
            }
        }
    }
}
