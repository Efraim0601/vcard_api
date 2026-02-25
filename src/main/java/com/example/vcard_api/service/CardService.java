package com.example.vcard_api.service;

import com.example.vcard_api.dto.BusinessCardDto;
import com.example.vcard_api.dto.SavedContactDto;
import com.example.vcard_api.entity.BusinessCard;
import com.example.vcard_api.entity.SavedContact;
import com.example.vcard_api.mapper.CardMapper;
import com.example.vcard_api.mapper.SavedContactMapper;
import com.example.vcard_api.repository.BusinessCardRepository;
import com.example.vcard_api.repository.SavedContactRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final BusinessCardRepository cardRepository;
    private final SavedContactRepository savedContactRepository;
    private final CardMapper cardMapper;
    private final SavedContactMapper savedContactMapper;

    public CardService(BusinessCardRepository cardRepository,
                       SavedContactRepository savedContactRepository,
                       CardMapper cardMapper,
                       SavedContactMapper savedContactMapper) {
        this.cardRepository = cardRepository;
        this.savedContactRepository = savedContactRepository;
        this.cardMapper = cardMapper;
        this.savedContactMapper = savedContactMapper;
    }

    /** Create a new card and assign an owner token (for "my card"). */
    @Transactional
    public BusinessCardDto createCard(BusinessCardDto request) {
        String ownerToken = UUID.randomUUID().toString();
        BusinessCard entity = cardMapper.toEntity(request, null, Instant.now(), ownerToken);
        entity = cardRepository.save(entity);
        BusinessCardDto dto = cardMapper.toDto(entity, true);
        dto.setOwnerToken(ownerToken);
        return dto;
    }

    /** Get "my card" by owner token. */
    public BusinessCardDto getMyCard(String ownerToken) {
        return cardRepository.findByOwnerToken(ownerToken)
                .map(c -> cardMapper.toDto(c, true))
                .orElse(null);
    }

    /** Update "my card" by owner token. */
    @Transactional
    public BusinessCardDto updateMyCard(String ownerToken, BusinessCardDto request) {
        BusinessCard existing = cardRepository.findByOwnerToken(ownerToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carte non trouvée"));
        existing.setFullName(request.getFullName());
        existing.setTitle(request.getTitle());
        existing.setCompany(request.getCompany());
        existing.setMobile(request.getMobile());
        existing.setOffice(request.getOffice());
        existing.setEmail(request.getEmail());
        existing.setWebsite(request.getWebsite());
        existing = cardRepository.save(existing);
        return cardMapper.toDto(existing, true);
    }

    /** Get card by public id (for QR scan / share link). */
    public BusinessCardDto getCardById(UUID id) {
        return cardRepository.findById(id)
                .map(c -> cardMapper.toDto(c, false))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carte introuvable"));
    }

    /** Save a card to "my contacts" for the given device. */
    @Transactional
    public void saveContact(UUID cardId, String saverDeviceId) {
        BusinessCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carte introuvable"));
        if (savedContactRepository.existsByCardIdAndSaverDeviceId(cardId, saverDeviceId)) {
            return; // already saved
        }
        SavedContact contact = SavedContact.builder()
                .card(card)
                .saverDeviceId(saverDeviceId)
                .build();
        savedContactRepository.save(contact);
    }

    public boolean isContactSaved(UUID cardId, String saverDeviceId) {
        return savedContactRepository.existsByCardIdAndSaverDeviceId(cardId, saverDeviceId);
    }

    public List<SavedContactDto> getSavedContacts(String saverDeviceId) {
        return savedContactRepository.findBySaverDeviceIdOrderBySavedAtDesc(saverDeviceId)
                .stream()
                .map(savedContactMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeContact(UUID contactId, String saverDeviceId) {
        SavedContact contact = savedContactRepository.findByIdAndSaverDeviceId(contactId, saverDeviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact non trouvé"));
        savedContactRepository.delete(contact);
    }
}
