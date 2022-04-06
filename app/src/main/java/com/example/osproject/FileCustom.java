package com.example.osproject;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

public class FileCustom {

    private Context context;
    private String filename;
    private String path;
    private File file;
    private double size;
    private Calendar uploadDate;
    private Uri uri;

    public FileCustom(Uri uri, Context context) {
        this.context = context;
        this.filename = new File(uri.getPath()).getName();
        this.uri = uri;
        this.path = uri.getPath();
        this.size = new File(uri.getPath()).length();
        this.uploadDate = Calendar.getInstance();
    }

    public String cyr2lat(char ch) {
        switch (ch) {
            case 'а':
                return "a";
            case 'б':
                return "b";
            case 'в':
                return "v";
            case 'г':
                return "g";
            case 'д':
                return "d";
            case 'е':
                return "e";
            case 'ё':
                return "je";
            case 'ж':
                return "zh";
            case 'з':
                return "z";
            case 'и':
                return "i";
            case 'й':
                return "y";
            case 'к':
                return "k";
            case 'л':
                return "l";
            case 'м':
                return "m";
            case 'н':
                return "n";
            case 'о':
                return "o";
            case 'п':
                return "p";
            case 'р':
                return "r";
            case 'с':
                return "s";
            case 'т':
                return "t";
            case 'у':
                return "u";
            case 'ф':
                return "f";
            case 'х':
                return "h";
            case 'ц':
                return "c";
            case 'ч':
                return "ch";
            case 'ш':
                return "sh";
            case 'щ':
                return "sch";
            case 'ъ':
                return "";
            case 'ы':
                return "i";
            case 'ь':
                return "";
            case 'э':
                return "e";
            case 'ю':
                return "u";
            case 'я':
                return "ia";
            default:
                return String.valueOf(ch);
        }
    }

    public String cyr2lat(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (char c : s.toCharArray()) {
            sb.append(cyr2lat(c));
        }
        return sb.toString();
    }

    public void upload() throws IOException {
        FTPClient fClient = new FTPClient();
        fClient.setControlEncoding("UTF-8");
        FileInputStream fInput = new FileInputStream(this.context.getContentResolver().openFileDescriptor(uri, "rw").getFileDescriptor());
        try {
            fClient.connect("backup-storage5.hostiman.ru");
            fClient.enterLocalPassiveMode();
            fClient.login("s222776", "Tmmm8eTKwZ9fHUqh");
            fClient.setFileType(FTP.BINARY_FILE_TYPE);
            fClient.storeFile(cyr2lat(this.filename), fInput);
            fClient.logout();
            fClient.disconnect();
            System.out.println("ВСЕ ПОЛУЧИЛОСЬ!");
        } catch (IOException ex) {

            System.out.println("ОШИБКА В UPLOAD!");
            System.err.println(ex);
        }
    }

    public Uri getUri() {
        return this.uri;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getPath() {
        return this.path;
    }

    public double getSize() {
        return this.size;
    }

    public Calendar getUploadDate() {
        return this.uploadDate;
    }

    public File getFile() {
        return file;
    }
}
