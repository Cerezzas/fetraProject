package az.coders.fera_project.controller.user;


import az.coders.fera_project.dto.MediaDto;
import az.coders.fera_project.entity.Media;
import az.coders.fera_project.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController("userMediaController")
@RequestMapping("/medias") // базовый путь
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;


    @GetMapping("/{id}") //getById
    public ResponseEntity<Resource> getMediaById(@PathVariable Integer id) {
        Media media = mediaService.getMedia(id);
        MediaType mediaType = mediaService.getMediaType(media.getPath());
        return ResponseEntity.ok()
                .contentType(mediaType)
                //.header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename="+media.getPath())
                .body(mediaService.getResource(media.getPath()));

    }

}
