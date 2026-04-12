package com.digitalid.portal;

import com.digitalid.audit.AuditAction;
import com.digitalid.audit.AuditLog;
import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;
import com.digitalid.service.IdentityService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;

public class DrivingLicencePortal implements IdentityConsumer {

    private static final int MINIMUM_DRIVING_AGE = 17;

    private IdentityService identityService;
    private AuditLog auditLog;

    public DrivingLicencePortal(IdentityService identityService, AuditLog auditLog) {
        this.identityService = identityService;
        this.auditLog = auditLog;
    }

    public String verifyIdentity(String identityId) {
        DigitalId identity = identityService.getIdentity(identityId);

        if (identity.getStatus() == IdentityStatus.SUSPENDED) {
            auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                    "DRIVING_LICENCE", "Identity is suspended");
            return "DENIED - identity is currently suspended";
        }

        if (identity.getStatus() != IdentityStatus.ACTIVE) {
            auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                    "DRIVING_LICENCE", "Identity not active");
            return "DENIED - identity is not active";
        }

        auditLog.log(AuditAction.VERIFICATION_REQUESTED, identityId,
                "DRIVING_LICENCE", "Basic verification passed");
        return "VERIFIED - identity is active";
    }

    public String checkEligibility(String identityId) {
        DigitalId identity = identityService.getIdentity(identityId);

        if (identity.getStatus() == IdentityStatus.SUSPENDED) {
            auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                    "DRIVING_LICENCE", "Eligibility denied - identity is suspended");
            return "DENIED - identity is currently suspended (temporary restriction)";
        }

        if (identity.getStatus() != IdentityStatus.ACTIVE) {
            auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                    "DRIVING_LICENCE", "Eligibility denied - identity not active");
            return "DENIED - identity is not active";
        }

        int age = calculateAge(identity.getDateOfBirth());
        if (age < MINIMUM_DRIVING_AGE) {
            auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                    "DRIVING_LICENCE", "Eligibility denied - under minimum driving age");
            return "DENIED - applicant is under the minimum driving age of " + MINIMUM_DRIVING_AGE;
        }

        auditLog.log(AuditAction.VERIFICATION_REQUESTED, identityId,
                "DRIVING_LICENCE", "Eligibility check passed");
        return "ELIGIBLE - identity is active and meets age requirement";
    }

    public String lookupIdentity(String identityId) {
        DigitalId identity = identityService.getIdentity(identityId);

        if (identity.getStatus() != IdentityStatus.ACTIVE) {
            auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                    "DRIVING_LICENCE", "Lookup denied - identity not active");
            return "DENIED - identity is not active";
        }

        auditLog.log(AuditAction.VERIFICATION_REQUESTED, identityId,
                "DRIVING_LICENCE", "Identity lookup performed");
        return "Name: " + identity.getFullName()
                + ", Date of Birth: " + identity.getDateOfBirth()
                + ", Status: " + identity.getStatus();
    }

    public String getOrganisationName() {
        return "Driving Licence Authority";
    }

    private int calculateAge(String dateOfBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dob = LocalDate.parse(dateOfBirth, formatter);
        return Period.between(dob, LocalDate.now()).getYears();
    }
}
