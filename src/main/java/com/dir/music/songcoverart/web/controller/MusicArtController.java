package com.dir.music.songcoverart.web.controller;


import com.dir.music.songcoverart.service.MusicArtStorageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/musicArt")
public class MusicArtController {

    private final MusicArtStorageService musicArtStorageService;

    public MusicArtController(MusicArtStorageService musicArtStorageService) {
        this.musicArtStorageService = musicArtStorageService;
    }


    @GetMapping("/gimme/{id}")
    public ResponseEntity<Resource> streamMusicArt(@PathVariable Long id) {
        Resource musicArt = musicArtStorageService.loadMusicArtFile(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(musicArt);
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<String> uploadMusicArt(@PathVariable Long id, @RequestParam("file")MultipartFile file) throws IOException {
        if(musicArtStorageService.saveMusicArtFileAndPath(file, id)) {
            return ResponseEntity.ok().body("Music art is uploaded.");
        } else {
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("Everyone Love Teapot");
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMusicArt(@PathVariable Long id) {
        if (musicArtStorageService.deleteMusicPathAndFile(id)) {
            return ResponseEntity.ok().body("Delete Music art file is Successful");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Music art file not found.");
        }
    }


}
