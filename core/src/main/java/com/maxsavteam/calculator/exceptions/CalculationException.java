/*
 * Copyright (C) 2021 MaxSav Team
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.maxsavteam.calculator.exceptions;

import java.util.Map;

import static java.util.Map.entry;

public class CalculationException extends RuntimeException {

	public static final int INVALID_BINARY_OPERATOR = 0;
	public static final int NEGATIVE_PARAMETER_OF_LOG = 1;
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
	public static final int INVALID_BRACKETS_SEQUENCE = 14;
	public static final int FACTORIAL_LIMIT_EXCEEDED = 15;
	public static final int AVERAGE_FUNCTION_HAS_NO_ARGUMENTS = 16;
	public static final int NUMBER_FORMAT_EXCEPTION = 17;
	public static final int FUNCTIONS_OPERANDS_CANNOT_BE_LISTS = 18;
	public static final int BINARY_OPERATOR_CANNOT_BE_APPLIED_TO_LISTS = 19;
	public static final int OPERATOR_OR_FUNCTION_CANNOT_BE_APPLIED_TO_LIST_OF_LISTS = 20;
	public static final int TOO_FEW_ARGUMENTS = 21;
	public static final int UNKNOWN_CONSTANT = 22;
	public static final int INVALID_ASIN_ACOS_VALUE = 23;
	public static final int SUFFIX_CANNOT_BE_LIST = 25;
	public static final int INVALID_VALUE_FOR_TANGENT = 26;
	public static final int INVALID_VALUE_FOR_COTANGENT = 27;

	private static final Map<Integer, String> messagesMap = Map.ofEntries(
			entry(INVALID_BINARY_OPERATOR, "Binary operator does not have left or right operand or both"),
			entry(NEGATIVE_PARAMETER_OF_LOG, "Unable to find logarithm of negative number or of zero"),
			entry(ROOT_OF_EVEN_DEGREE_OF_NEGATIVE_NUMBER, "Unable to find root of even degree of negative number"),
			entry(NAN, "Not a number"),
			entry(UNDEFINED, "Undefined"),
			entry(DIVISION_BY_ZERO, "Division by zero"),
			entry(INVALID_OPERATOR_FOR_PERCENT, "Invalid operator for percent"),
			entry(FUNCTION_SUFFIX_AND_OPERAND_NULL, "Function suffix and operand are both null"),
			entry(UNKNOWN_FUNCTION, "Unknown function"),
			entry(UNKNOWN_BRACKET_TYPE, "Unknown bracket type"),
			entry(UNKNOWN_SUFFIX_OPERATOR, "Unknown suffix operator"),
			entry(NO_OPERAND_FOR_SUFFIX_OPERATOR, "No operand for suffix operator"),
			entry(REQUESTED_EMPTY_NODE, "Requested empty node"),
			entry(INVALID_BRACKETS_SEQUENCE, "Invalid brackets sequence"),
			entry(FACTORIAL_LIMIT_EXCEEDED, "Factorial limit exceeded"),
			entry(AVERAGE_FUNCTION_HAS_NO_ARGUMENTS, "Average function has no arguments"),
			entry(NUMBER_FORMAT_EXCEPTION, "Number format exception"),
			entry(FUNCTIONS_OPERANDS_CANNOT_BE_LISTS, "Functions operands cannot be lists"),
			entry(BINARY_OPERATOR_CANNOT_BE_APPLIED_TO_LISTS, "Binary operators cannot be applied to lists"),
			entry(OPERATOR_OR_FUNCTION_CANNOT_BE_APPLIED_TO_LIST_OF_LISTS, "Operators or functions cannot be applied to list of lists"),
			entry(TOO_FEW_ARGUMENTS, "Too few arguments"),
			entry(UNKNOWN_CONSTANT, "Unknown constant"),
			entry(INVALID_ASIN_ACOS_VALUE, "Invalid asin or acos value"),
			entry(SUFFIX_CANNOT_BE_LIST, "Suffix can't be list"),
			entry(INVALID_VALUE_FOR_TANGENT, "Invalid value for tangent"),
			entry(INVALID_VALUE_FOR_COTANGENT, "Invalid value for cotangent")
	);

	private final int errorCode;

	private final String additionalMessage;

	public int getErrorCode() {
		return errorCode;
	}

	@Override
	public String getMessage() {
		if (errorCode == NUMBER_FORMAT_EXCEPTION)
			return "NumberFormatException: " + getCause().getMessage();
		if (messagesMap.containsKey(errorCode))
			return messagesMap.get(errorCode) + (additionalMessage == null ? "" : ": " + additionalMessage);
		return additionalMessage;
	}

	public CalculationException(int error, String message) {
		errorCode = error;
		additionalMessage = message;
	}

	public CalculationException(int error) {
		this(error, (String) null);
	}

	public CalculationException(int error, Throwable cause) {
		super(cause);
		errorCode = error;
		additionalMessage = null;
	}
}
