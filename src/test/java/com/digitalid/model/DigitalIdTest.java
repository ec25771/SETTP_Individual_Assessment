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
                "15/05/1990",
                "AB123456C",
                "10 Downing Street, London",
                "john.smith@email.com",
                "07700900000"
        );
    }

    @Test
    @DisplayName("New identity defaults to ACTIVE status")
    void newIdentityDefaultsToActive() {
        assertEquals(IdentityStatus.ACTIVE, identity.getStatus());
    }

    @Test
    @DisplayName("Two identities get different ids")
    void twoIdentitiesGetDifferentIds() {
        DigitalId other = new DigitalId(
                "Jane Doe", "20/03/1985", "CD789012E",
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
}
