package com.aibilling.account.service;

import com.aibilling.account.model.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    
    Account create(Account account, UUID entityId, UUID paymentTermId, UUID billingCycleId);

    Account update(UUID id, Account accountDetails, UUID paymentTermId, UUID billingCycleId);

    Account findById(UUID id);

    List<Account> findByEntityId(UUID entityId);

    void delete(UUID id);
}
