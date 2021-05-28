package com.shieldfc.webPhotoArchive.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shieldfc.webPhotoArchive.model.Photo;
import com.shieldfc.webPhotoArchive.model.PhotoSource;
import com.shieldfc.webPhotoArchive.service.PhotoConsumerService;
import com.shieldfc.webPhotoArchive.service.PhotoService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.ServletContextResource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PhotoController {
    @Autowired
    private PhotoService service;

    @Autowired
    private PhotoConsumerService consumerService;

    @Autowired
    private ServletContext servletContext;


    @RequestMapping("/init")
    public String init(Model model){
        List<PhotoSource> list = null;
        try {
            list = consumerService.loadPhotoData();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        List<Photo> fullList = new ArrayList<Photo>();
        for (PhotoSource source : list){
            try {
                fullList.add(consumerService.downloadPhoto(source));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        service.saveList(fullList);
        model.addAttribute("numPhotos", fullList.size());
        return "init";
    }

    @RequestMapping("/list-all")
    public String listAll(Model model){
        model.addAttribute("getAllPhotos", service.getAll());
        return "index";
    }

    @RequestMapping("/list-by-album-id/{id}")
    public String listByAlbum(@PathVariable(name="id") long id, Model model){
        model.addAttribute("albumPhotos", service.getPhotosByAlbumId(id));
        model.addAttribute("albumNumber",id);
        return "album";
    }

    @RequestMapping(value = "/get-photo-by-id/{id}", method = RequestMethod.GET)
    public void photo(@PathVariable(name="id") long id, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        Resource resource = new FileSystemResource(service.getPhotoById(id).getLocalPath());
        InputStream in = resource.getInputStream();
        IOUtils.copy(in, response.getOutputStream());
    }




}
