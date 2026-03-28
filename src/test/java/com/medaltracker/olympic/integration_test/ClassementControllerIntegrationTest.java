package com.medaltracker.olympic.integration_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.medaltracker.olympic.entity.Pays;
import com.medaltracker.olympic.repository.AthleteRepository;
import com.medaltracker.olympic.repository.MedailleRepository;
import com.medaltracker.olympic.repository.PaysRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClassementControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private MedailleRepository medailleRepository;

    @Autowired
    private AthleteRepository athleteRepository;

    private Pays pays;

    @BeforeEach
    void setup() {
        medailleRepository.deleteAllInBatch();
        athleteRepository.deleteAllInBatch();
        paysRepository.deleteAllInBatch();
        
        Pays p = new Pays();
        p.setNom("Senegal");
        p.setCode("SEN");
        pays = paysRepository.save(p);
    }

    // Test classement default
    @Test
    void shouldGetClassementDefault() throws Exception {
        mockMvc.perform(get("/api/v1/classement"))
                .andExpect(status().isOk());
    }

    // Test classement tri = or
    @Test
    void shouldGetClassementByOr() throws Exception {
        mockMvc.perform(get("/api/v1/classement")
                .param("tri", "or"))
                .andExpect(status().isOk());
    }

    // Test stats pays
    @Test
    void shouldGetStatsForPays() throws Exception {
        mockMvc.perform(get("/api/v1/classement/pays/" + pays.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.TOTAL").exists());
    }
}