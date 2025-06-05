package dev.biddan.nubblev2.study.applicationform.repository;

import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyApplicationFormRepository extends JpaRepository<StudyApplicationForm, Long> {

}
