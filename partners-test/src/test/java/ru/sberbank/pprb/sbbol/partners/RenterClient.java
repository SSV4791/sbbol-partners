package ru.sberbank.pprb.sbbol.partners;

import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import ru.sberbank.pprb.sbbol.partners.renter.model.*;

import javax.annotation.Nonnull;

@Component
public class RenterClient {
    private final JsonRestClient jsonRestClient;

    public RenterClient(WebApplicationContext webApplicationContext) {
        this.jsonRestClient = new JsonRestClient(webApplicationContext);
    }

    @Nonnull
    public Renter createRenter(@Nonnull Renter renter) {
        return jsonRestClient.post("renter/create", renter, Renter.class);
    }

    @Nonnull
    public Renter getRenter(@Nonnull RenterIdentifier renterIdentifier) {
        return jsonRestClient.post("renter/get", renterIdentifier, Renter.class);
    }

    @Nonnull
    public Renter updateRenter(@Nonnull Renter renter) {
        return jsonRestClient.post("renter/update", renter, Renter.class);
    }

    @Nonnull
    public RenterListResponse getRenters(@Nonnull RenterFilter renterFilter) {
        return jsonRestClient.post("renter/view", renterFilter, RenterListResponse.class);
    }

    @Nonnull
    public Version version() {
        return jsonRestClient.get("renter/version", null, Version.class);
    }

}
