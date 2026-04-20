package com.digitalid.repository;

import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IdentityRepository")
class IdentityRepositoryTest {

    private IdentityRepository repository;
    private String testFilePath = "test_identities.txt";

    @BeforeEach
    void setUp() {
        File file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
        }
        repository = new IdentityRepository(testFilePath);
    }

    @AfterEach
    void tearDown() {
        File file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
        }
    }

    private DigitalId createTestIdentity() {
        return new DigitalId(
                "John Smith", "15/05/1990", "AB123456C",
                "10 Downing Street", "john@email.com", "07700900000"
        );
    }

    @Test
    @DisplayName("Save and find identity by id")
    void saveAndFindById() {
        DigitalId identity = createTestIdentity();
        repository.save(identity);

        DigitalId found = repository.findById(identity.getId());
        assertNotNull(found);
        assertEquals("John Smith", found.getFullName());
    }

    @Test
    @DisplayName("Find returns null for unknown id")
    void findReturnsNullForUnknownId() {
        assertNull(repository.findById("does-not-exist"));
    }

    @Test
    @DisplayName("findAll returns all saved identities")
    void findAllReturnsAll() {
        repository.save(createTestIdentity());
        repository.save(new DigitalId(
                "Jane Doe", "20/03/1985", "CD789012E",
                "5 Oxford Road", "jane@email.com", "07700900001"
        ));

        assertEquals(2, repository.findAll().size());
    }

    @Test
    @DisplayName("Update changes the stored identity")
    void updateChangesIdentity() {
        DigitalId identity = createTestIdentity();
        repository.save(identity);

        identity.setAddress("New Address");
        repository.update(identity);

        DigitalId found = repository.findById(identity.getId());
        assertEquals("New Address", found.getAddress());
    }

    @Test
    @DisplayName("Data persists after reload from file")
    void dataPersistsAfterReload() {
        DigitalId identity = createTestIdentity();
        repository.save(identity);

        IdentityRepository reloaded = new IdentityRepository(testFilePath);

        DigitalId found = reloaded.findById(identity.getId());
        assertNotNull(found);
        assertEquals("John Smith", found.getFullName());
        assertEquals("AB123456C", found.getNationalIdentifier());
        assertEquals(IdentityStatus.ACTIVE, found.getStatus());
    }
}
