package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeChange;

import java.util.List;

public class DocumentTypeUpdateValidationImpl extends AbstractValidatorImpl<DocumentTypeChange> {

    @Override
    public void validator(List<String> errors, DocumentTypeChange entity) {
        commonValidationUuid(errors,entity.getId());
    }
}
