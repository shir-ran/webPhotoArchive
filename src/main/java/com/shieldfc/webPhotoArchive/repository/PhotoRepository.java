package com.shieldfc.webPhotoArchive.repository;

import com.shieldfc.webPhotoArchive.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    @Query("select p from Photo p where p.albumId = ?1")
    List<Photo> findAllByAlbumId(long albumId);
}
