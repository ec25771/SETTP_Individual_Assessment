package com.digitalid.portal;

import com.digitalid.audit.AuditLog;
import com.digitalid.exception.ValidationException;
import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;
import com.digitalid.repository.IdentityRepository;
import com.digitalid.service.IdentityService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Organisation Portals")
class PortalTest {

    private IdentityService service;
    private AuditLog auditLog;
    private TaxPortal taxPortal;
    private EmployerPortal employerPortal;
    private DrivingLicencePortal drivingLicencePortal;
    private BankPortal bankPortal;
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
        bankPortal = new BankPortal(service, auditLog);
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

    private DigitalId createMinorIdentity() {
        LocalDate tenYearsAgo = LocalDate.now().minusYears(10);
        String dob = tenYearsAgo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return service.createIdentity(
                "Young Person", dob, "YP123456Z",
                "5 Youth Lane", "young@email.com", "07700900001"
        );
    }

    @Test
    @DisplayName("Tax portal verifies active identity")
    void taxVerifiesActive() {
        DigitalId identity = createTestIdentity();
        String result = taxPortal.verifyIdentity(identity.getId());
        assertTrue(result.startsWith("VERIFIED"));
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


    @Test
    @DisplayName("Tax portal passes when no suspension in reporting period")
    void taxPeriodPassesClean() {
        DigitalId identity = createTestIdentity();
        String result = taxPortal.verifyForTaxPeriod(identity.getId(), "01/01/2020", "31/12/2020");
        assertTrue(result.startsWith("VERIFIED"));
    }

    @Test
    @DisplayName("Tax portal fails when suspended during reporting period")
    void taxPeriodFailsSuspended() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);
        service.changeStatus(identity.getId(), IdentityStatus.ACTIVE);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String result = taxPortal.verifyForTaxPeriod(identity.getId(), "01/01/2020", today);
        assertTrue(result.contains("DENIED") && result.contains("suspended during the reporting period"));
    }

    @Test
    @DisplayName("Tax portal passes when suspension was outside reporting period")
    void taxPeriodPassesSuspensionOutsideRange() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);
        service.changeStatus(identity.getId(), IdentityStatus.ACTIVE);

        String result = taxPortal.verifyForTaxPeriod(identity.getId(), "01/01/2000", "31/12/2000");
        assertTrue(result.startsWith("VERIFIED"));
    }


    @Test
    @DisplayName("Tax portal lookup returns name and national ID")
    void taxLookupReturnsDetails() {
        DigitalId identity = createTestIdentity();
        String result = taxPortal.lookupIdentity(identity.getId());
        assertTrue(result.contains("John Smith"));
        assertTrue(result.contains("AB123456C"));
        assertFalse(result.contains("Downing Street"));
    }

    @Test
    @DisplayName("Tax portal lookup denied for inactive identity")
    void taxLookupDeniedInactive() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);
        String result = taxPortal.lookupIdentity(identity.getId());
        assertTrue(result.startsWith("DENIED"));
    }

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

    @Test
    @DisplayName("Employer portal denies identity lookup")
    void employerLookupDenied() {
        DigitalId identity = createTestIdentity();
        assertThrows(ValidationException.class, () ->
                employerPortal.lookupIdentity(identity.getId())
        );
    }

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

    @Test
    @DisplayName("Driving licence eligibility passes for adult")
    void drivingLicenceEligibleAdult() {
        DigitalId identity = createTestIdentity();
        String result = drivingLicencePortal.checkEligibility(identity.getId());
        assertTrue(result.startsWith("ELIGIBLE"));
    }

    @Test
    @DisplayName("Driving licence eligibility denied for minor")
    void drivingLicenceIneligibleMinor() {
        DigitalId identity = createMinorIdentity();
        String result = drivingLicencePortal.checkEligibility(identity.getId());
        assertTrue(result.contains("under the minimum driving age"));
    }

    @Test
    @DisplayName("Driving licence eligibility denied for suspended identity")
    void drivingLicenceEligibilityDeniedSuspended() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);
        String result = drivingLicencePortal.checkEligibility(identity.getId());
        assertTrue(result.contains("temporary restriction"));
    }

    @Test
    @DisplayName("Driving licence lookup returns name and DOB")
    void drivingLicenceLookupReturnsDetails() {
        DigitalId identity = createTestIdentity();
        String result = drivingLicencePortal.lookupIdentity(identity.getId());
        assertTrue(result.contains("John Smith"));
        assertTrue(result.contains("15/05/1990"));
        assertFalse(result.contains("AB123456C"));
    }

    @Test
    @DisplayName("Driving licence lookup denied for inactive identity")
    void drivingLicenceLookupDeniedInactive() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.REVOKED);
        String result = drivingLicencePortal.lookupIdentity(identity.getId());
        assertTrue(result.startsWith("DENIED"));
    }

    @Test
    @DisplayName("Bank portal confirms active identity")
    void bankConfirmsActive() {
        DigitalId identity = createTestIdentity();
        String result = bankPortal.verifyIdentity(identity.getId());
        assertTrue(result.startsWith("VALID"));
    }

    @Test
    @DisplayName("Bank portal rejects suspended identity")
    void bankRejectsSuspended() {
        DigitalId identity = createTestIdentity();
        service.changeStatus(identity.getId(), IdentityStatus.SUSPENDED);
        String result = bankPortal.verifyIdentity(identity.getId());
        assertTrue(result.startsWith("INVALID"));
    }

    @Test
    @DisplayName("Bank portal denies identity lookup")
    void bankLookupDenied() {
        DigitalId identity = createTestIdentity();
        assertThrows(ValidationException.class, () ->
                bankPortal.lookupIdentity(identity.getId())
        );
    }

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

    @Test
    @DisplayName("Unauthorised lookup attempt is recorded in audit log")
    void unauthorisedLookupIsAudited() {
        DigitalId identity = createTestIdentity();
        try {
            employerPortal.lookupIdentity(identity.getId());
        } catch (ValidationException e) {
            // expected
        }

        boolean found = false;
        for (String entry : auditLog.getEntries()) {
            if (entry.contains("ACCESS_DENIED") && entry.contains("Unauthorised lookup")) {
                found = true;
            }
        }
        assertTrue(found);
    }
}
