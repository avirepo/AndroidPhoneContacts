package com.avi.contacts.model;

/**
 * Class ContactQueryType created on 16/10/16 - 4:04 PM.
 * All copyrights reserved to the Zoomvy.
 * Class behaviour is to provide contact data type
 */

public enum ContactQueryType {
    //Each contacts with all the associated email will keep in single entity
    COMBINED_EMAIL,
    //Each contacts with all the associated phone number will keep in single entity
    COMBINED_PHONE,
    //Contacts for multiple associated number keep as separate entity
    MULTIPLE_PHONE,
    //Contacts for multiple associated email keep as separate entity
    MULTIPLE_EMAIL,
    //Contacts for multiple associated number and associated email keep in single entity
    COMBINED_EMAIL_PHONE,
    //All Contacts for with multiple associated email and associated number keep as separate entity
    ALL_PHONE_EMAIL
}
