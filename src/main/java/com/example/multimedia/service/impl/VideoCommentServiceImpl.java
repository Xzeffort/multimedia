package com.example.multimedia.service.impl;

import com.example.multimedia.domian.User;
import com.example.multimedia.domian.Video;
import com.example.multimedia.domian.VideoComment;
import com.example.multimedia.domian.VideoReply;
import com.example.multimedia.domian.abstractdomian.AbstractComment;
import com.example.multimedia.dto.CommentDTO;
import com.example.multimedia.dto.PageDTO;
import com.example.multimedia.dto.ReplyDTO;
import com.example.multimedia.dto.SimpleUserDTO;
import com.example.multimedia.repository.VideoCommentRepository;
import com.example.multimedia.repository.VideoReplyRepository;
import com.example.multimedia.service.CommentService;
import com.example.multimedia.service.ReplyService;
import com.example.multimedia.service.UserService;
import com.example.multimedia.service.VideoService;
import com.example.multimedia.util.ResultVoUtil;
import com.example.multimedia.util.UserUtil;
import com.example.multimedia.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CookiesEason
 * 2018/08/05 19:37
 */
@Service(value = "VideoCommentService")
@Transactional(rollbackFor = Exception.class)
public class VideoCommentServiceImpl implements CommentService {

    @Autowired
    private VideoCommentRepository videoCommentRepository;

    @Autowired
    private VideoService videoService;

    @Autowired
    @Qualifier(value = "VideoReplyService")
    private ReplyService replyService;

    @Autowired
    private UserService userService;

    @Override
    public ResultVo createComment(Long videoId,String content) {
        VideoComment videoComment = new VideoComment();
        if (videoService.findById(videoId).getData()==null){
            return ResultVoUtil.error(0,"视频不存在,无法进行评论");
        }
        videoComment.setVideoId(videoId);
        videoComment.setContent(content);
        videoComment.setFromUid(userService.findByUsername(UserUtil.getUserName()).getId());
        videoCommentRepository.save(videoComment);
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo getComments(Long videoId,int commentPage) {
        int size=10;
        Pageable pageable = PageRequest.of(commentPage,size);
        Page<VideoComment> videoComments = videoCommentRepository.findAllByVideoId(pageable,videoId);
        List<CommentDTO> commentList = new ArrayList<>();
        videoComments.getContent().forEach(comment -> {
            User user = userService.findById(comment.getFromUid());
            List<VideoReply> replyList = replyService.findAllByCommentId(comment.getId());
            List<ReplyDTO> replyDTOList = new ArrayList<>();
            replyList.forEach(videoReply -> {
                ReplyDTO replyDTO = new ReplyDTO(videoReply,
                        new SimpleUserDTO(userService.findById(videoReply.getFromUid())),
                        new SimpleUserDTO(userService.findById(videoReply.getToUid())));
                replyDTOList.add(replyDTO);
            });
            CommentDTO commentDTO = new CommentDTO(comment,user,replyDTOList);
            commentList.add(commentDTO);
        });
        PageDTO<CommentDTO> comments = new PageDTO<>(commentList,videoComments.getTotalElements(),
                (long) videoComments.getTotalPages());
        return ResultVoUtil.success(comments);
    }

    @Override
    public AbstractComment findById(Long id) {
        return videoCommentRepository.findVideoCommentById(id);
    }

    @Override
    public void deleteById(Long id) {
        Long deleteId = videoCommentRepository.deleteByIdAndFromUid(id,getUid());
        if (deleteId!=0){
            replyService.deleteAllByCommentId(id);
        }
    }

    private Long getUid(){
        return userService.findByUsername(UserUtil.getUserName()).getId();
    }


}
