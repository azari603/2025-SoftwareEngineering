package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.ReadingStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SetStatusRequest {

    @NotNull(message = "상태 값은 필수입니다.")
    private ReadingStatusType status;
}
