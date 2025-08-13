package com.enotessa.gpt.enums;

public enum ProfessionEnum {
    JAVA_JUNIOR("Java Junior"),
    JAVA_MIDDLE("Java Middle"),
    JAVA_SENIOR("Java Senior"),
    KOTLIN_JUNIOR("Kotlin Junior");

    private final String displayName;

    ProfessionEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ProfessionEnum fromLabel(String label) {
        for (ProfessionEnum profession : values()) {
            if (profession.getDisplayName().equalsIgnoreCase(label)) {
                return profession;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }
}
