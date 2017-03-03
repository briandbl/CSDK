package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;
import java.util.NoSuchElementException;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContactsList;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.contactslist.Contact;
import com.intel.context.item.contactslist.ContactRelatedEvent;
import com.intel.context.item.contactslist.ContactRelationship;

public class ContactsListener extends IApplicationListener {

    private final String LOG_TAG = ContactsListener.class.getName();
    
    public ContactsListener(UpdateNotifier updateNotifier, int index) {
        super("Contacts", ContextType.CONTACTS, updateNotifier, index);
    }

    @Override
    public void onReceive(Item item) {
        if(item instanceof ContactsList) {
            setLastKnownItem(item);
            notifyUpdate();
        }
    }

    @Override
    public void onError(ContextError error) {
        mUpdateNotifier.notifyError("ContactsListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }

    @Override
    public boolean shouldNotStartSensing() {
        return false;
    }

    @Override
    protected void addProviderOptions(List<ProviderOption> providerOptions) {
        providerOptions.add(ProviderOption.createFromEnum(MONITOR_INTERVAL.class, MONITOR_INTERVAL.ONE_MINUTE));
        providerOptions.add(ProviderOption.createFromEnum(HARVESTING_MODE.class, HARVESTING_MODE.FULL_DETAILS));
    }
    
    @Override
    public Bundle getProviderOptionsBundle() {
        Bundle bundle = new Bundle();
        
        bundle.putLong(MONITOR_INTERVAL.class.getSimpleName(), getProviderOptionEnum(MONITOR_INTERVAL.class).getInterval());
        bundle.putString(HARVESTING_MODE.class.getSimpleName(), getProviderOptionEnum(HARVESTING_MODE.class).name());
        
        return bundle;
    }

    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for provider.
    }

    @Override
    protected String describeItem(Item item) {
        ContactsList contacts = (ContactsList) item;
        
        List<Contact> contactList = contacts.getContacts();
        StringBuilder sb = new StringBuilder();
        
        for(Contact contact : contactList) {
            appendContact(sb, contact).append("\n");
        }
        
        return sb.toString();
    }
    
    private StringBuilder appendContact(StringBuilder sb, Contact contact) {
        
        // Add all required items.
        sb.append("Name:\t").append(contact.getName()).append("\n");
        
        // Add all optional items. If the element is not available, the category will not be shown.
        try {
            appendStringList(sb, "Home Phones", contact.getHomePhones());
        } catch(NoSuchElementException ex) {
        }
        
        try {
            appendStringList(sb, "Mobile Phones", contact.getMobilePhones());
        } catch(NoSuchElementException ex) {
        }
        
        try {
            appendStringList(sb, "Work Phones", contact.getWorkPhones());
        } catch(NoSuchElementException ex) {
        }
        
        try {
            appendStringList(sb, "Other Phones", contact.getOtherPhones());
        } catch(NoSuchElementException ex) {
        }
        
        try {
            appendStringList(sb, "Personal Emails", contact.getPersonalEmails());
        } catch(NoSuchElementException ex) {
        }
        
        try {
            appendStringList(sb, "Work Emails", contact.getWorkEmails());
        } catch(NoSuchElementException ex) {
        }
        
        try {
            appendStringList(sb, "Other Emails", contact.getOtherEmails());
        } catch(NoSuchElementException ex) {
        }
        
        try {
            appendStringList(sb, "Home Addresses", contact.getHomeAddresses());
        } catch(NoSuchElementException ex) {
        }
        
        try {
            appendStringList(sb, "Work Addresses", contact.getWorkAddresses());
        } catch(NoSuchElementException ex) {
        }
        
        try {
            appendStringList(sb, "Other Addresses", contact.getOtherAddresses());
        } catch(NoSuchElementException ex) {
        }
        
        try {
            appendEventsList(sb, contact.getEvents());
        } catch(NoSuchElementException ex) {
        }
        
        try {
            appendRelationships(sb, contact.getRelationships());
        } catch(NoSuchElementException ex) {
        }
        
        return sb;
    }
    
    private StringBuilder appendStringList(StringBuilder sb, String listName, List<String> list) {
        if(list.isEmpty())
            return sb;
        
        sb.append(listName).append(":\n");
        for(String s : list) {
            sb.append("\t").append(s).append("\n");
        }
        
        return sb;
    }
    
    private StringBuilder appendEventsList(StringBuilder sb, List<ContactRelatedEvent> events) {
        if (events.isEmpty())
            return sb;
        
        sb.append("Events:\n");
        for(ContactRelatedEvent event : events) {
            try {
                String name = event.getName();
                sb.append("\tName: ");
                sb.append(name).append("\n");
            } catch(NoSuchElementException ex) {
            }
            
            try {
                String date = event.getDate();
                sb.append("\tDate: ");
                sb.append(date).append("\n");
            } catch(NoSuchElementException ex) {
            }
        }
        
        return sb;
    }
    
    private StringBuilder appendRelationships(StringBuilder sb, List<ContactRelationship> relationships) {
        if (relationships.isEmpty())
            return sb;
        
        sb.append("Relationships:\n");
        for(ContactRelationship relationship : relationships) {
            try {
                String name = relationship.getName();
                sb.append("\tName: ");
                sb.append(name).append("\n");
            } catch(NoSuchElementException ex) {
            }
            
            try {
                String name = relationship.getRelationship().name();
                sb.append("\tType: ");
                sb.append(name).append("\n");
            } catch(NoSuchElementException ex) {
            }
        }
                
        return sb;
    }

    public static enum HARVESTING_MODE {
        LOW_DETAILS,
        MEDIUM_DETAILS,
        HIGH_DETAILS,
        FULL_DETAILS
    }
}
