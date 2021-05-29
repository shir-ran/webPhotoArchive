package com.shieldfc.webPhotoArchive.model;

import lombok.Data;
import org.hibernate.annotations.Target;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="photo")
@Data
public class Photo {

    @Id
    private long id;

    private String name;

    private String timestamp;

    private String localPath;

    private Long fileSize;

    private Long albumId;

    public static Photo fromPhotoSource(PhotoSource source){
        Photo photo = new Photo();
        photo.setAlbumId(source.getAlbumId());
        photo.setId(source.getId());
        photo.setName(source.getTitle());
        photo.setTimestamp(LocalDateTime.now().toString());

        return photo;
    }
}
