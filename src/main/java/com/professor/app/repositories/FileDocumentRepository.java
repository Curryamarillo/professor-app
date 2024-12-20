package com.professor.app.repositories;

import com.professor.app.entities.FileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileDocumentRepository extends MongoRepository<FileDocument, String> {

    List<FileDocument> findByUserId(String userId);

    Optional<FileDocument> findByFileName(String name);
}
