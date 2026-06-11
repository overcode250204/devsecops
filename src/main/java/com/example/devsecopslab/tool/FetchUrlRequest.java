package com.example.devsecopslab.tool;

import jakarta.validation.constraints.NotBlank;

public record FetchUrlRequest(
        @NotBlank String url
) {}
