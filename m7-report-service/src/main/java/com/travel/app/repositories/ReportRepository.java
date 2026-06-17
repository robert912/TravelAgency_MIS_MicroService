package com.travel.app.repositories;

import com.travel.app.dtos.PackageRankingReportDTO;
import com.travel.app.dtos.SalesReportDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository {

    List<SalesReportDTO> findSalesReportByPeriod(LocalDateTime startDate, LocalDateTime endDate);

    List<PackageRankingReportDTO> findPackageRankingReportByPeriod(LocalDateTime startDate, LocalDateTime endDate);
}
