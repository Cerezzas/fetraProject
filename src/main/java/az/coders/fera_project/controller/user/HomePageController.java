package az.coders.fera_project.controller.user;

import az.coders.fera_project.dto.homepage.HomePageDto;
import az.coders.fera_project.service.HomePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomePageController {

    private final HomePageService homePageService;

    @GetMapping
    public ResponseEntity<HomePageDto> getHomePageData() {
        return ResponseEntity.ok(homePageService.getHomePageData());
    }

    @GetMapping("/instagram-link")
    public ResponseEntity<String> getInstagramLink() {
        return ResponseEntity.ok(homePageService.getInstagramLink());
    }

//    // Внедряем сервис через конструктор
//    public HomePageController(HomePageService homePageService) {
//        this.homePageService = homePageService;
//    }
//
//    @GetMapping("/instagram-link")
//    public String getInstagramLink() {
//        return homePageService.getInstagramLink();  // Делаем вызов через сервис
//    }
}

