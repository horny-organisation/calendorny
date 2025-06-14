package ru.calendorny.dto;

import lombok.Builder;

@Builder
public record LabelDto(
    Long id,
    String name,
    String color
) {
}
