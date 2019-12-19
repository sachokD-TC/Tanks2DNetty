package com.tanks2d.netty.client.worker;

import com.tanks2d.netty.client.gui.ClientGUI;

public class GunLoader implements Runnable {
    private ClientGUI clientGUI;

    public GunLoader(ClientGUI clientGUI){
        this.clientGUI = clientGUI;
    }
    @Override
    public void run() {
        for(int i=0; i!= 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.clientGUI.setIsGunLoaded(true);
    }
}
