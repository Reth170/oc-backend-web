package jp.co.intellisea.oc.web.sales.controller;

import com.alibaba.fastjson.JSONObject;
import jp.co.intellisea.oc.web.sales.common.ErrorMessage;
import jp.co.intellisea.oc.web.sales.common.SuccessMessage;
import jp.co.intellisea.oc.web.sales.entity.Contact;
import jp.co.intellisea.oc.web.sales.service.impl.ContactServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ContactController {
    @Autowired
    private ContactServiceImpl contactService;

    @ResponseBody
    @RequestMapping(value = "/contact/add", method = RequestMethod.POST)
    public JSONObject addContact(HttpServletRequest req,
                                 @RequestParam("contact_id") Integer contactId,
                                 @RequestParam("name") String name,
                                 @RequestParam("phone_number") String phoneNumber,
                                 @RequestParam("mail") String mail,
                                 @RequestParam("duties") String duties
                           ){
        Contact contact = new Contact();
        contact.setContactId(contactId);
        contact.setName(name);
        contact.setPhoneNumber(phoneNumber);
        contact.setMail(mail);
        contact.setDuties(duties);
        boolean res = contactService.addContact(contact);
        if(res)
            return new SuccessMessage("add contact successful!", true).getMessage();
        return new ErrorMessage("add contact error.").getMessage();
    }

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public JSONObject allContact(){return new  SuccessMessage< List<Contact>>(null, contactService.allContact()).getMessage();}


    @RequestMapping(value = "/contact/search", method = RequestMethod.GET)
    public JSONObject searchContact(@RequestParam String query) {
        return new SuccessMessage< List<Contact>>(null, contactService.selectByFuzzySearch(query)).getMessage();
    }


    @RequestMapping(value = "/contact/delete", method = RequestMethod.DELETE)
    public JSONObject deleteContact(HttpServletRequest req,
                                    @RequestParam("id") Integer contactId){
        boolean res = contactService.deleteContactByPrimaryKey(contactId);
        if(res)
            return new SuccessMessage("delete contact successful!", true).getMessage();
        return new ErrorMessage("delete contact error.").getMessage();
    }

    @RequestMapping(value = "/contact/update", method = RequestMethod.POST)
    public JSONObject updateContact(HttpServletRequest req,
                                 @RequestParam("contact_id") Integer contactId,
                                 @RequestParam("name") String name,
                                 @RequestParam("phone_number") String phoneNumber,
                                 @RequestParam("mail") String mail,
                                 @RequestParam("duties") String duties
    ){
        Contact contact = new Contact();
        contact.setContactId(contactId);
        contact.setName(name);
        contact.setPhoneNumber(phoneNumber);
        contact.setMail(mail);
        contact.setDuties(duties);
        boolean res = contactService.updatePrimaryKey(contact);
        if(res)
            return new SuccessMessage("add contact successful!", true).getMessage();
        return new ErrorMessage("add contact error.").getMessage();
    }
}