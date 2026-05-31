package com.air_ops_system.common.dto;

import java.util.List;

public record PagedResponseDTO<T>(
    List<T> data,
    PaginationMeta pagination
) {
  public record PaginationMeta(int page, int size, long total, int totalPages) {}
}
