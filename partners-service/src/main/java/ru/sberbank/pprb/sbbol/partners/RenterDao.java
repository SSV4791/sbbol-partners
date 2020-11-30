package ru.sberbank.pprb.sbbol.partners;

import org.springframework.stereotype.Service;
import sbp.sbt.sdk.DataspaceCorePacketClient;
import sbp.sbt.sdk.search.DataspaceCoreSearchClient;

@Service
public class RenterDao {
    private final DataspaceCorePacketClient packetClient;
    private final DataspaceCoreSearchClient searchClient;

    public RenterDao(DataspaceCorePacketClient packetClient, DataspaceCoreSearchClient searchClient) {
        this.packetClient = packetClient;
        this.searchClient = searchClient;
    }
}
