package com.tanks2d.netty.server.entity;

import java.util.HashMap;
import java.util.Map;

public class ScorePerRoom {

    public static Map<Integer, Map<String, Score>> scorePerRoomMap = new HashMap<>();

    /**
     * Static structure to store user scores
     * as map -
     * keys - roomId
     * values - map of scores - keys names, values - scores
     *
     * @param roomId - number of room
     * @return
     */
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

    /**
     *
     * @param roomId - id of room
     * @param tankName - tank to remove
     */
    public static void removeTankScore(Integer roomId, String tankName) {
        Map<String, Score> scoresMap = scorePerRoomMap.get(roomId);
        if (scoresMap != null && scoresMap.containsKey(tankName))
            scoresMap.remove(tankName);
    }
}
