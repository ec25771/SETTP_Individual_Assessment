package com.digitalid.audit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class AuditLog {

    private String filePath;

    public AuditLog(String filePath) {
        this.filePath = filePath;
    }

    public void log(AuditAction action, String identityId, String performedBy, String details) {
        String entry = LocalDateTime.now() + "|" + action + "|" + identityId
                + "|" + performedBy + "|" + details;
        appendToFile(entry);
    }

    public ArrayList<String> getEntries() {
        return readFile();
    }

    public ArrayList<String> getEntriesForIdentity(String identityId) {
        ArrayList<String> all = readFile();
        ArrayList<String> filtered = new ArrayList<>();
        for (String entry : all) {
            if (entry.contains("|" + identityId + "|")) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    private void appendToFile(String entry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(entry);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing audit log: " + e.getMessage());
        }
    }

    private ArrayList<String> readFile() {
        ArrayList<String> entries = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return entries;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    entries.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading audit log: " + e.getMessage());
        }
        return entries;
    }
}
