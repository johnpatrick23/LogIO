package com.oneclique.logio.model;

import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.QuestionModel;

import java.io.Serializable;
import java.util.List;

public class QuestionListModel implements Serializable {
    private List<List<QuestionModel>> questionListModelList;

    public List<List<QuestionModel>> getQuestionListModelList() {
        return questionListModelList;
    }

    public void setQuestionListModelList(List<List<QuestionModel>> questionListModelList) {
        this.questionListModelList = questionListModelList;
    }
}
