package dev.biddan.nubblev2.study.problem.controller;

import dev.biddan.nubblev2.argument.userid.CurrentUserId;
import dev.biddan.nubblev2.interceptor.auth.AuthRequired;
import dev.biddan.nubblev2.study.problem.controller.dto.ProblemApiRequest;
import dev.biddan.nubblev2.study.problem.controller.dto.ProblemApiResponse;
import dev.biddan.nubblev2.study.problem.service.ProblemService;
import dev.biddan.nubblev2.study.problem.service.dto.ProblemInfo;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/study-groups")
@RequiredArgsConstructor
public class ProblemApiController {

    private final ProblemService problemService;

    @PostMapping("/{studyGroupId}/problems")
    @ResponseStatus(HttpStatus.CREATED)
    @AuthRequired
    public ResponseEntity<ProblemApiResponse.Create> createProblem(
            @PathVariable Long studyGroupId,
            @Valid @RequestBody ProblemApiRequest.Create request,
            @CurrentUserId Long userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ProblemApiResponse.Create(problemService.createProblem(studyGroupId, request, userId)));
    }

    @GetMapping("/{studyGroupId}/problems")
    public ResponseEntity<ProblemInfo.PageList> getProblemsWithPage(
            @PathVariable Long studyGroupId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        if (page < 1) {
            page = 1;
        }
        if (limit < 1) {
            limit = 1;
        }
        limit = Math.min(limit, 100);
        
        ProblemInfo.PageList pageList = problemService.findProblemsWithPage(studyGroupId, page, limit);
        return ResponseEntity.ok(pageList);
    }

    @DeleteMapping("/{studyGroupId}/problems/{problemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @AuthRequired
    public ResponseEntity<Void> deleteProblem(
            @PathVariable Long studyGroupId,
            @PathVariable Long problemId,
            @CurrentUserId Long userId
    ) {
        problemService.deleteProblem(problemId, userId);
        return ResponseEntity.noContent().build();
    }
}