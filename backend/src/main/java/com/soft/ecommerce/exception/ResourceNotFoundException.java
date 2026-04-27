package com.soft.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException {
    private Long fieldId;
    private String fieldContent;
    private String field;
    private String entityName;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String entityName, String field, String fieldContent) {
        super(String.format("NOT FOUND %s WITH %s : %s", entityName, field, fieldContent));
        this.entityName = entityName;
        this.field = field;
        this.fieldContent = fieldContent;
    }

    public ResourceNotFoundException(String entityName, String field, Long fieldId) {
        super(String.format("NOT FOUND %s WITH %s : %d", entityName, field, fieldId));
        this.entityName = entityName;
        this.field = field;
        this.fieldId = fieldId;
    }

}
