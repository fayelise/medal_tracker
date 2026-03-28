package com.medaltracker.olympic.integration_test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.medaltracker.olympic.repository.CompetitionRepository;
import com.medaltracker.olympic.repository.MedailleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 
class CompetitionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MedailleRepository medailleRepository;

    @BeforeEach
    void setup() {
        medailleRepository.deleteAllInBatch();
        competitionRepository.deleteAllInBatch();
    }

    // GET all
    @Test
    void shouldGetAllCompetitions() throws Exception {
        mockMvc.perform(get("/api/v1/competitions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // CREATE
    @Test
    void shouldCreateCompetition() throws Exception {

        String json = """
        {
            "nom": "JO 2024",
            "discipline": "Athletisme",
            "dateDebut": "2024-07-01",
            "dateFin": "2024-07-30",
            "statut": "EN_COURS"
        }
        """;

        mockMvc.perform(post("/api/v1/competitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("JO 2024"));
    }

    // GET by ID
    @Test
    void shouldGetCompetitionById() throws Exception {

        String json = """
        {
            "nom": "CAN",
            "discipline": "Football",
            "dateDebut": "2023-01-01",
            "dateFin": "2023-02-01",
            "statut": "TERMINEE"
        }
        """;

        String response = mockMvc.perform(post("/api/v1/competitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mockMvc.perform(get("/api/v1/competitions/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("CAN"));
    }

    // UPDATE
    @Test
    void shouldUpdateCompetition() throws Exception {

        String json = """
        {
            "nom": "Old Comp",
            "discipline": "Basket",
            "dateDebut": "2022-01-01",
            "dateFin": "2022-02-01",
            "statut": "EN_COURS"
                }
        """;

        String response = mockMvc.perform(post("/api/v1/competitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        String updatedJson = """
        {
            "nom": "New Comp",
            "discipline": "Basket",
            "dateDebut": "2022-01-01",
            "dateFin": "2022-02-01",
            "statut": "EN_COURS"
        }
        """;

        mockMvc.perform(put("/api/v1/competitions/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("New Comp"));
    }

    // DELETE
    @Test
    void shouldDeleteCompetition() throws Exception {

        String json = """
        {
            "nom": "Delete Comp",
            "discipline": "Tennis",
            "dateDebut": "2025-01-01",
            "dateFin": "2025-02-01",
            "statut": "EN_COURS"
        }
        """;

        String response = mockMvc.perform(post("/api/v1/competitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = ((Number) JsonPath.read(response, "$.id")).longValue();

        mockMvc.perform(delete("/api/v1/competitions/" + id))
                .andExpect(status().isNoContent());
    }
}
