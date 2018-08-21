package com.example.multimedia.repository.search;

import com.example.multimedia.domian.maindomian.search.VideoCommentSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author CookiesEason
 * 2018/08/12 17:21
 */
public interface VideoCommentSearchRepository extends ElasticsearchRepository<VideoCommentSearch,Long> {
    Page<VideoCommentSearch> findAllByContent(String content, Pageable pageable);

    void deleteAllByVideoid(Long videoId);

}