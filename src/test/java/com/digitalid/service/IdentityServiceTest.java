package com.digitalid.service;

import com.digitalid.audit.AuditLog;
import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;
import com.digitalid.exception.IdentityNotFoundException;
import com.digitalid.exception.InvalidStatusTransitionException;
import com.digitalid.exception.ValidationException;
import com.digitalid.repository.IdentityRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IdentityService")
class IdentityServiceTest {

    private IdentityService service;
    private AuditLog auditLog;
    private String testFilePath = "test_service_identities.txt";
    private String testAuditPath = "test_service_audit.txt";

    @BeforeEach
    void setUp() {
        deleteIfExists(testFilePath);
        deleteIfExists(testAuditPath);
        IdentityRepository repository = new IdentityRepository(testFilePath);
        auditLog = new AuditLog(testAuditPath);
        service = new IdentityService(repository, auditLog);
    }

    @AfterEach
    void tearDown() {
        deleteIfExists(testFilePath);
        deleteIfExists(testAuditPath);
    }

    private void deleteIfExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    private DigitalId createTestIdentity() {
        return service.createIdentity(
                "John Smith", "15/05/1990", "AB123456C",
                "10 Downing Street", "john@email.com", "07700900000"
        );
    }

    @Test
    @DisplayName("Create identity and retrieve it")
    void createAndRetrieve() {
        DigitalId created = createTestIdentity();
        DigitalId found = service.getIdentity(created.getId());

        assertEquals("John Smith", found.getFullName());
        assertEquals(IdentityStatus.ACTIVE, found.getStatus());
    }

    @Test
    @DisplayName("Create with blank name throws ValidationException")
    void createWithBlankNameThrows() {
        assertThrows(ValidationException.class, () -> service.createIdentity(
                "", "15/05/1990", "AB123456C",
                "10 Downing Street", "john@email.com", "07700900000"
        ));
    }

    @Test
    @DisplayName("Get unknown identity throws IdentityNotFoundException")
    void getUnknownThrows() {
        assertThrows(IdentityNotFoundException.class, () ->
                service.getIdentity("does-not-exist")
        );
    }

    @Test
    @DisplayName("Get all identities returns correct count")
    void getAllIdentities() {
        createTestIdentity();
        createTestIdentity();
        assertEquals(2, service.getAllIdentities().size());
    }

    @Test
    @DisplayName("Update address on active identity")
    void updateAddress() {
        DigitalId identity = createTestIdentity();
        service.updateAddress(identity.getId(), "New Address");

        assertEquals("New Address", service.getIdentity(identity.getId()).getAddress());
    }

    @Test
    @DisplayName("Update email on active identity")
    void updateEmail() {
        DigitalId identity = createTestIdentity();
        service.updateContactEmail(identity.getId(), "new@email.com");

        assertEquals("new@email.com", service.getIdentity(identity.getId()).getContactEmail());
    }

    @Test
    @DisplayName("Update phone on active identity")
    void updatePhone() {
        DigitalId identity = createTestIdentity();
        service.updateContactPhone(identity.getId(), "07700999999");

        assertEquals("07700999999", service.getIdentity(identity.getId()).getContactPhone());
    }

    @Test
    @DisplayName("Update with blank value throws ValidationException")
    void updateWithBlankThrows() {
        DigitalId identity = createTestIdentity();
        assertThrows(ValidationException.class, () ->
                service.updateAddress(identity.getId(), "")
        );
    }

    @Test
    @DisplayName("Suspend an active identity")
    void suspendActiveIdentity() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);

        assertEquals(IdentityStatus.SUSPENDED, service.getIdentity(identity.getId()).getStatus());
    }

    @Test
    @DisplayName("Reactivate a suspended identity")
    void reactivateSuspendedIdentity() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);
        service.changeStatus(identity.getId(), IdentityStatus.ACTIVE);

        assertEquals(IdentityStatus.ACTIVE, service.getIdentity(identity.getId()).getStatus());
    }

    @Test
    @DisplayName("Revoke an active identity")
    void revokeActiveIdentity() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.REVOKED);

        assertEquals(IdentityStatus.REVOKED, service.getIdentity(identity.getId()).getStatus());
    }

    @Test
    @DisplayName("Cannot transition from revoked")
    void cannotTransitionFromRevoked() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.REVOKED);

        assertThrows(InvalidStatusTransitionException.class, () ->
                service.changeStatus(identity.getId(), IdentityStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("Cannot update a revoked identity")
    void cannotUpdateRevokedIdentity() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.REVOKED);

        assertThrows(ValidationException.class, () ->
                service.updateAddress(identity.getId(), "New Address")
        );
    }

    @Test
    @DisplayName("Full lifecycle: create, update, suspend, reactivate, revoke")
    void fullLifecycle() {
        DigitalId identity = createTestIdentity();
        String id = identity.getId();

        service.updateAddress(id, "Updated Address");
        assertEquals("Updated Address", service.getIdentity(id).getAddress());

        service.changeStatus(id, IdentityStatus.SUSPENDED);
        assertEquals(IdentityStatus.SUSPENDED, service.getIdentity(id).getStatus());

        service.changeStatus(id, IdentityStatus.ACTIVE);
        assertEquals(IdentityStatus.ACTIVE, service.getIdentity(id).getStatus());

        service.changeStatus(id, IdentityStatus.REVOKED);
        assertEquals(IdentityStatus.REVOKED, service.getIdentity(id).getStatus());

        assertThrows(ValidationException.class, () ->
                service.updateAddress(id, "Should Fail")
        );
    }

    @Test
    @DisplayName("Audit log records identity creation")
    void auditLogRecordsCreation() {
        DigitalId identity = createTestIdentity();
        assertTrue(auditLog.getEntries().get(0).contains("IDENTITY_CREATED"));
    }

    @Test
    @DisplayName("Audit log records status change")
    void auditLogRecordsStatusChange() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);

        boolean found = false;
        for (String entry : auditLog.getEntries()) {
            if (entry.contains("STATUS_CHANGED")) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    @DisplayName("Audit log filters by identity id")
    void auditLogFiltersByIdentity() {
        DigitalId first = createTestIdentity();
        DigitalId second = service.createIdentity(
                "Jane Doe", "20/03/1985", "CD789012E",
                "5 Oxford Road", "jane@email.com", "07700900001"
        );

        service.updateAddress(first.getId(), "New Address");

        assertEquals(2, auditLog.getEntriesForIdentity(first.getId()).size());
        assertEquals(1, auditLog.getEntriesForIdentity(second.getId()).size());
    }
}
