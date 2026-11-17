package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    private final String resource;

    private final String field;

    private final Object value;

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(resource + " not found (" + field + "=" + value + ")");
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
}
