package com.dir.music.songcoverart.service;

import com.dir.music.songcoverart.repository.MusicArtModel;
import com.dir.music.songcoverart.repository.MusicArtRepository;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class MusicArtStorageServiceImpl implements MusicArtStorageService {

    private final MusicArtRepository musicArtRepository;

    private final String basePath;
    private final static String jpegExtension = ".jpeg";
    private final static String uglySlash = "/";

    private final Path musicArtStorageLocation;

    public MusicArtStorageServiceImpl(MusicArtRepository musicArtRepository, @Value("${app.music.storage.path}") String path) {
        this.musicArtStorageLocation = Paths.get(path).toAbsolutePath().normalize();
        this.musicArtRepository = musicArtRepository;
        this.basePath = path;
    }

    @Override
    public Resource loadMusicArtFile(Long id) {
        try {
            Optional<MusicArtModel> songArtPath =  musicArtRepository.findById(id);
            if(songArtPath.isPresent()) {
                Path file = this.musicArtStorageLocation.resolve(songArtPath.get().getFileName()).normalize();
                Resource resource = new UrlResource(file.toUri());
                if(resource.exists()) {
                    return  resource;
                } else {
                    throw new FileNotFoundException("File not found:" + songArtPath.get().getFileName());
                }

            } else {
                throw new FileNotFoundException("Sound art does not exist.");
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean saveMusicArtFileAndPath(MultipartFile file, Long id) throws IOException {
        final MusicArtModel musicArtModel = new MusicArtModel();
        String fileName = specialCharacterChecker(Objects.requireNonNull(file.getOriginalFilename()));
        String relativePath = fileName + jpegExtension;
        String musicArtPath = basePath + uglySlash + fileName + jpegExtension;

        try {
            File createFilePath = new File(musicArtPath);
            if (!createFilePath.exists()) {
                if (new File(musicArtPath).mkdirs()) {
                    file.transferTo(createFilePath);
                }
            } else {
                return Boolean.FALSE;
            }
            musicArtModel.setId(id);
            musicArtModel.setFileName(relativePath);
            musicArtRepository.saveAndFlush(musicArtModel);
            return Boolean.TRUE;

        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public Boolean deleteMusicPathAndFile(Long id) {

        try {
            Optional<MusicArtModel> songModel = musicArtRepository.findById(id);
            if(songModel.isPresent()) {
                String musicPath = basePath + uglySlash + songModel.get().getFileName();
                FileSystemUtils.deleteRecursively(new File(musicPath));
                musicArtRepository.deleteById(id);
                return Boolean.TRUE;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return Boolean.FALSE;
    }

    private String specialCharacterChecker(String value) {
        return value.replaceAll("[^a-zA-Z0-9]", "").replaceAll(" ", "").toLowerCase();
    }
}
