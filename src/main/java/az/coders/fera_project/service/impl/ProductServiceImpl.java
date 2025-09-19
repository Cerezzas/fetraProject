package az.coders.fera_project.service.impl;

import az.coders.fera_project.config.EnhancedObjectMapper;
import az.coders.fera_project.dto.homepage.ProductMainPageDto;
import az.coders.fera_project.dto.homepage.ReviewMainPageDto;
import az.coders.fera_project.dto.product.user.ProductDetailsDto;
import az.coders.fera_project.dto.product.user.ProductFilterRequestDto;
import az.coders.fera_project.dto.product.admin.ProductRequestDto;
import az.coders.fera_project.entity.Category;
import az.coders.fera_project.entity.Media;
import az.coders.fera_project.entity.Product;
import az.coders.fera_project.entity.Review;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.repository.CategoryRepository;
import az.coders.fera_project.repository.MediaRepository;
import az.coders.fera_project.repository.ProductRepository;
import az.coders.fera_project.repository.ReviewRepository;
import az.coders.fera_project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MediaRepository mediaRepository;
    private final EnhancedObjectMapper mapper;
    private final ReviewRepository reviewRepository;

//    @Override
//    public List<ProductDetailsDto> getProducts() {
//        List<Product> products = productRepository.findAll();
//        return products.stream()
//                .map(this::mapToProductDetailsDto)
//                .collect(Collectors.toList());
//    }
@Override
public List<ProductMainPageDto> getProducts() {
    return mapper.convertList(productRepository.findAll(), ProductMainPageDto.class);
}


    @Override
    public ProductDetailsDto getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // 🟡 Основной маппинг
        ProductDetailsDto dto = mapToProductDetailsDto(product);

        // ✅ Добавляем topReviews
        List<ReviewMainPageDto> topReviews = product.getReviews().stream()
                .filter(r -> r.getRating() != null && r.getRating() >= 4.0)
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .limit(2)
                .map(r -> new ReviewMainPageDto(
                        r.getUserName(), // Имя пользователя
                        r.getComment(),        // Комментарий
                        r.getRating(),         // Рейтинг
                        r.getCreatedAt()       // Дата
                ))
                .toList();

        dto.setTopReviews(topReviews); // Устанавливаем поле

        return dto;
    }

    // 🛠 Метод маппинга (вынесен в отдельный, можно использовать в getProducts())
    private ProductDetailsDto mapToProductDetailsDto(Product product) {
        ProductDetailsDto dto = new ProductDetailsDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setOldPrice(product.getOldPrice());
        dto.setImage(product.getImage());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setAvailableColors(product.getAvailableColors());
        dto.setMaterial(product.getMaterial());
        dto.setColorName(product.getColorName());
        dto.setWeightKg(product.getWeightKg());
        dto.setWidthCm(product.getWidthCm());
        dto.setHeightCm(product.getHeightCm());
        dto.setDepthCm(product.getDepthCm());
        dto.setDetailedDescription(product.getDetailedDescription());
        dto.setAdditionalInformation(product.getAdditionalInformation());
        dto.setShippingAndDelivery(product.getShippingAndDelivery());
        dto.setReturnDays(product.getReturnDays());
        dto.setDeliveryDate(product.getDeliveryDate());
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());

        // Похожие товары, если они у тебя есть
        if (product.getSimilarProducts() != null) {
            List<ProductMainPageDto> similarProducts = product.getSimilarProducts().stream()
                    .map(this::mapToMainPageDto)
                    .collect(Collectors.toList());
            dto.setSimilarProducts(similarProducts);
        }

        // ⚠️ topReviews маппим отдельно в getProductById()

        return dto;
    }



    @Override
    public ProductDetailsDto createProduct(ProductRequestDto dto) {
        Product product = mapper.convertValue(dto, Product.class);

        // Категория
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            product.setCategory(category);
        }

        // Похожие продукты
        if (dto.getSimilarProductIds() != null && !dto.getSimilarProductIds().isEmpty()) {
            List<Product> similar = productRepository.findAllById(dto.getSimilarProductIds());
            product.setSimilarProducts(similar);
        }

        Product saved = productRepository.save(product); // Сначала сохраняем сам продукт

        // 🔁 Добавляем обратные связи у похожих продуктов
        if (saved.getSimilarProducts() != null && !saved.getSimilarProducts().isEmpty()) {
            for (Product similarProduct : saved.getSimilarProducts()) {
                if (!similarProduct.getSimilarProducts().contains(saved)) {
                    similarProduct.getSimilarProducts().add(saved);
                    productRepository.save(similarProduct);
                }
            }
        }

        return convertToDetailsDto(saved);
    }

    @Override
    public void addImageToProduct(Integer productId, Integer mediaId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new NotFoundException("Media not found"));

        product.setImage(media);
        productRepository.save(product);
    }

    @Override
    public void deleteImageFromProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        product.setImage(null);
        productRepository.save(product);
    }

    @Override
    public ProductDetailsDto updateProduct(Integer id, ProductRequestDto dto) {
        Product existing = findProductById(id);

        // Обновление полей
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getPrice() != null) existing.setPrice(dto.getPrice());
        if (dto.getOldPrice() != null) existing.setOldPrice(dto.getOldPrice());
    //    if (dto.getMainImageUrl() != null) existing.setImageUrl(dto.getMainImageUrl());
        if (dto.getStockQuantity() != null) existing.setStockQuantity(dto.getStockQuantity());
        if (dto.getAvailableColors() != null) existing.setAvailableColors(dto.getAvailableColors());
        if (dto.getMaterial() != null) existing.setMaterial(dto.getMaterial());
        if (dto.getColorName() != null) existing.setColorName(dto.getColorName());
        if (dto.getWeightKg() != null) existing.setWeightKg(dto.getWeightKg());
        if (dto.getWidthCm() != null) existing.setWidthCm(dto.getWidthCm());
        if (dto.getHeightCm() != null) existing.setHeightCm(dto.getHeightCm());
        if (dto.getDepthCm() != null) existing.setDepthCm(dto.getDepthCm());
        if (dto.getDetailedDescription() != null) existing.setDetailedDescription(dto.getDetailedDescription());
        if (dto.getAdditionalInformation() != null) existing.setAdditionalInformation(dto.getAdditionalInformation());
        if (dto.getShippingAndDelivery() != null) existing.setShippingAndDelivery(dto.getShippingAndDelivery());
        if (dto.getReturnDays() != null) existing.setReturnDays(dto.getReturnDays());
        if (dto.getDeliveryDate() != null) existing.setDeliveryDate(dto.getDeliveryDate());

        // Категория
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            existing.setCategory(category);
        }

        // Похожие продукты (обновление двусторонней связи)
        if (dto.getSimilarProductIds() != null) {
            List<Product> newSimilar = productRepository.findAllById(dto.getSimilarProductIds());

            // Удаляем старые обратные связи
            for (Product oldSimilar : existing.getSimilarProducts()) {
                if (!newSimilar.contains(oldSimilar)) {
                    oldSimilar.getSimilarProducts().remove(existing);
                    productRepository.save(oldSimilar);
                }
            }

            // Добавляем новые обратные связи
            for (Product newSim : newSimilar) {
                if (!newSim.getSimilarProducts().contains(existing)) {
                    newSim.getSimilarProducts().add(existing);
                    productRepository.save(newSim);
                }
            }

            existing.setSimilarProducts(newSimilar);
        }

        Product saved = productRepository.save(existing);
        return convertToDetailsDto(saved);
    }

    @Override
    public void deleteProduct(Integer id) {
        Product product = findProductById(id);

        // Удаляем связи с similarProducts (двусторонняя)
        for (Product similar : product.getSimilarProducts()) {
            similar.getSimilarProducts().remove(product);
            productRepository.save(similar);
        }

        product.getSimilarProducts().clear();

        productRepository.delete(product); // не по id, а по объекту
    }




    private Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    // 🔁 Хелпер для конвертации в ProductDetailsDto c similarProducts
    private ProductDetailsDto convertToDetailsDto(Product product) {
        ProductDetailsDto dto = mapper.convertValue(product, ProductDetailsDto.class);

        // Список похожих товаров
        dto.setSimilarProducts(
                mapper.convertList(product.getSimilarProducts(), ProductMainPageDto.class)
        );

        // Получаем отзывы для продукта
        List<Review> reviews = reviewRepository.findByProductId(product.getId());

        // Средний рейтинг
        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        // Количество отзывов
        int reviewCount = reviews.size();

        dto.setAverageRating(averageRating);
        dto.setReviewCount(reviewCount);

        return dto;
    }


    @Override
    public List<ProductMainPageDto> filterProducts(ProductFilterRequestDto request) {
        // 1. Загружаем все продукты
        List<Product> products = productRepository.findAll();

        // 2. Фильтрация по категориям
        if (request.getCategoryNames() != null && !request.getCategoryNames().isEmpty()) {

            List<Integer> categoryIdsToFilter;

            if (request.getCategoryNames().contains("All")) {
                // All — не фильтруем по категориям
                categoryIdsToFilter = null;
            } else if (request.getCategoryNames().contains("Others")) {
                // Others — все категории, кроме стандартных
                List<String> standard = List.of("Living room", "Kitchen", "Bedroom", "Bathroom");
                List<Category> others = categoryRepository.findAll().stream()
                        .filter(c -> !standard.contains(c.getName()))
                        .toList();
                categoryIdsToFilter = others.stream().map(Category::getId).toList();
            } else {
                // Обычные выбранные категории
                List<Category> selected = categoryRepository.findAllByNameIn(request.getCategoryNames());
                categoryIdsToFilter = selected.stream().map(Category::getId).toList();
            }

            if (categoryIdsToFilter != null && !categoryIdsToFilter.isEmpty()) {
                products = products.stream()
                        .filter(p -> p.getCategory() != null && categoryIdsToFilter.contains(p.getCategory().getId()))
                        .collect(Collectors.toList());
            }
        }

        // 3. Фильтрация по цветам
        if (request.getColors() != null && !request.getColors().isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getAvailableColors() != null &&
                            p.getAvailableColors().stream().anyMatch(request.getColors()::contains))
                    .collect(Collectors.toList());
        }

        // 4. Фильтрация по цене
        if (request.getMinPrice() != null) {
            products = products.stream()
                    .filter(p -> p.getPrice() != null &&
                            BigDecimal.valueOf(p.getPrice()).compareTo(request.getMinPrice()) >= 0)
                    .collect(Collectors.toList());
        }

        if (request.getMaxPrice() != null) {
            products = products.stream()
                    .filter(p -> p.getPrice() != null &&
                            BigDecimal.valueOf(p.getPrice()).compareTo(request.getMaxPrice()) <= 0)
                    .collect(Collectors.toList());
        }

        // 5. Сортировка
        Comparator<Product> comparator = switch (request.getSortBy()) {
            case "priceAsc" -> Comparator.comparing(Product::getPrice);
            case "priceDesc" -> Comparator.comparing(Product::getPrice, Comparator.nullsLast(Double::compare)).reversed();
            case "popularity" -> Comparator.comparing(Product::getReviewCount, Comparator.nullsLast(Integer::compare)).reversed();
            default -> Comparator.comparing(Product::getId);
        };

        products.sort(comparator);

        // 6. Маппим вручную
        return products.stream()
                .map(this::mapToMainPageDto)
                .toList();
    }


    // Упрощённый маппинг продукта для главной/фильтра
    private ProductMainPageDto mapToMainPageDto(Product product) {
        ProductMainPageDto dto = new ProductMainPageDto();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setOldPrice(product.getOldPrice());
        dto.setImage(product.getImage());
        dto.setAverageRating(product.getAverageRating());
        return dto;
    }





}


