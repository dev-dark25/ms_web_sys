package com.example.web.mapper.primary;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface ResourceRecordMapper {

    List selectByAccount(Map map);

    //    void batchInsert(List list);
    void batchInsert(int accountId, List list);

}
