package dev.biddan.nubblev2.study.announcement.controller.dto;

import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementInfo;

public class StudyAnnouncementApiResponse {

    public record Basic(
            StudyAnnouncementInfo.Basic studyAnnouncement
    ) {

    }
}
