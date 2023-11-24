package ru.diploma.cloudstor;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import ru.diploma.cloudstor.entity.CloudFile;
import ru.diploma.cloudstor.entity.Role;
import ru.diploma.cloudstor.entity.User;
import ru.diploma.cloudstor.model.EnumRoles;
import ru.diploma.cloudstor.web.request.AuthRequest;
import ru.diploma.cloudstor.web.response.AuthResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataTest {
    public static final String USERNAME_1 = "admin@gmail.com";
    public static final String USERNAME_2 = "user@gmail.com";
    public static final String USER_UNAUTHORIZED = "unauthorized@gmail.com";
    public static final String PASSWORD = "100";
    public static final Long USER_ID = 1L;
    public static final String BEARER_TOKEN = "Bearer Token";
    public static final String FILENAME_1 = "file";
    public static final String FILENAME_2 = "file2";
    public static final String FILENAME_NEW = "file_new";
    public static final String TOKEN_1 = "token1";
    public static final String TOKEN_2 = "token2";
    public static final Set<Role> ROLE_SET = new HashSet<>(List.of(new Role(EnumRoles.ROLE_ADMIN)));
    public static final UserDetails USER_DETAILS = new User(USER_ID, USERNAME_1, PASSWORD, ROLE_SET);
    public static final AuthResponse RESPONSE = new AuthResponse(TOKEN_1);
    public static final AuthRequest REQUEST = new AuthRequest(USERNAME_1, PASSWORD);
    public static final MockMultipartFile MULTIPART_FILE = new MockMultipartFile(
            FILENAME_1,
            FILENAME_1.getBytes()
    );
    public static CloudFile TEST_FILE_1 = null;
    public static CloudFile TEST_FILE_2 = null;

    static {
        try {
            TEST_FILE_1 = new CloudFile(
                    FILENAME_1,
                    LocalDateTime.now(),
                    MULTIPART_FILE.getContentType(),
                    MULTIPART_FILE.getBytes(),
                    MULTIPART_FILE.getSize(),
                    USER_ID
            );
            TEST_FILE_2 = new CloudFile(FILENAME_2,
                    LocalDateTime.now(),
                    MULTIPART_FILE.getContentType(),
                    MULTIPART_FILE.getBytes(),
                    MULTIPART_FILE.getSize(),
                    USER_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CloudFile FILE_EDIT_NAME = new CloudFile(FILENAME_NEW,
            TEST_FILE_1.getDate(),
            TEST_FILE_1.getType(),
            TEST_FILE_1.getFileData(),
            TEST_FILE_1.getSize(),
            TEST_FILE_1.getUserId()
    );

    public static final List<CloudFile> CLOUD_FILES = List.of(
            TEST_FILE_1,
            TEST_FILE_2
    );
}
