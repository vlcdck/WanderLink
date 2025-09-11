package com.backend.mappers;

import com.backend.dto.hike.HikeDTO;
import com.backend.dto.hike.HikeSummaryDTO;
import com.backend.models.hike.Hike;

public class HikeMapper {

    public static HikeDTO toDTO(Hike hike) {
        if (hike == null) return null;

        HikeDTO dto = new HikeDTO();
        dto.setId(hike.getId());
        dto.setTitle(hike.getTitle());
        dto.setDescription(hike.getDescription());
        dto.setDifficulty(hike.getDifficulty());
        dto.setLocation(hike.getLocation());
        dto.setStartDate(hike.getStartDate());
        dto.setEndDate(hike.getEndDate());
        dto.setMaxParticipants(hike.getMaxParticipants());
        dto.setPrice(hike.getPrice());

        if (hike.getOrganizer() != null) {
            dto.setOrganizerId(hike.getOrganizer().getId());
            dto.setOrganizerName(hike.getOrganizer().getUsername());
        }

        if (hike.getParticipants() != null) {
            dto.setParticipantsCount(hike.getParticipants().size());
        } else {
            dto.setParticipantsCount(0);
        }

        return dto;
    }

    public static HikeSummaryDTO toSummaryDTO(Hike hike) {
        if (hike == null) return null;

        HikeSummaryDTO dto = new HikeSummaryDTO();
        dto.setId(hike.getId());
        dto.setTitle(hike.getTitle());
        dto.setDifficulty(hike.getDifficulty());
        dto.setLocation(hike.getLocation());
        dto.setStartDate(hike.getStartDate());
        dto.setEndDate(hike.getEndDate());
        return dto;
    }


}
