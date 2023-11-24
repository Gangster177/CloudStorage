package ru.diploma.cloudstor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.diploma.cloudstor.entity.CloudFile;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<CloudFile, Long> {

    void deleteByUserIdAndFilename(Long userId, String filename);

    CloudFile findByUserIdAndFilename(Long userId, String filename);

    @Query(value = "select * from netology_diploma.files f where f.user_id = ?1 order by f.id desc limit ?2", nativeQuery = true)
    List<CloudFile> findAllByUserIdWithLimit(Long userId, int limit);

    @Modifying(clearAutomatically = true)
    @Query("update CloudFile  f set f.filename = :newName where f.filename = :fileName and f.userId = :userId")
    void updateFileNameByUserId(@Param("userId") Long userId, @Param("fileName") String oldFileName, @Param("newName") String newFileName);
}