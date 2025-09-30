package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;


import java.util.Collection;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
   @Autowired
   private StudentRepository studentRepository;
   private FacultyRepository facultyRepository;
    private final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.info("Was invoked method for find student");
        return studentRepository.findById(id).get();
    }

    public Student editStudent(long id, Student student) {
        logger.info("Was invoked method for edit student");
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method for delete student");
        studentRepository.deleteById(id);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.info("Was invoked method for find student for students in the age range");
        return studentRepository.findByAgeBetween(min, max);
    }

    public List<Student> findByFacultyId(Long Id) {
        logger.info("Was invoked method for find student by faculty id");
        return studentRepository.findByFacultyId(Id);
    }

    public int getCountStudents() {
        logger.info("The method of counting the number of students was invoked");
        return studentRepository.getCountStudents();
    }

    public int getAverageAgeStudents() {
        logger.info("The method of searching for the average age of students was called");
        return studentRepository.getAverageAgeStudents();
    }

    public List<Student> getLastFiveStudents() {
        logger.info("The method of deducing the last five students was called");
        return studentRepository.getLastFiveStudents();
    }

    public List<String> getSortedStudentNamesStartingWithA() {

        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .filter(name -> {
                    String upperName = name.toUpperCase();
                    return upperName.startsWith("A") || upperName.startsWith("А");
                })
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .map(String::toUpperCase)
                .toList();
    }

    public Double getAverageAgeWithStream() {

        List<Student> allStudents = studentRepository.findAll();

        if (allStudents.isEmpty()) {
            return 0.0;
        }

        double averageAge = allStudents.stream()
                .filter(student -> student.getAge() > 0)
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);

        return averageAge;
    }
}