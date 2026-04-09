package com.digitalid.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DigitalId")
class DigitalIdTest {

    private DigitalId identity;

    @BeforeEach
    void setUp() {
        identity = new DigitalId(
                "John Smith",
                "1990-05-15",
                "AB123456C",
                "10 Downing Street, London",
                "john.smith@email.com",
                "07700900000"
        );
    }

    @Test
    @DisplayName("New identity has a generated id")
    void newIdentityHasGeneratedId() {
        assertNotNull(identity.getId());
        assertFalse(identity.getId().isEmpty());
    }

    @Test
    @DisplayName("New identity defaults to ACTIVE status")
    void newIdentityDefaultsToActive() {
        assertEquals(IdentityStatus.ACTIVE, identity.getStatus());
    }

    @Test
    @DisplayName("New identity has a created date")
    void newIdentityHasCreatedDate() {
        assertNotNull(identity.getCreatedDate());
        assertFalse(identity.getCreatedDate().isEmpty());
    }

    @Test
    @DisplayName("Immutable fields are set from constructor")
    void immutableFieldsSetCorrectly() {
        assertEquals("John Smith", identity.getFullName());
        assertEquals("1990-05-15", identity.getDateOfBirth());
        assertEquals("AB123456C", identity.getNationalIdentifier());
    }

    @Test
    @DisplayName("Mutable fields can be updated")
    void mutableFieldsCanBeUpdated() {
        identity.setAddress("20 New Street, London");
        identity.setContactEmail("john.new@email.com");
        identity.setContactPhone("07700900001");

        assertEquals("20 New Street, London", identity.getAddress());
        assertEquals("john.new@email.com", identity.getContactEmail());
        assertEquals("07700900001", identity.getContactPhone());
    }

    @Test
    @DisplayName("Status can be changed")
    void statusCanBeChanged() {
        identity.setStatus(IdentityStatus.SUSPENDED);
        assertEquals(IdentityStatus.SUSPENDED, identity.getStatus());
    }

    @Test
    @DisplayName("Two identities get different ids")
    void twoIdentitiesGetDifferentIds() {
        DigitalId other = new DigitalId(
                "Jane Doe", "1985-03-20", "CD789012E",
                "5 Oxford Road", "jane@email.com", "07700900002"
        );
        assertNotEquals(identity.getId(), other.getId());
    }

    @Test
    @DisplayName("toFileString and fromFileString round-trip preserves data")
    void fileStringRoundTrip() {
        String fileString = identity.toFileString();
        DigitalId loaded = DigitalId.fromFileString(fileString);

        assertEquals(identity.getId(), loaded.getId());
        assertEquals(identity.getFullName(), loaded.getFullName());
        assertEquals(identity.getDateOfBirth(), loaded.getDateOfBirth());
        assertEquals(identity.getNationalIdentifier(), loaded.getNationalIdentifier());
        assertEquals(identity.getAddress(), loaded.getAddress());
        assertEquals(identity.getContactEmail(), loaded.getContactEmail());
        assertEquals(identity.getContactPhone(), loaded.getContactPhone());
        assertEquals(identity.getStatus(), loaded.getStatus());
        assertEquals(identity.getCreatedDate(), loaded.getCreatedDate());
    }

    @Test
    @DisplayName("toString contains key identity information")
    void toStringContainsKeyInfo() {
        String result = identity.toString();
        assertTrue(result.contains("John Smith"));
        assertTrue(result.contains("AB123456C"));
        assertTrue(result.contains("ACTIVE"));
    }
}
