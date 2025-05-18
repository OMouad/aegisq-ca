package io.aegisq.repository;

import io.aegisq.domain.Certificate;
import io.aegisq.domain.CertificateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByStatus(CertificateStatus status);

}
