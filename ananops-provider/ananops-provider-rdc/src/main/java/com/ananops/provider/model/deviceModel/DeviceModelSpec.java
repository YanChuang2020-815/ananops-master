package com.ananops.provider.model.deviceModel;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import lombok.Data;

import java.util.List;

/**
 * Created by rongshuai on 2020/7/6 11:53
 */
@Data
@JsonDeserialize(
        using = JsonDeserializer.None.class
)
public class DeviceModelSpec implements KubernetesResource {
    private static final long serialVersionUID = -8538862329567647385L;

    private List<DeviceModelProperty> properties;
}
