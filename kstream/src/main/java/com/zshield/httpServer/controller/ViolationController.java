package com.zshield.httpServer.controller;

import com.zshield.httpServer.ViolationIssuedThread;
import com.zshield.httpServer.common.BaseController;
import com.zshield.httpServer.domain.Violation;
import com.zshield.httpServer.domain.ViolationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/violation")
public class ViolationController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ViolationController.class);
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response violationAdd(String body) {
        Violation violation = ViolationFactory.parsingData(body);
        if (violation != null) {
            if (violation.isIssued()) {
                ViolationIssuedThread.prepareProcessedViolations.put(violation.getRule_id(), violation);
            }
            logger.info("[/violation post request success,ruleid:{}, dependence registered request success]", violation.getRule_id());
        }
        return  defaultSuccess();
    }
}
