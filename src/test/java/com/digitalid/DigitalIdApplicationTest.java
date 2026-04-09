package com.digitalid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Digital ID Application")
class DigitalIdApplicationTest {

    @Test
    @DisplayName("Application starts without errors")
    void applicationStartsSuccessfully() {
        assertDoesNotThrow(() -> DigitalIdApplication.main(new String[]{}));
    }
}
