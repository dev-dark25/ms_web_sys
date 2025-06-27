package com.example.web.service;

import java.util.List;
import java.util.Map;

public interface UPService {

    List getResource(Map map);

    List getPermanentResource();
    List getTimeLimitResource();

    List getResourceRecord(Map map);

}
