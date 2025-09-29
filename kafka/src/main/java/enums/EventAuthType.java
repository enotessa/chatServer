package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventAuthType {
    REGISTERED_EVENT("REGISTERED"),
    LOGGED_IN_EVENT("LOGGED_IN"),
    LOGGED_OUT_EVENT("LOGGED_OUT");

    private final String displayName;
}
