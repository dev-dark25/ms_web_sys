package com.example.web.mapper.secondary;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface AccountsMapper {

    List selectAll();

    int selectCountByLoggedin();

    List selectById(int id);
}
