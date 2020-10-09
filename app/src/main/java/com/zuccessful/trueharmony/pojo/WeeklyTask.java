package com.zuccessful.trueharmony.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class WeeklyTask implements Serializable {
    // private String id;
    private ArrayList<Integer> alarmIds;
    private String name;
    private HashMap<String, Boolean> days;
    private HashMap <String,String> subtasks;
    private String date_start;
    private String date_end;

   /* public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    private Boolean done;
*/
    public Boolean getSubstask_status() {
        return substask_status;
    }

    public void setSubstask_status(Boolean substask_status) {
        this.substask_status = substask_status;
    }

    private Boolean substask_status;

    public WeeklyTask() {
    }

    public WeeklyTask(String name  , HashMap<String, Boolean> days, HashMap<String, String>subtasks, String start, String end , Boolean status) {
         this.name = name;
         this.days = days;
         this.date_start=start;
         this.date_end=end;
         this.subtasks=subtasks;
      }
    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }
    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

public HashMap<String,String> getSubtasks() {
    return subtasks;
}

    public void setSubtasks(HashMap<String, String> subtasks) {
        this.subtasks = subtasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public HashMap<String, Boolean> getDays() {
        return days;
    }

    public void setDays(HashMap<String, Boolean> days) {
        this.days = days;
    }



}
