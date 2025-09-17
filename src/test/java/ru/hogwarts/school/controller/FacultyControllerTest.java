package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url;
    private Faculty testFaculty;

    @BeforeEach
    void prepare() {
        url = "http://localhost:" + port + "/faculty";
        testFaculty = new Faculty();
        testFaculty.setName("TestFaculty");
        testFaculty.setColor("TestColor");
    }

    @Test
    void getFacultyInfoTest() {

        Faculty createdFaculty = restTemplate.postForObject(url, testFaculty, Faculty.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(url + "/" + createdFaculty.getId(),Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdFaculty.getId(), response.getBody().getId());
        assertEquals("TestFaculty", response.getBody().getName());
    }

    @Test
    void createFacultyTest() {
        ResponseEntity<Faculty> response = restTemplate.postForEntity(url,testFaculty,Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("TestFaculty", response.getBody().getName());
        assertEquals("TestColor", response.getBody().getColor());
    }

    @Test
    void editFacultyTest() {

        Faculty createdFaculty = restTemplate.postForObject(url, testFaculty, Faculty.class);

        createdFaculty.setName("TestFaculty2");
        createdFaculty.setColor("TestColor2");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> request = new HttpEntity<>(createdFaculty, headers);

        ResponseEntity<Faculty> response = restTemplate.exchange(url,HttpMethod.PUT,request,Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdFaculty.getName(), response.getBody().getName());
        assertEquals(createdFaculty.getColor(), response.getBody().getColor());
    }

    @Test
    void deleteFacultyTest() {

        Faculty createdFaculty = restTemplate.postForObject(url, testFaculty, Faculty.class);

        restTemplate.delete(url + "/" + createdFaculty.getId());

        ResponseEntity<Faculty> response = restTemplate.getForEntity(url + "/" + createdFaculty.getId(),Faculty.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getFacultiesByColorTest() {

        restTemplate.postForObject(url, testFaculty, Faculty.class);

        ResponseEntity<Collection> response = restTemplate.getForEntity(url + "/color/TestColor",Collection.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getFacultiesByWrongName() {

        ResponseEntity<Collection> response = restTemplate.getForEntity(url + "/name/234",Collection.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
