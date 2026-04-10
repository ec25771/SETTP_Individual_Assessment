package com.digitalid.validation;

import com.digitalid.model.IdentityStatus;
import com.digitalid.exception.InvalidStatusTransitionException;
import com.digitalid.exception.ValidationException;

import java.util.LinkedHashMap;
import java.util.Map;

public class IdentityValidator {

    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " must not be blank");
        }
    }

    public static void validateCreation(String fullName, String dateOfBirth,
                                        String nationalIdentifier, String address,
                                        String contactEmail, String contactPhone) {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("Full name", fullName);
        fields.put("Date of birth", dateOfBirth);
        fields.put("National identifier", nationalIdentifier);
        fields.put("Address", address);
        fields.put("Contact email", contactEmail);
        fields.put("Contact phone", contactPhone);

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            validateNotBlank(entry.getValue(), entry.getKey());
        }
    }

    public static void validateStatusTransition(IdentityStatus current, IdentityStatus target) {
        if (current == target) {
            throw new InvalidStatusTransitionException(current, target);
        }
        if (current == IdentityStatus.REVOKED) {
            throw new InvalidStatusTransitionException(current, target);
        }
    }
}
