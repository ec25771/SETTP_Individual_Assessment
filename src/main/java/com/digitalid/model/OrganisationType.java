package com.digitalid.model;

public enum OrganisationType {
    CENTRAL_AUTHORITY("Central Authority"),
    TAX_AUTHORITY("Tax Authority"),
    DRIVING_LICENCE("Driving Licence Authority"),
    EMPLOYER("Employer"),
    BANK("Bank"),
    WELFARE("Welfare Service");

    private final String displayName;

    OrganisationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
