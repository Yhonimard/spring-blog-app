package yhoni.blog.utils.image;

import net.coobird.thumbnailator.Thumbnails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImgService {
    @Autowired
    private ImgRepository imgRepository;

    public String uploadImg(MultipartFile file, String name) throws IOException {
        Img img = new Img();
        img.setId(21);
        img.setName(name);
//        img.setImage(converToWebP(file.getBytes()));
        img.setImage(converToWebP(file.getBytes()));
        imgRepository.save(img);
        return "image seuccess upload";
    }

    private byte[] converToWebP(byte[] imgBytes) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(imgBytes))
                .scale(1)
                .outputQuality(0.1)
                .outputFormat("png")
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}
