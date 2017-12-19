package com.javadbadirkhanly.cleeviotaskproject.models;

import java.io.File;

/**
 * Created by javadbadirkhanly on 12/18/17.
 */

public class FileModel {

    private String name;

    private String path;

    private File file;

    private boolean isDirectory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", file=" + file +
                ", isDirectory=" + isDirectory +
                '}';
    }
}
