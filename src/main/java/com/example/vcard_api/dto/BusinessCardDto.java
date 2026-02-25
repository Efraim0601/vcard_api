package com.example.vcard_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessCardDto {

    private String id;
    private String fullName;
    private String title;
    private String company;
    private String mobile;
    private String office;
    private String email;
    private String website;
    private String createdAt;
    /** Returned only when creating/updating "my card" so the frontend can store it. */
    private String ownerToken;
}
