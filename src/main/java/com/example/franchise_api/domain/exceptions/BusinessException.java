package com.example.franchise_api.domain.exceptions;

import com.example.franchise_api.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class BusinessException extends ProcessorException {

    public BusinessException(TechnicalMessage technicalMessage, Object param) {
        super(String.format(technicalMessage.getMessage(), param), technicalMessage);
    }


}
