package com.example.osproject;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class FileCustom {

    private Context context;
    private String filename;
    private double size;
    private Calendar uploadDate;
    private Uri uri;

    public FileCustom(Uri uri, Context context) {
        this.context = context;
        this.uri = uri;
        this.filename = getFileName();
        this.size = getFileSize();
        this.uploadDate = Calendar.getInstance();
    }

    public FileCustom(String filename) {
        this.filename = filename;
        this.uploadDate = Calendar.getInstance();
    }

    private double getFileSize() {
        Cursor returnCursor =
                context.getContentResolver().query(this.uri, null, null, null, null);

        int size = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        return ((double)returnCursor.getLong(size) / 1024) / 1024;
    }

    private String getFileName() {
        Cursor returnCursor =
                context.getContentResolver().query(this.uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
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
            System.out.println("ОШИБКА ПРИ ВЫГРУЗКЕ ФАЙЛА С СЕРВЕРА!");
        }
    }

    public void downloadFile() throws FileNotFoundException {
        FTPClient client = new FTPClient();
        FileOutputStream fos;
        String mPath;
        mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + this.filename;

        fos = new FileOutputStream(mPath);
        try {
            client.connect("backup-storage5.hostiman.ru");
            client.enterLocalPassiveMode();
            client.login("s222776", "Tmmm8eTKwZ9fHUqh");
            client.retrieveFile("/" + this.filename, fos);
            client.logout();
            client.disconnect();
            System.out.println("ВСЕ ПОЛУЧИЛОСЬ!");
        } catch (IOException e) {
            System.out.println("ОШИБКА ПРИ СКАЧИВАНИИ ФАЙЛА С СЕРВЕРА!");
        }
    }

    public Uri getUri() {
        return this.uri;
    }

    public String getName() {
        return this.filename;
    }

    public double getSize() {
        return this.size;
    }

    public Calendar getUploadDate() {
        return this.uploadDate;
    }

}
