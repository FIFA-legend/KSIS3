package by.bsuir.service;

import by.bsuir.entity.FileInfo;
import by.bsuir.entity.Type;
import by.bsuir.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FileServiceImpl implements FileService {

    private final String DIRECTORY_PATH = "D:\\KSIS_Files";

    private final FileRepository repository;

    @Autowired
    public FileServiceImpl(FileRepository repository) {
        this.repository = repository;
    }

    @Override
    public File getFile(Long id) {
        FileInfo info = repository.findById(id);
        if (info == null) return null;
        return new File(DIRECTORY_PATH + info.getPath());
    }

    @Override
    public List<FileInfo> getAll(Long id) {
        List<FileInfo> files = new LinkedList<>();
        FileInfo info = repository.findById(id);
        if (info == null) return files;
        File directory = new File(DIRECTORY_PATH + info.getPath());
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String path = info.getPath();
            if (file.isDirectory()) {
                path += file.getName() + "\\";
            } else {
                path += file.getName();
            }
            files.add(repository.findByPath(path));
        }
        return files;
    }

    @Override
    public boolean update(Long id, String name) {
        FileInfo info = repository.findById(id);
        if (info.getType() == Type.FILE) {
            return updateFile(info, name);
        }
        if (info.getType() == Type.DIRECTORY) {
            return updateDirectory(info, name);
        }
        return false;
    }

    private boolean updateFile(FileInfo info, String name) {
        String extension = info.getName().substring(info.getName().lastIndexOf('.'));
        String path = info.getPath().substring(0, info.getPath().lastIndexOf('\\') + 1);
        Path source = Paths.get(DIRECTORY_PATH + info.getPath());
        Path target = Paths.get(DIRECTORY_PATH + "\\" + path + name + extension);
        try {
            Files.move(source, target);
            info.setPath(path + name + extension);
            info.setName(name + extension);
            repository.save(info);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean updateDirectory(FileInfo info, String name) {
        String path = info.getPath().substring(0, info.getPath().length() - info.getName().length()) + name + "\\";
        Path source = Paths.get(DIRECTORY_PATH + info.getPath());
        Path target = Paths.get(DIRECTORY_PATH + path);
        List<FileInfo> files = getAll(info.getId());
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getType() == Type.DIRECTORY) {
                files.addAll(getAll(files.get(i).getId()));
            }
        }
        try {
            Files.move(source, target);
            for (FileInfo i : files) {
                i.setPath(i.getPath().replace(info.getPath(), path));
                repository.save(i);
            }
            info.setPath(path);
            info.setName(name + "\\");
            repository.save(info);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean saveFile(Long id, File file) {
        FileInfo info = repository.findById(id);
        if (info == null) return false;
        File saveFile = new File(DIRECTORY_PATH + info.getPath() + file.getName());
        if (saveFile.exists()) return false;
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(saveFile));
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            bos.write(bis.readAllBytes());
            String path = info.getPath() + file.getName();
            repository.save(new FileInfo(file.getName(), path, Type.FILE));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean saveDirectory(Long id, String name) {
        FileInfo info = repository.findById(id);
        if (info == null) return false;
        File file = new File(DIRECTORY_PATH + info.getPath() + name);
        if (!file.mkdir()) return false;
        FileInfo newDir = new FileInfo(name, info.getPath() + name, Type.DIRECTORY);
        repository.save(newDir);
        return true;
    }

    @Override
    public boolean delete(Long id) {
        FileInfo info = repository.findById(id);
        if (info.getType() == Type.FILE) {
            return deleteFile(info);
        }
        if (info.getType() == Type.DIRECTORY) {
            return deleteDirectory(info);
        }
        return false;
    }

    private boolean deleteFile(FileInfo info) {
        File fileToDelete = new File(DIRECTORY_PATH + info.getPath());
        if (fileToDelete.delete()) {
            repository.deleteById(info.getId());
            return true;
        }
        return false;
    }

    private boolean deleteDirectory(FileInfo info) {
        List<FileInfo> files = getAll(info.getId());
        files.add(0, info);
        for (int i = 1; i < files.size(); i++) {
            FileInfo file = files.get(i);
            if (file.getType() == Type.DIRECTORY) {
                files.addAll(getAll(file.getId()));
            }
        }
        for (int i = files.size() - 1; i >= 0; i--) {
            FileInfo fileInfo = files.get(i);
            File fileToDelete = new File(DIRECTORY_PATH + fileInfo.getPath());
            if (fileToDelete.delete()) {
                repository.deleteById(fileInfo.getId());
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean copy(Long sourceId, Long targetId) {
        FileInfo target = repository.findById(targetId);
        if (target.getType() == Type.FILE) return false;
        FileInfo source = repository.findById(sourceId);
        if (source.getType() == Type.FILE) {
            return copyFile(source, target);
        }
        return false;
    }

    private boolean copyFile(FileInfo source, FileInfo target) {
        Path sourcePath = Paths.get(DIRECTORY_PATH + source.getPath());
        Path targetPath = Paths.get(DIRECTORY_PATH + target.getPath() + source.getName());
        try {
            Files.copy(sourcePath, targetPath);
            repository.save(new FileInfo(source.getName(), target.getPath() + source.getName(), Type.FILE));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean move(Long sourceId, Long targetId) {
        FileInfo target = repository.findById(targetId);
        if (target.getType() == Type.FILE) return false;
        FileInfo source = repository.findById(sourceId);
        if (source.getType() == Type.DIRECTORY) {
            return moveDirectory(source, target);
        }
        if (source.getType() == Type.FILE) {
            return moveFile(source, target);
        }
        return false;
    }

    private boolean moveDirectory(FileInfo source, FileInfo target) {
        Path sourcePath = Paths.get(DIRECTORY_PATH + source.getPath());
        Path targetPath = Paths.get(DIRECTORY_PATH + target.getPath() + source.getName());
        List<FileInfo> files = getAll(source.getId());
        String pathToReplace = source.getPath();
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getType() == Type.DIRECTORY) {
                files.addAll(getAll(files.get(i).getId()));
            }
        }
        try {
            Files.move(sourcePath, targetPath);
            source.setPath(target.getPath() + source.getName());
            repository.save(source);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        for (FileInfo info : files) {
            info.setPath(info.getPath().replace(pathToReplace, target.getPath() + source.getName()));
            repository.save(info);
        }
        return true;
    }

    private boolean moveFile(FileInfo source, FileInfo target) {
        Path sourcePath = Paths.get(DIRECTORY_PATH + source.getPath());
        Path targetPath = Paths.get(DIRECTORY_PATH + target.getPath() + source.getName());
        try {
            Files.move(sourcePath, targetPath);
            source.setPath(target.getPath() + source.getName());
            repository.save(source);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private int commonPath(String source, String target) {
        int length = Math.min(source.length(), target.length());
        for (int i = 1; i < length; i++) {
            if (source.charAt(i) != target.charAt(i)) return i - 1;
        }
        return length;
    }
}
