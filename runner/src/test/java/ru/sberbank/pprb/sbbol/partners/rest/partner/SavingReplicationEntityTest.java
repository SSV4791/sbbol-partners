package ru.sberbank.pprb.sbbol.partners.rest.partner;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.exception.SbbolException;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModelResponse;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.ReplicationEntityMapperRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.ERROR;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.CANCEL;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.INIT;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.UPDATING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.BaseAccountSignControllerTest.createValidAccountsSign;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.BaseAccountSignControllerTest.deleteAccountSign;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

class SavingReplicationEntityTest extends BaseAccountControllerTest {

    @Autowired
    private ReplicationEntityMapperRegistry mapperRegistry;

    @Autowired
    private ReplicationRepository replicationRepository;

    @MockBean
    private LegacySbbolAdapter legacySbbolAdapter;

    @Autowired
    private ReplicationJob replicationJob;

    @Test
    void testReplicationEntityMapperRegistry() {
        var expectedReplicationEntityTypes = List.of(
            CREATING_COUNTERPARTY,
            UPDATING_COUNTERPARTY,
            DELETING_COUNTERPARTY,
            CREATING_SIGN,
            DELETING_SIGN
        );
        assertThat(mapperRegistry)
            .isNotNull();
        assertThat(mapperRegistry.getReplicationEntityTypeSet())
            .containsAll(expectedReplicationEntityTypes);
        assertThat(mapperRegistry.getReplicationEntityMapperList()).asList()
            .hasSize(5);
    }

