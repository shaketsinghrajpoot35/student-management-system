package com.smartstudent.main.controller;

import com.smartstudent.main.dto.response.ApiResponseDTO;
import com.smartstudent.main.dto.response.DashboardAnalyticsDTO;
import com.smartstudent.main.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponseDTO<DashboardAnalyticsDTO>> getAnalytics() {
        log.info("Fetching dashboard analytics");
        return ResponseEntity.ok(ApiResponseDTO.success(
                "Dashboard analytics retrieved successfully",
                dashboardService.getAnalytics()
        ));
    }
}
