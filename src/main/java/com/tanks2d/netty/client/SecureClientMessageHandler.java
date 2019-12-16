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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.tanks2d.netty.client.utils.constants.Commands.*;

/**
 * Handles a client-side channel.
 */
public class SecureClientMessageHandler extends SimpleChannelInboundHandler<String> {
    public static int roomId;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg.contains(ROOM_NUMBER_SIGN) && roomId == 0) {
            roomId = Integer.parseInt(msg.substring(0, msg.indexOf(ROOM_NUMBER_SIGN)));
        } else {
            msg = ROOM_NUMBER_SIGN + roomId + msg;
        }
        System.out.println(msg);
        msg = msg.substring(msg.indexOf(ROOM_NUMBER_SIGN));
        if (msg.contains(UPDATE)) SecureClient.getClient().updateTank(msg);
        else if(msg.contains(REMOVE))SecureClient.getClient().removeTank(msg);
        else if(msg.contains(EXIT))SecureClient.getClient().removeTank(msg);
        else if (msg.contains(SHOT))  SecureClient.getClient().shot(msg);
        else if(msg.contains(CHAT_DELIMITER)) SecureClient.getClient().sendMessageToChat(msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
