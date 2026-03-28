package com.medaltracker.olympic.unit_test;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.medaltracker.olympic.dto.PaysDTO;
import com.medaltracker.olympic.entity.Pays;
import com.medaltracker.olympic.exception.ResourceNotFoundException;
import com.medaltracker.olympic.repository.PaysRepository;
import com.medaltracker.olympic.service.PaysService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaysServiceTest {

    @Mock
    private PaysRepository paysRepository;

    @InjectMocks
    private PaysService paysService;

    private Pays pays;

    @BeforeEach
    void setUp() {
        pays = new Pays();
        pays.setId(1L);
        pays.setNom("Senegal");
        pays.setCode("SEN");
        pays.setDrapeau("senegal.png");
    }

    // =========================
    // getAll
    // =========================
    @Test
    void shouldReturnAllPays() {
        when(paysRepository.findAll()).thenReturn(List.of(pays));

        List<Pays> result = paysService.getAll();

        assertEquals(1, result.size());
        assertEquals("Senegal", result.get(0).getNom());
        verify(paysRepository, times(1)).findAll();
    }

    // =========================
    // getById 
    // =========================
    @Test
    void shouldReturnPaysById() {
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));

        Pays result = paysService.getById(1L);

        assertNotNull(result);
        assertEquals("SEN", result.getCode());
    }

    // =========================
    // getById - NOT FOUND
    // =========================
    @Test
    void shouldThrowExceptionWhenPaysNotFound() {
        when(paysRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            paysService.getById(1L);
        });
    }

    // =========================
    // create
    // =========================
    @Test
    void shouldCreatePays() {
        PaysDTO dto = new PaysDTO("Senegal", "SEN", "senegal.png");
        when(paysRepository.save(any(Pays.class))).thenReturn(pays);

        Pays result = paysService.create(dto);

        assertNotNull(result);
        assertEquals("Senegal", result.getNom());
        verify(paysRepository).save(any(Pays.class));
    }

    // =========================
    // createAll
    // =========================
    @Test
    void shouldCreateAllPays() {
        PaysDTO dto = new PaysDTO("Senegal", "SEN", "senegal.png");
        when(paysRepository.saveAll(anyList())).thenReturn(List.of(pays));

        List<Pays> result = paysService.createAll(List.of(dto));

        assertEquals(1, result.size());
        verify(paysRepository).saveAll(anyList());
    }

    // =========================
    // update - SUCCESS
    // =========================
    @Test
    void shouldUpdatePays() {
        PaysDTO updatedDto = new PaysDTO("Senegal Updated", "SEN", "senegal_updated.png");
        
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        when(paysRepository.save(any(Pays.class))).thenReturn(pays);

        Pays result = paysService.update(1L, updatedDto);

        assertNotNull(result);
        verify(paysRepository).save(any(Pays.class));
    }

    // =========================
    // delete
    // =========================
    @Test
    void shouldDeletePays() {
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        doNothing().when(paysRepository).delete(any(Pays.class));

        paysService.delete(1L);

        verify(paysRepository).delete(any(Pays.class));
    }
}
