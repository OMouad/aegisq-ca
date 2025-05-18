package io.aegisq.repository;

import io.aegisq.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class CertificateRepositoryIT {

    @Container
    static PostgreSQLContainer<?> pg =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("pki")
                    .withUsername("pki")
                    .withPassword("pki");

    @DynamicPropertySource
    static void dbProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }

    @Autowired
    CertificateRepository repo;

    @Test
    void saveAndLoadCertificate() {
        Certificate cert = new Certificate();
        cert.setSerial(42L);
        cert.setSubjectDn("CN=Alice,O=AegisQ");
        cert.setNotBefore(OffsetDateTime.now());
        cert.setNotAfter(OffsetDateTime.now().plusYears(1));
        cert.setPem("-----BEGIN CERTIFICATE----- FAKE");
        cert.setKid("kid123");

        repo.save(cert);

        var fetched = repo.findById(42L);
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getSubjectDn()).isEqualTo("CN=Alice,O=AegisQ");
    }
}
