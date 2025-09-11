package com.backend.dto.hike;

import com.backend.models.hike.Difficulty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HikeDTO {
    private Long id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxParticipants;
    private Double price;

    private Long organizerId;
    private String organizerName;

    private Integer participantsCount;
}
