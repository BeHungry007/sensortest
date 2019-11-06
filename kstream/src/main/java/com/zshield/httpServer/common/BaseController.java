package com.zshield.httpServer.common;

import javax.ws.rs.core.Response;

public class BaseController {

    //返回成功
    public static Response success() {
        return Response.ok().build();
    }

    //返回成功
    public static Response defaultSuccess() {
        return Response.ok("{\"success\":true}").build();
    }

    //返回成功消息
    public static Response success(String message) {
        return Response.ok(message).build();
    }

    //返回失败400，请求错误
    public static Response error() {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    //返回失败消息500，服务器异常
    public Response error(String message) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).type("application/json").build();
    }

}
