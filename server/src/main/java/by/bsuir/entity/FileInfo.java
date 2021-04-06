package by.bsuir.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "files")
public class FileInfo extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    @Column(name = "type")
    private Type type;

    public FileInfo(String name, String path, Type type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }
}
