package com.medaltracker.olympic.integration_test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.medaltracker.olympic.repository.AthleteRepository;
import com.medaltracker.olympic.repository.MedailleRepository;
import com.medaltracker.olympic.repository.PaysRepository;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class PaysControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private MedailleRepository medailleRepository;

    @BeforeEach
    void setup() {
        medailleRepository.deleteAllInBatch();
        athleteRepository.deleteAllInBatch();
        paysRepository.deleteAllInBatch();
    }

    //  Test GET all
    @Test
    void shouldGetAllPays() throws Exception {
        mockMvc.perform(get("/api/v1/pays"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // Test POST (create)
    @Test
    void shouldCreatePays() throws Exception {
        String json = """
            {
                "nom": "Senegal",
                "code": "SEN",
                "drapeau": "senegal.png"
            }
            """;

        mockMvc.perform(post("/api/v1/pays")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Senegal"))
                .andExpect(jsonPath("$.code").value("SEN"));
    }

    // Test GET by ID
    @Test
    void shouldGetPaysById() throws Exception {
        // First create a pays
        String json = """
            {
                "nom": "Senegal",
                "code": "SEN",
                "drapeau": "senegal.png"
            }
            """;

        String response = mockMvc.perform(post("/api/v1/pays")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract ID (simple way)
        Long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mockMvc.perform(get("/api/v1/pays/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Senegal"));
    }

    // Test UPDATE
    @Test
    void shouldUpdatePays() throws Exception {
        // Create pays
        String json = """
            {
                "nom": "Italy",
                "code": "ITA",
                "drapeau": "italy.png"
            }
            """;

        String response = mockMvc.perform(post("/api/v1/pays")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        String updatedJson = """
            {
                "nom": "Italia",
                "code": "ITA",
                "drapeau": "italia.png"
            }
            """;

        mockMvc.perform(put("/api/v1/pays/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Italia"));
    }

    //  Test DELETE
    @Test
    void shouldDeletePays() throws Exception {
        String json = """
            {
                "nom": "Germany",
                "code": "GER",
                "drapeau": "germany.png",
                "athletes": [],
                "medailles": []
            }
        """;

        var result = mockMvc.perform(post("/api/v1/pays")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isCreated()) 
        .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println(response);


        Long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mockMvc.perform(delete("/api/v1/pays/" + id))
                .andExpect(status().isNoContent());
    }
}
