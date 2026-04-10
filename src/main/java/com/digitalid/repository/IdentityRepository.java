package com.digitalid.repository;

import com.digitalid.model.DigitalId;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class IdentityRepository {

    private ArrayList<DigitalId> identities;
    private String filePath;

    public IdentityRepository(String filePath) {
        this.filePath = filePath;
        this.identities = new ArrayList<>();
        loadFromFile();
    }

    public void save(DigitalId identity) {
        identities.add(identity);
        saveToFile();
    }

    public DigitalId findById(String id) {
        for (DigitalId identity : identities) {
            if (identity.getId().equals(id)) {
                return identity;
            }
        }
        return null;
    }

    public boolean existsById(String id) {
        return findById(id) != null;
    }

    public ArrayList<DigitalId> findAll() {
        return identities;
    }

    public void update(DigitalId identity) {
        for (int i = 0; i < identities.size(); i++) {
            if (identities.get(i).getId().equals(identity.getId())) {
                identities.set(i, identity);
                saveToFile();
                return;
            }
        }
    }

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    identities.add(DigitalId.fromFileString(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading identities: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (DigitalId identity : identities) {
                writer.write(identity.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving identities: " + e.getMessage());
        }
    }
}
