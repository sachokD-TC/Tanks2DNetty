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
    public static final String NAME_END_SIGN = "]";
    private int roomId;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg.contains(ROOM_NUMBER_SIGN) && SecureClient.getClient() != null) {
            try {
                System.out.println(msg);
                if(msg.contains(NAME_END_SIGN))
                    roomId = Integer.parseInt(msg.substring(msg.indexOf(NAME_END_SIGN) +1, msg.indexOf(ROOM_NUMBER_SIGN)));
                else
                    roomId = Integer.parseInt(msg.substring(0, msg.indexOf(ROOM_NUMBER_SIGN)));
                if (msg.contains(UPDATE)) SecureClient.getClient().updateTank(msg);
                else if (msg.contains(SCORES + SCORES_DELIMITER)) SecureClient.getClient().processScores(msg);
                else if (msg.contains(REMOVE)) SecureClient.getClient().removeTank(msg);
                else if (msg.contains(EXIT)) SecureClient.getClient().exitTank(msg);
                else if (msg.contains(SHOT)) SecureClient.getClient().shot(msg);
                else if (msg.contains(CHAT_DELIMITER)) SecureClient.getClient().sendMessageToChat(msg);
            } catch (NumberFormatException num){
                System.out.println("Wrong room number " + num);
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public int getRoomId() {
        return roomId;
    }
}
