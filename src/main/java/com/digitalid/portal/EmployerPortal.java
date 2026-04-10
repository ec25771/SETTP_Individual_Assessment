package com.digitalid.portal;

import com.digitalid.audit.AuditAction;
import com.digitalid.audit.AuditLog;
import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;
import com.digitalid.service.IdentityService;

public class EmployerPortal implements IdentityConsumer {

    private IdentityService identityService;
    private AuditLog auditLog;

    public EmployerPortal(IdentityService identityService, AuditLog auditLog) {
        this.identityService = identityService;
        this.auditLog = auditLog;
    }

    public String verifyIdentity(String identityId) {
        DigitalId identity = identityService.getIdentity(identityId);

        if (identity.getStatus() == IdentityStatus.ACTIVE) {
            auditLog.log(AuditAction.VERIFICATION_REQUESTED, identityId,
                    "EMPLOYER", "Employment verification passed");
            return "VALID - identity confirmed for employment";
        }

        auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                "EMPLOYER", "Identity not active");
        return "INVALID - identity is not active";
    }

    public String getOrganisationName() {
        return "Employer";
    }
}
