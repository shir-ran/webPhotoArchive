package com.shieldfc.webPhotoArchive.service;

import com.shieldfc.webPhotoArchive.model.Photo;
import com.shieldfc.webPhotoArchive.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository repo;

    public List<Photo> getAll(){
        return repo.findAll();
    }

    public void create(Photo photo){
        repo.save(photo);
    }

    public Photo getPhotoById(long id){
        return repo.getById(id);
    }

    public void delete(long id){
        repo.deleteById(id);
    }

    public void deleteAll(){
        repo.deleteAll();
    }
}
