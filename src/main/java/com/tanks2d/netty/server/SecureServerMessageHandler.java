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

import com.tanks2d.netty.server.entity.Score;
import com.tanks2d.netty.server.entity.ScorePerRoom;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Map;

import static com.tanks2d.netty.server.SecureServerInitializer.isSomeoneRegistered;

/**
 * Handles a server-side channel.
 */
public class SecureServerMessageHandler extends SimpleChannelInboundHandler<String> {

    static final Map<Integer, ChannelGroup> channelsMap = new HashMap<>();
    static final Map<String, String> namesMap = new HashMap<>();
    private static final String REGISTER = "Register";
    private static final String REMOVE = "Remove";
    private static final String EXIT = "Exit";
    private static final String SCORES_$ = "Scores$";
    private int numberOfTanks;

    public SecureServerMessageHandler(int numberOfTanks) {
        this.numberOfTanks = numberOfTanks;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        if (!isSomeoneRegistered.get())
            isSomeoneRegistered.set(true);
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                (GenericFutureListener<Future<Channel>>) future -> {
                    ChannelGroup channels;
                    int roomId = channelsMap.keySet().size();
                    if (roomId == 0) {
                        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                        roomId++;
                        channels.add(ctx.channel());
                        ctx.writeAndFlush(roomId + "#player joined one \n");
                        namesMap.put(ctx.channel().id().asLongText(), "");
                        channelsMap.put(roomId, channels);
                    } else {
                        channels = channelsMap.get(roomId);
                        if (channels != null && channels.size() - 1 >= numberOfTanks) {
                            roomId++;
                            channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                            ctx.writeAndFlush(roomId + "#new room opened \n");
                        }
                    }
                    if (channels != null && !channels.contains(ctx.channel())) {
                        channels.add(ctx.channel());
                        ctx.writeAndFlush(roomId + "#player joined two \n");
                        namesMap.put(ctx.channel().id().asLongText(), "");
                        channelsMap.put(roomId, channels);
                    }
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
                if (name.equals("")) {
                    name = getNameFromRegisterCommand(msg, channelId);
                    namesMap.put(ctx.channel().id().asLongText(), name);
                }
                sendMessageToRoom(roomId, msg, name);
                if (msg.contains("Register") || msg.contains("Remove")) {
                    processCommand(roomId, msg, ctx);
                }
                if (msg.contains("Exit")) {
                    processCommand(roomId, msg, ctx);
                    sendMessageToRoom(roomId, name + " left the room # " + roomId, name);
                    channelsMap.remove(ctx.channel());
                    namesMap.remove(channelId);
                    ctx.close();
                }
            }
        }
    }


    private void processCommand(Integer roomId, String msg, ChannelHandlerContext ctx) {
        String name = msg.split(",")[1];
        if (msg.contains(REGISTER)) {
            processRegisterCommand(roomId, name);
        } else if (msg.contains(REMOVE)) {
            String killerName = msg.split(",")[2];
            processRemoveCommand(roomId, killerName);
        } else if (msg.contains(EXIT)) {
            processExitCommand(roomId, name);
        }
        msg = roomId + "#" + SCORES_$ + ScorePerRoom.getRoomScores(roomId);
        sendMessageToRoom(roomId, msg, name);
    }

    private void processExitCommand(Integer roomId, String name) {
        Map<String, Score> scoreMap = ScorePerRoom.scorePerRoomMap.get(roomId);
        scoreMap.remove(name);
    }

    private void processRegisterCommand(Integer roomId, String name) {
        Map<String, Score> scoreMap = ScorePerRoom.scorePerRoomMap.get(roomId);
        if (scoreMap == null) {
            scoreMap = new HashMap<>();
        }
        if (scoreMap.get(name) == null) {
            scoreMap.put(name, new Score(name, 0));
            ScorePerRoom.scorePerRoomMap.put(roomId, scoreMap);
        }
    }

    private void processRemoveCommand(Integer roomId, String killerName) {
        Map<String, Score> scoreMap = ScorePerRoom.scorePerRoomMap.get(roomId);
        Score score = scoreMap.get(killerName);
        score.setScore(score.getScore() + 50);
    }

    private String getNameFromRegisterCommand(String command, String chanelId) {
        if (!command.contains(":")) {
            String[] params = command.split(",");
            return params[1];
        } else {
            return command.substring(command.indexOf("#") + 1, command.indexOf(":"));
        }
    }

    public void sendMessageToRoom(Integer roomId, String message, String name) {
        ChannelGroup channels = channelsMap.get(roomId);
        System.out.println("command - " + message + ", name - " + name);
        for (Channel c : channels) {
            if (namesMap.get(c.id().asLongText()) != null && !namesMap.get(c.id().asLongText()).equals(name)) {
                c.writeAndFlush("[" + name + "]" + message + "\n");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
