package com.uju.platform.certification.controller;

import com.uju.platform.common.Result;
import com.uju.platform.certification.entity.OrganizerCertification;
import com.uju.platform.certification.service.CertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证模块")
@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @Operation(summary = "提交企业认证")
    @PostMapping
    public Result<OrganizerCertification> submit(Authentication authentication,
                                                  @RequestBody OrganizerCertification certification) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(certificationService.submitCertification(userId, certification));
    }

    @Operation(summary = "查看我的认证")
    @GetMapping("/me")
    public Result<OrganizerCertification> getMyCertification(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(certificationService.getMyCertification(userId));
    }
}
