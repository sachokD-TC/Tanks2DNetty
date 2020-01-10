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
package com.tanks2d.netty.server;

import com.tanks2d.netty.server.entity.ScorePerRoom;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;


public final class SecureServer implements Runnable {

    private final int port;
    private final int numbetOfTanks;
    private SecureServerInitializer secureServerInitializer;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    /**
     * Server constructor
     *
     * @param port          - port for connection
     * @param numberOfTanks - max number of tanks in one room
     * @throws CertificateException
     * @throws SSLException
     */
    public SecureServer(int port, int numberOfTanks) throws CertificateException, SSLException {
        this.port = port;
        this.numbetOfTanks = numberOfTanks;
    }

    @Override
    public void run() {
        SelfSignedCertificate ssc = null;
        try {
            ssc = new SelfSignedCertificate();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        SslContext sslCtx = null;
        try {
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                    .build();
        } catch (SSLException e) {
            e.printStackTrace();
        }
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            secureServerInitializer = new SecureServerInitializer(sslCtx, numbetOfTanks);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(secureServerInitializer);
            b.bind(port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            shutdownServer();
        }
    }

    /**
     * @param roomId  - room id
     * @param message - message to send
     * @param name    - name of sender
     */
    public void sendCommandToRoom(Integer roomId, String message, String name) {
        secureServerInitializer.secureServerMessageHandler().sendMessageToRoom(roomId, message, name);
    }

    public void shutdownServer() {
        if (bossGroup != null && workerGroup != null) {
            try {
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync();
                SecureServerMessageHandler.channelsMap.clear();
                SecureServerMessageHandler.namesMap.clear();
                ScorePerRoom.scorePerRoomMap.clear();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
