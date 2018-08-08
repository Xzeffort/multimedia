package com.example.multimedia.service.impl.videoserviceimpl;

import com.example.multimedia.domian.User;
import com.example.multimedia.domian.videodomian.VideoComment;
import com.example.multimedia.dto.SimpleUserDTO;
import com.example.multimedia.dto.VideoDTO;
import com.example.multimedia.dto.VideosDTO;
import com.example.multimedia.domian.videodomian.Tags;
import com.example.multimedia.domian.videodomian.Video;
import com.example.multimedia.repository.TagsRepository;
import com.example.multimedia.repository.VideoRepository;
import com.example.multimedia.service.*;
import com.example.multimedia.util.ResultVoUtil;
import com.example.multimedia.util.UserUtil;
import com.example.multimedia.vo.ResultVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * @author CookiesEason
 * 2018/08/03 16:39
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class VideoServiceImpl implements VideoService {

    private final static String PREFIX_VIDEO="video/";

    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    @Qualifier(value = "VideoCommentService")
    private CommentService videoCommentService;

    @Autowired
    @Qualifier(value = "VideoLikeService")
    private LikeService videoLikeService;

    @Override
    public ResultVo uploadVideo(String title,String introduction,String tag,MultipartFile multipartFile) {
        Video video = new Video();
        if (multipartFile==null){
            video.setTitle(title);
            video.setIntroduction(introduction);
            video.setUserId(getUid());
            return saveVideo(video, tagsRepository.findByTag(tag));
        }
        if (!multipartFile.getOriginalFilename().contains(PREFIX_VIDEO)){
            ResultVo resultVo = fileService.uploadFile(multipartFile);
            if (resultVo.getCode()==0){
                return resultVo;
            }
            video.setTitle(title);
            video.setIntroduction(introduction);
            video.setUserId(getUid());
            video.setVideoUrl(resultVo.getData().toString());
            return saveVideo(video, tagsRepository.findByTag(tag));
        }else {
            return ResultVoUtil.error(0,"请注意上传的文件为视频");
        }

    }

    @Override
    public ResultVo findMyVideos(int page,String order,boolean isEnable) {
        int size = 10;
        Sort sort = new Sort(Sort.Direction.DESC,order);
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Video> videos = videoRepository.findAllByUserIdAndEnable(pageable,getUid(),isEnable);
        VideosDTO videosDTO = new VideosDTO(videos.getContent(),videos.getTotalElements(),
                (long) videos.getTotalPages());
        return ResultVoUtil.success(videosDTO);
    }

    @Override
    public ResultVo findVideos(int page,int size,String order) {
        Sort sort = new Sort(Sort.Direction.DESC,order);
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Video> videos = videoRepository.findAllByEnable(pageable,true);
        VideosDTO videosDTO = new VideosDTO(videos.getContent(),videos.getTotalElements(),
                (long) videos.getTotalPages());
        return ResultVoUtil.success(videosDTO);
    }

    @Override
    public ResultVo findAllByTag(int page, int size, String order, String tag) {
        Sort sort = new Sort(Sort.Direction.DESC,order);
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Video> videos = videoRepository.findAllByEnableAndTagsTag(pageable,true,tag);
        VideosDTO videosDTO = new VideosDTO(videos.getContent(),videos.getTotalElements(),
                (long) videos.getTotalPages());
        return ResultVoUtil.success(videosDTO);
    }

    @Override
    public ResultVo findAllByUserId(int page, int size, String order, Long userId) {
        Sort sort = new Sort(Sort.Direction.DESC,order);
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Video> videos = videoRepository.findAllByUserIdAndEnable(pageable,userId,true);
        VideosDTO videosDTO = new VideosDTO(videos.getContent(),videos.getTotalElements(),
                (long) videos.getTotalPages());
        return ResultVoUtil.success(videosDTO);
    }

    @Override
    public ResultVo deleteById(long id) {
        videoRepository.deleteByIdAndUserId(id,getUid());
        videoCommentService.deleteAllBycontentId(id);
        videoLikeService.deleteAllById(id);
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo updateVideo(long id,String title, String introduction, String tag) {
        Video video = videoRepository.findByIdAndUserId(id,getUid());
        video.setTitle(title);
        video.setIntroduction(introduction);
        Tags tags = tagsRepository.findByTag(tag);
        return saveVideo(video,tags);
    }

    @Override
    public ResultVo findById(long id) {
        VideoDTO videoDTO = new VideoDTO(
                new SimpleUserDTO(getUser(UserUtil.getUserName())),
                videoRepository.findById(id));
        return ResultVoUtil.success(videoDTO);
    }

    @Override
    public Video findById(Long id) {
        Optional<Video> video = videoRepository.findById(id);
        return video.orElse(null);
    }

    @Override
    public Video save(Video video) {
        return videoRepository.save(video);
    }

    @Override
    public void play(Long videoId) {
        Video video = findById(videoId);
        video.setPlayCount(video.getPlayCount()+1);
        save(video);
    }

    private ResultVo saveVideo(Video video, Tags tags) {
        if (tags!=null){
            video.setTags(tags);
            save(video);
            return ResultVoUtil.success();
        }else {
            return ResultVoUtil.error(0,"分类不存在,请检查你选择的分类");
        }
    }

    public User getUser(String username){
        return userService.findByUsername(username);
    }

    private Long getUid(){
        return userService.findByUsername(UserUtil.getUserName()).getId();
    }

}
