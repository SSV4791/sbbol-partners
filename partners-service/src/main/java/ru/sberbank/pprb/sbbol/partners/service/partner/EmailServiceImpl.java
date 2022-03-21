package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.entity.partner.EmailEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.EmailMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.EmailsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;

import java.util.UUID;

abstract class EmailServiceImpl implements EmailService {

    public static final String DOCUMENT_NAME = "email";

    private final EmailRepository emailRepository;
    private final EmailMapper emailMapper;

    public EmailServiceImpl(EmailRepository emailRepository, EmailMapper emailMapper) {
        this.emailRepository = emailRepository;
        this.emailMapper = emailMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public EmailsResponse getEmails(EmailsFilter emailsFilter) {
        var response = emailRepository.findByFilter(emailsFilter);
        var emailResponse = new EmailsResponse();
        for (var entity : response) {
            emailResponse.addEmailsItem(emailMapper.toEmail(entity));
        }
        var pagination = emailsFilter.getPagination();
        emailResponse.setPagination(
            new Pagination()
                .offset(pagination.getOffset())
                .count(pagination.getCount())
        );
        var size = response.size();
        if (pagination.getCount() < size) {
            emailResponse.getPagination().hasNextPage(Boolean.TRUE);
            emailResponse.getEmails().remove(size - 1);
        }
        return emailResponse;
    }

    @Override
    @Transactional
    public EmailResponse saveEmail(EmailCreate email) {
        var emailEntity = emailMapper.toEmail(email);
        EmailEntity savedEmail = emailRepository.save(emailEntity);
        var response = emailMapper.toEmail(savedEmail);
        return new EmailResponse().email(response);
    }

    @Override
    @Transactional
    public EmailResponse updateEmail(Email email) {
        var uuid = UUID.fromString(email.getId());
        var foundEmail = emailRepository.getByDigitalIdAndUuid(email.getDigitalId(), uuid);
        if (foundEmail == null) {
            throw new EntryNotFoundException(DOCUMENT_NAME, uuid);
        }
        if (email.getVersion() <= foundEmail.getVersion()) {
            throw new OptimisticLockingFailureException("Версия документа в базе данных " + foundEmail.getVersion() +
                " больше или равна версии документа в запросе version=" + email.getVersion());
        }
        emailMapper.updateEmail(email, foundEmail);
        var savedEmail = emailRepository.save(foundEmail);
        var response = emailMapper.toEmail(savedEmail);
        return new EmailResponse().email(response);
    }

    @Override
    @Transactional
    public void deleteEmail(String digitalId, String id) {
        var foundEmail = emailRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
        if (foundEmail == null) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
        }
        emailRepository.delete(foundEmail);
    }
}
