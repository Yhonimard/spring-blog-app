package yhoni.blog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import yhoni.blog.entity.PostImage;
import yhoni.blog.repository.PostImageRepository;
import yhoni.blog.service.PostImageService;

@Service
public class PostImageServiceImpl implements PostImageService {

    @Autowired
    private PostImageRepository postImageRepository;

    @Override
    public byte[] getImageById(String imgId) {
        PostImage postImage = postImageRepository.findById(imgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "image is empty"));
        return postImage.getImage();
    }
}
