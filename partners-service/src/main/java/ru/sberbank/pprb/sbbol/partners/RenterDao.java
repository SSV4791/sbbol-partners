package ru.sberbank.pprb.sbbol.partners;

import com.sbt.pprb.ac.graph.AbstractProxyCollectionWith;
import com.sbt.pprb.ac.graph.collection.GraphCollection;
import org.springframework.stereotype.Service;
import ru.sberbank.pprb.sbbol.partners.graph.RenterGraph;
import ru.sberbank.pprb.sbbol.partners.graph.get.LegalAddressGet;
import ru.sberbank.pprb.sbbol.partners.graph.get.PhysicalAddressGet;
import ru.sberbank.pprb.sbbol.partners.graph.get.RenterGet;
import ru.sberbank.pprb.sbbol.partners.graph.with.RenterCollectionWith;
import ru.sberbank.pprb.sbbol.partners.grasp.RenterGrasp;
import ru.sberbank.pprb.sbbol.partners.mapper.RenterMapper;
import ru.sberbank.pprb.sbbol.partners.packet.LegalAddressRef;
import ru.sberbank.pprb.sbbol.partners.packet.PhysicalAddressRef;
import ru.sberbank.pprb.sbbol.partners.packet.RenterRef;
import ru.sberbank.pprb.sbbol.partners.packet.packet.Packet;
import ru.sberbank.pprb.sbbol.partners.renter.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterListResponse;
import sbp.sbt.sdk.DataspaceCorePacketClient;
import sbp.sbt.sdk.exception.SdkJsonRpcClientException;
import sbp.sbt.sdk.search.DataspaceCoreSearchClient;

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

        RenterCollectionWith<? extends RenterGrasp> collectionWith = RenterGraph.createCollection()
                .withUuid()
                .withDigitalId()
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
                .withLegalAddress(legalAddressWith -> legalAddressWith
                        .withZipCode()
                        .withRegionCode()
                        .withRegion()
                        .withCity()
                        .withLocality()
                        .withStreet()
                        .withBuilding()
                        .withBuildingBlock()
                        .withFlat()
                )
                .withPhysicalAddress(physicalAddressWith -> physicalAddressWith
                        .withZipCode()
                        .withRegionCode()
                        .withRegion()
                        .withCity()
                        .withLocality()
                        .withStreet()
                        .withBuilding()
                        .withBuildingBlock()
                        .withFlat())
                .setWhere(where -> where.digitalIdEq(renterFilter.getDigitalId()))
                .setSortingAdvanced(advancedSortBuilder -> advancedSortBuilder.desc(RenterGrasp::objectId));

        Paginator paginator = new Paginator(renterFilter.getPagination());
        paginator.update(collectionWith);

        GraphCollection<RenterGet> result;
        try {
            result = searchClient.searchRenter(collectionWith);
        } catch (SdkJsonRpcClientException e) {
            throw new RuntimeException(e);
        }

        List<Renter> contracts = result.getCollection()
                .stream()
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

            if (renter.getPhysicalAddress() != null) {
                packet.physicalAddress.create(param -> {
                    mapper.createAddressParam(renter.getPhysicalAddress(), param);
                    param.setRenter(renterRef);
                });
            }

            if (renter.getLegalAddress() != null) {
                packet.legalAddress.create(param -> {
                    mapper.createAddressParam(renter.getLegalAddress(), param);
                    param.setRenter(renterRef);
                });
            }
            packetClient.execute(packet);
            return getRenter(uuid, renter.getDigitalId());
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
                            .withPhysicalAddress()
                            .withLegalAddress()
                            .setWhere(where -> where.uuidEq(renter.getUuid())));

            if (renterCollection.size() != 1) {
                throw new RuntimeException("Запись не найдена или записей больше 1");
            }
            RenterGet renterGet = renterCollection.get(0);

            packet.renter.update(
                    RenterRef.of(renterGet.getObjectId()),
                    updateChargeParam -> {
                        mapper.updateRenterParam(renter, updateChargeParam);
                    });

            PhysicalAddressGet physicalAddress = renterGet.getPhysicalAddress();
            packet.physicalAddress.update(
                    PhysicalAddressRef.of(physicalAddress.getObjectId()),
                    updateAddressParam -> {
                        mapper.updateAddressParam(renter.getPhysicalAddress(), updateAddressParam);
                    });

            LegalAddressGet legalAddress = renterGet.getLegalAddress();
            packet.legalAddress.update(
                    LegalAddressRef.of(legalAddress.getObjectId()),
                    updateAddressParam -> {
                        mapper.updateAddressParam(renter.getLegalAddress(), updateAddressParam);
                    });

            packetClient.execute(packet);

            return getRenter(renter.getUuid(), renter.getDigitalId());
        } catch (SdkJsonRpcClientException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получение арендатора по идентификатору договора
     *
     * @param renterUuid Идентификатор арендатора
     * @return арендатор
     */
    Renter getRenter(String renterUuid, String digitalId) {
        RenterCollectionWith<? extends RenterGrasp> renterCollectionWith =
                RenterGraph.createCollection()
                        .withUuid()
                        .withDigitalId()
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
                        .withLegalAddress(legalAddressWith -> legalAddressWith
                                    .withZipCode()
                                    .withRegionCode()
                                    .withRegion()
                                    .withCity()
                                    .withLocality()
                                    .withStreet()
                                    .withBuilding()
                                    .withBuildingBlock()
                                    .withFlat()
                        )
                        .withPhysicalAddress(physicalAddressWith -> physicalAddressWith
                                .withZipCode()
                                .withRegionCode()
                                .withRegion()
                                .withCity()
                                .withLocality()
                                .withStreet()
                                .withBuilding()
                                .withBuildingBlock()
                                .withFlat())
                        .setWhere(where -> where.uuidEq(renterUuid).and(where.digitalIdEq(digitalId)));

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

    public class Paginator {
        private final Pagination pagination;

        public Paginator(Pagination pagination) {
            this.pagination = pagination;
        }

        public void update(AbstractProxyCollectionWith collectionWith) {
            if (pagination != null) {
                if (pagination.getOffset() != null) {
                    collectionWith.setOffset(pagination.getOffset());
                }
                if (pagination.getCount() != null) {
                    collectionWith.setLimit(pagination.getCount() + 1);
                }
            }
        }

        public Pagination create(GraphCollection graphCollection) {
            int offset = pagination != null && pagination.getOffset() != null ? pagination.getOffset() : 0;
            boolean hasNextPage = pagination != null && pagination.getCount() != null ? graphCollection.size() > pagination.getCount() : false;
            int count = hasNextPage ? pagination.getCount() : graphCollection.size();

            return new Pagination().offset(offset).count(count).hasNextPage(hasNextPage);
        }

        public int getLimit() {
            return pagination != null && pagination.getCount() != null ? pagination.getCount() : Integer.MAX_VALUE;
        }
    }

}
