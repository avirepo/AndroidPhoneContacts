# PhoneContacts
------------------
------------------

Android Phone contact allows you to fetch PhoneBook contacts using simple query builder.
It have great performance even with large number of contacts phone book.


For Gradle dependency please add following lines.
Add Maven repository in your app build.gradle file

    android{
        .....
        repositories {
            maven {
                url 'https://dl.bintray.com/vikasgoyal/maven'
            }
        }

     }

Add dependency with following lines on build.gradle.

        compile 'com.avi.android:contacts:1.0'


For Maven dependency please add following lines:

    <dependency>
      <groupId>com.avi.android</groupId>
      <artifactId>contacts</artifactId>
      <version>1.0</version>
      <type>pom</type>
    </dependency>

Now your project is ready to use the ContactLib

If you want to use **ContactFetchManager** First you need to Build a query for that.
**ContactQuery** will accept 6 types of **ContactQueryType** find there details below:-



    COMBINED_EMAIL //Each contacts with all the associated email will keep in single entity
    COMBINED_PHONE //Each contacts with all the associated phone number will keep in single entity
    MULTIPLE_PHONE //Contacts for multiple associated number keep as separate entity
    MULTIPLE_EMAIL //Contacts for multiple associated email keep as separate entity
    COMBINED_EMAIL_PHONE //Contacts for multiple associated number and associated email keep in single entity
    ALL_PHONE_EMAIL //All Contacts for with multiple associated email and associated number keep as separate entity

Lets take example for fetching COMBINED_EMAIL_PHONE which basically provide all the saved contacts in phone book with single entity for a contact and
combined all number and emails for that contact inside array or **LabeledData**.

 ```java
        ContactsFetchManager.getInstance().fetchContacts(new ContactQuery
                        .Builder(ContactQueryType.COMBINED_EMAIL_PHONE)
                        .withResponseListener(this)
                        .build(this));
 ```


 **Known Issue :-**
  Currently pagination feature inside the **ContactQuery** is not working in next release I will try to update that one.