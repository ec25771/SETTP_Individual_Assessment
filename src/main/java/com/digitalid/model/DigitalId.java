package com.digitalid.model;

import java.util.UUID;
import java.time.LocalDate;

public class DigitalId {

    private String id;
    private String fullName;
    private String dateOfBirth;
    private String nationalIdentifier;
    private String address;
    private String contactEmail;
    private String contactPhone;
    private IdentityStatus status;
    private String createdDate;

    public DigitalId(String fullName, String dateOfBirth, String nationalIdentifier,
                     String address, String contactEmail, String contactPhone) {
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.nationalIdentifier = nationalIdentifier;
        this.address = address;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.status = IdentityStatus.ACTIVE;
        this.createdDate = LocalDate.now().toString();
    }

    private DigitalId(String id, String fullName, String dateOfBirth, String nationalIdentifier,
                      String address, String contactEmail, String contactPhone,
                      IdentityStatus status, String createdDate) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.nationalIdentifier = nationalIdentifier;
        this.address = address;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.status = status;
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNationalIdentifier() {
        return nationalIdentifier;
    }

    public String getAddress() {
        return address;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public IdentityStatus getStatus() {
        return status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setStatus(IdentityStatus status) {
        this.status = status;
    }

    public String toFileString() {
        return id + "|" + fullName + "|" + dateOfBirth + "|" + nationalIdentifier
                + "|" + address + "|" + contactEmail + "|" + contactPhone
                + "|" + status.name() + "|" + createdDate;
    }

    public static DigitalId fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new DigitalId(
                parts[0], parts[1], parts[2], parts[3],
                parts[4], parts[5], parts[6],
                IdentityStatus.valueOf(parts[7]), parts[8]
        );
    }

    @Override
    public String toString() {
        return "[" + id + "] " + fullName
                + " | DOB: " + dateOfBirth
                + " | NI: " + nationalIdentifier
                + " | Status: " + status
                + " | Address: " + address
                + " | Email: " + contactEmail
                + " | Phone: " + contactPhone;
    }
}
