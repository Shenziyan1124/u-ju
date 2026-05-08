package com.uju.platform.certification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uju.platform.common.BusinessException;
import com.uju.platform.certification.entity.OrganizerCertification;
import com.uju.platform.certification.mapper.CertificationMapper;
import com.uju.platform.user.entity.User;
import com.uju.platform.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationMapper certificationMapper;
    private final UserMapper userMapper;

    @Transactional
    public OrganizerCertification submitCertification(Long userId, OrganizerCertification certification) {
        Long count = certificationMapper.selectCount(
                new LambdaQueryWrapper<OrganizerCertification>()
                        .eq(OrganizerCertification::getUserId, userId)
                        .ne(OrganizerCertification::getStatus, 2)
        );
        if (count > 0) {
            throw new BusinessException(400, "您已有待审核或已通过的认证申请");
        }

        certification.setUserId(userId);
        certification.setStatus(0);
        certificationMapper.insert(certification);
        return certification;
    }

    public OrganizerCertification getMyCertification(Long userId) {
        return certificationMapper.selectOne(
                new LambdaQueryWrapper<OrganizerCertification>()
                        .eq(OrganizerCertification::getUserId, userId)
                        .orderByDesc(OrganizerCertification::getCreatedAt)
                        .last("LIMIT 1")
        );
    }

    @Transactional
    public void reviewCertification(Long certificationId, Integer status, String remark, Long reviewerId) {
        OrganizerCertification cert = certificationMapper.selectById(certificationId);
        if (cert == null) {
            throw new BusinessException(404, "认证申请不存在");
        }

        cert.setStatus(status);
        cert.setReviewRemark(remark);
        cert.setReviewerId(reviewerId);
        cert.setReviewedAt(java.time.LocalDateTime.now());
        certificationMapper.updateById(cert);

        if (status == 1) {
            User user = userMapper.selectById(cert.getUserId());
            if (user != null) {
                user.setRole("ORGANIZER");
                userMapper.updateById(user);
            }
        }
    }
}
