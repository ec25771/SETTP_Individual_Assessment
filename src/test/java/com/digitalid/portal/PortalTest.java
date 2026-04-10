package com.digitalid.portal;

import com.digitalid.audit.AuditLog;
import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;
import com.digitalid.repository.IdentityRepository;
import com.digitalid.service.IdentityService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Organisation Portals")
class PortalTest {

    private IdentityService service;
    private AuditLog auditLog;
    private TaxPortal taxPortal;
    private EmployerPortal employerPortal;
    private DrivingLicencePortal drivingLicencePortal;
    private String testFilePath = "test_portal_identities.txt";
    private String testAuditPath = "test_portal_audit.txt";

    @BeforeEach
    void setUp() {
        deleteIfExists(testFilePath);
        deleteIfExists(testAuditPath);
        IdentityRepository repository = new IdentityRepository(testFilePath);
        auditLog = new AuditLog(testAuditPath);
        service = new IdentityService(repository, auditLog);
        taxPortal = new TaxPortal(service, auditLog);
        employerPortal = new EmployerPortal(service, auditLog);
        drivingLicencePortal = new DrivingLicencePortal(service, auditLog);
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
                "John Smith", "1990-05-15", "AB123456C",
                "10 Downing Street", "john@email.com", "07700900000"
        );
    }

    // --- TaxPortal ---

    @Test
    @DisplayName("Tax portal verifies active identity")
    void taxVerifiesActive() {
        DigitalId identity = createTestIdentity();
        String result = taxPortal.verifyIdentity(identity.getId());
        assertTrue(result.startsWith("VERIFIED"));
    }

    @Test
    @DisplayName("Tax portal flags previously suspended identity")
    void taxFlagsPreviouslySuspended() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);
        service.changeStatus(identity.getId(), IdentityStatus.ACTIVE);

        String result = taxPortal.verifyIdentity(identity.getId());
        assertTrue(result.contains("VERIFIED WITH FLAG"));
    }

    @Test
    @DisplayName("Tax portal denies suspended identity")
    void taxDeniesSuspended() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);

        String result = taxPortal.verifyIdentity(identity.getId());
        assertTrue(result.startsWith("DENIED"));
    }

    @Test
    @DisplayName("Tax portal denies revoked identity")
    void taxDeniesRevoked() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.REVOKED);

        String result = taxPortal.verifyIdentity(identity.getId());
        assertTrue(result.startsWith("DENIED"));
    }

    // --- EmployerPortal ---

    @Test
    @DisplayName("Employer portal confirms active identity")
    void employerConfirmsActive() {
        DigitalId identity = createTestIdentity();
        String result = employerPortal.verifyIdentity(identity.getId());
        assertTrue(result.startsWith("VALID"));
    }

    @Test
    @DisplayName("Employer portal rejects suspended identity")
    void employerRejectsSuspended() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);

        String result = employerPortal.verifyIdentity(identity.getId());
        assertTrue(result.startsWith("INVALID"));
    }

    @Test
    @DisplayName("Employer portal rejects revoked identity")
    void employerRejectsRevoked() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.REVOKED);

        String result = employerPortal.verifyIdentity(identity.getId());
        assertTrue(result.startsWith("INVALID"));
    }

    // --- DrivingLicencePortal ---

    @Test
    @DisplayName("Driving licence portal verifies active identity")
    void drivingLicenceVerifiesActive() {
        DigitalId identity = createTestIdentity();
        String result = drivingLicencePortal.verifyIdentity(identity.getId());
        assertTrue(result.startsWith("VERIFIED"));
    }

    @Test
    @DisplayName("Driving licence portal denies suspended identity")
    void drivingLicenceDeniedSuspended() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);

        String result = drivingLicencePortal.verifyIdentity(identity.getId());
        assertTrue(result.contains("suspended"));
    }

    @Test
    @DisplayName("Driving licence portal denies revoked identity")
    void drivingLicenceDeniesRevoked() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.REVOKED);

        String result = drivingLicencePortal.verifyIdentity(identity.getId());
        assertTrue(result.startsWith("DENIED"));
    }

    // --- Interface contract ---

    @Test
    @DisplayName("All portals return their organisation name")
    void organisationNames() {
        assertEquals("Tax Authority", taxPortal.getOrganisationName());
        assertEquals("Employer", employerPortal.getOrganisationName());
        assertEquals("Driving Licence Authority", drivingLicencePortal.getOrganisationName());
    }

    // --- Audit logging ---

    @Test
    @DisplayName("Verification is recorded in audit log")
    void verificationIsAudited() {
        DigitalId identity = createTestIdentity();
        taxPortal.verifyIdentity(identity.getId());

        boolean found = false;
        for (String entry : auditLog.getEntries()) {
            if (entry.contains("VERIFICATION_REQUESTED")) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    @DisplayName("Denied access is recorded in audit log")
    void deniedAccessIsAudited() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.REVOKED);
        employerPortal.verifyIdentity(identity.getId());

        boolean found = false;
        for (String entry : auditLog.getEntries()) {
            if (entry.contains("ACCESS_DENIED")) {
                found = true;
            }
        }
        assertTrue(found);
    }
}
