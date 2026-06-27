package com.aibilling.account.mapper;

import com.aibilling.account.dto.AccountCreateRequest;
import com.aibilling.account.dto.AccountResponse;
import com.aibilling.account.dto.AccountUpdateRequest;
import com.aibilling.account.model.Account;
import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.model.BillingCycle;
import com.aibilling.setup.model.PaymentTerm;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(AccountCreateRequest request) {
        if (request == null) {
            return null;
        }

        Account account = new Account();
        account.setAccountName(request.getAccountName());
        account.setNatureOfBusiness(request.getNatureOfBusiness());
        account.setCreditLimit(request.getCreditLimit());
        account.setCreditClassification(request.getCreditClassification());
        account.setCreditRisk(request.getCreditRisk());

        return account;
    }

    public Account toEntity(AccountUpdateRequest request) {
        if (request == null) {
            return null;
        }

        Account account = new Account();
        account.setAccountName(request.getAccountName());
        account.setNatureOfBusiness(request.getNatureOfBusiness());
        account.setStatus(request.getStatus());
        account.setCreditLimit(request.getCreditLimit());
        account.setCreditClassification(request.getCreditClassification());
        account.setCreditRisk(request.getCreditRisk());

        return account;
    }

    public AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }

        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        if (account.getEntity() != null) {
            response.setEntityId(account.getEntity().getId());
        }
        response.setAccountName(account.getAccountName());
        response.setNatureOfBusiness(account.getNatureOfBusiness());
        response.setStatus(account.getStatus());
        response.setCreditLimit(account.getCreditLimit());
        
        if (account.getPaymentTerm() != null) {
            response.setPaymentTerm(toLookupResponse(account.getPaymentTerm()));
        }
        
        if (account.getBillingCycle() != null) {
            response.setBillingCycle(toLookupResponse(account.getBillingCycle()));
        }
        
        response.setCreditClassification(account.getCreditClassification());
        response.setCreditRisk(account.getCreditRisk());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());

        return response;
    }

    private LookupResponse toLookupResponse(PaymentTerm pt) {
        LookupResponse lr = new LookupResponse();
        lr.setId(pt.getId());
        lr.setCode(pt.getCode());
        lr.setName(pt.getName());
        return lr;
    }

    private LookupResponse toLookupResponse(BillingCycle bc) {
        LookupResponse lr = new LookupResponse();
        lr.setId(bc.getId());
        lr.setCode(bc.getCode());
        lr.setName(bc.getName());
        return lr;
    }
}
