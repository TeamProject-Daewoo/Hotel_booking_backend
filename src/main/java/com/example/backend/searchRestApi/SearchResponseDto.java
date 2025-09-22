package com.example.backend.searchRestApi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SearchResponseDto {
    private List<SearchCardDto> searchCards = new ArrayList<>();
    private Map<String, Integer> counts = new LinkedHashMap<>();
}
