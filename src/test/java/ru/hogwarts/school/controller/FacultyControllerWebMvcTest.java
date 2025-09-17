package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyService facultyService;

    private final Faculty testFaculty = new Faculty();

    {
        testFaculty.setId(1L);
        testFaculty.setName("TestFaculty");
        testFaculty.setColor("TestColor");
    }

    @Test
    void getFacultyInfoTest() throws Exception {
        Mockito.when(facultyService.findFaculty(1L)).thenReturn(testFaculty);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestFaculty"))
                .andExpect(jsonPath("$.color").value("TestColor"));
    }

    @Test
    void createFacultyTest() throws Exception {
        Mockito.when(facultyService.addFaculty(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestFaculty"));
    }

    @Test
    void editFacultyTest() throws Exception {
        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setId(1L);
        updatedFaculty.setName("TestFaculty2");
        updatedFaculty.setColor("TestColor2");

        Mockito.when(facultyService.editFaculty(1L,any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestFaculty2"))
                .andExpect(jsonPath("$.color").value("TestColor2"));
    }

    @Test
    void deleteFacultyTest() throws Exception {
        Mockito.doNothing().when(facultyService).deleteFaculty(1L);

        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getFacultiesByColorTest() throws Exception {
        Mockito.when(facultyService.getFacultyByColor("TestColor")).thenReturn(List.of(testFaculty));

        mockMvc.perform(get("/faculty/color/TestColor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].color").value("TestColor"));
    }

    @Test
    void getFacultiesByWrongColor() throws Exception {
        Mockito.when(facultyService.getFacultyByColor("WrongColor")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/faculty/color/WrongColor"))
                .andExpect(status().isNotFound());
    }

}