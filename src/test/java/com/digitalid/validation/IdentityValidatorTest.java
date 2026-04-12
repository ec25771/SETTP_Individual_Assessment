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
                "John Smith", "15/05/1990", "AB123456C",
                "10 Downing Street", "john@email.com", "07700900000"
        ));
    }

    @Test
    @DisplayName("Null name throws ValidationException")
    void nullNameThrows() {
        assertThrows(ValidationException.class, () -> IdentityValidator.validateCreation(
                null, "15/05/1990", "AB123456C",
                "10 Downing Street", "john@email.com", "07700900000"
        ));
    }

    @Test
    @DisplayName("Blank email throws ValidationException")
    void blankEmailThrows() {
        assertThrows(ValidationException.class, () -> IdentityValidator.validateCreation(
                "John Smith", "15/05/1990", "AB123456C",
                "10 Downing Street", "  ", "07700900000"
        ));
    }

    @Test
    @DisplayName("Empty national identifier throws ValidationException")
    void emptyNationalIdThrows() {
        assertThrows(ValidationException.class, () -> IdentityValidator.validateCreation(
                "John Smith", "15/05/1990", "",
                "10 Downing Street", "john@email.com", "07700900000"
        ));
    }

    @Test
    @DisplayName("Multiple blank fields are all listed in error message")
    void multipleBlankFieldsListed() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                IdentityValidator.validateCreation(
                        "", null, "",
                        "10 Downing Street", "", "07700900000"
                ));
        String message = exception.getMessage();
        assertTrue(message.contains("Full name") && message.contains("National identifier")
                && message.contains("Contact email"));
    }

    // --- Full name ---

    @Test
    @DisplayName("Name without space is rejected")
    void nameWithoutSpaceRejected() {
        assertThrows(ValidationException.class, () -> IdentityValidator.validateCreation(
                "John", "15/05/1990", "AB123456C",
                "10 Downing Street", "john@email.com", "07700900000"
        ));
    }

    // --- Date of birth ---

    @Test
    @DisplayName("Valid DOB in dd/mm/yyyy format passes")
    void validDobPasses() {
        assertTrue(IdentityValidator.isValidDateOfBirth("15/05/1990"));
    }

    @Test
    @DisplayName("DOB in yyyy-mm-dd format is rejected")
    void wrongDobFormatRejected() {
        assertFalse(IdentityValidator.isValidDateOfBirth("1990-05-15"));
    }

    @Test
    @DisplayName("DOB with invalid month is rejected")
    void invalidMonthRejected() {
        assertFalse(IdentityValidator.isValidDateOfBirth("15/13/1990"));
    }

    @Test
    @DisplayName("DOB with invalid day is rejected")
    void invalidDayRejected() {
        assertFalse(IdentityValidator.isValidDateOfBirth("32/05/1990"));
    }

    @Test
    @DisplayName("DOB with letters is rejected")
    void dobWithLettersRejected() {
        assertFalse(IdentityValidator.isValidDateOfBirth("ab/cd/efgh"));
    }

    // --- Email ---

    @Test
    @DisplayName("Valid email passes")
    void validEmailPasses() {
        assertTrue(IdentityValidator.isValidEmail("john@email.com"));
    }

    @Test
    @DisplayName("Email without @ is rejected")
    void emailWithoutAtRejected() {
        assertFalse(IdentityValidator.isValidEmail("johnemail.com"));
    }

    @Test
    @DisplayName("Email with two @ is rejected")
    void emailWithTwoAtRejected() {
        assertFalse(IdentityValidator.isValidEmail("john@@email.com"));
    }

    @Test
    @DisplayName("Email without dot after @ is rejected")
    void emailWithoutDotAfterAtRejected() {
        assertFalse(IdentityValidator.isValidEmail("john@emailcom"));
    }

    // --- Phone ---

    @Test
    @DisplayName("Valid UK phone starting with 0 passes")
    void validUkPhonePasses() {
        assertTrue(IdentityValidator.isValidPhone("07700900000"));
    }

    @Test
    @DisplayName("Valid +44 phone passes")
    void validPlus44PhonePasses() {
        assertTrue(IdentityValidator.isValidPhone("+447700900000"));
    }

    @Test
    @DisplayName("Phone too short is rejected")
    void phoneTooShortRejected() {
        assertFalse(IdentityValidator.isValidPhone("0770090"));
    }

    @Test
    @DisplayName("Phone not starting with 0 or +44 is rejected")
    void phoneWrongPrefixRejected() {
        assertFalse(IdentityValidator.isValidPhone("17700900000"));
    }

    @Test
    @DisplayName("Phone with letters is rejected")
    void phoneWithLettersRejected() {
        assertFalse(IdentityValidator.isValidPhone("0770090abc0"));
    }

    @Test
    @DisplayName("+44 phone with wrong digit count is rejected")
    void plus44WrongLengthRejected() {
        assertFalse(IdentityValidator.isValidPhone("+4477009000"));
    }

    // --- Multiple format errors listed together ---

    @Test
    @DisplayName("Multiple format errors are all listed")
    void multipleFormatErrorsListed() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                IdentityValidator.validateCreation(
                        "John", "1990-05-15", "AB123456C",
                        "10 Downing Street", "bademail", "12345"
                ));
        String message = exception.getMessage();
        assertTrue(message.contains("Full name"));
        assertTrue(message.contains("Date of birth"));
        assertTrue(message.contains("Contact email"));
        assertTrue(message.contains("Contact phone"));
    }

    // --- Status transitions ---

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
