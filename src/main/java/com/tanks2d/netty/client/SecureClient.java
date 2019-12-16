/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.tanks2d.netty.client;

import com.tanks2d.netty.client.entity.Tank;
import com.tanks2d.netty.client.gui.ClientGUI;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.net.ConnectException;
import java.util.Collection;

/**
 * Simple SSL chat client modified from {@link }.
 */
public final class SecureClient {

    private Channel ch;
    private EventLoopGroup group;
    private static SecureClient client;
    private ClientGUI clientGUI;

    private SecureClient(String host, int port, ClientGUI clientGUI) throws SSLException, InterruptedException {
        final SslContext sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new SecureClientInitializer(sslCtx, host, port));
        this.ch = b.connect(host, port).sync().channel();
        this.clientGUI = clientGUI;
    }

    public static SecureClient getClient() {
        return client;
    }

    public static SecureClient getClient(String host, int port, ClientGUI clientGUI) {
        if (client == null) {
            try {
                client = new SecureClient(host, port, clientGUI);
            } catch (SSLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    public static boolean checkConnection(String host, int port) {
        SslContext sslCtx = null;
        try {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (SSLException e) {
            e.printStackTrace();
        }

        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new SecureClientInitializer(sslCtx, host, port));
        boolean isActive = false;
        try {
            b.connect(host, port).sync().channel().isActive();
            isActive = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isActive;
    }

    public void updateTank(String command) {
        String[] params = command.split(",");
        String name = params[1];
        int x = Integer.parseInt(params[2]);
        int y = Integer.parseInt(params[3]);
        int direction = Integer.parseInt(params[4]);
        clientGUI.boardPanel.updateTank(name, x, y, direction);
    }

    public void registerTank(String command) {
        String[] params = command.split(",");
        String name = params[1];
        Integer roomId = Integer.parseInt(params[0].substring(1, params[0].indexOf("[")));
        int x = Integer.parseInt(params[2]);
        int y = Integer.parseInt(params[3]);
        int direction = Integer.parseInt(params[4]);
        Tank tank = new Tank(x, y, direction, name);
        if (!clientGUI.boardPanel.isTankOnBoard(name)) {
            clientGUI.boardPanel.registerNewTank(tank);
            clientGUI.boardPanel.repaint();
            sendAllTanksOnBoard(roomId);
        }
    }

    public void sendAllTanksOnBoard(Integer roomId) {
        Collection<Tank> tanks = clientGUI.boardPanel.getTanks().values();
        for (Tank tank : tanks) {
            String msg = roomId + "#Register,";
            msg += tank.getTankName() + ",";
            msg += tank.getXposition() + ",";
            msg += tank.getYposition() + ",";
            msg += tank.getDirection();
            sendCommandToServer(msg);
        }
    }

    public void removeTank(String msg) {
        clientGUI.boardPanel.removeTank(msg.split(",")[1]);
    }

    public void sendCommandToServer(String command) {
        ch.writeAndFlush(SecureClientMessageHandler.roomId + "#" + command + "\r\n");
    }

    public void closeConnection() {
        group.shutdownGracefully();
    }

    public void shot(String msg) {
        String[] params = msg.split(",");
        String name = params[1];
        clientGUI.boardPanel.getTank(name).shot();
    }

    public void sendMessageToChat(String msg) {
        if (!msg.contains("#" + clientGUI.getClientTank().getTankName() + ":")) {
            msg = msg.substring(msg.lastIndexOf("#"));
            clientGUI.sendMessageToServerChat(msg + "\n");
        }
    }
}
