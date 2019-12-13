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

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles a server-side channel.
 */
public class SecureServerMessageHandler extends SimpleChannelInboundHandler<String> {

    static final Map<Integer, ChannelGroup> channelsMap = new HashMap<>();
    static final Map<String, String> namesMap = new HashMap<>();
    private int numberOfTanks;

    public SecureServerMessageHandler(int numberOfTanks) {
        this.numberOfTanks = numberOfTanks;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // Once session is secured, send a greeting and register the channel to the global channel
        // list so the channel received the messages from others.
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                (GenericFutureListener<Future<Channel>>) future -> {
                    ChannelGroup channels;
                    int roomId = channelsMap.keySet().size();
                    if (roomId == 0) {
                        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                        roomId++;
                    } else {
                        channels = channelsMap.get(roomId);
                        if (channels.size() - 1 == numberOfTanks) {
                            roomId++;
                            channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                            ctx.writeAndFlush(roomId + "#new room opened \n");
                        }
                    }
                    channels.add(ctx.channel());
                    ctx.writeAndFlush(roomId + "#player joined \n");
                    namesMap.put(ctx.channel().id().asLongText(), "");
                    channelsMap.put(roomId, channels);
                });
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg.contains("#")) {
            Integer roomId = Integer.parseInt(msg.substring(0, msg.indexOf("#")));
            String name = "";
            String channelId = ctx.channel().id().asLongText();
            if (namesMap.containsKey(channelId)) {
                name = namesMap.get(channelId);
                if (name.equals(""))
                    name = getNameFromRegisterCommand(msg, channelId);
                namesMap.put(ctx.channel().id().asLongText(), name);
            }
            if (msg.contains("Remove")) {
                channelsMap.remove(ctx.channel());
                namesMap.remove(channelId);
            }
            sendMessageToRoom(roomId, msg, name, ctx);
            if ("Exit".equals(msg.toLowerCase().substring(msg.indexOf("#")))) {
                sendMessageToRoom(roomId, name + " left the room # " + roomId, name, ctx);
                ctx.close();
                channelsMap.remove(ctx.channel());
                namesMap.remove(channelId);
            }
        }
    }

    private String getNameFromRegisterCommand(String command, String chanelId) {
        if (!command.contains(":")) {
            String[] params = command.split(",");
            return params[1];
        } else {
            return command.substring(command.indexOf("#") + 1, command.indexOf(":"));
        }
    }

    private void sendMessageToRoom(Integer roomId, String message, String name, ChannelHandlerContext ctx) {
        ChannelGroup channels = channelsMap.get(roomId);
        for (Channel c : channels) {
            c.writeAndFlush("[" + name + "]" + message + "\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
