package ru.diploma.cloudstor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.diploma.cloudstor.entity.CloudFile;
import ru.diploma.cloudstor.entity.User;
import ru.diploma.cloudstor.repository.AuthenticationRepository;
import ru.diploma.cloudstor.repository.FileRepository;
import ru.diploma.cloudstor.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.diploma.cloudstor.DataTest.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("=== Testing File service ===")
public class FileServiceTest {
    @Spy
    @InjectMocks
    FileService fileService;
    @Mock
    FileRepository fileRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    AuthenticationRepository authenticationRepository;

    @BeforeEach
    void setUp() {
        when(fileService.getUserIdFromToken(BEARER_TOKEN)).thenReturn(Optional.of(USER_ID));
    }

    @Test
    void uploadFileTest() {
        fileService.uploadFile(BEARER_TOKEN, FILENAME_1, MULTIPART_FILE);
        when(fileRepository.findByUserIdAndFilename(USER_ID, FILENAME_1)).thenReturn(TEST_FILE_1);
        CloudFile result = fileRepository.findByUserIdAndFilename(USER_ID, FILENAME_1);
        assertEquals(TEST_FILE_1, result);
    }

    @Test
    void deleteFileTest() {
        fileService.deleteFile(BEARER_TOKEN, FILENAME_1);
        CloudFile result = fileRepository.findByUserIdAndFilename(USER_ID, FILENAME_1);
        verify(fileRepository, Mockito.times(1)).deleteByUserIdAndFilename(USER_ID, FILENAME_1);
        assertNull(result);
    }

    @Test
    void downloadFileTest() {
        when(fileRepository.findByUserIdAndFilename(USER_ID, FILENAME_1)).thenReturn(TEST_FILE_1);
        CloudFile result = fileService.downloadFile(BEARER_TOKEN, FILENAME_1);
        assertEquals(TEST_FILE_1, result);
    }

    @Test
    void editFileTest() {
        when(fileRepository.findByUserIdAndFilename(USER_ID, FILENAME_NEW)).thenReturn(FILE_EDIT_NAME);
        fileRepository.updateFileNameByUserId(USER_ID, FILENAME_1, FILENAME_NEW);
        fileService.editFile(BEARER_TOKEN, FILENAME_1, FILENAME_NEW);
        CloudFile result = fileRepository.findByUserIdAndFilename(USER_ID, FILENAME_NEW);
        assertEquals(FILENAME_NEW, result.getFilename());
    }

    @Test
    void getAllFilesTest() {
        Map<String, Long> expectedList = Map.of(
                FILENAME_1, TEST_FILE_1.getSize(),
                FILENAME_2, TEST_FILE_2.getSize());
        when(fileRepository.findAllByUserIdWithLimit(USER_ID, CLOUD_FILES.size())).thenReturn(CLOUD_FILES);
        Map<String, Long> resultList = fileService.getAllFiles(BEARER_TOKEN, expectedList.size());
        assertEquals(expectedList, resultList);
    }

    @Test
    void getUserIdFromTokenTest() {
        Optional<User> user = Optional.of((User) USER_DETAILS);
        Optional<Long> result = fileService.getUserIdFromToken(BEARER_TOKEN);
        assertEquals(ofNullable(user.get().getId()), result);
    }
}
