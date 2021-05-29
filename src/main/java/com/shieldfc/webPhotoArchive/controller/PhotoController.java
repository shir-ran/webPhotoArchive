package com.shieldfc.webPhotoArchive.controller;

import com.shieldfc.webPhotoArchive.model.Photo;
import com.shieldfc.webPhotoArchive.service.PhotoConsumerService;
import com.shieldfc.webPhotoArchive.service.PhotoService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

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
        StringBuffer errors = new StringBuffer();
        List<Photo> fullList = consumerService.initializeArchive(errors);
        service.saveList(fullList);
        model.addAttribute("numPhotos", fullList.size());
        model.addAttribute("totalFileSize", fullList.stream().mapToLong(Photo::getFileSize).sum());
        model.addAttribute("error",errors.toString());
        model.addAttribute("success", errors.toString().isBlank());

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
    public void photo(@PathVariable(name="id") long id, HttpServletResponse response) throws IOException{
        response.setContentType("image/jpeg");
        try {
            Photo photo = service.getPhotoById(id);
            Resource resource = new FileSystemResource(photo.getLocalPath());
            InputStream in = resource.getInputStream();
            IOUtils.copy(in, response.getOutputStream());
        } catch (EntityNotFoundException | FileNotFoundException ex){
            URL url = new URL("https://www.nationalpetregister.org/assets/img/no-photo.jpg");
            InputStream in = new BufferedInputStream(url.openStream());
            IOUtils.copy(in, response.getOutputStream());
        }
    }

    @RequestMapping("/clean")
    public String clean(Model model){
        try {
            consumerService.cleanDirectory();
            service.deleteAll();
            model.addAttribute("numPhotos", 0);
            model.addAttribute("totalFileSize", 0);
            model.addAttribute("success", true);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error","Attempt to clean archive failed.");
            model.addAttribute("success", false);
        }

        return "init";
    }




}
