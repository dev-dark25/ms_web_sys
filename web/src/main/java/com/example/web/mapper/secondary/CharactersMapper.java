package com.example.web.mapper.secondary;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface CharactersMapper {

    List selectAll();

    int selectCount();

    List selectByPagination(Map map);

    List selectById(int id);
}
