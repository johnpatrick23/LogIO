package com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel;

import java.io.Serializable;

public class UsersModel implements Serializable {

    private String a_id;
    private String a_username;
    private String a_last_used;
    private String a_level_1_stars;
    private String a_level_2_stars;
    private String a_level_3_stars;
    private String a_level_4_stars;
    private String a_level_5_stars;
    private String a_level_6_stars;
    private String a_level_7_stars;
    private String a_number_of_access;
    private String a_hint;
    private String a_add_time;
    private String a_slow_time;

    public String getA_add_time() {
        return a_add_time;
    }

    public void setA_add_time(String a_add_time) {
        this.a_add_time = a_add_time;
    }

    public String getA_hint() {
        return a_hint;
    }

    public void setA_hint(String a_hint) {
        this.a_hint = a_hint;
    }

    public String getA_slow_time() {
        return a_slow_time;
    }

    public void setA_slow_time(String a_slow_time) {
        this.a_slow_time = a_slow_time;
    }

    public String getA_number_of_access() {
        return a_number_of_access;
    }

    public void setA_number_of_access(String a_number_of_access) {
        this.a_number_of_access = a_number_of_access;
    }

    public String getA_id() {
        return a_id;
    }

    public void setA_id(String a_id) {
        this.a_id = a_id;
    }

    public String getA_last_used() {
        return a_last_used;
    }

    public void setA_last_used(String a_last_used) {
        this.a_last_used = a_last_used;
    }

    public String getA_username() {
        return a_username;
    }

    public void setA_username(String a_username) {
        this.a_username = a_username;
    }

    public String getA_level_1_stars() {
        return a_level_1_stars;
    }

    public void setA_level_1_stars(String a_level_1_stars) {
        this.a_level_1_stars = a_level_1_stars;
    }

    public String getA_level_2_stars() {
        return a_level_2_stars;
    }

    public void setA_level_2_stars(String a_level_2_stars) {
        this.a_level_2_stars = a_level_2_stars;
    }

    public String getA_level_3_stars() {
        return a_level_3_stars;
    }

    public void setA_level_3_stars(String a_level_3_stars) {
        this.a_level_3_stars = a_level_3_stars;
    }

    public String getA_level_4_stars() {
        return a_level_4_stars;
    }

    public void setA_level_4_stars(String a_level_4_stars) {
        this.a_level_4_stars = a_level_4_stars;
    }

    public String getA_level_5_stars() {
        return a_level_5_stars;
    }

    public void setA_level_5_stars(String a_level_5_stars) {
        this.a_level_5_stars = a_level_5_stars;
    }

    public String getA_level_6_stars() {
        return a_level_6_stars;
    }

    public void setA_level_6_stars(String a_level_6_stars) {
        this.a_level_6_stars = a_level_6_stars;
    }

    public String getA_level_7_stars() {
        return a_level_7_stars;
    }

    public void setA_level_7_stars(String a_level_7_stars) {
        this.a_level_7_stars = a_level_7_stars;
    }
}
