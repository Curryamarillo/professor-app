package com.professor.app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "users")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Tutor extends User{

    private List<String> tutoredStudentsId;

    ///  method to add student courseId
    public void addCourseId(String studentId) {
        if (!tutoredStudentsId.contains(studentId)) {
            tutoredStudentsId.add(studentId);
        }
    }
    /// method to add students list to tutored
    public void addStudentList(List<String> studentListId) {

        if (tutoredStudentsId == null) {
            tutoredStudentsId = new ArrayList<>();
        }
        for (String studentId : studentListId) {
            if (!tutoredStudentsId.contains(studentId)) {
                tutoredStudentsId.add(studentId);
            }
        }
    }
}
