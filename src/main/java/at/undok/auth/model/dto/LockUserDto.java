package at.undok.auth.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LockUserDto {
    private UUID id;
    private boolean lock;
}
