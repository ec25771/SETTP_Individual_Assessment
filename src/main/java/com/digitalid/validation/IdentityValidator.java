package com.digitalid.validation;

import com.digitalid.model.IdentityStatus;
import com.digitalid.exception.InvalidStatusTransitionException;
import com.digitalid.exception.ValidationException;

import java.util.ArrayList;
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

        ArrayList<String> errors = new ArrayList<>();

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (entry.getValue() == null || entry.getValue().trim().isEmpty()) {
                errors.add(entry.getKey() + " must not be blank");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        if (!fullName.contains(" ")) {
            errors.add("Full name must contain at least a first and last name");
        }

        if (!isValidDateOfBirth(dateOfBirth)) {
            errors.add("Date of birth must be in dd/mm/yyyy format");
        }

        if (!isValidEmail(contactEmail)) {
            errors.add("Contact email must contain exactly one @ and at least one . after the @");
        }

        if (!isValidPhone(contactPhone)) {
            errors.add("Contact phone must be 11 digits starting with 0, or +44 followed by 10 digits");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }

    public static boolean isValidDateOfBirth(String dob) {
        if (dob.length() != 10) {
            return false;
        }
        if (dob.charAt(2) != '/' || dob.charAt(5) != '/') {
            return false;
        }
        String day = dob.substring(0, 2);
        String month = dob.substring(3, 5);
        String year = dob.substring(6, 10);

        if (!isAllDigits(day) || !isAllDigits(month) || !isAllDigits(year)) {
            return false;
        }

        int d = Integer.parseInt(day);
        int m = Integer.parseInt(month);

        return d >= 1 && d <= 31 && m >= 1 && m <= 12;
    }

    public static boolean isValidEmail(String email) {
        int atCount = 0;
        int atIndex = -1;
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                atCount++;
                atIndex = i;
            }
        }
        if (atCount != 1) {
            return false;
        }

        String afterAt = email.substring(atIndex + 1);
        return afterAt.contains(".");
    }

    public static boolean isValidPhone(String phone) {
        if (phone.startsWith("+44")) {
            String digits = phone.substring(3);
            return digits.length() == 10 && isAllDigits(digits);
        }
        if (phone.startsWith("0")) {
            return phone.length() == 11 && isAllDigits(phone);
        }
        return false;
    }

    private static boolean isAllDigits(String value) {
        for (char c : value.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
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
