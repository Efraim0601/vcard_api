package com.example.vcard_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "business_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    private String mobile;
    private String office;
    private String email;
    private String website;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Token allowing the owner to update "my card". Stored by the frontend (e.g. localStorage).
     */
    @Column(unique = true)
    private String ownerToken;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
