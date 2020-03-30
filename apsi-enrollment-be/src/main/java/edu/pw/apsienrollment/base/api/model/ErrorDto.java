package edu.pw.apsienrollment.base.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {
    private int code;
    private String message;
    private String uri;
}
