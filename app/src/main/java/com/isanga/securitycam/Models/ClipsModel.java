package com.isanga.securitycam.Models;


import java.io.File;

/**
 * Clips model class for RecyclerView
 */
public class ClipsModel {
    private String title;
    private File thumbnail;

    public ClipsModel(String title, File file) {
        this.title = title;
        this.thumbnail = file;
    }

    public String getTitle() {
        return title;
    }

    public File getThumbnail() {
        return thumbnail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setThumbnail(File thumbnail) {
        this.thumbnail = thumbnail;
    }
}
