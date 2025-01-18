package dev.biddan.nubblev2.user.error.exception;

import dev.biddan.nubblev2.common.error.BaseException;

public class UserLoginIdAlreadyExistsException extends BaseException {

    public UserLoginIdAlreadyExistsException(String loginId) {
        super(String.format("아이디 '%s' 이(가) 이미 존재합니다.", loginId));
    }
}
