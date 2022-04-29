package com.project.osproject;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class FileCustom {

    private Context context;
    private String filename;
    private double size;
    private Calendar uploadDate;
    private Uri uri;
    private boolean flag;
    private FirebaseAuth fbAuth;
    private String FilePath;

    public FileCustom(Uri uri, Context context, FirebaseAuth fbAuth) {
        this.context = context;
        this.uri = uri;
        this.fbAuth = fbAuth;
        this.filename = getFileName();
        this.size = getFileSize();
        this.uploadDate = Calendar.getInstance();
    }

    public FileCustom(Uri uri, Context context, FirebaseAuth fbAuth, String FilePath) {
        this.context = context;
        this.uri = uri;
        this.fbAuth = fbAuth;
        this.filename = getFileName();
        this.size = getFileSize();
        this.uploadDate = Calendar.getInstance();
        this.FilePath = FilePath;
    }

    public FileCustom(Context context, FirebaseAuth fbAuth, String FilePath) {
        this.context = context;
        this.fbAuth = fbAuth;
        this.uploadDate = Calendar.getInstance();
        this.FilePath = FilePath;
    }

    public FileCustom(String filename, Context context, FirebaseAuth fbAuth) {
        this.context = context;
        this.filename = filename;
        this.fbAuth = fbAuth;
        this.uploadDate = Calendar.getInstance();
    }

    public FileCustom(String filename, Context context, FirebaseAuth fbAuth, String FilePath) {
        this.context = context;
        this.filename = filename;
        this.fbAuth = fbAuth;
        this.uploadDate = Calendar.getInstance();
        this.FilePath = FilePath;
    }
    
    public boolean getFlag(){
        return this.flag;
    }

    private double getFileSize() {
        Cursor returnCursor =
                context.getContentResolver().query(this.uri, null, null, null, null);

        int size = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        return ((double) returnCursor.getLong(size) / 1024) / 1024;
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

    public void CreateDir() {
        FTPClient client = new FTPClient();
        client.setControlEncoding("UTF-8");
        try {
            client.connect("backup-storage5.hostiman.ru");
            client.login("s222776", "Tmmm8eTKwZ9fHUqh");
            String[] splitter = (fbAuth.getUid() + "/" + FilePath).split("/");
            String absPath = "";
            for(int i = 0; i < splitter.length; ++i)
            {
                absPath += "/" + splitter[i];
                if(!checkIfDirectoryExists(absPath)) {
                    client.makeDirectory(absPath);
                }
            }
        }
        catch (IOException e){
            Toast.makeText(context, "Ошибка при создании папки", Toast.LENGTH_SHORT).show();
        }
    }

    public static void removeDirectory(String parentDir, FTPClient ftpClient, String currentDir) throws IOException {
        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }

        FTPFile[] subFiles = ftpClient.listFiles(dirToList);

        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and the directory itself
                    continue;
                }
                String filePath = parentDir + "/" + currentDir + "/"
                        + currentFileName;
                if (currentDir.equals("")) {
                    filePath = parentDir + "/" + currentFileName;
                }

                if (aFile.isDirectory()) {
                    removeDirectory( dirToList, ftpClient, currentFileName);
                } else {
                    boolean deleted = ftpClient.deleteFile(filePath);
                }
            }

            boolean removed = ftpClient.removeDirectory(dirToList);
        }
    }


    public void DeleteDir() {
        FTPClient client = new FTPClient();
        client.setControlEncoding("UTF-8");
        String userId = this.fbAuth.getUid();

        try {
            client.connect("backup-storage5.hostiman.ru");
            client.login("s222776", "Tmmm8eTKwZ9fHUqh");
            client.enterLocalPassiveMode();
            if (FilePath != null) {
                removeDirectory(userId + FilePath + "/", client, filename);
            } else {
                removeDirectory(userId, client, filename);
            }
        } catch (IOException e) {
            Toast.makeText(context, "Ошибка при удалении папки", Toast.LENGTH_SHORT).show();
        }



    }

    private boolean checkIfDirectoryExists(String path) {
        FTPClient client = new FTPClient();
        client.setControlEncoding("UTF-8");
        try {
            client.connect("backup-storage5.hostiman.ru");
            client.login("s222776", "Tmmm8eTKwZ9fHUqh");
            client.enterLocalPassiveMode();
            client.changeWorkingDirectory(path);
            int returnCode = client.getReplyCode();
            if (returnCode == 550) {
                return false;
            }
            return true;
        } catch (IOException ex) {
            this.flag = false;
            System.out.println("ОШИБКА ПРИ ПРОВЕРКЕ ДИРЕКТОРИИ НА СУЩЕСТВОВАНИЕ!\n" + ex);
        }
        return false;
    }



    public void upload() {
        FTPClient client = new FTPClient();
        client.setControlEncoding("UTF-8");
        try {
            String userId = this.fbAuth.getUid();
            FileInputStream fInput = new FileInputStream(this.context.getContentResolver().openFileDescriptor(this.uri, "r").getFileDescriptor());
            client.connect("backup-storage5.hostiman.ru");
            client.login("s222776", "Tmmm8eTKwZ9fHUqh");
            client.enterLocalPassiveMode();
            client.setBufferSize(1048576);
            System.out.println("РАЗМЕР БУФЕРА = " + client.getBufferSize());
            client.setFileType(FTP.BINARY_FILE_TYPE);
            if(!checkIfDirectoryExists(userId)) {
                client.makeDirectory(userId);
            }
            String[] splitter = (userId + "/" + FilePath + "/").split("/");
            String absPath = "";
            for(int i = 0; i < splitter.length; ++i)
            {
                absPath += "/" + splitter[i];
                if(!checkIfDirectoryExists(absPath)) {
                    client.makeDirectory(absPath);
                }
            }
            if(FilePath != null) {
                client.storeFile(userId + "/" + FilePath + "/" + this.filename, fInput);
            }
            else{
                client.storeFile(userId + "/" + this.filename, fInput);
            }
            client.logout();
            client.disconnect();
            fInput.close();
            this.flag = true;
            System.out.println("ВСЕ ПОЛУЧИЛОСЬ!");
        } catch (IOException ex) {
            this.flag = false;
            System.out.println("ОШИБКА ПРИ ВЫГРУЗКЕ ФАЙЛА НА СЕРВЕР!\n" + ex);
        }
    }

    public void downloadFile() {
        FTPClient client = new FTPClient();
        client.setControlEncoding("UTF-8");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), this.filename);
        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            client.connect("backup-storage5.hostiman.ru");
            client.enterLocalPassiveMode();
            client.login("s222776", "Tmmm8eTKwZ9fHUqh");
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.retrieveFile(fbAuth.getUid()+ "/" + FilePath + "/" + this.filename, outputStream);
            client.logout();
            client.disconnect();
            outputStream.close();
            System.out.println("ФАЙЛ УДАЛЕН!");
            Toast.makeText(this.context, "Файл удален!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            System.out.println("ОШИБКА ПРИ СКАЧИВАНИИ ФАЙЛА С СЕРВЕРА!\n" + e);
        }
    }

    public void downloadAndOpen() {
        FTPClient client = new FTPClient();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), this.filename);
        client.setControlEncoding("UTF-8");
        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            client.connect("backup-storage5.hostiman.ru");
            client.enterLocalPassiveMode();
            client.login("s222776", "Tmmm8eTKwZ9fHUqh");
            client.setFileType(FTP.BINARY_FILE_TYPE);
            if(FilePath != null) {
                client.retrieveFile(fbAuth.getUid() + "/" + FilePath + "/" + this.filename, outputStream);
            }
            else{
                client.retrieveFile(fbAuth.getUid() + "/" + this.filename, outputStream);
            }
            client.logout();
            client.disconnect();
            outputStream.close();
            System.out.println("ВСЕ ПОЛУЧИЛОСЬ!");
        } catch (IOException e) {
            System.out.println("ОШИБКА ПРИ СКАЧИВАНИИ ФАЙЛА С СЕРВЕРА!\n" + e);
        }

        Uri uriLocal = FileProvider.getUriForFile(this.context, this.context.getApplicationContext().getPackageName() + ".provider", file);
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uriLocal, this.context.getContentResolver().getType(uriLocal));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this.context, "Не найдено приложений для открытия этого файла", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteFile() {
        FTPClient client = new FTPClient();
        client.setControlEncoding("UTF-8");
        try {
            client.connect("backup-storage5.hostiman.ru");
            client.enterLocalPassiveMode();
            client.login("s222776", "Tmmm8eTKwZ9fHUqh");
            client.setFileType(FTP.BINARY_FILE_TYPE);
            if(FilePath != null) {
                client.deleteFile(fbAuth.getUid()+ "/" + FilePath + "/" + this.filename);
            }
            else{
                client.deleteFile(fbAuth.getUid()+ "/" + this.filename);
            }

            client.logout();
            client.disconnect();
            System.out.println("ФАЙЛ УДАЛЁН!");
        } catch (IOException e) {
            System.out.println("ОШИБКА ПРИ УДАЛЕНИИ ФАЙЛА!\n" + e);
        }
    }

    public void renameFile(String newFileName) {
        FTPClient client = new FTPClient();
        client.setControlEncoding("UTF-8");
        try {
            client.connect("backup-storage5.hostiman.ru");
            client.enterLocalPassiveMode();
            client.login("s222776", "Tmmm8eTKwZ9fHUqh");
            client.setFileType(FTP.BINARY_FILE_TYPE);
            String userId = this.fbAuth.getUid();
            if(FilePath != null) {
                client.rename(userId + "/" + FilePath + "/" + this.filename, userId + "/" + FilePath + "/" + newFileName);
            }
            else{
                client.rename(userId + "/" + this.filename, userId + "/" + newFileName);
            }
            client.logout();
            client.disconnect();
            System.out.println("ФАЙЛ rename!");
        } catch (IOException e) {
            System.out.println("ОШИБКА ПРИ УДАЛЕНИИ ФАЙЛА!\n" + e);
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
