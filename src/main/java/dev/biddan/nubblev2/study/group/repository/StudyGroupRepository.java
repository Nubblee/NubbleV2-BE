package dev.biddan.nubblev2.study.group.repository;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.MeetingDay;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    @Query("""
        SELECT sg.id, lang
        FROM StudyGroup sg
        JOIN sg.languages.languages lang
        WHERE sg.id IN :studyGroupIds
        """)
    List<Object[]> findLanguagesByStudyGroupIds(@Param("studyGroupIds") List<Long> studyGroupIds);

    @Query("""
        SELECT sg.id, level
        FROM StudyGroup sg
        JOIN sg.difficultyLevels.values level
        WHERE sg.id IN :studyGroupIds
        """)
    List<Object[]> findDifficultyLevelsByStudyGroupIds(@Param("studyGroupIds") List<Long> studyGroupIds);

    @Query("""
        SELECT sg.id, days
        FROM StudyGroup sg
        JOIN sg.meeting.mainMeetingDays days
        WHERE sg.id IN :studyGroupIds
        """)
    List<Object[]> findMeetingDaysByStudyGroupIds(List<Long> studyGroupIds);

    // 편의 메서드 - default로 구현
    default Map<Long, List<StudyGroup.ProgrammingLanguage>> findLanguagesMapByStudyGroupIds(List<Long> studyGroupIds) {
        if (studyGroupIds.isEmpty()) {
            return Map.of();
        }

        return findLanguagesByStudyGroupIds(studyGroupIds).stream()
                .collect(groupingBy(
                        row -> (Long) row[0],
                        mapping(row -> (StudyGroup.ProgrammingLanguage) row[1], toList())
                ));
    }

    default Map<Long, List<StudyGroup.DifficultyLevel>> findDifficultyLevelsMapByStudyGroupIds(List<Long> studyGroupIds) {
        if (studyGroupIds.isEmpty()) {
            return Map.of();
        }

        return findDifficultyLevelsByStudyGroupIds(studyGroupIds).stream()
                .collect(groupingBy(
                        row -> (Long) row[0],
                        mapping(row -> (StudyGroup.DifficultyLevel) row[1], toList())
                ));
    }

    default Map<Long, List<MeetingDay>> findMeetingDaysMapByStudyGroupIds(List<Long> studyGroupIds) {
        if (studyGroupIds.isEmpty()) {
            return Map.of();
        }

        return findMeetingDaysByStudyGroupIds(studyGroupIds).stream()
                .collect(groupingBy(
                        row -> (Long) row[0],
                        mapping(row -> (StudyGroup.MeetingDay) row[1], toList())
                ));
    }
}
