package az.coders.fera_project.controller.admin;


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


@RequiredArgsConstructor
@RestController("adminMediaController")
@RequestMapping("/admin/medias")
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

    @PostMapping //create Media
    public ResponseEntity<MediaDto> uploadMedia(@RequestParam Integer mediaTypeId, @RequestParam MultipartFile file) {
        return new ResponseEntity<>(mediaService.uploadMedia(mediaTypeId, file) , HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedia(@PathVariable Integer id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }


}
