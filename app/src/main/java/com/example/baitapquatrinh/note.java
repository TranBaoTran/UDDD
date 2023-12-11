package com.example.baitapquatrinh;

import java.time.LocalDateTime;
import java.util.List;

public class note {
    long id;
    String title;
    String content;
    String saveDate;
    List<String> uris;
    public note(){}

    public note(String title, String content, String saveDate,List<String> uris){
        this.title=title;
        this.content=content;
        this.saveDate=saveDate;
        this.uris=uris;
    }
    public note(long id,String title, String content, String saveDate,List<String> uris){
        this.id=id;
        this.title=title;
        this.content=content;
        this.saveDate=saveDate;
        this.uris=uris;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(String saveDate) {
        this.saveDate = saveDate;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id=id;
    }
    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }
}
