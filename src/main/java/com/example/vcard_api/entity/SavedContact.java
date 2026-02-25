package com.example.vcard_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "saved_contacts", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "card_id", "saver_device_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedContact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private BusinessCard card;

    /**
     * Device/session id of the user who saved this contact (from frontend X-Device-Id header).
     */
    @Column(name = "saver_device_id", nullable = false)
    private String saverDeviceId;

    @Column(nullable = false, updatable = false)
    private Instant savedAt;

    @PrePersist
    void prePersist() {
        if (savedAt == null) {
            savedAt = Instant.now();
        }
    }
}
