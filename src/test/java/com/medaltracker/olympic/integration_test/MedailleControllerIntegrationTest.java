package com.medaltracker.olympic.integration_test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medaltracker.olympic.entity.Athlete;
import com.medaltracker.olympic.entity.Competition;
import com.medaltracker.olympic.entity.Pays;
import com.medaltracker.olympic.entity.enums.StatutCompetition;
import com.medaltracker.olympic.entity.enums.TypeMedaille;
import com.medaltracker.olympic.repository.AthleteRepository;
import com.medaltracker.olympic.repository.CompetitionRepository;
import com.medaltracker.olympic.repository.MedailleRepository;
import com.medaltracker.olympic.repository.PaysRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MedailleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MedailleRepository medailleRepository;

    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Athlete athlete;
    private Competition competition;

    @BeforeEach
    void setup() {
        medailleRepository.deleteAllInBatch();
        athleteRepository.deleteAllInBatch();
        competitionRepository.deleteAllInBatch();
        paysRepository.deleteAllInBatch();

        Pays p = new Pays();
        p.setNom("Jamaica");
        p.setCode("JAM");
        p = paysRepository.save(p);

        Athlete a = new Athlete();
        a.setNom("Usain Bolt");
        a.setDiscipline("Athletisme");
        a.setPays(p);
        athlete = athleteRepository.save(a);

        Competition c = new Competition();
        c.setNom("100m");
        c.setDiscipline("Athletisme");
        c.setStatut(StatutCompetition.TERMINEE);
        c.setDateDebut(LocalDate.of(2024, 6, 1));
        c.setDateFin(LocalDate.of(2024, 6, 30));
        competition = competitionRepository.save(c);
    }

    // Test GET all
    @Test
    void shouldGetAllMedailles() throws Exception {
        mockMvc.perform(get("/api/v1/medailles"))
                .andExpect(status().isOk());
    }

    // Test POST attribuer
    @Test
    void shouldAttribuerMedaille() throws Exception {
        String json = """
        {
            "athleteId": %d,
            "competitionId": %d,
            "type": "OR",
            "dateObtention": "2024-07-01"
        }
        """.formatted(athlete.getId(), competition.getId());

        mockMvc.perform(post("/api/v1/medailles/attribuer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("OR"));
    }

    // Test GET by id
    @Test
    void shouldGetMedailleById() throws Exception {
        String json = """
        {
            "athleteId": %d,
            "competitionId": %d,
            "type": "ARGENT",
            "dateObtention": "2024-07-01"
        }
        """.formatted(athlete.getId(), competition.getId());

        String response = mockMvc.perform(post("/api/v1/medailles/attribuer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long medailleId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/v1/medailles/" + medailleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("ARGENT"));
    }

    // Test GET by athlete
    @Test
    void shouldGetByAthlete() throws Exception {
        String json = """
        {
            "athleteId": %d,
            "competitionId": %d,
            "type": "BRONZE",
            "dateObtention": "2024-07-01"
        }
        """.formatted(athlete.getId(), competition.getId());

        mockMvc.perform(post("/api/v1/medailles/attribuer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/medailles/athlete/" + athlete.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("BRONZE"));
    }

    // Test GET by competition
    @Test
    void shouldGetByCompetition() throws Exception {
        String json = """
        {
            "athleteId": %d,
            "competitionId": %d,
            "type": "OR",
            "dateObtention": "2024-07-01"
        }
        """.formatted(athlete.getId(), competition.getId());

        mockMvc.perform(post("/api/v1/medailles/attribuer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/medailles/competition/" + competition.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("OR"));
    }
}
