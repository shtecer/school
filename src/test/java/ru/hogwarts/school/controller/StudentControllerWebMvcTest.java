package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    private final Student testStudent = new Student();

    {
        testStudent.setId(22L);
        testStudent.setName("Germiona");
        testStudent.setAge(15);
    }

    @Test
    void getStudentInfoTest() throws Exception {
        Mockito.when(studentService.findStudent(22L)).thenReturn(testStudent);

        mockMvc.perform(get("/student/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(22))
                .andExpect(jsonPath("$.name").value("Germiona"))
                .andExpect(jsonPath("$.age").value(15));
    }

    @Test
    void createStudentTest() throws Exception {
        Mockito.when(studentService.addStudent(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(22))
                .andExpect(jsonPath("$.name").value("Germiona"));
    }

    @Test
    void editStudentTest() throws Exception {
        Student updatedStudent = new Student();
        updatedStudent.setId(33L);
        updatedStudent.setName("Germiona2");
        updatedStudent.setAge(17);

        Mockito.when(studentService.editStudent(22L,any(Student.class))).thenReturn(updatedStudent);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Germiona2"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void deleteStudentTest() throws Exception {
        Mockito.doNothing().when(studentService).deleteStudent(22L);

        mockMvc.perform(delete("/student/2"))
                .andExpect(status().isOk());
    }

    @Test
    void findStudentsByAgeBetweenTest() throws Exception {
        Mockito.when(studentService.findByAgeBetween(10, 55)).thenReturn(List.of(testStudent));

        mockMvc.perform(get("/student/age/")
                        .param("min", "10")
                        .param("max", "55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].age").value(15));
    }

    @Test
    void testGetStudentsByAgeRangeNotFound() throws Exception {
        Mockito.when(studentService.findByAgeBetween(100, 200)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/student/age/")
                        .param("min", "100")
                        .param("max", "200"))
                .andExpect(status().isNotFound());
    }

}