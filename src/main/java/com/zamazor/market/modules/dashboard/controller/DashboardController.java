package com.zamazor.market.modules.dashboard.controller;

import com.zamazor.market.modules.dashboard.models.dto.DashboardOverviewDto;
import com.zamazor.market.modules.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
	private final DashboardService dashboardService;

	@GetMapping("/overview")
	public ResponseEntity<DashboardOverviewDto> getOverview() {
		return ResponseEntity.ok(dashboardService.getDashboardOverview());
	}
}