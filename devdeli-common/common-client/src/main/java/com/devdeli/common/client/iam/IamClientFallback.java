package com.devdeli.common.client.iam;

import com.devdeli.common.UserAuthority;
import com.devdeli.common.dto.response.ApiResponse;
import com.devdeli.common.enums.ServiceUnavailableError;
import com.devdeli.common.exception.ForwardInnerAlertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IamClientFallback implements FallbackFactory<IamClient> {
    @Override
    public IamClient create(Throwable cause) {
        return new FallbackWithFactory(cause);
    }

    @Slf4j
    static class FallbackWithFactory implements IamClient {
        private final Throwable cause;

        FallbackWithFactory(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public ApiResponse<UserAuthority> getUserAuthority(String userId) {
            if (cause instanceof ForwardInnerAlertException) {
                return ApiResponse.<UserAuthority>builder()
                        .code(ServiceUnavailableError.SERVICE_UNAVAILABLE_ERROR.getCode())
                        .message(ServiceUnavailableError.SERVICE_UNAVAILABLE_ERROR.getCode().toString())
                        .build();
            }
            return ApiResponse.<UserAuthority>builder()
                    .code(ServiceUnavailableError.IAM_SERVICE_UNAVAILABLE_ERROR.getCode())
                    .message(ServiceUnavailableError.IAM_SERVICE_UNAVAILABLE_ERROR.getMessage())
                    .build();
        }
    }
}
