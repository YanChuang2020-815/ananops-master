package com.ananops.provider.model.device;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.NodeSelector;
import lombok.Data;

/**
 * Created by rongshuai on 2020/7/2 17:08
 */
@Data
@JsonDeserialize(
        using = JsonDeserializer.None.class
)
public class DeviceSpec implements KubernetesResource {
    private static final long serialVersionUID = -9163473238200924067L;

    private DeviceModelRef deviceModelRef;

    private NodeSelector nodeSelector;
}
