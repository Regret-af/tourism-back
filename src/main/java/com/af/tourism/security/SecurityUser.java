package com.af.tourism.security;

import com.af.tourism.common.enums.RoleCode;
import com.af.tourism.common.enums.UserStatus;
import com.af.tourism.pojo.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Spring Security 标准登录用户模型。
 */
@Data
@NoArgsConstructor
public class SecurityUser implements UserDetails {

    private Long userId;

    private String email;

    private String username;

    private String passwordHash;

    private String nickname;

    private Integer status;

    private List<String> roleCodes = Collections.emptyList();

    private Collection<? extends GrantedAuthority> authorities = Collections.emptyList();

    public static SecurityUser from(User user, List<String> roleCodes) {
        SecurityUser securityUser = new SecurityUser();
        securityUser.setUserId(user.getId());
        securityUser.setEmail(user.getEmail());
        securityUser.setUsername(user.getUsername());
        securityUser.setPasswordHash(user.getPasswordHash());
        securityUser.setNickname(user.getNickname());
        securityUser.setStatus(user.getStatus());

        List<String> safeRoleCodes = roleCodes == null ? Collections.emptyList() : roleCodes;
        securityUser.setRoleCodes(safeRoleCodes);
        securityUser.setAuthorities(buildAuthorities(safeRoleCodes));
        return securityUser;
    }

    private static Collection<? extends GrantedAuthority> buildAuthorities(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return Collections.emptyList();
        }
        return roleCodes.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(roleCode -> new SimpleGrantedAuthority("ROLE_" + roleCode))
                .collect(Collectors.toList());
    }

    public boolean hasRole(RoleCode roleCode) {
        return roleCode != null && roleCodes.contains(roleCode.getValue());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.isEnabled(status);
    }
}
