package com.example.vcard_api.repository;

import com.example.vcard_api.entity.BusinessCard;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessCardRepository extends JpaRepository<BusinessCard, UUID> {

    Optional<BusinessCard> findByOwnerToken(String ownerToken);
}
