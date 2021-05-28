package com.shieldfc.webPhotoArchive.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shieldfc.webPhotoArchive.model.Photo;
import com.shieldfc.webPhotoArchive.model.PhotoSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PhotoConsumerService {
    @Value("${url.photo.source}")
    private String sourceUrl;

    private String localDirectory = "C:\\develop\\webPhotoArchive\\photos";

    private final RestTemplate restTemplate = new RestTemplate();



    public List<PhotoSource> loadPhotoData() throws JsonProcessingException {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(sourceUrl, String.class);
        String jsonString = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<>() {});
    }

    public Photo downloadPhoto(PhotoSource source) throws IOException {
        Photo photo = new Photo();
        photo.setAlbumId(source.getAlbumId());
        photo.setId(source.getId());
        photo.setName(source.getTitle());
        photo.setTimestamp(LocalDateTime.now().toString());

        String[] splitString = source.getThumbnailUrl().split("\\.");
        String format = splitString[splitString.length - 1];
        String localPath = localDirectory + "\\" + source.getTitle().replace(' ','_') + "." + format;

        URL url = new URL(source.getThumbnailUrl());
        BufferedImage image = ImageIO.read(url);
        File file = new File(localPath);
        ImageIO.write(image, format, file);

        photo.setFileSize(getFileSize(image));
        photo.setLocalPath(localPath);
        return photo;
    }

    private long getFileSize(BufferedImage image) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        boolean resultWrite = ImageIO.write(image, "PNG", bos);
        byte[] imageInBytes = bos.toByteArray();
        return imageInBytes.length;
    }


}
