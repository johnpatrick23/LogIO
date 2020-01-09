package com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel;

import java.io.Serializable;

public class UserLogsModel implements Serializable {

    private String a_id;
    private String a_username;
    private String a_level;
    private String a_popup_message_time;
    private String a_star;
    private String a_average_time;

    public String getA_username() {
        return a_username;
    }

    public void setA_username(String a_username) {
        this.a_username = a_username;
    }

    public String getA_level() {
        return a_level;
    }

    public void setA_level(String a_level) {
        this.a_level = a_level;
    }

    public String getA_id() {
        return a_id;
    }

    public void setA_id(String a_id) {
        this.a_id = a_id;
    }

    public String getA_star() {
        return a_star;
    }

    public void setA_star(String a_star) {
        this.a_star = a_star;
    }

    public String getA_average_time() {
        return a_average_time;
    }

    public void setA_average_time(String a_average_time) {
        this.a_average_time = a_average_time;
    }

    public String getA_popup_message_time() {
        return a_popup_message_time;
    }

    public void setA_popup_message_time(String a_popup_message_time) {
        this.a_popup_message_time = a_popup_message_time;
    }

}
