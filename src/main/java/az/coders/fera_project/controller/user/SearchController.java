package az.coders.fera_project.controller.user;

import az.coders.fera_project.dto.homepage.ProductMainPageDto;
import az.coders.fera_project.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<ProductMainPageDto>> search(@RequestParam("query") String query) {
        List<ProductMainPageDto> result = searchService.searchByProductName(query);
        return ResponseEntity.ok(result);
    }
}
