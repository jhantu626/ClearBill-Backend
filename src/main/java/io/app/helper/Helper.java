package io.app.helper;

import io.app.dto.BusinessDto;
import io.app.model.Business;
import org.springframework.stereotype.Service;

@Service
public class Helper {
    public BusinessDto businessToDto(Business business) {
        return BusinessDto.builder()
                .id(business.getId())
                .name(business.getName())
                .gstNo(business.getGstNo())
                .address(business.getAddress())
                .stateCode(business.getStateCode())
                .logo(business.getLogo())
                .createdAt(business.getCreatedAt())
                .updatedAt(business.getUpdatedAt())
                .build();
    }
}
