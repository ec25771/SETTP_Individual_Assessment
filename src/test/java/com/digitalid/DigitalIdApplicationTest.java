package com.digitalid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Digital ID Application")
class DigitalIdApplicationTest {

    private InputStream originalIn = System.in;

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        new File("identities.txt").delete();
        new File("audit.txt").delete();
    }

    @Test
    @DisplayName("Application starts and exits without errors")
    void applicationStartsSuccessfully() {
        System.setIn(new ByteArrayInputStream("0\n".getBytes()));
        assertDoesNotThrow(() -> DigitalIdApplication.main(new String[]{}));
    }
}
