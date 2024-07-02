package com.codelink.clbackend.service;
import java.util.Date;

import com.codelink.clbackend.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class UserServiceTest {
    @Resource
    private UserService userService;


    @Test
    public void testAddUser() {
        User user = new User();
        user.setUserAccount("yupidog");
        user.setUsername("yupi");
        user.setUserPassword("yupi123123");
        user.setUserAvatar("");
        user.setUserGender(0);
        user.setUserAge(20);
        user.setUserArea("北京");
        user.setUserPhone("123123");
        user.setUserEmail("456456");
        user.setUserTags("131231");
        user.setGithub("12312313");
        user.setPersonalWeb("123131");
        user.setCsdn("123133");
        user.setTeamIds("");
        user.setFriendsIds("");
        user.setIsDelete(0);
        boolean result = userService.save(user);
        System.out.println(user.getUid());
        Assertions.assertTrue(result);
    }


    @Test
    public void userRegister() {
        String userAccount = "yupiDog123";
        String userPassword = "yupi12345";
        String checkPassword = "yupi12345";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        System.out.println(result);

        String userAccount2 = "yupiDog";
        long result1 = userService.userRegister(userAccount, userPassword, checkPassword);
        System.out.println(result1);

        String userAccount3 = "yupiDog!!!!";
        long result2 = userService.userRegister(userAccount, userPassword, checkPassword);
        System.out.println(result2);
    }
}