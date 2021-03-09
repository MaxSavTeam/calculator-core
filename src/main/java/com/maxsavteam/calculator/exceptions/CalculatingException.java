package com.maxsavteam.calculator.exceptions;

public class CalculatingException extends RuntimeException {

    public static final String[] messages = new String[]{
            "Binary operator does not have left or right operand or both",
            "Unable to find logarithm of negative number or of zero",
            "Impossible to find tan of 90",
            "Unable to find root of even degree of negative number",
            "Not a number",
            "Undefined",
            "Division by zero",
            "Invalid operator for percent",
            "Functions suffix and operand are both null",
            "Unknown function",
            "Unknown bracket type",
            "Unknown suffix operator",
            "No operand for suffix operator",
            "Requested empty node",
    };
    public static final int INVALID_BINARY_OPERATOR = 0;
    public static final int NEGATIVE_PARAMETER_OF_LOG = 1;
    public static final int TAN_OF_90 = 2;
    public static final int ROOT_OF_EVEN_DEGREE_OF_NEGATIVE_NUMBER = 3;
    public static final int NAN = 4;
    public static final int UNDEFINED = 5;
    public static final int DIVISION_BY_ZERO = 6;
    public static final int INVALID_OPERATOR_FOR_PERCENT = 7;
    public static final int FUNCTION_SUFFIX_AND_OPERAND_NULL = 8;
    public static final int UNKNOWN_FUNCTION = 9;
    public static final int UNKNOWN_BRACKET_TYPE = 10;
    public static final int UNKNOWN_SUFFIX_OPERATOR = 11;
    public static final int NO_OPERAND_FOR_SUFFIX_OPERATOR = 12;
    public static final int REQUESTED_EMPTY_NODE = 13;

    private final int errorCode;

    public int getErrorCode(){
        return errorCode;
    }

    @Override
    public String getMessage() {
        if(errorCode >= 0 && errorCode < messages.length)
            return messages[errorCode];
        return "";
    }

    public CalculatingException(int error){
        errorCode = error;
    }
}