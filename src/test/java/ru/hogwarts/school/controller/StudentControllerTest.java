package ru.hogwarts.school.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Student;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;
    private String url;
    private Student testStudent;
    private final String testName = "Germiona";
    private final int testAge = 15;
    private final String testNameNew = "Germiona2";
    private final int testAgeNew = 17;

    @BeforeEach
    void prepare() {
        url = "http://localhost:" + port + "/student";
        testStudent = new Student();
        testStudent.setName(testName);
        testStudent.setAge(testAge);
    }

    @Test
    void GetStudentInfoTest() {

        Student createdStudent = restTemplate.postForObject(url, testStudent, Student.class);
        ResponseEntity<Student> response = restTemplate.getForEntity(url + "/" + createdStudent.getId(),Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdStudent.getId(), response.getBody().getId());
        assertEquals(testName, response.getBody().getName());
        assertEquals(testAge, response.getBody().getAge());
    }

    @Test
    void createStudentTest() {
        ResponseEntity<Student> response = restTemplate.postForEntity(url, testStudent, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(testName, response.getBody().getName());
        assertEquals(testAge, response.getBody().getAge());
    }

    @Test
    void editStudentTest() {

        Student createdStudent = restTemplate.postForObject(url, testStudent, Student.class);
        createdStudent.setName(testNameNew);
        createdStudent.setAge(testAgeNew);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Student> request = new HttpEntity<>(createdStudent, headers);

        ResponseEntity<Student> response = restTemplate.exchange(url,HttpMethod.PUT,request,Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testNameNew, response.getBody().getName());
        assertEquals(testAgeNew, response.getBody().getAge());
    }

    @Test
    void deleteStudentTest() {

        Student createdStudent = restTemplate.postForObject(url, testStudent, Student.class);
        restTemplate.delete(url + "/" + createdStudent.getId());
        ResponseEntity<Student> response = restTemplate.getForEntity(url + "/" + createdStudent.getId(),Student.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void findStudentsByAgeBetweenTest() {

        restTemplate.postForObject(url, testStudent, Student.class);

        ResponseEntity<Collection> response = restTemplate.getForEntity(url + "/age/?min=13&max=25", Collection.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

}