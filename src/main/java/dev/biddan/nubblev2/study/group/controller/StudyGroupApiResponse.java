package dev.biddan.nubblev2.study.group.controller;

import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo;

public class StudyGroupApiResponse {

    public record Private(
            StudyGroupInfo.Private studyGroup
    ) {
    }
}
