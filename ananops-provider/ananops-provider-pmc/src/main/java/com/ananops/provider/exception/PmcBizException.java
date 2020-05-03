package com.ananops.provider.exception;

import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 项目管理异常处理
 * Created By ChengHao On 2019/12/5
 */
@Slf4j
public class PmcBizException extends BusinessException {

    private static final long serialVersionUID = -6173294167804561500L;

    /**
     * Instantiates a new Pmc rpc exception.
     */
    public PmcBizException() {
    }

    /**
     * Instantiates a new Pmc rpc exception.
     * @param code
     * @param msgFormat
     * @param args
     */
    public PmcBizException(int code, String msgFormat, Object... args) {
        super(code, msgFormat, args);
        log.info("<== PmcRpcException, code:{}, message:{}", this.code, super.getMessage());
    }

    /**
     * Instantiates a new Pmc rpc exception.
     * @param code
     * @param message
     */
    public PmcBizException(int code, String message) {
        super(code, message);
        log.info("<== PmcRpcException, code:{}, message:{}", this.code, super.getMessage());
    }

    /**
     * Instantiates a new Pmc rpc exception
     * @param codeEnum
     */
    public PmcBizException(ErrorCodeEnum codeEnum) {
        super(codeEnum, codeEnum.msg());
        log.info("<== PmcRpcException, code:{}, message:{}", this.code, super.getMessage());
    }

    /**
     * Instantiates a new Pmc rpc exception
     * @param codeEnum
     * @param args
     */
    public PmcBizException(ErrorCodeEnum codeEnum, Object... args) {
        super(codeEnum, args);
        log.info("<== PmcRpcException, code:{}, message:{}", this.code, super.getMessage());
    }
}
