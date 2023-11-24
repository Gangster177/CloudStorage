package ru.diploma.cloudstor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    private static final int DATABASE_PORT = 5432;
    private static final String DATABASE_NAME = "postgres";
    private static final String DATABASE_USERNAME = "admin";
    private static final String DATABASE_PASSWORD = "admin1";
    private static final Network CLOUD_NETWORK = Network.newNetwork();

    @Container
    public static PostgreSQLContainer<?> databaseContainer = new PostgreSQLContainer<>("postgres")
            .withNetwork(CLOUD_NETWORK)
            .withExposedPorts(DATABASE_PORT)
            .withDatabaseName(DATABASE_NAME)
            .withUsername(DATABASE_USERNAME)
            .withPassword(DATABASE_PASSWORD);

    @Test
    void contextLoads() {
        System.out.printf(" DataBase Url: %s\n DatabaseName: %s\n Username(): %s\n Password() %s\n Network ID: %s\n Port:  %s\n"
                , databaseContainer.getJdbcUrl()
                , databaseContainer.getDatabaseName()
                , databaseContainer.getUsername()
                , databaseContainer.getPassword()
                , CLOUD_NETWORK.getId()
                , databaseContainer.getMappedPort(DATABASE_PORT)
        );
        Assertions.assertTrue(databaseContainer.isRunning());
    }
}
