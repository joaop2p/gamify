package com.example.gamify.models.dtos.input;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProfileInputDTO (
        @Schema(name="userName", description="Nome do usuário", example="John Doe")
        String name,
        @Schema(name="avatarUrl", description="URL do avatar do usuário", example="https://example.com/avatar.jpg")
        String avatarUrl
){
}
