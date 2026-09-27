package com.janconnect.dto;

import com.janconnect.entity.User;
import lombok.Data;

import java.time.Instant;

@Data
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String mobile;
    private String role;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserDTO from(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
