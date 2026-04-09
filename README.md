# Digital ID Platform

A backend system for managing and verifying digital identities across a federated ecosystem of organisations.

## Overview

The Digital ID Platform enables a central authority to manage digital identities (creation, update, status changes) while allowing authorised consumer organisations to verify and look up identity information through dedicated portals.

### Key Capabilities

- **Identity Lifecycle Management** — Create, update, and manage the status of Digital IDs (Central Authority only)
- **Identity Verification & Lookup** — Organisation-specific verification with tailored responses
- **Organisation Portals** — Role-based access for tax, driving licence, welfare, employer, bank, and other organisations
- **Audit Trail** — All key actions are logged for traceability
- **Validation & Authorisation** — All requests validated against business rules and organisation permissions

## System Structure

```
src/main/java/com/digitalid/
├── model/          # Domain entities and value objects (DigitalId, IdentityStatus, etc.)
├── service/        # Business logic (IdentityService, VerificationService)
├── repository/     # Data storage layer
├── validation/     # Input validation rules
├── audit/          # Audit trail logging
├── portal/         # Organisation portal interfaces and implementations
├── exception/      # Custom exception types
└── DigitalIdApplication.java   # Main entry point
```

## Prerequisites

- Java 17 or later
- Maven 3.8+

## How to Build and Run

```bash
# Compile the project
mvn clean compile

# Run all tests
mvn clean test

# Run the application
mvn clean compile exec:java
```

## GitHub Repository

[Repository link to be added]
