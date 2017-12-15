package com.example.nathalie.myownapp;

import java.util.List;

/**
 * Created by Nathalie on 15-12-2017.
 */

public class Question {
    private String question;
    private List answers;
    private String correctAnswer;
    private String category;

    // Constructor
    public Question(String question, List answers, String correctAnswer, String category) {
        this.question = question;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List getAnswers() {
        return answers;
    }

    public void setAnswers(List answers) {
        this.answers = answers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
