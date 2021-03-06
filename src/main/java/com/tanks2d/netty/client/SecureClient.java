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
import com.tanks2d.netty.server.entity.ScorePerRoom;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.util.Collection;

import static com.tanks2d.netty.client.utils.constants.Commands.*;


public final class SecureClient {

    private Channel channel;
    private EventLoopGroup group;
    private static SecureClient client;
    private ClientGUI clientGUI;
    private SecureClientInitializer secureClientInitializer = null;

    /**
     * @param host      - ip or DNS name for server
     * @param port      - port to connect with server
     * @param clientGUI - client Graphic interface
     * @throws SSLException
     * @throws InterruptedException
     */
    private SecureClient(String host, int port, ClientGUI clientGUI) throws SSLException, InterruptedException {
        final SslContext sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        secureClientInitializer = new SecureClientInitializer(sslCtx, host, port);
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(secureClientInitializer);
        this.channel = b.connect(host, port).sync().channel();
        this.clientGUI = clientGUI;
    }

    /**
     * @return instasnce of SecureClient
     */
    public static SecureClient getClient() {
        return client;
    }

    /**
     * need it for auto tests
     */
    public static void disposeClient() {
        System.out.println("dispose client");
        client = null;
    }

    /**
     * make client variable singleton - if it's null - return new instance
     * if not - return already created one.
     *
     * @param host      - ip or DNS name for server
     * @param port      - port to connect with server
     * @param clientGUI - client Graphic interface
     * @return
     */
    public static SecureClient getClient(String host, int port, ClientGUI clientGUI) {
        System.out.println("get Client = " + client);
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

    /**
     * @param host - ip or DNS name for server
     * @param port - port to connect with server*
     * @return true if connection exists
     */
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

    /**
     * update tank position
     *
     * @param command - special string command delimited with comma
     */
    public void updateTank(String command) {
        String[] params = command.split(",");
        String name = params[1];
        int x = Integer.parseInt(params[2]);
        int y = Integer.parseInt(params[3]);
        int direction = Integer.parseInt(params[4]);
        clientGUI.boardPanel.updateTank(name, x, y, direction);
    }

    /**
     * Register new tank on board
     *
     * @param command - special string command delimited with comma
     */
    public void registerTank(String command) {
        String[] params = command.split(",");
        String name = params[1];
        int x = Integer.parseInt(params[2]);
        int y = Integer.parseInt(params[3]);
        int direction = Integer.parseInt(params[4]);
        Tank tank = new Tank(x, y, direction, name);
        if (!clientGUI.boardPanel.isTankOnBoard(name)) {
            clientGUI.boardPanel.registerNewTank(tank);
            clientGUI.boardPanel.repaint();
            sendAllTanksOnBoard();
        }
    }

    /**
     * Broadcast message with new tank appearance for all clients in room
     */
    public void sendAllTanksOnBoard() {
        Collection<Tank> tanks = clientGUI.boardPanel.getTanks().values();
        for (Tank tank : tanks) {
            String msg = REGISTER + DELIMITER;
            msg += tank.getTankName() + ",";
            msg += tank.getXposition() + ",";
            msg += tank.getYposition() + ",";
            msg += tank.getDirection();
            sendCommandToServer(msg);
        }
    }

    /**
     * process exit command
     *
     * @param command - special string command delimited with comma
     */
    public void exitTank(String command) {
        String[] params = command.split(",");
        removeTank(command);
    }

    /**
     * Process removing command (in case of death of tank)
     *
     * @param command - special string command delimited with comma
     */
    public void removeTank(String command) {
        String[] params = command.split(",");
        String killerName = params[2];
        if (!killerName.equals(params[1]))
            clientGUI.boardPanel.removeTank(params[1], killerName);
    }

    /**
     * send message to secure channel
     *
     * @param command - special string command delimited with comma
     */
    public void sendCommandToServer(String command) {
        channel.writeAndFlush(secureClientInitializer.getSecureClientMessageHandler().getRoomId() + "#" + command + "\r\n");
    }

    public void closeConnection() {
        group.shutdownGracefully();
    }

    /**
     * Process shot command
     *
     * @param command - special string command delimited with comma
     */
    public void shot(String command) {
        String[] params = command.split(",");
        String name = params[1];
        if (clientGUI.boardPanel.getTank(name) != null)
            clientGUI.boardPanel.getTank(name).shot();
    }

    /**
     * Process send to chat command
     *
     * @param command - special string command delimited with semi column
     */
    public void sendMessageToChat(String command) {
        if (!command.contains("#" + clientGUI.getClientTank().getTankName() + ":")) {
            command = command.substring(command.lastIndexOf("#"));
            clientGUI.sendMessageToServerChat(command + "\n");
        }
    }

    /**
     * @return - clientGUI
     */
    public ClientGUI getClientGUI() {
        return clientGUI;
    }

    /**
     * @return - clientInitializer
     */
    public SecureClientInitializer getSecureClientInitializer() {
        return secureClientInitializer;
    }

    /**
     * Update room scores
     *
     * @param command - special string command delimited with & sign
     */
    public void processScores(String command) {
        command = command.substring(command.indexOf(SCORES) + SCORES.length()).replace("&", "\n");
        clientGUI.updateRoomScores(command);
    }

}
