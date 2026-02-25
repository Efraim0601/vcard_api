package com.example.vcard_api.mapper;

import com.example.vcard_api.dto.BusinessCardDto;
import com.example.vcard_api.entity.BusinessCard;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public BusinessCardDto toDto(BusinessCard entity, boolean includeOwnerToken) {
        if (entity == null) return null;
        BusinessCardDto dto = BusinessCardDto.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .fullName(entity.getFullName())
                .title(entity.getTitle())
                .company(entity.getCompany())
                .mobile(entity.getMobile())
                .office(entity.getOffice())
                .email(entity.getEmail())
                .website(entity.getWebsite())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .build();
        if (includeOwnerToken && entity.getOwnerToken() != null) {
            dto.setOwnerToken(entity.getOwnerToken());
        }
        return dto;
    }

    public BusinessCard toEntity(BusinessCardDto dto, UUID id, Instant createdAt, String ownerToken) {
        if (dto == null) return null;
        return BusinessCard.builder()
                .id(id)
                .fullName(dto.getFullName())
                .title(dto.getTitle())
                .company(dto.getCompany())
                .mobile(dto.getMobile())
                .office(dto.getOffice())
                .email(dto.getEmail())
                .website(dto.getWebsite())
                .createdAt(createdAt != null ? createdAt : Instant.now())
                .ownerToken(ownerToken)
                .build();
    }
}
