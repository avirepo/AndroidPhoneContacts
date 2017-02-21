package com.avi.contacts.model;

import android.provider.ContactsContract;

/**
 * Class ContactsLabel created on 14/10/16 - 7:37 PM.
 * All copyrights reserved to the Zoomvy.
 * Class behaviour is..."Please Define here"
 */

public interface ContactsLabel {

    String TYPE_CUSTOM = "Custom";
    String TYPE_HOME = "Home";
    String TYPE_MOBILE = "Mobile";
    String TYPE_WORK = "Work";
    String TYPE_FAX_WORK = "Fax Work";
    String TYPE_FAX_HOME = "Fax Home";
    String TYPE_PAGER = "Pager";
    String TYPE_OTHER = "Other";
    String TYPE_CALLBACK = "Callback";
    String TYPE_CAR = "Car";
    String TYPE_COMPANY_MAIN = "Company Main";
    String TYPE_ISDN = "ISDN";
    String TYPE_MAIN = "Main";
    String TYPE_OTHER_FAX = "Other Fax";
    String TYPE_RADIO = "Radio";
    String TYPE_TELEX = "Telex";
    String TYPE_TTY_TDD = "Tty Tdd";
    String TYPE_WORK_MOBILE = "Work Mobile";
    String TYPE_WORK_PAGER = "Work Pager";
    String TYPE_ASSISTANT = "Assistant";
    String TYPE_MMS = "MMS";

    class LabelResolver {

        public static String resolvePhoneLabel(int phoneType) {
            switch (phoneType) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    return TYPE_HOME;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    return TYPE_MOBILE;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    return TYPE_WORK;
                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                    return TYPE_FAX_WORK;
                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                    return TYPE_FAX_HOME;
                case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                    return TYPE_PAGER;
                case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                    return TYPE_CALLBACK;
                case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                    return TYPE_CAR;
                case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                    return TYPE_COMPANY_MAIN;
                case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                    return TYPE_ISDN;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                    return TYPE_MAIN;
                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                    return TYPE_OTHER_FAX;
                case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                    return TYPE_RADIO;
                case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                    return TYPE_TELEX;
                case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                    return TYPE_TTY_TDD;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                    return TYPE_WORK_MOBILE;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                    return TYPE_WORK_PAGER;
                case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                    return TYPE_ASSISTANT;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                    return TYPE_MMS;
                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                    return TYPE_OTHER;
                default:
                    return TYPE_CUSTOM;
            }
        }

        public static String resolveEmailLabel(int phoneType) {
            switch (phoneType) {
                case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                    return TYPE_HOME;
                case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
                    return TYPE_MOBILE;
                case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                    return TYPE_WORK;
                case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
                    return TYPE_OTHER;
                default:
                    return TYPE_CUSTOM;
            }
        }
    }
}

