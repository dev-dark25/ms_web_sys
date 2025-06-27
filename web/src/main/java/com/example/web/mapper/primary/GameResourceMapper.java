package com.example.web.mapper.primary;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface GameResourceMapper {

    List selectList(Map map);

}
