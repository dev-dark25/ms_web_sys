package com.example.web.mapper.secondary;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface SkillsMapper {

    List selectAll();

    int selectCount(Map map);

    List selectByPagination(Map map);

    List selectById(int id);
}
