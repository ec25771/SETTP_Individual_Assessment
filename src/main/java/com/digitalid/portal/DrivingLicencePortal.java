package com.digitalid.portal;

import com.digitalid.audit.AuditAction;
import com.digitalid.audit.AuditLog;
import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;
import com.digitalid.service.IdentityService;

public class DrivingLicencePortal implements IdentityConsumer {

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
                "DRIVING_LICENCE", "Driving licence verification passed");
        return "VERIFIED - identity valid for driving licence";
    }

    public String getOrganisationName() {
        return "Driving Licence Authority";
    }
}
