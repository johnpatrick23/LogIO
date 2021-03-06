package com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel;

import java.io.Serializable;

public class UserAchievementsModel implements Serializable {

    private String a_id;
    private String a_level;
    private String a_stars;
    private String a_time_finished;
    private String a_description;
    private String a_number_of_tries;
    private String a_username;

    public String getA_username() {
        return a_username;
    }

    public void setA_username(String a_username) {
        this.a_username = a_username;
    }

    public String getA_number_of_tries() {
        return a_number_of_tries;
    }

    public void setA_number_of_tries(String a_number_of_tries) {
        this.a_number_of_tries = a_number_of_tries;
    }

    public String getA_description() {
        return a_description;
    }

    public void setA_description(String a_description) {
        this.a_description = a_description;
    }

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
