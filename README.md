# Digital ID Platform

A Java 17 console-based backend for managing and verifying digital identities across a federated ecosystem of organisations.

## Overview

The Digital ID Platform enables a central authority to manage digital identities while allowing authorised consumer organisations to verify and look up identity information through dedicated portals.

## Features

- **Identity Lifecycle Management** — Create, update, and manage the status of Digital IDs (Active → Suspended → Revoked)
- **Input Validation** — Name, DOB (dd/mm/yyyy), email, and phone format checks with all errors reported together
- **Organisation Portals** — Four portals with tailored verification, lookup, and eligibility checks
- **Audit Trail** — Append-only file-based log recording all key actions
- **File-Based Persistence** — Pipe-delimited text files for identity and audit data
- **Authorisation Enforcement** — Employer and Bank portals are denied access to identity attributes

## Status Transitions

ACTIVE <---> SUSPENDED ---> REVOKED

- REVOKED is terminal — no transitions out
- Same-to-same transitions are rejected

## Organisation Portals

| Portal | Verify | Lookup | Extra |
|--------|--------|--------|-------|
| Tax Authority | Active/inactive check | Name + National ID | Tax period suspension check |
| Driving Licence | Active/suspended/revoked | Name + DOB | Age eligibility (min 17) |
| Employer | Active/inactive check | Denied | — |
| Bank | Active/inactive check | Denied | — |

## Project Structure

- **DigitalIdApplication.java** — Console menu (entry point)
- **model/**
    - DigitalId.java — Core entity with file serialisation
    - IdentityStatus.java — ACTIVE, SUSPENDED, REVOKED
    - OrganisationType.java — Organisation type enum
- **repository/**
    - IdentityRepository.java — File-based CRUD (pipe-delimited)
- **service/**
    - IdentityService.java — Business logic with validation and audit
- **validation/**
    - IdentityValidator.java — Input format and status transition rules
- **exception/**
    - ValidationException.java
    - IdentityNotFoundException.java
    - InvalidStatusTransitionException.java
- **audit/**
    - AuditLog.java — Append-only file-based audit log
    - AuditAction.java — IDENTITY_CREATED, UPDATED, STATUS_CHANGED, etc.
- **portal/**
    - IdentityConsumer.java — Interface: verifyIdentity, lookupIdentity
    - TaxPortal.java
    - EmployerPortal.java
    - DrivingLicencePortal.java
    - BankPortal.java

## Data Files

Identities are stored in `identities.txt` using pipe-delimited format:

```
id|fullName|dateOfBirth|nationalIdentifier|address|email|phone|status|createdDate
```

Audit entries are stored in `audit.txt`:

```
timestamp|action|identityId|performedBy|details
```

## Prerequisites

- Java 17 or later
- Maven 3.8+

## How to Build and Run

```bash
# Run all tests
mvn clean test

# Run the application
mvn clean compile exec:java
```

## Testing

80 JUnit 5 tests across 6 test classes covering model, repository, service, validation, portal, and application layers. CI runs automatically on push via GitHub Actions.

## GitHub Repository

https://github.com/ec25771/SETTP_Individual_Assessment
