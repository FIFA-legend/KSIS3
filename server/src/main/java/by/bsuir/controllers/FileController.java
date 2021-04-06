package by.bsuir.controllers;

import by.bsuir.controllers.dto.Container;
import by.bsuir.entity.FileInfo;
import by.bsuir.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value = "/dir/{id}", produces = "application/json")
    public Container getFiles(@PathVariable Long id) {
        return new Container(fileService.getAll(id));
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<File> getFile(@PathVariable Long id) {
        File file = fileService.getFile(id);
        return new ResponseEntity<>(file, HttpStatus.OK);
        /*String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf('.'));
        MediaType mediaType;*/

        /*switch (extension) {
            case ".png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case ".jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case ".gif":
                mediaType = MediaType.IMAGE_GIF;
                break;
            case ".xml":
                mediaType = MediaType.TEXT_XML;
                break;
            case ".html":
                mediaType = MediaType.TEXT_HTML;
                break;
            case ".txt":
                mediaType = MediaType.TEXT_PLAIN;
                break;
            case ".pdf":
                mediaType = MediaType.APPLICATION_PDF;
                break;
            default:
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return ResponseEntity
                    .ok()
                    .contentLength(file.length())
                    .contentType(mediaType)
                    .body(new InputStreamResource(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;*/
    }

    @PostMapping("/files/{id}/upload")
    public ResponseEntity<Boolean> saveFile(@RequestBody File file, @PathVariable Long id) {
        return new ResponseEntity<>(fileService.saveFile(id, file), HttpStatus.OK);
    }

    @PostMapping("/dir/{id}/upload")
    public ResponseEntity<Boolean> saveDirectory(@PathVariable Long id, @RequestBody String dirName) {
        return new ResponseEntity<>(fileService.saveDirectory(id, dirName + "\\"), HttpStatus.OK);
    }

    @PutMapping(value = "/storage/{id}/put", consumes = "application/json")
    public boolean updateFile(@PathVariable Long id, @RequestBody FileInfo selectedFile) {
        return fileService.update(id, selectedFile.getName());
    }

    @DeleteMapping("/storage/{id}/delete")
    public boolean deleteFile(@PathVariable Long id) {
        return fileService.delete(id);
    }

    @RequestMapping("/storage/copy")
    public ResponseEntity<Boolean> copyFile(final HttpServletRequest request, @RequestParam Long source, @RequestParam Long target) {
        if (!request.getMethod().equals("GET")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(fileService.copy(source, target), HttpStatus.OK);
    }

    @RequestMapping("/storage/move")
    public ResponseEntity<Boolean> moveFile(final HttpServletRequest request, @RequestParam Long source, @RequestParam Long target) {
        if (!request.getMethod().equals("GET")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(fileService.move(source, target), HttpStatus.OK);
    }

}
