package com.uju.platform.certification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uju.platform.certification.entity.OrganizerCertification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CertificationMapper extends BaseMapper<OrganizerCertification> {
}
