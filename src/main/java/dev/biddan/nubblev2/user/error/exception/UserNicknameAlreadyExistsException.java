package dev.biddan.nubblev2.user.error.exception;

import dev.biddan.nubblev2.common.error.BaseException;

public class UserNicknameAlreadyExistsException extends BaseException {

    public UserNicknameAlreadyExistsException(final String nickname) {
        super(String.format("닉네임 '%s' 이(가) 이미 존재합니다.", nickname));
    }
}
