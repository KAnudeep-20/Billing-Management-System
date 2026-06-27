package com.aibilling.account.dto;

import com.aibilling.contact.dto.ContactResponse;
import com.aibilling.site.dto.SiteResponse;

import java.util.List;

public class AccountHierarchyResponse extends AccountResponse {

    private List<SiteResponse> sites;
    private List<ContactResponse> contacts;

    public List<SiteResponse> getSites() {
        return sites;
    }

    public void setSites(List<SiteResponse> sites) {
        this.sites = sites;
    }

    public List<ContactResponse> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactResponse> contacts) {
        this.contacts = contacts;
    }
}
