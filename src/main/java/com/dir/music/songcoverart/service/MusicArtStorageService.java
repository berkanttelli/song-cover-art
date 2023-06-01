package com.dir.music.songcoverart.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MusicArtStorageService {
    Resource loadMusicArtFile(Long id);

    Boolean saveMusicArtFileAndPath(MultipartFile file, Long id) throws IOException;

    Boolean deleteMusicPathAndFile(Long id);
}
