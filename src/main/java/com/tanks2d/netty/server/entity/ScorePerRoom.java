package com.tanks2d.netty.server.entity;

import java.util.HashMap;
import java.util.Map;

public class ScorePerRoom {

    public static Map<Integer, Map<String, Score>> scorePerRoomMap = new HashMap<>();

    public static String getRoomScores(Integer roomId) {
        Map<String, Score> scoresMap = scorePerRoomMap.get(roomId);
        if (scoresMap != null) {
            StringBuilder scores = new StringBuilder();
            for (String s : scoresMap.keySet()) {
                scores.append(s).append(" --> ").append(scoresMap.get(s).getScore()).append("&");
            }
            return scores.toString();
        }
        return "";
    }
}
