package com.redistemplate.domain.hashes.model.reqeust;

import com.redistemplate.common.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "redis hash collection request")
public record HashRequest(
    BaseRequest baseRequest,

    @Schema(description = "field")
    @NotBlank
    @NotNull
    String Field,

    @Schema(description = "name")
    @NotBlank
    @NotNull
    String Name
) {
}  
