package ru.diploma.cloudstor.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.diploma.cloudstor.entity.CloudFile;
import ru.diploma.cloudstor.entity.User;
import ru.diploma.cloudstor.exception.FileCloudException;
import ru.diploma.cloudstor.exception.InputDataException;
import ru.diploma.cloudstor.exception.UnauthorizedException;
import ru.diploma.cloudstor.mapper.FileMapper;
import ru.diploma.cloudstor.repository.AuthenticationRepository;
import ru.diploma.cloudstor.repository.FileRepository;
import ru.diploma.cloudstor.repository.UserRepository;
import ru.diploma.cloudstor.web.response.FileWebResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final AuthenticationRepository authenticationRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;


    @Transactional
    public void uploadFile(String authToken, String filename, MultipartFile file) {
        final Optional<Long> userId = getUserIdFromToken(authToken);
        if (!userId.isPresent()) {
            log.error("Invalid auth-token: Unauthorized");
            throw new UnauthorizedException("Invalid auth-token: Unauthorized");
        }
        final CloudFile fileFromBD = fileRepository.findByUserIdAndFilename(userId.get(), filename);
        if (fileFromBD != null) {
            log.error(String.format(" The file with name %s already exists in the storage", filename));
            throw new InputDataException(String.format(" The file with name %s already exists in the storage. " +
                    "Please enter a new name for the file ", filename));
        }

        try {
            CloudFile cloudFile = new CloudFile(filename, LocalDateTime.now(), file.getContentType(), file.getBytes(), file.getSize(), userId.get());
            fileRepository.save(cloudFile);
            log.info("Success upload file. User with ID {}", userId.get());
        } catch (IOException e) {
            log.error("InputDataException: Upload file: Input data exception");
            throw new InputDataException("Upload file: Input data exception");
        }
    }

    @Transactional
    public void deleteFile(String authToken, String filename) {
        final Optional<Long> userId = getUserIdFromToken(authToken);
        if (!userId.isPresent()) {
            log.error("Invalid auth-token: Unauthorized");
            throw new UnauthorizedException("Invalid auth-token: Unauthorized");
        }

        fileRepository.deleteByUserIdAndFilename(userId.get(), filename);
        final CloudFile deletedFile = fileRepository.findByUserIdAndFilename(userId.get(), filename);
        if (deletedFile != null) {
            log.error("FileCloudException : The file has not been deleted");
            throw new FileCloudException("The file has not been deleted");
        }
        log.info("Success delete file. User with ID {}", userId.get());
    }

    @Transactional
    public CloudFile downloadFile(String authToken, String filename) {
        final Optional<Long> userId = getUserIdFromToken(authToken);
        if (!userId.isPresent()) {
            log.error("Invalid auth-token: Unauthorized");
            throw new UnauthorizedException("Invalid auth-token: Unauthorized");
        }
        final CloudFile cloudFile = fileRepository.findByUserIdAndFilename(userId.get(), filename);
        if (cloudFile == null) {
            log.error(String.format("FileCloudException: The file with name %s was not found in the storage", filename));
            throw new FileCloudException(String.format("The file with name %s was not found in the storage", filename));
        }
        log.info("File downloaded successfully. User with ID {}", userId.get());
        return cloudFile;
    }

    @Transactional
    public void editFile(String authToken, String filename, String newFileName) {
        final Optional<Long> userId = getUserIdFromToken(authToken);
        if (userId.isEmpty()) {
            log.error("Invalid auth-token: Unauthorized");
            throw new UnauthorizedException("Invalid auth-token: Unauthorized");
        }

        fileRepository.updateFileNameByUserId(userId.get(), filename, newFileName);

        final CloudFile editedCloudFile = fileRepository.findByUserIdAndFilename(userId.get(), newFileName);
        if (editedCloudFile == null) {
            log.error("FileCloudException : Edited file not found ");
            throw new FileCloudException("Edited file not found ");
        } else {
            log.info(String.format("File %s edited successfully. New filename: %s", filename, editedCloudFile.getFilename()));
        }
    }


    @Transactional
    public List<FileWebResponse> getAllFiles(String authToken, int limit) {
        final Optional<Long> userId = getUserIdFromToken(authToken);
        if (!userId.isPresent()) {
            log.error("Invalid auth-token: Unauthorized");
            throw new UnauthorizedException("Invalid auth-token: Unauthorized");
        }
        List<CloudFile> files = fileRepository.findAllByUserIdWithLimit(userId.get(), limit);
        if (files == null) {
            log.error("FileCloudException: List of files not received ");
            throw new FileCloudException("List of files not received ");
        }
        return files.stream()
                .map(fileMapper::cloudFileToFileWebResponse)
                .sorted(Comparator.comparing(FileWebResponse::getFilename))
                .collect(Collectors.toList());
    }

    public Optional<Long> getUserIdFromToken(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            final String authTokenWithoutBearer = authToken.split(" ")[1];
            final String username = authenticationRepository.getUsernameByToken(authTokenWithoutBearer);
            Optional<User> user = userRepository.findByUsername(username);
            return user.isPresent() ? ofNullable(user.get().getId()) : Optional.empty();
        }
        return null;
    }
}
