package com.example.vcard_api.mapper;

import com.example.vcard_api.dto.SavedContactDto;
import com.example.vcard_api.entity.SavedContact;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class SavedContactMapper {

    private final CardMapper cardMapper;

    public SavedContactMapper(CardMapper cardMapper) {
        this.cardMapper = cardMapper;
    }

    public SavedContactDto toDto(SavedContact entity) {
        if (entity == null) return null;
        return SavedContactDto.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .cardId(entity.getCard() != null && entity.getCard().getId() != null
                        ? entity.getCard().getId().toString() : null)
                .card(cardMapper.toDto(entity.getCard(), false))
                .savedAt(entity.getSavedAt() != null ? entity.getSavedAt().toString() : null)
                .build();
    }
}
