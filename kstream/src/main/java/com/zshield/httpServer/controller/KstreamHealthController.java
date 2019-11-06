package com.zshield.httpServer.controller;

import com.zshield.httpServer.common.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;

//@Path("/healthmonitor")
public class KstreamHealthController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(KstreamHealthController.class);
    
    public static long streamChangingTimeMillis = -1l;
    public static long violationStreamChangingTimeMillis = -1l;
}
