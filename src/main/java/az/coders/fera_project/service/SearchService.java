package az.coders.fera_project.service;

import az.coders.fera_project.dto.homepage.ProductMainPageDto;

import java.util.List;

public interface SearchService {
    List<ProductMainPageDto> searchByProductName(String query);
}

