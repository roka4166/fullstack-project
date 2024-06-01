package com.roman;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
public class TestcontainersTest extends AbstractTestContainers {
    @Test
    void canStartPostgresDB() {
        assertThat(container.isCreated()).isTrue();
        assertThat(container.isRunning()).isTrue();
    }
    
    @Test
    void flywayTest(){
        Flyway flyway = Flyway.configure().
                dataSource(container.getJdbcUrl(), container.getUsername(), container.getPassword()).load();
        flyway.migrate();
    }
}
