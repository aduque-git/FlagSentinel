package org.example.flagsentinelpanel.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class OperatorOption {

    private String code;
    private String label;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperatorOption)) return false;
        OperatorOption that = (OperatorOption) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}

