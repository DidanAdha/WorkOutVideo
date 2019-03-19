package com.didanadha.miripnike.Data.Model;

public class Video {
    public String title,link;
    long duration;

    public Video(String title, String link,long duration) {
        this.title = title;
        this.link = link;
        this.duration = duration;
    }
    public Video(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
