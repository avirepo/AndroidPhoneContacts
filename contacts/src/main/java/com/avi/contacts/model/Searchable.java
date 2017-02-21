package com.avi.contacts.model;

/**
 * Class Searchable created on 22/05/16 - 4:11 PM.
 * All copyrights reserved to the Zoomvy.
 * Interface will help in searching inside the objects the classes should implement it and provide their
 * own search criteria different model classes can have different search criteria depends on their implementation
 */
public interface Searchable {
    /**
     * Method should return true if the provided search string matches the search criteria
     * else it will return false
     * @param searchString Searchable string
     * @return true if matches the search criteria else false
     */
    boolean contain(CharSequence searchString);
}
