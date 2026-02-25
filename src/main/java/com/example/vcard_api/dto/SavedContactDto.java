package com.example.vcard_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedContactDto {

    private String id;
    private String cardId;
    private BusinessCardDto card;
    private String savedAt;
}
