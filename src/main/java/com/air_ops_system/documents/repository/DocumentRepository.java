package com.air_ops_system.documents.repository;

import com.air_ops_system.documents.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
}
