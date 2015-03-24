package com.timw.autoassign.exception;

/**
 * Created by twebster on 18/01/14.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(final String msg) {
        super(msg);
    }
}
