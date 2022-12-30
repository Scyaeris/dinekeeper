package dinekeeper.util;

import java.io.IOException;

public class InvalidTableAssignmentException extends Exception {
    public InvalidTableAssignmentException() {}

    public InvalidTableAssignmentException(String message) { super(message); }
}
