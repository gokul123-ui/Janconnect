package com.janconnect.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA Routing Fallback Controller
 *
 * This controller ensures that all non-API, non-static-resource routes
 * are forwarded to index.html so that React Router can handle them
 * on the client side.
 *
 * Example:
 *   Browser navigates to http://localhost:8081/dashboard
 *   → Spring Boot serves index.html
 *   → React Router renders the <DashboardPage>
 */
@Controller
public class SpaRoutingController {

    /**
     * Catch-all for SPA routes:
     * - Does NOT match paths starting with /api/
     * - Does NOT match paths starting with /actuator/
     * - Does NOT match paths that look like static files (contain a ".")
     */
    @RequestMapping(value = {
            "/login",
            "/register",
            "/dashboard",
            "/submit",
            "/review",
            "/success",
            "/success/**",
            "/track",
            "/track/**",
            "/history",
            "/help",
            "/accessibility",
            "/leaderboard",
            "/admin",
            "/admin/**"
    })
    public String forward(HttpServletRequest request) {
        return "forward:/index.html";
    }
}
