package com.professor.app.repositories;

import com.professor.app.entities.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    boolean existsByCode(String code);

    Optional<Course> findByCode(String code);
}
