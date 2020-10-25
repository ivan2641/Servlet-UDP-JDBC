/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Web;

import java.io.Serializable;

/**
 *
 * @author vanab
 */
public class ExamAnswer implements Serializable {

    private boolean result;
    private String[] arrHistory;
    String sessionId;

    public ExamAnswer() {
    }

    public ExamAnswer(boolean result, String[] arrHistory, String sessionId) {
        this.result = result;
        this.arrHistory = arrHistory;
        this.sessionId = sessionId;
    }

    public ExamAnswer(boolean result, String sessionId) {
        this.result = result;
        this.sessionId = sessionId;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setArrHistory(String[] arrHistory) {
        if (arrHistory != null) {
            this.arrHistory = arrHistory;
        }
    }

    public boolean isResult() {
        return result;
    }

    public String[] getArrHistory() {
        return arrHistory;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
