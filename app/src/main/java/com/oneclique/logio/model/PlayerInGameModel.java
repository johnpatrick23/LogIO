package com.oneclique.logio.model;

import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UsersModel;

import java.io.Serializable;

public class PlayerInGameModel implements Serializable {
    private String username;
    private String selectedLevel;

    private UsersModel usersModel;

    public UsersModel getUsersModel() {
        return usersModel;
    }

    public void setUsersModel(UsersModel usersModel) {
        this.usersModel = usersModel;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setSelectedLevel(String selectedLevel) {
        this.selectedLevel = selectedLevel;
    }

    public String getSelectedLevel() {
        return selectedLevel;
    }
}
