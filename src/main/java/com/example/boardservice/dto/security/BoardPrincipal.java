package com.example.boardservice.dto.security;

import com.example.boardservice.dto.UserAccountDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record BoardPrincipal(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String email,
        String nickname,
        String memo
) implements UserDetails {

    public static BoardPrincipal of(String username, String password, String email, String nickname, String memo) {
        Set<RoleType> roleTypes = Set.of(RoleType.USER);
        return new BoardPrincipal(
                username,
                password,
                roleTypes.stream()
                        .map(RoleType::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet())
                ,
                email,
                nickname,
                memo
        );
    }

    public static BoardPrincipal from(UserAccountDto userAccountDto){
        return BoardPrincipal.of(userAccountDto.userId(),
                userAccountDto.userPassword(),
                userAccountDto.email(),
                userAccountDto.nickname(),
                userAccountDto.memo()
                );
    }

    public static UserAccountDto toDto(BoardPrincipal boardPrincipal){
        return UserAccountDto.of(boardPrincipal.username,
                boardPrincipal.password(),
                boardPrincipal.email(),
                boardPrincipal.nickname(),
                boardPrincipal.memo());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {return authorities;}

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return true;
    }

    public enum RoleType{
        USER("ROLE_USER");

        @Getter
        private final String name;

        RoleType(String name){
            this.name = name;
        }
    }
}
