package com.shieldfc.webPhotoArchive.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="photo")
@Data
public class Photo extends Image {

//    @Id
//    private long id;

    private String timestamp;

    private String localPath;

    private Long fileSize;


    public static Photo fromPhotoSource(PhotoSource source){
        Photo photo = new Photo();
        photo.setAlbumId(source.getAlbumId());
        photo.setId(source.getId());
        photo.setTitle(source.getTitle());
        photo.setTimestamp(LocalDateTime.now().toString());

        return photo;
    }
}
