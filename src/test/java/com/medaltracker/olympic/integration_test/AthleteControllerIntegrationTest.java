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
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;
import com.medaltracker.olympic.repository.AthleteRepository;
import com.medaltracker.olympic.repository.MedailleRepository;
import com.medaltracker.olympic.repository.PaysRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 
class AthleteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private MedailleRepository medailleRepository;

    @Autowired
    private PaysRepository paysRepository;

    @BeforeEach
    void setup() {
        medailleRepository.deleteAllInBatch();
        athleteRepository.deleteAllInBatch();
        paysRepository.deleteAllInBatch();
    }

    // Helper: create a Pays first
    private Long createPays() throws Exception {
        String json = """
        {
            "nom": "Senegal",
            "code": "SEN",
            "drapeau": "senegal.png",
            "athletes": [],
            "medailles": []
        }
        """;

        String response = mockMvc.perform(post("/api/v1/pays")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    // Test CREATE athlete
    @Test
    void shouldCreateAthlete() throws Exception {

        Long paysId = createPays();

        String json = """
        {
            "nom": "Faye",
            "prenom": "Elise",
            "discipline": "Athletisme",
            "dateNaissance": "2000-01-01",
            "paysId": %d
        }
        """.formatted(paysId);

        mockMvc.perform(post("/api/v1/athletes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Faye"));
    }

    // Test GET all
    @Test
    void shouldGetAllAthletes() throws Exception {
        mockMvc.perform(get("/api/v1/athletes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // Test GET by ID
    @Test
    void shouldGetAthleteById() throws Exception {

        Long paysId = createPays();

        String json = """
        {
            "nom": "Test",
            "prenom": "User",
            "discipline": "Judo",
            "dateNaissance": "1999-01-01",
            "paysId": %d
        }
        """.formatted(paysId);

        String response = mockMvc.perform(post("/api/v1/athletes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long athleteId = ((Number) JsonPath.read(response, "$.id")).longValue();

        mockMvc.perform(get("/api/v1/athletes/" + athleteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Test"));
    }

    // Test GET by pays
    @Test
    void shouldGetAthletesByPays() throws Exception {

        Long paysId = createPays();

        String json = """
        {
            "nom": "Ali",
            "prenom": "Test",
            "discipline": "Boxe",
            "dateNaissance": "1995-01-01",
            "paysId": %d
        }
        """.formatted(paysId);

        mockMvc.perform(post("/api/v1/athletes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/athletes/pays/" + paysId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // Test UPDATE
    @Test
    void shouldUpdateAthlete() throws Exception {

        Long paysId = createPays();

        String json = """
        {
            "nom": "Old",
            "prenom": "Name",
            "discipline": "Football",
            "dateNaissance": "2001-01-01",
            "paysId": %d
        }
        """.formatted(paysId);

        String response = mockMvc.perform(post("/api/v1/athletes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long athleteId = ((Number) JsonPath.read(response, "$.id")).longValue();

        String updatedJson = """
        {
            "nom": "New",
            "prenom": "Name",
            "discipline": "Basket",
            "dateNaissance": "2001-01-01",
            "paysId": %d
        }
        """.formatted(paysId);

        mockMvc.perform(put("/api/v1/athletes/" + athleteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("New"));
    }

    // Test DELETE
    @Test
    void shouldDeleteAthlete() throws Exception {

        Long paysId = createPays();

        String json = """
        {
            "nom": "Delete",
            "prenom": "Me",
            "discipline": "Tennis",
            "dateNaissance": "2002-01-01",
            "paysId": %d
        }
        """.formatted(paysId);

        String response = mockMvc.perform(post("/api/v1/athletes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long athleteId = ((Number) JsonPath.read(response, "$.id")).longValue();

        mockMvc.perform(delete("/api/v1/athletes/" + athleteId))
                .andExpect(status().isNoContent());
    }
}
