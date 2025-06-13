package dev.biddan.nubblev2.user.interest.controller.dto;

import dev.biddan.nubblev2.user.interest.servicce.dto.UserInterestInfo;

public class UserInterestApiResponse {

    public record Basic(
            UserInterestInfo.Basic userInterest
    ) {

    }

}
