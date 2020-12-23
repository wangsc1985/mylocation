package com.wangsc.mylocation.models;

import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2016/10/28.
 */

public class RunLog {
    private UUID id;
    private DateTime runTime;
    private String tag;
    private String item;
    private String message;

    public RunLog(UUID id) {
        this.id = id;
    }

    public RunLog(String item, String message) {
        this.id = UUID.randomUUID();
        this.runTime = new DateTime();
        this.tag = new DateTime(runTime.getTimeInMillis()).toLongDateTimeString();
        this.item = item;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DateTime getRunTime() {
        return runTime;
    }

    public void setRunTime(DateTime runTime) {
        this.runTime = runTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
