package com.shieldfc.webPhotoArchive.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shieldfc.webPhotoArchive.model.Photo;
import com.shieldfc.webPhotoArchive.model.PhotoSource;
import com.shieldfc.webPhotoArchive.service.PhotoConsumerService;
import com.shieldfc.webPhotoArchive.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PhotoController {
    @Autowired
    private PhotoService service;

    @Autowired
    private PhotoConsumerService consumerService;


    @RequestMapping("/init")
    public String init(){
        List<PhotoSource> list = null;
        try {
            list = consumerService.loadPhotoData();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        List<Photo> fullList = new ArrayList();
        for (PhotoSource source : list){
            try {
                fullList.add(consumerService.downloadPhoto(source));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        service.saveList(fullList);
        return list.toString();
    }


}
