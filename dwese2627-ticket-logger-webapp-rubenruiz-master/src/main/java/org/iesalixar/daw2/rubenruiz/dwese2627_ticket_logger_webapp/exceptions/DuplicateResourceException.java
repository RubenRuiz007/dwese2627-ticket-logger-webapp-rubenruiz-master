package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.exceptions;


public class DuplicateResourceException extends RuntimeException {

    private final String resource;

    private final String field;

    private final Object value;

    public DuplicateResourceException(String resource, String field, Object value) {
        super("Duplicate " + resource + " (" + field + "=" + value + ")");
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
}