package jp.co.intellisea.oc.web.sales.service;

import jp.co.intellisea.oc.web.sales.entity.Contact;

import java.util.List;

public interface ContactService {
    boolean addContact(Contact contact);

    List<Contact> allContact();

    Contact selectContactByPrimaryKey(Integer contactId);

    boolean deleteContactByPrimaryKey(Integer contactId);

    boolean updatePrimaryKey(Contact record);

    List<Contact> selectByFuzzySearch(String search);

    byte[] generateContactPdfFromContact();
}
