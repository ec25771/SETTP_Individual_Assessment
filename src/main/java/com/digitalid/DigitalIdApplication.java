package com.digitalid;

import com.digitalid.audit.AuditLog;
import com.digitalid.model.DigitalId;
import com.digitalid.model.IdentityStatus;
import com.digitalid.portal.BankPortal;
import com.digitalid.portal.DrivingLicencePortal;
import com.digitalid.portal.EmployerPortal;
import com.digitalid.portal.TaxPortal;
import com.digitalid.repository.IdentityRepository;
import com.digitalid.service.IdentityService;

import java.util.ArrayList;
import java.util.Scanner;

public class DigitalIdApplication {

    private static IdentityService service;
    private static AuditLog auditLog;
    private static TaxPortal taxPortal;
    private static EmployerPortal employerPortal;
    private static DrivingLicencePortal drivingLicencePortal;
    private static BankPortal bankPortal;
    private static Scanner scanner;

    public static void main(String[] args) {
        IdentityRepository repository = new IdentityRepository("identities.txt");
        auditLog = new AuditLog("audit.txt");
        service = new IdentityService(repository, auditLog);
        taxPortal = new TaxPortal(service, auditLog);
        employerPortal = new EmployerPortal(service, auditLog);
        drivingLicencePortal = new DrivingLicencePortal(service, auditLog);
        bankPortal = new BankPortal(service, auditLog);
        scanner = new Scanner(System.in);

        System.out.println("=== Digital ID Platform ===");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createIdentity();
                    break;
                case "2":
                    viewIdentity();
                    break;
                case "3":
                    listAllIdentities();
                    break;
                case "4":
                    updateAddress();
                    break;
                case "5":
                    updateContactEmail();
                    break;
                case "6":
                    updateContactPhone();
                    break;
                case "7":
                    changeStatus();
                    break;
                case "8":
                    verifyViaPortal();
                    break;
                case "9":
                    lookupViaPortal();
                    break;
                case "10":
                    viewAuditLog();
                    break;
                case "0":
                    running = false;
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("--- Main Menu ---");
        System.out.println("1.  Create Identity");
        System.out.println("2.  View Identity");
        System.out.println("3.  List All Identities");
        System.out.println("4.  Update Address");
        System.out.println("5.  Update Contact Email");
        System.out.println("6.  Update Contact Phone");
        System.out.println("7.  Change Status");
        System.out.println("8.  Verify Identity (Portal)");
        System.out.println("9.  Lookup Identity (Portal)");
        System.out.println("10. View Audit Log");
        System.out.println("0.  Exit");
        System.out.print("Choose an option: ");
    }

    private static void createIdentity() {
        System.out.print("Full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Date of birth (dd/mm/yyyy): ");
        String dob = scanner.nextLine();
        System.out.print("National identifier: ");
        String nid = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Contact email: ");
        String email = scanner.nextLine();
        System.out.print("Contact phone: ");
        String phone = scanner.nextLine();

        try {
            DigitalId identity = service.createIdentity(fullName, dob, nid, address, email, phone);
            System.out.println("Identity created: " + identity.getId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewIdentity() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();

        try {
            DigitalId identity = service.getIdentity(id);
            System.out.println(identity);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listAllIdentities() {
        ArrayList<DigitalId> identities = service.getAllIdentities();
        if (identities.isEmpty()) {
            System.out.println("No identities found.");
            return;
        }
        for (DigitalId identity : identities) {
            System.out.println(identity);
        }
    }

    private static void updateAddress() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("New address: ");
        String address = scanner.nextLine();

        try {
            service.updateAddress(id, address);
            System.out.println("Address updated.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateContactEmail() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("New email: ");
        String email = scanner.nextLine();

        try {
            service.updateContactEmail(id, email);
            System.out.println("Contact email updated.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateContactPhone() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("New phone: ");
        String phone = scanner.nextLine();

        try {
            service.updateContactPhone(id, phone);
            System.out.println("Contact phone updated.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void changeStatus() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        System.out.println("1. ACTIVE");
        System.out.println("2. SUSPENDED");
        System.out.println("3. REVOKED");
        System.out.print("Choose new status: ");
        String choice = scanner.nextLine().trim();

        IdentityStatus newStatus;
        switch (choice) {
            case "1":
                newStatus = IdentityStatus.ACTIVE;
                break;
            case "2":
                newStatus = IdentityStatus.SUSPENDED;
                break;
            case "3":
                newStatus = IdentityStatus.REVOKED;
                break;
            default:
                System.out.println("Invalid status.");
                return;
        }

        try {
            service.changeStatus(id, newStatus);
            System.out.println("Status changed to " + newStatus + ".");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void verifyViaPortal() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        System.out.println("1. Tax Authority - Basic Verification");
        System.out.println("2. Tax Authority - Tax Period Verification");
        System.out.println("3. Employer");
        System.out.println("4. Driving Licence Authority - Basic Verification");
        System.out.println("5. Driving Licence Authority - Eligibility Check");
        System.out.println("6. Bank");
        System.out.print("Choose portal: ");
        String choice = scanner.nextLine().trim();

        try {
            String result;
            switch (choice) {
                case "1":
                    result = taxPortal.verifyIdentity(id);
                    break;
                case "2":
                    System.out.print("Period start (dd/mm/yyyy): ");
                    String start = scanner.nextLine().trim();
                    System.out.print("Period end (dd/mm/yyyy): ");
                    String end = scanner.nextLine().trim();
                    result = taxPortal.verifyForTaxPeriod(id, start, end);
                    break;
                case "3":
                    result = employerPortal.verifyIdentity(id);
                    break;
                case "4":
                    result = drivingLicencePortal.verifyIdentity(id);
                    break;
                case "5":
                    result = drivingLicencePortal.checkEligibility(id);
                    break;
                case "6":
                    result = bankPortal.verifyIdentity(id);
                    break;
                default:
                    System.out.println("Invalid portal.");
                    return;
            }
            System.out.println("Result: " + result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void lookupViaPortal() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        System.out.println("1. Tax Authority");
        System.out.println("2. Employer");
        System.out.println("3. Driving Licence Authority");
        System.out.println("4. Bank");
        System.out.print("Choose portal: ");
        String choice = scanner.nextLine().trim();

        try {
            String result;
            switch (choice) {
                case "1":
                    result = taxPortal.lookupIdentity(id);
                    break;
                case "2":
                    result = employerPortal.lookupIdentity(id);
                    break;
                case "3":
                    result = drivingLicencePortal.lookupIdentity(id);
                    break;
                case "4":
                    result = bankPortal.lookupIdentity(id);
                    break;
                default:
                    System.out.println("Invalid portal.");
                    return;
            }
            System.out.println("Result: " + result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAuditLog() {
        System.out.println("1. View all entries");
        System.out.println("2. View entries for an identity");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine().trim();

        ArrayList<String> entries;
        if (choice.equals("2")) {
            System.out.print("Enter identity ID: ");
            String id = scanner.nextLine().trim();
            entries = auditLog.getEntriesForIdentity(id);
        } else {
            entries = auditLog.getEntries();
        }

        if (entries.isEmpty()) {
            System.out.println("No audit entries found.");
            return;
        }
        for (String entry : entries) {
            System.out.println(entry);
        }
    }
}
