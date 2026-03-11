package jp.co.intellisea.oc.web.sales.service.impl;

import jp.co.intellisea.oc.web.sales.common.PdfGenerator;
import jp.co.intellisea.oc.web.sales.dao.ContactMapper;
import jp.co.intellisea.oc.web.sales.entity.Contact;
import jp.co.intellisea.oc.web.sales.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactMapper contactMapper;

    @Override
    public boolean addContact(Contact contact){
        return contactMapper.insert(contact) > 0;
    }

    @Override
    public List<Contact> allContact() { return contactMapper.allContact(); }

    @Override
    public Contact selectContactByPrimaryKey(Integer contactId) { return contactMapper.selectByPrimaryKey(contactId); }

    @Override
    public boolean deleteContactByPrimaryKey(Integer contactId) { return contactMapper.deleteByPrimaryKey(contactId) > 0; }

    @Override
    public boolean updatePrimaryKey(Contact record) { return contactMapper.updateByPrimaryKey(record) > 0; }

    @Override
    public List<Contact> selectByFuzzySearch(String search) { return contactMapper.selectByFuzzySearch(search); }

    @Override
    public byte[] generateContactPdfFromContact() {
        List<Contact> contacts = contactMapper.allContact();
        String[] headers = {"ID", "Name", "Phone Number", "Mail", "Duties"};
        String[] getters = {"getContactId", "getName", "getPhoneNumber", "getMail", "getDuties"};
        return PdfGenerator.generateTablePdf("Contact List", headers, getters, contacts);
    }
}
