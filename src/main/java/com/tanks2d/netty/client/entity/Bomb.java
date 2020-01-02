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

import static com.tanks2d.netty.client.utils.constants.Commands.DELIMITER;
import static com.tanks2d.netty.client.utils.constants.Commands.REMOVE;

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

    /**
     * Constructor of Bomb
     * @param x
     * @param y
     * @param direction
     */
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

    public BufferedImage getBombBufferdImg() {
        return bombBuffImage;
    }

    /**
     * Check for collision (if bomb hit the target or not)
     * @param starterName
     * @return
     */
    public boolean checkCollision(String starterName) {
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
                SecureClient.getClient().sendCommandToServer(REMOVE + DELIMITER + tank.getTankName() + DELIMITER + starterName);
                clientTanks.remove(tank);
                return true;
            }
        }
        return false;
    }

    /**
     * Running bomb in parallel with tank threads
     * @param chekCollision
     * @param starterName - name of tank, that fire
     */
    public void startBombThread(boolean chekCollision, String starterName) {
        new BombShotThread(chekCollision, starterName).start();
    }

    private class BombShotThread extends Thread {
        private  boolean checkCollis;
        private String starterName;

        /**
         *
         * @param chCollision
         * @param starterName - name of tank, that fire
         */
        public BombShotThread(boolean chCollision, String starterName) {
            this.checkCollis = chCollision;
            this.starterName = starterName;
        }

        public void run() {
            if (checkCollis) {

                if (direction == 1) {
                    xPosi = 17 + xPosi;
                    while (yPosi > 50) {
                        yPosi = (int) (yPosi - yPosi * velocityY);
                        if (checkCollision(starterName)) {
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
                        if (checkCollision(starterName)) {
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
                        if (checkCollision(starterName)) {
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
                        if (checkCollision(starterName)) {
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
