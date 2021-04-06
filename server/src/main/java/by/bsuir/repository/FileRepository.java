package by.bsuir.repository;

import by.bsuir.entity.FileInfo;
import org.springframework.data.repository.Repository;

import javax.transaction.Transactional;

@Transactional
public interface FileRepository extends Repository<FileInfo, Long> {

    void save(FileInfo fileInfo);

    FileInfo findById(Long id);

    FileInfo findByPath(String path);

    void deleteById(Long id);

}
