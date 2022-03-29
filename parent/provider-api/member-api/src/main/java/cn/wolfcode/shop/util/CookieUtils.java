package cn.wolfcode.shop.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

    public final static String USER_TOKEN_NAME = "userToken";

    private final static String DEFAULT_COOKIE_DOMAIN = "localhost";
    private final static String DEFAULT_COOKIE_PATH = "/";
    private final static int DEFAULT_COOKIE_AGE = 1800;

    public static void addCookie(HttpServletResponse resp, String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setDomain(DEFAULT_COOKIE_DOMAIN);
        cookie.setPath(DEFAULT_COOKIE_PATH);
        cookie.setMaxAge(DEFAULT_COOKIE_AGE);
        resp.addCookie(cookie);
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0)
            return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
