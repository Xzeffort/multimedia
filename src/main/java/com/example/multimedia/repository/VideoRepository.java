package com.example.multimedia.repository;

import com.example.multimedia.domian.maindomian.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author CookiesEason
 * 2018/08/03 16:24
 */
public interface VideoRepository extends JpaRepository<Video,Long> {

    Video findById(long id);

    void deleteByIdAndUserId(long id,long userId);

    Video findByIdAndUserId(long id,long userId);

    Page<Video> findAllByUserIdAndEnable(Pageable pageable, Long userId,boolean enable);

    Page<Video> findAllByEnable(Pageable pageable, boolean enable);

    Page<Video> findAllByEnableAndTagsTag(Pageable pageable,boolean enable,String tag);

}