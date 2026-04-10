package com.digitalid.portal;

import com.digitalid.audit.AuditAction;
import com.digitalid.audit.AuditLog;
import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;
import com.digitalid.service.IdentityService;

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

        boolean wasSuspended = false;
        for (String entry : auditLog.getEntriesForIdentity(identityId)) {
            if (entry.contains("SUSPENDED")) {
                wasSuspended = true;
            }
        }

        auditLog.log(AuditAction.VERIFICATION_REQUESTED, identityId,
                "TAX_AUTHORITY", "Tax verification completed");

        if (wasSuspended) {
            return "VERIFIED WITH FLAG - identity was previously suspended";
        }
        return "VERIFIED - identity is valid for tax purposes";
    }

    public String getOrganisationName() {
        return "Tax Authority";
    }
}
