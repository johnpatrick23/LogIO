package com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel;

import java.io.Serializable;

public class QuestionModel implements Serializable {
    private String a_id;
    private String a_level;
    private String a_questiontype;
    private String a_question;
    private String a_choices;
    private String a_answer;
    private String a_timeduration;
    private String a_category;
    private String a_instruction;

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

    public String getA_instruction() {
        return a_instruction;
    }

    public void setA_question(String a_question) {
        this.a_question = a_question;
    }

    public String getA_question() {
        return a_question;
    }

    public void setA_instruction(String a_instruction) {
        this.a_instruction = a_instruction;
    }

    public String getA_choices() {
        return a_choices;
    }

    public void setA_choices(String a_choices) {
        this.a_choices = a_choices;
    }

    public String getA_answer() {
        return a_answer;
    }

    public void setA_answer(String a_answer) {
        this.a_answer = a_answer;
    }

    public String getA_category() {
        return a_category;
    }

    public void setA_category(String a_category) {
        this.a_category = a_category;
    }

    public String getA_questiontype() {
        return a_questiontype;
    }

    public void setA_questiontype(String a_questiontype) {
        this.a_questiontype = a_questiontype;
    }

    public String getA_timeduration() {
        return a_timeduration;
    }

    public void setA_timeduration(String a_timeduration) {
        this.a_timeduration = a_timeduration;
    }
}
