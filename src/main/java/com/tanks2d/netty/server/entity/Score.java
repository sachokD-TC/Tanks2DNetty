package com.tanks2d.netty.server.entity;

public class Score {
    private final String name;
    private int score;

    public Score(String name, int score){
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
