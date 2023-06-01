package com.dir.music.songcoverart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MusicArtRepository extends JpaRepository<MusicArtModel, Long> {

    @Override
    Optional<MusicArtModel> findById(Long aLong);
}
