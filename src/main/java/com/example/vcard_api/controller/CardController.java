package com.example.vcard_api.controller;

import com.example.vcard_api.dto.BusinessCardDto;
import com.example.vcard_api.dto.SavedContactDto;
import com.example.vcard_api.service.CardService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class CardController {

    private static final String HEADER_OWNER_TOKEN = "X-Owner-Token";
    private static final String HEADER_DEVICE_ID = "X-Device-Id";

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /** Create a new card (returns card with ownerToken for the frontend to store). */
    @PostMapping("/cards")
    public ResponseEntity<BusinessCardDto> createCard(@RequestBody BusinessCardDto request) {
        BusinessCardDto created = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Get my card (requires X-Owner-Token). */
    @GetMapping("/cards/me")
    public ResponseEntity<BusinessCardDto> getMyCard(@RequestHeader(value = HEADER_OWNER_TOKEN, required = false) String ownerToken) {
        if (ownerToken == null || ownerToken.isBlank()) {
            return ResponseEntity.notFound().build();
        }
        BusinessCardDto card = cardService.getMyCard(ownerToken);
        return card != null ? ResponseEntity.ok(card) : ResponseEntity.notFound().build();
    }

    /** Update my card (requires X-Owner-Token). */
    @PutMapping("/cards/me")
    public ResponseEntity<BusinessCardDto> updateMyCard(
            @RequestHeader(value = HEADER_OWNER_TOKEN, required = false) String ownerToken,
            @RequestBody BusinessCardDto request) {
        if (ownerToken == null || ownerToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(cardService.updateMyCard(ownerToken, request));
    }

    /** Get card by id (public, for QR scan / share link). */
    @GetMapping("/cards/{id}")
    public ResponseEntity<BusinessCardDto> getCardById(@PathVariable UUID id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    /** Save this card to my contacts (requires X-Device-Id). */
    @PostMapping("/cards/{id}/save")
    public ResponseEntity<Void> saveContact(
            @PathVariable UUID id,
            @RequestHeader(value = HEADER_DEVICE_ID, required = false) String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        cardService.saveContact(id, deviceId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** Check if current device has saved this card. */
    @GetMapping("/cards/{id}/saved")
    public ResponseEntity<Boolean> isContactSaved(
            @PathVariable UUID id,
            @RequestHeader(value = HEADER_DEVICE_ID, required = false) String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(cardService.isContactSaved(id, deviceId));
    }

    /** List my saved contacts (requires X-Device-Id). */
    @GetMapping("/contacts")
    public ResponseEntity<List<SavedContactDto>> getSavedContacts(
            @RequestHeader(value = HEADER_DEVICE_ID, required = false) String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(cardService.getSavedContacts(deviceId));
    }

    /** Delete a saved contact (requires X-Device-Id). */
    @DeleteMapping("/contacts/{contactId}")
    public ResponseEntity<Void> removeContact(
            @PathVariable UUID contactId,
            @RequestHeader(value = HEADER_DEVICE_ID, required = false) String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        cardService.removeContact(contactId, deviceId);
        return ResponseEntity.noContent().build();
    }
}
