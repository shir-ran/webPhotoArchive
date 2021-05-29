package com.shieldfc.webPhotoArchive.model;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class Image {
    @Id
    protected long id;
    protected long albumId;
    protected String title;
}
