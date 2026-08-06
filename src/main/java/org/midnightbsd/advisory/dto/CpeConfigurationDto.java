package org.midnightbsd.advisory.dto;

import java.util.List;

public record CpeConfigurationDto(
    int id, Integer parentId, String operator, Boolean negate, List<CpeRangeDto> matches) {}
