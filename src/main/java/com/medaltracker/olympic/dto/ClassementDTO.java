package com.medaltracker.olympic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClassementDTO {

    private String pays;
    private int orCount;
    private int argentCount;
    private int bronzeCount;
    private int total;
    private int points;
}
