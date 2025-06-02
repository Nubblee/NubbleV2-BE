package dev.biddan.nubblev2.study.announcement.repository;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.AnnouncementStatus;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyAnnouncementBlazeRepository {

    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final EntityViewManager evm;

    public PagedList<StudyAnnouncementView> findAnnouncements(List<String> statuses, int page, int size) {
        CriteriaBuilder<StudyAnnouncement> baseQueryBuilder = cbf
                .create(em, StudyAnnouncement.class)
                .orderByDesc("createdAt").orderByAsc("id");

        if (statuses != null && !statuses.isEmpty()) {
            List<AnnouncementStatus> announcementStatuses = statuses.stream()
                    .map(status -> {
                        try {
                            return AnnouncementStatus.valueOf(status.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

            if (!announcementStatuses.isEmpty()) {
                baseQueryBuilder.where("status").in(announcementStatuses);
            }
        }

        EntityViewSetting<StudyAnnouncementView, PaginatedCriteriaBuilder<StudyAnnouncementView>> setting =
                EntityViewSetting.create(StudyAnnouncementView.class, (page - 1) * size, size);

        return evm.applySetting(setting, baseQueryBuilder)
                .getResultList();
    }


}
