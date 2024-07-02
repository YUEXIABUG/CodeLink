package com.codelink.clbackend.mapper;

import com.codelink.clbackend.model.domain.Friends;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yuexiabug
 * @description 针对表【friends(好友表)】的数据库操作Mapper
 * @createDate 2024-03-15 10:18:49
 * @Entity generator.domain.Friends
 */
@Mapper
@Repository
public interface FriendsMapper extends BaseMapper<Friends> {

    // 查询friends表，并返回friendsUid字段
    @Select("select friendsUid from friends where uid = #{uid}")
    String getFriends(long uid);


    boolean updateFriends(long uid, String friendsUid, String createTime);

    void insertFriends(long uid, String friendsUid, String createTime);

    List<Long> getFriendsUidList(Long uid);
}




