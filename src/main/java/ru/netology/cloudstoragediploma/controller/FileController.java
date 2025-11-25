package ru.netology.cloudstoragediploma.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstoragediploma.dto.FileDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ru.netology.cloudstoragediploma.service.FileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;

    /**
     * Загрузка файла.
     *
     * @param file     Загружаемый файл
     * @param fileName Название файла
     */
    @PostMapping("/file")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> uploadFile(
            @NotNull @RequestPart("file") MultipartFile file,
            @RequestParam("filename") String fileName
    ) {
        log.info("Загрузка файла с именем {}", fileName);
        fileService.uploadFile(file, fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Удаление файла.
     *
     * @param fileName Название файла
     */
    @DeleteMapping("/file")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> deleteFile(@RequestParam("filename") String fileName) {
        log.info("Удаление файла с именем {}", fileName);
        fileService.deleteFile(fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Скачивание файла.
     *
     * @param filename Название файла
     * @return Содержимое файла
     */
    @GetMapping("/file")
    @ResponseBody
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String filename) {
        log.info("Скачивание файла с именем {}", filename);
        FileDTO file = fileService.downloadFile(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getFileByte());
    }

    /**
     * Переименование файла.
     *
     * @param filename Старое имя файла
     * @param fileDTO  Новый файл (с новым именем)
     */
    @PutMapping("/file")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> renameFile(
            @RequestParam String filename,
            @RequestBody FileDTO fileDTO
    ) {
        log.info("Переименовываем файл с именем {} на новое имя {}", filename, fileDTO.getFileName());
        fileService.editFileName(filename, fileDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Получение списка файлов.
     *
     * @param limit Ограничение по количеству файлов
     * @return Список файлов
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<List<FileDTO>> getAllFiles(@Min(1) @RequestParam int limit) {
        log.info("Запрашиваем список файлов с лимитом {}", limit);
        return new ResponseEntity<>(fileService.getAllFiles(limit), HttpStatus.OK);
    }
}