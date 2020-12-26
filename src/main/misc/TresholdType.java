package main.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TresholdType {
    BINARY("Binary"),
    BINARY_INVERTED("Binary inverted"),
    TRUNCATE("Truncate"),
    TO_ZERO("To zero"),
    TO_ZERO_INVERTED("To zero inverted");

    String name;
}