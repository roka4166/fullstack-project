package com.roman;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
public class TestcontainersTest extends AbstractTestContainersUnitTest{
    @Test
    void canStartPostgresDB() {
        assertThat(container.isCreated()).isTrue();
        assertThat(container.isRunning()).isTrue();
    }
    @Test
    void flywayTest(){
        Flyway flyway = Flyway.configure().dataSource(container.getJdbcUrl(), container.getUsername(), container.getPassword()).load();
        flyway.migrate();
    }
}
