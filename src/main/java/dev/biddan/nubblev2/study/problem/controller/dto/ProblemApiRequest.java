package dev.biddan.nubblev2.study.problem.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

public class ProblemApiRequest {

    @Builder
    public record Create(
            @NotBlank(message = "제목은 필수입니다")
            String title,
            
            @NotBlank(message = "URL은 필수입니다")
            String url,
            
            @NotNull(message = "날짜는 필수입니다")
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate date
    ) {
    }
}