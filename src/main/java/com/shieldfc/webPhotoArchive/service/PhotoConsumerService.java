package com.shieldfc.webPhotoArchive.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shieldfc.webPhotoArchive.model.Photo;
import com.shieldfc.webPhotoArchive.model.PhotoSource;
import org.apache.tomcat.util.http.fileupload.FileUtils;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoConsumerService {
    @Value("${photo.source.url}")
    private String sourceUrl;

    @Value("${photo.source.file}")
    private String localDirectory;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<PhotoSource> extractPhotosSourceData() throws JsonProcessingException {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(sourceUrl, String.class);
        String jsonString = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<>() {});
    }

    public Photo handlePhotoInitialization(PhotoSource source) throws IOException {
        String format = getFileFormat(source);
        String localPath = localDirectory + "\\" + source.getUndTitle() + "." + format;

        BufferedImage image = DownloadPhotoFromWeb(source);
        savePhotoToFile(format, localPath, image);

        Photo photo = Photo.fromPhotoSource(source);
        photo.setFileSize(getFileSize(image));
        photo.setLocalPath(localPath);
        return photo;
    }

    private void savePhotoToFile(String format, String localPath, BufferedImage image) throws IOException {
        File file = new File(localPath);
        ImageIO.write(image, format, file);
    }

    private BufferedImage DownloadPhotoFromWeb(PhotoSource source) throws IOException {
        return ImageIO.read(new URL(source.getThumbnailUrl()));
    }

    private String getFileFormat(PhotoSource source) {
        String[] splitString = source.getThumbnailUrl().split("\\.");
        String format = splitString[splitString.length - 1];
        format = "jfif".equals(format) ? "jpg": format;
        return format;
    }

    private long getFileSize(BufferedImage image) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", bos);
        byte[] imageInBytes = bos.toByteArray();
        return imageInBytes.length;
    }


    public void cleanDirectory() throws IOException {
        FileUtils.cleanDirectory(new File(localDirectory));
    }

    public List<Photo> initializeArchive(StringBuffer errors) {
        List<PhotoSource> photoSources;
        List<Photo> photos = new ArrayList<>();
        try {
            photoSources = extractPhotosSourceData();
            for (PhotoSource photoSource : photoSources){
                try {
                    photos.add(handlePhotoInitialization(photoSource));
                } catch (IOException e) {
                    e.printStackTrace();
                    errors.append("Failed getting the data for photo ").append(photoSource.getTitle())
                            .append(" from url source ").append(photoSource.getThumbnailUrl()).append("\n");
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            errors.append("Failed getting the photo list from the web.");
        }
        return photos;
    }
}
