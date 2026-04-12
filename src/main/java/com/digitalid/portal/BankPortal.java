package com.digitalid.portal;

import com.digitalid.audit.AuditAction;
import com.digitalid.audit.AuditLog;
import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;
import com.digitalid.exception.ValidationException;
import com.digitalid.service.IdentityService;

public class BankPortal implements IdentityConsumer {

    private IdentityService identityService;
    private AuditLog auditLog;

    public BankPortal(IdentityService identityService, AuditLog auditLog) {
        this.identityService = identityService;
        this.auditLog = auditLog;
    }

    public String verifyIdentity(String identityId) {
        DigitalId identity = identityService.getIdentity(identityId);

        if (identity.getStatus() == IdentityStatus.ACTIVE) {
            auditLog.log(AuditAction.VERIFICATION_REQUESTED, identityId,
                    "BANK", "Bank verification passed");
            return "VALID - identity confirmed for banking purposes";
        }

        auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                "BANK", "Identity not active");
        return "INVALID - identity is not active";
    }

    public String lookupIdentity(String identityId) {
        auditLog.log(AuditAction.ACCESS_DENIED, identityId,
                "BANK", "Unauthorised lookup attempt");
        throw new ValidationException("Bank portal is not authorised to access identity details");
    }

    public String getOrganisationName() {
        return "Bank";
    }
}
