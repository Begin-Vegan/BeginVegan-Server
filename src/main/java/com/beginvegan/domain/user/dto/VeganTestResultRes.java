package com.beginvegan.domain.user.dto;

import com.beginvegan.domain.user.domain.VeganType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VeganTestResultRes {

    private String nickname;

    private VeganType veganType;

    @Builder
    public VeganTestResultRes(String nickname, VeganType veganType) {
        this.nickname = nickname;
        this.veganType = veganType;
    }
}
