package com.af.tourism.pojo.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserForAdminVO {

    private Long id;

    private String email;

    private String username;

    private String nickname;

    private String avatarUrl;

    private String bio;

    private Integer status;

    private List<String> roles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public void setRoleCodes(String roleCodes) {
        if (roleCodes == null || roleCodes.trim().isEmpty()) {
            this.roles = Collections.emptyList();
            return;
        }
        this.roles = Arrays.stream(roleCodes.split(","))
                .filter(code -> code != null && !code.trim().isEmpty())
                .collect(Collectors.toList());
    }
}
