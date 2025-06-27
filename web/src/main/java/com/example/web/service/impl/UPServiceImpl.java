package com.example.web.service.impl;

import com.example.web.component.ExtractResourceUtil;
import com.example.web.mapper.primary.ResourceRecordMapper;
import com.example.web.service.UPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

//@Service
@Service("upService")
public class UPServiceImpl implements UPService {

    private Logger logger = LoggerFactory.getLogger(UPServiceImpl.class);

    @Resource
    private ExtractResourceUtil eru;

    @Resource
    private ResourceRecordMapper resourceRecordMapper;

    public List getResource(Map map) {
        List<Map> list = eru.getCompleteResources(map);
        return list;
    }

    public List getPermanentResource() {
        return null;
    }

    public List getTimeLimitResource() {
        return null;
    }

    public List getResourceRecord(Map map) {
        return resourceRecordMapper.selectByAccount(map);
    }
}
