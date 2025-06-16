package dev.biddan.nubblev2.study.group.repository;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;

@EntityView(StudyGroup.class)
public record StudyGroupView(
        @IdMapping
        Long id,

        @Mapping("name.value")
        String name,

        @Mapping("languages.mainLanguage")
        StudyGroup.ProgrammingLanguage mainLanguage,

        @Mapping("capacity.value")
        Integer capacity,

        @Mapping("meeting.meetingType")
        StudyGroup.MeetingType meetingType,

        @Mapping("meeting.meetingRegion")
        String meetingRegion
) {

}
