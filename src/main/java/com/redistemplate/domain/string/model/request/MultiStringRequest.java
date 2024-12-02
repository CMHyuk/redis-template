package com.redistemplate.domain.string.model.request;

import com.redistemplate.common.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "string multi set request")
public record MultiStringRequest(

    BaseRequest baseRequest,

    @Schema(description = "names")
    @NotBlank
    @NotNull
    String[] Names
) {
} 
