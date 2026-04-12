package com.digitalid.portal;

public interface IdentityConsumer {

    String verifyIdentity(String identityId);

    String lookupIdentity(String identityId);

    String getOrganisationName();
}
