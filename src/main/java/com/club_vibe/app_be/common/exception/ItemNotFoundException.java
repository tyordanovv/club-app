package com.club_vibe.app_be.common.exception;

public class ItemNotFoundException extends RuntimeException {

    /**
     *
     * @param identifier {@link String} of the item
     */
    public ItemNotFoundException(String item, String identifier) {
        super("Entity " + item + " with identifier = " + identifier + " not found!");
    }
}