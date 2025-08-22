package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentService {
    Student addStudent(Student student);
    Student findStudent(long id);
    Student editStudent(long id, Student student);
    void deleteStudent(long id);
    Collection<Student> findByAgeBetween(int min, int max);
    List<Student> findByFacultyId(Long Id);
}
