package com.shieldfc.webPhotoArchive.model;

import lombok.Data;

@Data
public class PhotoSource {
    private long albumId;

    private long id;

    private String title;

    private String url;

    private String thumbnailUrl;

    public String getUndTitle(){
        return title.replace(' ','_');
    }
}
