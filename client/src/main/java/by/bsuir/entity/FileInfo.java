package by.bsuir.entity;

public class FileInfo {

    private Long id;

    private String name;

    private String path;

    private Type type;

    public FileInfo() {
    }

    public FileInfo(String name, String path, Type type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public FileInfo(Long id, String name, String path, Type type) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
