package com.example.planest.Model;

public class Notes {

    private String notes_id;
    private String Title;
    private String notes;
    private String last_modified;
    private String password;
    private String user_id;

    public Notes(String title, String notes, String last_modified, String password, String user_id) {
        this.Title = title;
        this.notes = notes;
        this.last_modified = last_modified;
        this.password = password;
        this.user_id = user_id;
    }

    public String getNotes_id() {
        return notes_id;
    }

    public void setNotes_id(String notes_id) {
        this.notes_id = notes_id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
