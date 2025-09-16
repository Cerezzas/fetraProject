package az.coders.fera_project.service;

import az.coders.fera_project.dto.homepage.HomePageDto;

public interface HomePageService {
    // Метод для получения ссылки на Instagram
    String getInstagramLink();

    // Метод для получения данных для главной страницы
    HomePageDto getHomePageData();
}
