package com.backend.dto.hike;

import com.backend.models.hike.Difficulty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HikeSummaryDTO {
    private Long id;
    private String title;
    private Difficulty difficulty;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
}
