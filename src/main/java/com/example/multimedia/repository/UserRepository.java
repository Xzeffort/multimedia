package com.example.multimedia.repository;

import com.example.multimedia.domian.User;
import com.example.multimedia.domian.UserInfo;
import com.example.multimedia.domian.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author CookiesEason
 * 2018/07/23 15:19
 */
public interface UserRepository extends JpaRepository<User,Long> {

    User findUserById(Long id);

    User findByUsername(String username);

    User findByUserInfoNickname(String nickname);

    Page<User> findAllByRoleListIn(Pageable pageable, List<UserRole> userRoles);

    List<User> findByUsernameOrUserInfoNickname(String username,String nickname);

    List<User> findUsersByIdIn(List<Long> ids);

    @Query(value = "SELECT SUM(like_count) FROM (select SUM(like_count) as like_count from video where user_id = :id\n" +
            "UNION\n" +
            "select SUM(like_count)as like_count from article where user_id = :id)t",nativeQuery = true)
    Long getUserWorkHot(@Param("id") Long userId);

    @Query(value = "SELECT * FROM (SELECT t.user_id,SUM(num) as hot from (SELECT user_id ,SUM(like_count) as num from `user` INNER JOIN article ON article.user_id = `user`.id\n" +
            "GROUP BY `user`.id\n" +
            "UNION ALL\n" +
            "SELECT user_id,SUM(like_count) as num from `user` INNER JOIN video ON video.user_id = `user`.id\n" +
            "GROUP BY `user`.id) t\n" +
            "GROUP BY t.user_id\n" +
            "ORDER BY SUM(t.num)\n" +
            ")ids INNER JOIN `user` ON ids.user_id = `user`.id order by hot DESC ",
            countQuery = "select count(*) from user"
            ,nativeQuery = true)
    Page<User> getHotUsers(Pageable pageable);


    @Query(value = "SELECT count(*) FROM `user` WHERE TO_DAYS(NOW()) - TO_DAYS(date) <=:day",nativeQuery = true)
    int countNewRegister(@Param("day") int day);

}
