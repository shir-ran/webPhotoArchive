package com.shieldfc.webPhotoArchive.repository;

import com.shieldfc.webPhotoArchive.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
