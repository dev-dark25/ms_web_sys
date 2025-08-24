package com.example.web.mapper.secondary;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface AccountsMapper {

    List selectAll(Map req);

    int selectCountByLoggedin();

    List selectById(int id);

    void update(Map req);
}
