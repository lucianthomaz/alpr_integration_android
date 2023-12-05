package com.lucianthomaz.alpr_application;

public class AlertActionDto {
    private int alertId;
    private int userId;
    private Boolean accepted;

    public AlertActionDto(int alertId, int userId, Boolean accepted) {
        this.alertId = alertId;
        this.userId = userId;
        this.accepted = accepted;
    }

    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
