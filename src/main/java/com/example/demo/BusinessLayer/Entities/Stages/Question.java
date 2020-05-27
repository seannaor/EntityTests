package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Results.Answer;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "questions")
public class Question {

    @EmbeddedId
    private QuestionID questionID;
    @MapsId("stageID")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
    })
    private QuestionnaireStage questionnaireStage;
    @Column(name = "question_json", columnDefinition = "json")
    private String questionJson;

    public Question() {
    }

    public Question(int qIdx, QuestionnaireStage questionnaireStage, String questionJson) {
        this.questionID = new QuestionID(qIdx, questionnaireStage.getStageID());
        this.questionnaireStage = questionnaireStage;
        this.questionJson = questionJson;
    }

    public int getIndex() {
        return questionID.questionIndex;
    }

    public QuestionID getQuestionID() {
        return questionID;
    }

    public void setQuestionID(QuestionID questionID) {
        this.questionID = questionID;
    }

    public String getQuestionJson() {
        return questionJson;
    }

    public void setQuestionJson(String questionJson) {
        this.questionJson = questionJson;
    }

    public Stage.StageID getStageID() {
        return this.questionnaireStage.getStageID();
    }

    public Answer answer(Object data, QuestionnaireResult questionnaireResult) throws ParseException, FormatException {
        JSONObject jQuestion = (JSONObject) new JSONParser().parse(questionJson);

        switch ((String) jQuestion.get("questionType")) {
            case "open":
            case "multiChoice":
                return new Answer(data.toString(), this, questionnaireResult);
            default:
                throw new FormatException("american, open or multi-choice question");
        }
    }

    @Embeddable
    public static class QuestionID implements Serializable {
        @Column(name = "question_index")
        int questionIndex;
        private Stage.StageID stageID;

        public QuestionID() {
        }

        public QuestionID(int questionIndex, Stage.StageID stageID) {
            this.questionIndex = questionIndex;
            this.stageID = stageID;
        }
    }
}
