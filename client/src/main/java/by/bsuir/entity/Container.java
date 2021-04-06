package by.bsuir.entity;

import java.util.List;

public class Container {

    private List<FileInfo> files;

    public Container() {
    }

    public Container(List<FileInfo> files) {
        this.files = files;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }
}
