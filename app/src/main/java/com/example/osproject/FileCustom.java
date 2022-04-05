package com.example.osproject;

import android.net.Uri;

import java.io.File;
import java.util.Calendar;

public class FileCustom {

    private File file;
    private String fileName;
    private String path;
    private double size;
    private String uploadDate;
    private Uri uri;

    public FileCustom(Uri uri) {
        this.uri = uri;
        this.file = new File(uri.getPath());
        this.path = uri.getPath();
        this.size = ((double)this.file.length() / (1024 * 1024));
        this.fileName = new File(uri.getPath()).getName();
        this.uploadDate = Calendar.getInstance().toString();
    }

    public Uri getUri() {
        return this.uri;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getPath() {
        return this.path;
    }

    public double getSize() {
        return this.size;
    }

    public String getUploadDate() {
        return this.uploadDate;
    }

    public File getFile() {
        return file;
    }
}
