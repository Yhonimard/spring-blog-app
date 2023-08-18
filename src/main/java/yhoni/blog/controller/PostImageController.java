package yhoni.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yhoni.blog.service.PostImageService;

@RestController
@RequestMapping("/api/post/image")
public class PostImageController {
    @Autowired
    private PostImageService postImageService;

    @GetMapping(value = "/{imageId}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable("imageId") String imageId
    ) {
        byte[] image = postImageService.getImageById(imageId);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(image);
    }


}
