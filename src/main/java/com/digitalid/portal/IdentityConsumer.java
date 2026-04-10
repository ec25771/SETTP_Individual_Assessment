package com.digitalid.portal;

import com.digitalid.model.DigitalId;

public interface IdentityConsumer {

    String verifyIdentity(String identityId);

    String getOrganisationName();
}
