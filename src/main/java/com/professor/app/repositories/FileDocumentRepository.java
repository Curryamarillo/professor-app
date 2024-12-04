package com.professor.app.repositories;

import com.professor.app.entities.FileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileDocumentRepository extends MongoRepository<FileDocument, String> {

    Optional<FileDocument> findByUserId(String userId);

    Optional<FileDocument> findByName(String name);
}
