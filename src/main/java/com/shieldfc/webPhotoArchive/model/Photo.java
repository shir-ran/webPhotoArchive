package com.shieldfc.webPhotoArchive.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="photo")
@Data
public class Photo {

    @Id
    private long id;

    private String name;

    private LocalDateTime timestamp;

    private String localPath;

    private Long fileSize;

    private Long albumId;
}
