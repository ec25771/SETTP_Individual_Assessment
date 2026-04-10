package com.digitalid.service;

import com.digitalid.audit.AuditAction;
import com.digitalid.audit.AuditLog;
import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;
import com.digitalid.exception.IdentityNotFoundException;
import com.digitalid.exception.ValidationException;
import com.digitalid.repository.IdentityRepository;
import com.digitalid.validation.IdentityValidator;

import java.util.ArrayList;

public class IdentityService {

    private IdentityRepository repository;
    private AuditLog auditLog;

    public IdentityService(IdentityRepository repository, AuditLog auditLog) {
        this.repository = repository;
        this.auditLog = auditLog;
    }

    public DigitalId createIdentity(String fullName, String dateOfBirth,
                                    String nationalIdentifier, String address,
                                    String contactEmail, String contactPhone) {
        IdentityValidator.validateCreation(fullName, dateOfBirth,
                nationalIdentifier, address, contactEmail, contactPhone);

        DigitalId identity = new DigitalId(fullName, dateOfBirth,
                nationalIdentifier, address, contactEmail, contactPhone);
        repository.save(identity);
        auditLog.log(AuditAction.IDENTITY_CREATED, identity.getId(),
                "CENTRAL_AUTHORITY", "Created identity for " + fullName);
        return identity;
    }

    public DigitalId getIdentity(String id) {
        DigitalId identity = repository.findById(id);
        if (identity == null) {
            throw new IdentityNotFoundException(id);
        }
        return identity;
    }

    public ArrayList<DigitalId> getAllIdentities() {
        return repository.findAll();
    }

    public void updateAddress(String id, String newAddress) {
        IdentityValidator.validateNotBlank(newAddress, "Address");
        DigitalId identity = getIdentity(id);
        checkNotRevoked(identity);
        identity.setAddress(newAddress);
        repository.update(identity);
        auditLog.log(AuditAction.IDENTITY_UPDATED, id,
                "CENTRAL_AUTHORITY", "Updated address");
    }

    public void updateContactEmail(String id, String newEmail) {
        IdentityValidator.validateNotBlank(newEmail, "Contact email");
        DigitalId identity = getIdentity(id);
        checkNotRevoked(identity);
        identity.setContactEmail(newEmail);
        repository.update(identity);
        auditLog.log(AuditAction.IDENTITY_UPDATED, id,
                "CENTRAL_AUTHORITY", "Updated contact email");
    }

    public void updateContactPhone(String id, String newPhone) {
        IdentityValidator.validateNotBlank(newPhone, "Contact phone");
        DigitalId identity = getIdentity(id);
        checkNotRevoked(identity);
        identity.setContactPhone(newPhone);
        repository.update(identity);
        auditLog.log(AuditAction.IDENTITY_UPDATED, id,
                "CENTRAL_AUTHORITY", "Updated contact phone");
    }

    public void changeStatus(String id, IdentityStatus newStatus) {
        DigitalId identity = getIdentity(id);
        IdentityValidator.validateStatusTransition(identity.getStatus(), newStatus);
        IdentityStatus oldStatus = identity.getStatus();
        identity.setStatus(newStatus);
        repository.update(identity);
        auditLog.log(AuditAction.STATUS_CHANGED, id,
                "CENTRAL_AUTHORITY", oldStatus + " -> " + newStatus);
    }

    private void checkNotRevoked(DigitalId identity) {
        if (identity.getStatus() == IdentityStatus.REVOKED) {
            throw new ValidationException("Cannot update a revoked identity");
        }
    }
}
