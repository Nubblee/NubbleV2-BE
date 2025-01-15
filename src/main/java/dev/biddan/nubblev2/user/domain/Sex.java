package dev.biddan.nubblev2.user.domain;

import java.util.Arrays;

public enum Sex {
    MALE, FEMALE, NONE;

    private static final String ALLOWED_VALUES = Arrays.toString(Sex.values());

    public static Sex from(String sex) {
        if (sex == null || sex.isBlank()) {
            throw new IllegalArgumentException("sex는 null이거나 비어있을 수 없습니다.");
        }

        String upperSex = sex.trim().toUpperCase();

        try {
            return Sex.valueOf(upperSex);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("잘못된 Sex 값: '%s', 허용되는 Sex 값들: %s", sex, ALLOWED_VALUES));
        }
    }
}
