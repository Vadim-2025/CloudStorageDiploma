package ru.netology.cloudstoragediploma.service;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstoragediploma.dto.FileDTO;
import ru.netology.cloudstoragediploma.entity.File;
import ru.netology.cloudstoragediploma.entity.User;
import ru.netology.cloudstoragediploma.exception.FileNotFoundException;
import ru.netology.cloudstoragediploma.exception.InvalidInputDataException;
import ru.netology.cloudstoragediploma.exception.UserNotFoundException;
import ru.netology.cloudstoragediploma.repository.FileRepository;
import ru.netology.cloudstoragediploma.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileService {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public void uploadFile(@NonNull MultipartFile file, String fileName) {
        if (file.isEmpty()) {
            throw new FileNotFoundException("Файл пуст", 0);
        }
        log.info("Загрузка файла с именем {}", fileName);

        Long userId = getAuthorizedUser().getId();

        if (fileRepository.findFileByUserIdAndFileName(userId, fileName).isPresent()) {
            throw new InvalidInputDataException("Имя файла уже занято. Укажите другое имя", userId);
        }

        String hash = getHashOfFile(file);
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            throw new InvalidInputDataException("Ошибка чтения файла", userId);
        }

        fileRepository.save(File.builder()
                .hash(hash)
                .fileName(fileName)
                .type(file.getContentType())
                .size(file.getSize())
                .fileByte(fileBytes)
                .createdDate(LocalDateTime.now())
                .user(User.builder().id(userId).build())
                .build());
    }

    public FileDTO downloadFile(String fileName) {
        Long userId = getAuthorizedUser().getId();
        File file = getFileFromStorage(fileName, userId);

        return FileDTO.builder()
                .fileName(file.getFileName())
                .type(file.getType())
                .fileByte(file.getFileByte())
                .build();
    }

    public void editFileName(String oldFilename, FileDTO fileDTO) {
        Long userId = getAuthorizedUser().getId();
        File file = getFileFromStorage(oldFilename, userId);
        file.setFileName(fileDTO.getFileName());
        fileRepository.save(file);
    }

    public void deleteFile(String fileName) {
        Long userId = getAuthorizedUser().getId();
        File file = getFileFromStorage(fileName, userId);
        file.setDelete(true);
        file.setUpdatedDate(LocalDateTime.now());
        fileRepository.save(file);
    }

    public List<FileDTO> getAllFiles(int limit) {
        Long userId = getAuthorizedUser().getId();
        List<File> files = fileRepository.findFilesByUserIdWithLimit(userId, limit);
        return files.stream()
                .filter(file -> !file.isDelete())
                .map(file -> FileDTO.builder()
                        .fileName(file.getFileName())
                        .type(file.getType())
                        .date(file.getCreatedDate())
                        .size(file.getSize())
                        .build())
                .collect(Collectors.toList());
    }

    private String getHashOfFile(MultipartFile file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (InputStream fis = file.getInputStream()) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    md.update(buffer, 0, read);
                }
            }
            StringBuilder result = new StringBuilder();
            for (byte b : md.digest()) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException | IOException ex) {
            throw new InvalidInputDataException("Ошибка при обработке файла", 0);
        }
    }

    private User getAuthorizedUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findUserByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден", 0));
    }

    private File getFileFromStorage(String fileName, Long userId) {
        Optional<File> fileOpt = fileRepository.findFileByUserIdAndFileName(userId, fileName);
        if (!fileOpt.isPresent()) {
            throw new FileNotFoundException("Файл с указанным именем не найден", userId);
        }
        return fileOpt.get();
    }
}