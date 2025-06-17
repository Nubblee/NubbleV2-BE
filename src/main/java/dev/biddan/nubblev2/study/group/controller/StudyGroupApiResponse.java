package dev.biddan.nubblev2.study.group.controller;

import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo;
import java.util.List;

public class StudyGroupApiResponse {

    public record Detail(
            StudyGroupInfo.Detail studyGroup
    ) {

    }

    public record MyStudyGroups(
            List<MyStudyGroup> studyGroups
    ) {

        public static MyStudyGroups of(List<StudyGroup> studyGroups) {
            List<MyStudyGroup> privates = studyGroups.stream()
                    .map(sg -> new MyStudyGroup(
                            sg.getName().getValue(),
                            sg.getLanguages().getMainLanguage().name()))
                    .toList();

            return new MyStudyGroups(privates);
        }
    }

    public record MyStudyGroup(
            String name,
            String mainLanguage
    ) {

    }
}
