package com.aibilling.account.service.impl;

import com.aibilling.account.model.Account;
import com.aibilling.account.repository.AccountRepository;
import com.aibilling.account.service.AccountService;
import com.aibilling.common.enums.Status;
import com.aibilling.contact.service.ContactService;
import com.aibilling.exception.BusinessException;
import com.aibilling.exception.ResourceNotFoundException;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.repository.EntityRepository;
import com.aibilling.setup.model.BillingCycle;
import com.aibilling.setup.model.PaymentTerm;
import com.aibilling.setup.repository.BillingCycleRepository;
import com.aibilling.setup.repository.PaymentTermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final EntityRepository entityRepository;
    private final PaymentTermRepository paymentTermRepository;
    private final BillingCycleRepository billingCycleRepository;
    private final ContactService contactService;

    public AccountServiceImpl(AccountRepository accountRepository, 
                              EntityRepository entityRepository,
                              PaymentTermRepository paymentTermRepository, 
                              BillingCycleRepository billingCycleRepository,
                              ContactService contactService) {
        this.accountRepository = accountRepository;
        this.entityRepository = entityRepository;
        this.paymentTermRepository = paymentTermRepository;
        this.billingCycleRepository = billingCycleRepository;
        this.contactService = contactService;
    }

    @Override
    @Transactional
    public Account create(Account account, UUID entityId, UUID paymentTermId, UUID billingCycleId) {
        Entity entity = entityRepository.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", entityId.toString()));

        if (entity.getStatus() == Status.DELETED) {
            throw new BusinessException("Cannot add account to a deleted Entity");
        }

        account.setEntity(entity);
        setSetupData(account, paymentTermId, billingCycleId);

        Account savedAccount = accountRepository.save(account);

        // Auto-create "Missing Information" placeholder contact
        contactService.createPlaceholderContact(savedAccount.getId());

        return savedAccount;
    }

    @Override
    @Transactional
    public Account update(UUID id, Account accountDetails, UUID paymentTermId, UUID billingCycleId) {
        Account existing = findById(id);

        if (existing.getStatus() == Status.DELETED) {
            throw new BusinessException("Cannot update a deleted Account");
        }

        existing.setAccountName(accountDetails.getAccountName());
        existing.setNatureOfBusiness(accountDetails.getNatureOfBusiness());
        existing.setStatus(accountDetails.getStatus());
        existing.setCreditLimit(accountDetails.getCreditLimit());
        existing.setCreditClassification(accountDetails.getCreditClassification());
        existing.setCreditRisk(accountDetails.getCreditRisk());

        setSetupData(existing, paymentTermId, billingCycleId);

        return accountRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findByEntityId(UUID entityId) {
        if (!entityRepository.existsById(entityId)) {
            throw new ResourceNotFoundException("Entity", "id", entityId.toString());
        }
        return accountRepository.findByEntityIdAndStatusNot(entityId, Status.DELETED);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Account account = findById(id);

        if (account.getStatus() == Status.DELETED) {
            return; // Idempotent
        }

        // Validate constraint: Each Entity must have at least one Account
        long activeAccounts = accountRepository.countByEntityIdAndStatusNot(account.getEntity().getId(), Status.DELETED);
        if (activeAccounts <= 1) {
            throw new BusinessException("Cannot delete the final account of an entity. Each entity must have at least one active account.");
        }

        account.setStatus(Status.DELETED);
        accountRepository.save(account);
    }

    private void setSetupData(Account account, UUID paymentTermId, UUID billingCycleId) {
        if (paymentTermId != null) {
            PaymentTerm pt = paymentTermRepository.findById(paymentTermId)
                    .orElseThrow(() -> new ResourceNotFoundException("PaymentTerm", "id", paymentTermId.toString()));
            account.setPaymentTerm(pt);
        } else {
            account.setPaymentTerm(null);
        }

        if (billingCycleId != null) {
            BillingCycle bc = billingCycleRepository.findById(billingCycleId)
                    .orElseThrow(() -> new ResourceNotFoundException("BillingCycle", "id", billingCycleId.toString()));
            account.setBillingCycle(bc);
        } else {
            account.setBillingCycle(null);
        }
    }
}
