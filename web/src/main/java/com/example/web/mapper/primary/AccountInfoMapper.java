package com.example.web.mapper.primary;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Mapper
public interface AccountInfoMapper {

    Map selectById(Map map);

    void updateById(Map map);

}
