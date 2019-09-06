package com.xq.exportdata.service;

import com.xq.exportdata.entity.User;
import com.xq.exportdata.mapper.primary.UserMapper1;
import com.xq.exportdata.mapper.secondary.UserMapper2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService{
    @Autowired
    private UserMapper1 userMapper1;

    @Autowired
    private UserMapper2 userMapper2;

    public List<User> findAllUserMapper1(){
        return userMapper1.findAll();
    }
    public List<User> findAllUserMapper2(){
        return userMapper2.findAll();
    }

    public void addOneUserMapper1(User user){
        userMapper1.addOne(user);
    }

    public void addOneUserMapper2(User user){
        userMapper2.addOne(user);
    }

    public  void updateOneUserMapper1(User user){
        userMapper1.updateOne(user);
    }

    public  void updateOneUserMapper2(User user){
        userMapper2.updateOne(user);
    }

    public   void delOneUserMapper1(Integer id){
        userMapper1.delOne(id);
    }
    public   void delOneUserMapper2(Integer id){
        userMapper2.delOne(id);
    }
}