    @Test
    void testCreatingCounterpartyReplica_whenCreatingAccount() {
        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());
        doReturn(null)
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .create(any(), any());
        var partner = createValidPartner();
        var account = createValidAccount(
            partner.getId(),
            partner.getDigitalId()
        );
        var actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        checkReplicationEntityList(
            actualReplicationEntities,
            CREATING_COUNTERPARTY,
            account.getDigitalId(),
            account.getId(),
            INIT,
            0
        );
    }

    @Test
    void testCreatingCounterpartyReplica_whenCreatingPartnerByFullModel() {
        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());
        doReturn(null)
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .create(any(), any());
        var request = PartnerControllerTest.getValidFullModelLegalEntityPartner();
        var createdPartner = post(
            baseRoutePath + "/full-model",
            HttpStatus.CREATED,
            request,
            PartnerCreateFullModelResponse.class
        );
        var createdAccounts = createdPartner.getAccounts();
        for (var account : createdAccounts) {
            var actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
            checkReplicationEntityList(
                actualReplicationEntities,
                CREATING_COUNTERPARTY,
                createdPartner.getDigitalId(),
                account.getId(),
                INIT,
                0
            );
        }
    }

    @Test
    void testUpdatingCounterpartyReplica_whenUpdatingPartner() {
        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());
        doReturn(null)
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .create(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .update(any(), any());
        var partner = createValidPartner();
        var account = createValidAccount(
            partner.getId(),
            partner.getDigitalId()
        );
        put(
            baseRoutePath,
            HttpStatus.OK,
            partner,
            Partner.class
        );
        var actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        checkReplicationEntityList(
            actualReplicationEntities,
            UPDATING_COUNTERPARTY,
            account.getDigitalId(),
            account.getId(),
            INIT,
            0
        );
    }

    @Test
    void testUpdatingCounterpartyReplica_whenUpdatingAccount() {
        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());
        doReturn(null)
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .create(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .update(any(), any());
        var partner = createValidPartner();
        var account = createValidAccount(
            partner.getId(),
            partner.getDigitalId()
        );
        doReturn(new Counterparty())
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        changeAccount(updateAccount(account));
        var actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        checkReplicationEntityList(
            actualReplicationEntities,
            UPDATING_COUNTERPARTY,
            account.getDigitalId(),
            account.getId(),
            INIT,
            0
        );
    }

    @Test
    void testDeletingCounterpartyReplica_whenDeletingAccount() {
        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());
        doReturn(null)
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .create(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .delete(any(), any());
        var partner = createValidPartner();
        var account = createValidAccount(
            partner.getId(),
            partner.getDigitalId()
        );
        doReturn(new Counterparty())
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        deleteAccount(account.getDigitalId(), account.getId());
        var actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        checkReplicationEntityList(
            actualReplicationEntities,
            DELETING_COUNTERPARTY,
            account.getDigitalId(),
            account.getId(),
            INIT,
            0
        );
    }

    @Test
    void testDeletingCounterpartyReplica_whenDeletingPartner() throws JsonProcessingException {
        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());
        doReturn(null)
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .create(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .delete(any(), any());
        var partner = createValidPartner();
        var account = createValidAccount(
            partner.getId(),
            partner.getDigitalId()
        );
        doReturn(new Counterparty())
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        delete(
            "/partners/{digitalId}",
            HttpStatus.NO_CONTENT,
            Map.of("ids", List.of(partner.getId())),
            Map.of("Fraud-Meta-Data", getBase64FraudMetaData()),
            partner.getDigitalId()
        ).getBody();
        var actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        checkReplicationEntityList(
            actualReplicationEntities,
            DELETING_COUNTERPARTY,
            account.getDigitalId(),
            account.getId(),
            INIT,
            0
        );
    }

    @Test
    void testCreatingSingReplica_whenCreatingAccountSign() throws JsonProcessingException {
        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());
        doReturn(null)
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .create(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .saveSign(any(), any());
        var partner = createValidPartner();
        var account = createValidAccount(
            partner.getId(),
            partner.getDigitalId()
        );
        doReturn(new Counterparty())
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        createValidAccountsSign(account.getDigitalId(), account.getId(), account.getVersion(), getBase64FraudMetaData());
        var actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        checkReplicationEntityList(
            actualReplicationEntities,
            CREATING_SIGN,
            account.getDigitalId(),
            account.getId(),
            INIT,
            0
        );
    }
    @Test
    void testDeletingSignReplica_whenDeletingSignAccount() throws JsonProcessingException {
        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());
        doReturn(null)
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .create(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .removeSign(any(), any());
        var partner = createValidPartner();
        var account = createValidAccount(
            partner.getId(),
            partner.getDigitalId()
        );
        doReturn(new Counterparty())
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        createValidAccountsSign(account.getDigitalId(), account.getId(), account.getVersion(), getBase64FraudMetaData());
        deleteAccountSign(account.getDigitalId(), account.getId());
        var actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        checkReplicationEntityList(
            actualReplicationEntities,
            DELETING_SIGN,
            account.getDigitalId(),
            account.getId(),
            INIT,
            0
        );
    }

    @Test
    void testRaceConditionResolver_whenUpdatingAccount() throws JsonProcessingException, InterruptedException {
        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());
        doReturn(null)
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .create(any(), any());
        doThrow(SbbolException.class)
            .when(legacySbbolAdapter)
            .update(any(), any());
        var partner = createValidPartner();
        var account = createValidAccount(
            partner.getId(),
            partner.getDigitalId()
        );
        doReturn(new Counterparty())
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        var updatedAccount = changeAccount(updateAccount(account));
        var actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        checkReplicationEntityList(
            actualReplicationEntities,
            UPDATING_COUNTERPARTY,
            account.getDigitalId(),
            account.getId(),
            INIT,
            0
        );
        replicationJob.execute();
        actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        checkReplicationEntityList(
            actualReplicationEntities,
            UPDATING_COUNTERPARTY,
            account.getDigitalId(),
            account.getId(),
            ERROR,
            1
        );
        doReturn(new Counterparty())
            .when(legacySbbolAdapter)
            .update(any(), any());
        changeAccount(updateAccount(updatedAccount));
        actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        assertThat(actualReplicationEntities)
            .isNotNull();
        checkReplicationEntityList(
            actualReplicationEntities,
            UPDATING_COUNTERPARTY,
            account.getDigitalId(),
            account.getId(),
            CANCEL
        );
    }

    @Test
    void testRaceConditionResolver_whenCreatingAccount() throws JsonProcessingException, InterruptedException {
        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());
        doReturn(null)
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        when(legacySbbolAdapter.create(any(), any()))
            .thenReturn(new Counterparty());
        var partner = createValidPartner();
        doReturn(new Counterparty())
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        var account = createValidAccount(
            partner.getId(),
            partner.getDigitalId()
        );
        var actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        actualReplicationEntities = replicationRepository.findByEntityId(UUID.fromString(account.getId()));
        assertThat(actualReplicationEntities)
            .isNotNull();
        checkReplicationEntityList(
            actualReplicationEntities,
            CREATING_COUNTERPARTY,
            account.getDigitalId(),
            account.getId(),
            CANCEL
        );
    }

    private void checkReplicationEntityList(
        List<ReplicationEntity> actualEntities,
        ReplicationEntityType expectedEntityType,
        String expectedDigitalId,
        String expectedAccountId,
        ReplicationEntityStatus entityStatus
    ) {
        checkReplicationEntityList(
            actualEntities,
            expectedEntityType,
            expectedDigitalId,
            expectedAccountId,
            entityStatus,
            null
        );
    }

    private void checkReplicationEntityList(
        List<ReplicationEntity> actualEntities,
        ReplicationEntityType expectedEntityType,
        String expectedDigitalId,
        String expectedAccountId,
        ReplicationEntityStatus entityStatus,
        Integer retry
    ) {
        assertThat(actualEntities).asList()
            .isNotEmpty();
        var expectedReplicationEntity = new ReplicationEntity()
            .digitalId(expectedDigitalId)
            .entityId(UUID.fromString(expectedAccountId))
            .entityType(expectedEntityType)
            .entityStatus(entityStatus);
        var actualReplicationEntities = actualEntities.stream()
            .filter(it -> it.getEntityType() == expectedEntityType)
            .collect(Collectors.toList());
        for (var actualReplicationEntity : actualReplicationEntities) {
            if (Objects.nonNull(retry)) {
                expectedReplicationEntity.setRetry(retry);
                assertThat(actualReplicationEntity)
                    .usingRecursiveComparison()
                    .ignoringFields(
                        "uuid",
                        "digitalUserId",
                        "version",
                        "lastModifiedDate",
                        "entityData",
                        "createDate")
                    .isEqualTo(expectedReplicationEntity);
            } else {
                assertThat(actualReplicationEntity)
                    .usingRecursiveComparison()
                    .ignoringFields(
                        "uuid",
                        "digitalUserId",
                        "version",
                        "lastModifiedDate",
                        "entityData",
                        "createDate",
                        "retry")
                    .isEqualTo(expectedReplicationEntity);
            }
        }
    }
}
