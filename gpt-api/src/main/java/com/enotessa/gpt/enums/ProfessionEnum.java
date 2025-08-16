package com.enotessa.gpt.enums;

import lombok.Getter;

@Getter
public enum ProfessionEnum {
    JAVA_JUNIOR("Java Junior"),
    JAVA_MIDDLE("Java Middle"),
    JAVA_SENIOR("Java Senior"),
    KOTLIN_JUNIOR("Kotlin Junior"),
    KOTLIN_MIDDLE("Kotlin Middle"),
    KOTLIN_SENIOR("Kotlin Senior"),
    IOS_JUNIOR("Ios Swift Junior"),
    IOS_MIDDLE("Ios Swift Middle"),
    IOS_SENIOR("Ios Swift Senior");

    private final String displayName;

    ProfessionEnum(String displayName) {
        this.displayName = displayName;
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
