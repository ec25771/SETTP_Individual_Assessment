package com.digitalid.portal;

import com.digitalid.audit.AuditAction;
import com.digitalid.audit.AuditLog;
import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;
import com.digitalid.service.IdentityService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaxPortal implements IdentityConsumer {

    private IdentityService identityService;
    private AuditLog auditLog;

    public TaxPortal(IdentityService identityService, AuditLog auditLog) {
        this.identityService = identityService;
        this.auditLog = auditLog;
    }

    public String verifyIdentity(String identityId) {
        DigitalId identity = identityService.getIdentity(identityId);

        if (identity.getStatus() != IdentityStatus.ACTIVE) {
            auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                    "TAX_AUTHORITY", "Identity not active");
            return "DENIED - identity is not active";
        }

        auditLog.log(AuditAction.VERIFICATION_REQUESTED, identityId,
                "TAX_AUTHORITY", "Basic tax verification passed");
        return "VERIFIED - identity is active";
    }

    public String verifyForTaxPeriod(String identityId, String periodStart, String periodEnd) {
        DigitalId identity = identityService.getIdentity(identityId);

        if (identity.getStatus() != IdentityStatus.ACTIVE) {
            auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                    "TAX_AUTHORITY", "Identity not active for tax period " + periodStart + " to " + periodEnd);
            return "DENIED - identity is not active";
        }

        LocalDate start = LocalDate.parse(periodStart, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate end = LocalDate.parse(periodEnd, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        boolean suspendedDuringPeriod = false;
        for (String entry : auditLog.getEntriesForIdentity(identityId)) {
            if (entry.contains("SUSPENDED")) {
                LocalDateTime entryTime = extractTimestamp(entry);
                if (entryTime != null) {
                    LocalDate entryDate = entryTime.toLocalDate();
                    if (!entryDate.isBefore(start) && !entryDate.isAfter(end)) {
                        suspendedDuringPeriod = true;
                    }
                }
            }
        }

        auditLog.log(AuditAction.VERIFICATION_REQUESTED, identityId,
                "TAX_AUTHORITY", "Tax period verification for " + periodStart + " to " + periodEnd);

        if (suspendedDuringPeriod) {
            return "DENIED - identity was suspended during the reporting period " + periodStart + " to " + periodEnd;
        }
        return "VERIFIED - identity is valid for tax period " + periodStart + " to " + periodEnd;
    }

    public String lookupIdentity(String identityId) {
        DigitalId identity = identityService.getIdentity(identityId);

        if (identity.getStatus() != IdentityStatus.ACTIVE) {
            auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                    "TAX_AUTHORITY", "Lookup denied - identity not active");
            return "DENIED - identity is not active";
        }

        auditLog.log(AuditAction.VERIFICATION_REQUESTED, identityId,
                "TAX_AUTHORITY", "Identity lookup performed");
        return "Name: " + identity.getFullName()
                + ", National ID: " + identity.getNationalIdentifier()
                + ", Status: " + identity.getStatus();
    }

    public String getOrganisationName() {
        return "Tax Authority";
    }

    private LocalDateTime extractTimestamp(String entry) {
        try {
            String timestamp = entry.substring(0, entry.indexOf("|"));
            return LocalDateTime.parse(timestamp.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
