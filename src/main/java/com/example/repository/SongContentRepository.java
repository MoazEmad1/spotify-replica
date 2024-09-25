package com.example.repository;



import com.example.domain.SongContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SongContentRepository extends JpaRepository<SongContent, Long> {

    Optional<SongContent> findOneBySongPublicId(UUID publicId);

}
