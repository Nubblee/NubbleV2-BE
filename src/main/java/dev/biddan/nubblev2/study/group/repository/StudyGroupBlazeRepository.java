package dev.biddan.nubblev2.study.group.repository;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyGroupBlazeRepository {

    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final EntityViewManager evm;

    public PagedList<StudyGroupView> findStudyGroups(int page, int size) {
        CriteriaBuilder<StudyGroup> baseQueryBuilder = cbf
                .create(em, StudyGroup.class)
                .orderByDesc("createdAt")
                .orderByDesc("id");

        EntityViewSetting<StudyGroupView, PaginatedCriteriaBuilder<StudyGroupView>> setting =
                EntityViewSetting.create(StudyGroupView.class, (page - 1) * size, size);

        return evm.applySetting(setting, baseQueryBuilder)
                .getResultList();
    }
}
