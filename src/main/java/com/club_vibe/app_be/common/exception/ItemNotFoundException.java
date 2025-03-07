package com.club_vibe.app_be.common.exception;

public class ItemNotFoundException extends RuntimeException {

    /**
     *
     * @param identifier {@link String} of the item
     */
    public ItemNotFoundException(String identifier) {
        super("Item with identifier = " + identifier + " not found!");
    }
}