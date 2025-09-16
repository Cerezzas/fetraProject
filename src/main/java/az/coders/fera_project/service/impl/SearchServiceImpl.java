package az.coders.fera_project.service.impl;

import az.coders.fera_project.config.EnhancedObjectMapper;
import az.coders.fera_project.dto.homepage.ProductMainPageDto;
import az.coders.fera_project.entity.Product;
import az.coders.fera_project.repository.ProductRepository;
import az.coders.fera_project.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ProductRepository productRepository;
    private final EnhancedObjectMapper mapper;



    @Override
    public List<ProductMainPageDto> searchByProductName(String query) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(query);
        return mapper.convertList(products, ProductMainPageDto.class);
    }
}
