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
            printLoginMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    centralAuthorityMenu();
                    break;
                case "2":
                    taxAuthorityMenu();
                    break;
                case "3":
                    drivingLicenceMenu();
                    break;
                case "4":
                    employerMenu();
                    break;
                case "5":
                    bankMenu();
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

    private static void printLoginMenu() {
        System.out.println();
        System.out.println("--- Select Organisation ---");
        System.out.println("1. Central Authority");
        System.out.println("2. Tax Authority");
        System.out.println("3. Driving Licence Authority");
        System.out.println("4. Employer");
        System.out.println("5. Bank");
        System.out.println("0. Exit");
        System.out.print("Log in as: ");
    }

    private static void centralAuthorityMenu() {
        System.out.println("\n[Logged in as Central Authority]");
        boolean active = true;
        while (active) {
            System.out.println();
            System.out.println("--- Central Authority ---");
            System.out.println("1. Create Identity");
            System.out.println("2. View Identity");
            System.out.println("3. List All Identities");
            System.out.println("4. Update Address");
            System.out.println("5. Update Contact Email");
            System.out.println("6. Update Contact Phone");
            System.out.println("7. Change Status");
            System.out.println("8. View Audit Log");
            System.out.println("0. Log out");
            System.out.print("Choose an option: ");
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
                    viewAuditLog();
                    break;
                case "0":
                    active = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void taxAuthorityMenu() {
        System.out.println("\n[Logged in as Tax Authority]");
        boolean active = true;
        while (active) {
            System.out.println();
            System.out.println("--- Tax Authority ---");
            System.out.println("1. Verify Identity");
            System.out.println("2. Verify for Tax Period");
            System.out.println("3. Lookup Identity");
            System.out.println("0. Log out");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    verifyViaTax();
                    break;
                case "2":
                    verifyForTaxPeriod();
                    break;
                case "3":
                    lookupViaTax();
                    break;
                case "0":
                    active = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void drivingLicenceMenu() {
        System.out.println("\n[Logged in as Driving Licence Authority]");
        boolean active = true;
        while (active) {
            System.out.println();
            System.out.println("--- Driving Licence Authority ---");
            System.out.println("1. Verify Identity");
            System.out.println("2. Check Eligibility");
            System.out.println("3. Lookup Identity");
            System.out.println("0. Log out");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    verifyViaDrivingLicence();
                    break;
                case "2":
                    checkDrivingEligibility();
                    break;
                case "3":
                    lookupViaDrivingLicence();
                    break;
                case "0":
                    active = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void employerMenu() {
        System.out.println("\n[Logged in as Employer]");
        boolean active = true;
        while (active) {
            System.out.println();
            System.out.println("--- Employer ---");
            System.out.println("1. Verify Identity");
            System.out.println("2. Lookup Identity");
            System.out.println("0. Log out");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    verifyViaEmployer();
                    break;
                case "2":
                    lookupViaEmployer();
                    break;
                case "0":
                    active = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void bankMenu() {
        System.out.println("\n[Logged in as Bank]");
        boolean active = true;
        while (active) {
            System.out.println();
            System.out.println("--- Bank ---");
            System.out.println("1. Verify Identity");
            System.out.println("2. Lookup Identity");
            System.out.println("0. Log out");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    verifyViaBank();
                    break;
                case "2":
                    lookupViaBank();
                    break;
                case "0":
                    active = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
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

    private static void verifyViaTax() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        try {
            System.out.println("Result: " + taxPortal.verifyIdentity(id));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void verifyForTaxPeriod() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Period start (dd/mm/yyyy): ");
        String start = scanner.nextLine().trim();
        System.out.print("Period end (dd/mm/yyyy): ");
        String end = scanner.nextLine().trim();
        try {
            System.out.println("Result: " + taxPortal.verifyForTaxPeriod(id, start, end));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void lookupViaTax() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        try {
            System.out.println("Result: " + taxPortal.lookupIdentity(id));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void verifyViaDrivingLicence() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        try {
            System.out.println("Result: " + drivingLicencePortal.verifyIdentity(id));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void checkDrivingEligibility() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        try {
            System.out.println("Result: " + drivingLicencePortal.checkEligibility(id));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void lookupViaDrivingLicence() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        try {
            System.out.println("Result: " + drivingLicencePortal.lookupIdentity(id));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void verifyViaEmployer() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        try {
            System.out.println("Result: " + employerPortal.verifyIdentity(id));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void lookupViaEmployer() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        try {
            System.out.println("Result: " + employerPortal.lookupIdentity(id));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void verifyViaBank() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        try {
            System.out.println("Result: " + bankPortal.verifyIdentity(id));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void lookupViaBank() {
        System.out.print("Enter identity ID: ");
        String id = scanner.nextLine().trim();
        try {
            System.out.println("Result: " + bankPortal.lookupIdentity(id));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
