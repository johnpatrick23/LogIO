package com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel;

import java.io.Serializable;

public class UserAchievementsModel implements Serializable {
    private String a_id;
    private String a_level;
    private String a_stars;
    private String a_time_finished;

    public String getA_id() {
        return a_id;
    }

    public void setA_id(String a_id) {
        this.a_id = a_id;
    }

    public String getA_level() {
        return a_level;
    }

    public void setA_level(String a_level) {
        this.a_level = a_level;
    }

    public String getA_stars() {
        return a_stars;
    }

    public void setA_stars(String a_stars) {
        this.a_stars = a_stars;
    }

    public String getA_time_finished() {
        return a_time_finished;
    }

    public void setA_time_finished(String a_time_finished) {
        this.a_time_finished = a_time_finished;
    }
}
