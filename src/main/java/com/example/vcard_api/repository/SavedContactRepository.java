package com.example.vcard_api.repository;

import com.example.vcard_api.entity.SavedContact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedContactRepository extends JpaRepository<SavedContact, UUID> {

    List<SavedContact> findBySaverDeviceIdOrderBySavedAtDesc(String saverDeviceId);

    Optional<SavedContact> findByIdAndSaverDeviceId(UUID id, String saverDeviceId);

    boolean existsByCardIdAndSaverDeviceId(UUID cardId, String saverDeviceId);
}
