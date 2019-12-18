package com.tanks2d.netty.client.entity;

import java.util.HashMap;
import java.util.Map;

public class RoomScores {

    public static Map<String, Score> scoresMap = new HashMap<>();

    public static String getRoomScores() {
        StringBuilder scores = new StringBuilder();
        for (String s : scoresMap.keySet()) {
            scores.append(s).append(": ").append(scoresMap.get(s).getScore()).append("\n");
        }
        return scores.toString();
    }
}
