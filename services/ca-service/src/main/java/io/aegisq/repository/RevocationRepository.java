package io.aegisq.repository;

import io.aegisq.domain.RevocationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevocationRepository
        extends JpaRepository<RevocationEvent, Long> { }
