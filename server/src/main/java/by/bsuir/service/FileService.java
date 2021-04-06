package by.bsuir.service;

import by.bsuir.entity.FileInfo;

import java.io.File;
import java.util.List;

public interface FileService {

    File getFile(Long id);

    List<FileInfo> getAll(Long id);

    boolean update(Long id, String text);

    boolean saveFile(Long id, File file);

    boolean saveDirectory(Long id, String name);

    boolean delete(Long id);

    boolean copy(Long sourceId, Long targetId);

    boolean move(Long sourceId, Long targetId);

}
