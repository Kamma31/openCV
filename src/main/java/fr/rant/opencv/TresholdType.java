package fr.rant.opencv;

public enum TresholdType {
    BINARY("Binary"),
    BINARY_INVERTED("Binary inverted"),
    TRUNCATE("Truncate"),
    TO_ZERO("To zero"),
    TO_ZERO_INVERTED("To zero inverted");

    String name;

    TresholdType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}