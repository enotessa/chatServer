package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuthEvent {
    private String eventType;
    private String userId;
    private LocalDateTime timestamp;
}
