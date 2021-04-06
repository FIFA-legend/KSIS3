package by.bsuir.controllers.dto;

import by.bsuir.entity.FileInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Container {

    List<FileInfo> files;

    public Container(List<FileInfo> files) {
        this.files = files;
    }

}
