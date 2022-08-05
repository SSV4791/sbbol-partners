package ru.sberbank.pprb.sbbol.partners.service.mapper.renter;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.renter.LegalAddress;
import ru.sberbank.pprb.sbbol.partners.entity.renter.PhysicalAddress;
import ru.sberbank.pprb.sbbol.partners.mapper.renter.RenterMapper;
import ru.sberbank.pprb.sbbol.renter.model.Renter;

import static org.assertj.core.api.Assertions.assertThat;

@Deprecated(forRemoval = true)
public class RenterMapperTest extends BaseUnitConfiguration {

    private static final RenterMapper mapper = Mappers.getMapper(RenterMapper.class);

    @Test
    void toRenter() {
        Renter expected = factory.manufacturePojo(Renter.class);
        var renter = mapper.toRenter(expected);
        Renter actual = mapper.toRenter(renter);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("dulName", "checkResults")
            .isEqualTo(actual);
    }

    @Test
    void toRenterLegalAddress() {
        LegalAddress expected = factory.manufacturePojo(LegalAddress.class);
        var renter = mapper.toRentalAddress(expected);
        LegalAddress actual = mapper.toLegalAddress(renter);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "renter"
            )
            .isEqualTo(actual);
    }

    @Test
    void toRenterPhysicalAddress() {
        PhysicalAddress expected = factory.manufacturePojo(PhysicalAddress.class);
        var renter = mapper.toRentalAddress(expected);
        PhysicalAddress actual = mapper.toPhysicalAddress(renter);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "renter"
            )
            .isEqualTo(actual);
    }
}
