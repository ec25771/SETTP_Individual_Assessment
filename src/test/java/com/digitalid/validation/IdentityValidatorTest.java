package com.digitalid.validation;

import com.digitalid.model.IdentityStatus;
import com.digitalid.exception.InvalidStatusTransitionException;
import com.digitalid.exception.ValidationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IdentityValidator")
class IdentityValidatorTest {

    @Test
    @DisplayName("Valid creation inputs pass validation")
    void validCreationPasses() {
        assertDoesNotThrow(() -> IdentityValidator.validateCreation(
                "John Smith", "1990-05-15", "AB123456C",
                "10 Downing Street", "john@email.com", "07700900000"
        ));
    }

    @Test
    @DisplayName("Null name throws ValidationException")
    void nullNameThrows() {
        assertThrows(ValidationException.class, () -> IdentityValidator.validateCreation(
                null, "1990-05-15", "AB123456C",
                "10 Downing Street", "john@email.com", "07700900000"
        ));
    }

    @Test
    @DisplayName("Blank email throws ValidationException")
    void blankEmailThrows() {
        assertThrows(ValidationException.class, () -> IdentityValidator.validateCreation(
                "John Smith", "1990-05-15", "AB123456C",
                "10 Downing Street", "  ", "07700900000"
        ));
    }

    @Test
    @DisplayName("Empty national identifier throws ValidationException")
    void emptyNationalIdThrows() {
        assertThrows(ValidationException.class, () -> IdentityValidator.validateCreation(
                "John Smith", "1990-05-15", "",
                "10 Downing Street", "john@email.com", "07700900000"
        ));
    }

    @Test
    @DisplayName("ACTIVE to SUSPENDED is valid")
    void activeToSuspendedIsValid() {
        assertDoesNotThrow(() -> IdentityValidator.validateStatusTransition(
                IdentityStatus.ACTIVE, IdentityStatus.SUSPENDED
        ));
    }

    @Test
    @DisplayName("ACTIVE to REVOKED is valid")
    void activeToRevokedIsValid() {
        assertDoesNotThrow(() -> IdentityValidator.validateStatusTransition(
                IdentityStatus.ACTIVE, IdentityStatus.REVOKED
        ));
    }

    @Test
    @DisplayName("SUSPENDED to ACTIVE is valid")
    void suspendedToActiveIsValid() {
        assertDoesNotThrow(() -> IdentityValidator.validateStatusTransition(
                IdentityStatus.SUSPENDED, IdentityStatus.ACTIVE
        ));
    }

    @Test
    @DisplayName("SUSPENDED to REVOKED is valid")
    void suspendedToRevokedIsValid() {
        assertDoesNotThrow(() -> IdentityValidator.validateStatusTransition(
                IdentityStatus.SUSPENDED, IdentityStatus.REVOKED
        ));
    }

    @Test
    @DisplayName("REVOKED to ACTIVE throws exception")
    void revokedToActiveThrows() {
        assertThrows(InvalidStatusTransitionException.class, () ->
                IdentityValidator.validateStatusTransition(
                        IdentityStatus.REVOKED, IdentityStatus.ACTIVE
                ));
    }

    @Test
    @DisplayName("REVOKED to SUSPENDED throws exception")
    void revokedToSuspendedThrows() {
        assertThrows(InvalidStatusTransitionException.class, () ->
                IdentityValidator.validateStatusTransition(
                        IdentityStatus.REVOKED, IdentityStatus.SUSPENDED
                ));
    }

    @Test
    @DisplayName("Same status transition throws exception")
    void sameStatusThrows() {
        assertThrows(InvalidStatusTransitionException.class, () ->
                IdentityValidator.validateStatusTransition(
                        IdentityStatus.ACTIVE, IdentityStatus.ACTIVE
                ));
    }
}
