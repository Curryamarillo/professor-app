package com.professor.app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Assistant extends User{

    private List<String> courseId;

    private List<String> duties;

    /// method to add duties
    public void addDuty(String duty) {
        if (!duties.contains(duty)) {
            duties.add(duty);
        }
    }
    /// method to remove duty
    public void removeDuty(String duty) {
        duties.remove(duty);
    }
    /// method to update duty
    public void updateDuties(List<String> newDuties) {
        duties.clear();
        duties.addAll(newDuties);
    }
    /// method to add courses
    public void addCourseId(String newCourseId) {
        if (!courseId.contains(newCourseId)) {
            courseId.add(newCourseId);
        }
    }
    ///  method to remove course
    public void removeCourseId(String newCourseId) {
        courseId.remove(newCourseId);
    }
    /// method to update course
    public void updateCourseId(List<String> newCourseId) {
        courseId.clear();
        courseId.addAll(newCourseId);
    }
}
