package com.prism.springas.utils.oAuth2;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class oAuthToken  {

    public HttpServletRequest request;
    public HttpServletResponse response;

    public oAuthToken(HttpServletRequest request2, HttpServletResponse response2) {
        this.request=   (request2);
        this.response=   (response2);
    }

    private Map<String, Cookie> ReadCookieMap() {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

    public  String GetCookie(String name) {
        Map<String, Cookie> cookieMap = ReadCookieMap();
        if (cookieMap.containsKey(name)) {
            Cookie cookie = (Cookie) cookieMap.get(name);
            return cookie.getValue()+"";
        } else {
            return "";
        }
    }

    public   void SetCookie(String e2Token, String cookieValue) {
        _SetCookie(e2Token, cookieValue, -1);

    }

    private   void _SetCookie(String e2Token, String string, Integer MaxAge) {
        if(response==null)return ;
        Cookie cookie = new Cookie(e2Token, string);
        cookie.setPath("/");
        if (MaxAge != null)
            cookie.setMaxAge(MaxAge);
        if(response!=null)
            response.addCookie(cookie);
    }
}
