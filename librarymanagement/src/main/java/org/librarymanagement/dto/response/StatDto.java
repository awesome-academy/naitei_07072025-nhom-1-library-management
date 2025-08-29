package org.librarymanagement.dto.response;

public record StatDto(
        long current,   // giá trị tuần này
        double change   // % thay đổi so với tuần trước
) {}
