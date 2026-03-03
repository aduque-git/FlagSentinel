package com.example.flagsentinelapi.dto.ruleoperator;

import java.util.Arrays;
import java.util.List;

public enum RuleOperator {

    EQUALS("equals", "Equals") {
        @Override
        public boolean evaluate(String actual, String expected) {
            return actual.equals(expected);
        }
    },

    NOT_EQUALS("not_equals", "Not equals") {
        @Override
        public boolean evaluate(String actual, String expected) {
            return !actual.equals(expected);
        }
    },

    CONTAINS("contains", "Contains") {
        @Override
        public boolean evaluate(String actual, String expected) {
            return actual.contains(expected);
        }
    },

    GREATER_THAN("greater_than", "Greater than") {
        @Override
        public boolean evaluate(String actual, String expected) {
            try {
                return Double.parseDouble(actual) > Double.parseDouble(expected);
            } catch (NumberFormatException e) {
                return false;
            }
        }
    },

    LESS_THAN("less_than", "Less than") {
        @Override
        public boolean evaluate(String actual, String expected) {
            try {
                return Double.parseDouble(actual) < Double.parseDouble(expected);
            } catch (NumberFormatException e) {
                return false;
            }
        }
    };

    private final String code;
    private final String label;

    RuleOperator(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public abstract boolean evaluate(String actual, String expected);

    public static RuleOperator fromCode(String code) {
        for (RuleOperator op : values()) {
            if (op.code.equals(code)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + code);
    }

    public static List<OperatorDTO> asDtoList() {
        return Arrays.stream(values())
                .map(op -> new OperatorDTO(op.getCode(), op.getLabel()))
                .toList();
    }

}
