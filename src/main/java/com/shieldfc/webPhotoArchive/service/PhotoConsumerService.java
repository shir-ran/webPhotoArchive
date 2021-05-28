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
        List<PhotoSource> list = mapper.readValue(jsonString, new TypeReference<List<PhotoSource>>() {
        });
        return list;
    }

    public Photo downloadPhoto(PhotoSource source) throws IOException {
        Photo photo = getPhoto(source);

        String[] splitString = source.getThumbnailUrl().split("\\.");
        String format = splitString[splitString.length - 1];
        String localPath = localDirectory + "\\" + source.getTitle().replace(' ','_') + "." + format;

        URL url = new URL(source.getThumbnailUrl());
        BufferedImage image = ImageIO.read(url);
        File file = new File(localPath);
        ImageIO.write(image, format, file);

        photo.setFileSize(file.getTotalSpace());
        photo.setLocalPath(localPath);
        return photo;
    }

    private Photo getPhoto(PhotoSource source) {
        Photo photo = new Photo();
        photo.setAlbumId(source.getAlbumId());
        photo.setId(source.getId());
        photo.setName(source.getTitle());
        photo.setTimestamp(LocalDateTime.now().toString());
        return photo;
    }


}
