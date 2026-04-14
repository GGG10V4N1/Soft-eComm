package com.soft.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException {
    private Long fieldId;
    private String fieldName;
    private String field;
    private String entityName;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String entityName, String field, String fieldName) {
        super( entityName + "NOT FOUND WITH " + field + ":" + fieldName );
        this.entityName = entityName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceNotFoundException(String entityName, String field, Long fieldId) {
        super( entityName + "NOT FOUND WITH " + field + ":" + fieldId.toString() );
        this.entityName = entityName;
        this.field = field;
        this.fieldId = fieldId;
    }

}
