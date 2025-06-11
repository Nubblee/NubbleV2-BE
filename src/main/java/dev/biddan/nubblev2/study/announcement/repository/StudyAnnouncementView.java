package dev.biddan.nubblev2.study.announcement.repository;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.AnnouncementStatus;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.ClosedReason;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EntityView(StudyAnnouncement.class)
public record StudyAnnouncementView(
        @IdMapping
        Long id,

        @Mapping("studyGroup.id")
        Long studyGroupId,

        @Mapping("title.value")
        String title,

        @Mapping("description.value")
        String description,

        @Mapping("recruitCapacity.value")
        Integer recruitCapacity,

        @Mapping("endDate.value")
        LocalDate endDate,

        AnnouncementStatus status,

        ClosedReason closedReason,

        LocalDateTime createdAt,

        LocalDateTime closedAt,

        @Mapping("studyGroup.name.value")
        String studyGroupName
) {

}
