package ru.sberbank.pprb.sbbol.partners;

import com.sbt.pprb.ac.graph.collection.GraphCollection;
import org.springframework.stereotype.Service;
import ru.sberbank.pprb.sbbol.partners.graph.RenterGraph;
import ru.sberbank.pprb.sbbol.partners.graph.get.RenterGet;
import ru.sberbank.pprb.sbbol.partners.graph.with.RenterCollectionWith;
import ru.sberbank.pprb.sbbol.partners.grasp.RenterGrasp;
import ru.sberbank.pprb.sbbol.partners.mapper.RenterMapper;
import ru.sberbank.pprb.sbbol.partners.packet.RenterRef;
import ru.sberbank.pprb.sbbol.partners.packet.packet.Packet;
import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterListResponse;
import sbp.sbt.sdk.DataspaceCorePacketClient;
import sbp.sbt.sdk.exception.SdkJsonRpcClientException;
import sbp.sbt.sdk.search.DataspaceCoreSearchClient;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RenterDao {
    private final DataspaceCorePacketClient packetClient;
    private final DataspaceCoreSearchClient searchClient;

    private final RenterMapper mapper;

    public RenterDao(DataspaceCorePacketClient packetClient, DataspaceCoreSearchClient searchClient, RenterMapper mapper) {
        this.packetClient = packetClient;
        this.searchClient = searchClient;
        this.mapper = mapper;
    }

    /**
     * Получение списка арендаторов по заданному фильтру
     *
     * @param renterFilter фильтр для поиска арендаторов
     * @return список арендаторов, удовлетворяющих заданному фильтру
     */
    RenterListResponse getRenters(RenterFilter renterFilter) {
        Packet packet = new Packet();

        RenterCollectionWith<? extends RenterGrasp> collectionWith = RenterGraph.createCollection()
                .withUuid()
                .withRenterType()
                .withLegalName()
                .withInn()
                .withKpp()
                .withOgrn()
                .withOkpo()
                .withLastName()
                .withFirstName()
                .withMiddleName()
                .withDulType()
                .withDulSerie()
                .withDulNumber()
                .withDulDivisionIssue()
                .withDulDateIssue()
                .withDulDivisionCode()
                .withAccount()
                .withBankBic()
                .withBankName()
                .withBankAccount()
                .withPhoneNumbers()
                .withEmails()
                .withLegalAddress()
                .withPhysicalAddress();

        GraphCollection<RenterGet> result;
        try {
            result = searchClient.searchRenter(collectionWith);
        } catch (SdkJsonRpcClientException e) {
            throw new RuntimeException(e);
        }

        List<Renter> contracts = result.getCollection()
                .stream()
                .sorted(Comparator.comparing(RenterGet::getObjectId).reversed())
                .map(mapper::renterToFront)
                .collect(Collectors.toList());

        return new RenterListResponse().items(contracts);
    }

    /**
     * Создание нового арендатора
     *
     * @param renter данные арендатора
     * @return арендатор
     */
    Renter createRenter(Renter renter) {
        Packet packet = new Packet();

        String uuid = UUID.randomUUID().toString();
        try {
            RenterRef renterRef = packet.renter.create(param -> {
                mapper.createRenterParam(renter, param);
                param.setUuid(uuid);
            });

            packetClient.execute(packet);
            return getRenter(uuid);
        } catch (SdkJsonRpcClientException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Редактирование арендатора
     *
     * @param renter новые данные арендатора
     * @return арендатор
     */
    Renter updateRenter(Renter renter) {
        Packet packet = new Packet();
        try {
            GraphCollection<RenterGet> renterCollection = searchClient.searchRenter(
                    with -> with
                            .setWhere(where -> where.uuidEq(renter.getUuid())));


            if (renterCollection.size() == 0) {
                throw new RuntimeException("Запись не найдена для редактирования");
            }
            RenterGet renterGet = renterCollection.get(0);

            packet.renter.update(
                    RenterRef.of(renterGet.getObjectId()),
                    updateChargeParam -> {
                        mapper.updateRenterParam(renter, updateChargeParam);
                    });
            packetClient.execute(packet);

            return getRenter(renter.getUuid());
        } catch (SdkJsonRpcClientException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получение арендатора по идентификатору договора
     *
     * @param renterGuid Идентификатор арендатора
     * @return арендатор
     */
    Renter getRenter(String renterGuid) {
        Packet packet = new Packet();
        RenterCollectionWith<? extends RenterGrasp> renterCollectionWith =
                RenterGraph.createCollection()
                        .withUuid()
                        .withRenterType()
                        .withLegalName()
                        .withInn()
                        .withKpp()
                        .withOgrn()
                        .withOkpo()
                        .withLastName()
                        .withFirstName()
                        .withMiddleName()
                        .withDulType()
                        .withDulSerie()
                        .withDulNumber()
                        .withDulDivisionIssue()
                        .withDulDateIssue()
                        .withDulDivisionCode()
                        .withAccount()
                        .withBankBic()
                        .withBankName()
                        .withBankAccount()
                        .withPhoneNumbers()
                        .withEmails()
                        .withLegalAddress()
                        .withPhysicalAddress()
                        .setWhere(where -> where.uuidEq(renterGuid));

        GraphCollection<RenterGet> renterSearchResult;
        try {
            renterSearchResult = searchClient.searchRenter(renterCollectionWith);
        } catch (SdkJsonRpcClientException e) {
            throw new RuntimeException(e);
        }

        if (renterSearchResult.isEmpty()) {
            return null;
        } else if (renterSearchResult.size() > 1) {
            throw new IndexOutOfBoundsException("Too many results Renters found");
        }

        RenterGet renterGet = renterSearchResult.get(0);
        return mapper.renterToFront(renterGet);
    }

}
