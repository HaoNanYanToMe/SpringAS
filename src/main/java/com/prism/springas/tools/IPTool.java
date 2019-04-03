package com.prism.springas.tools;
import com.prism.springas.controller.oAuth2.LoginOAuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

@Component
public class IPTool {

    private final Logger logger = LoggerFactory.getLogger(LoginOAuthController.class);

    public String GetRealIP(HttpServletRequest request) {
        if(request==null)
            try {
                return  InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                logger.error("---IPTool ERROR:(获取IP时出现问题)-->"+e.getMessage(),e);
                return "127.1.1.1";
            }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
