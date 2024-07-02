package com.codelink.clbackend.mapper;

import com.codelink.clbackend.model.domain.FriendsApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codelink.clbackend.model.domain.User;

import java.util.List;

/**
* @author yuexiabug
* @description 针对表【friends_application(好友申请表)】的数据库操作Mapper
* @createDate 2024-03-16 10:17:39
* @Entity generator.domain.FriendsApplication
*/
public interface FriendsApplicationMapper extends BaseMapper<FriendsApplication> {


    FriendsApplication getFriendsApplication(long aid);

    int getFriendsApplicationListFlag(long uid);

    List<FriendsApplication> getFriendsApplicationList(long uid);
}




