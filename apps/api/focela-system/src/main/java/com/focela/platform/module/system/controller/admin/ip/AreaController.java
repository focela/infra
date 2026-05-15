package com.focela.platform.module.system.controller.admin.ip;

import cn.hutool.core.lang.Assert;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.ip.core.Area;
import com.focela.platform.framework.ip.core.utils.AreaUtils;
import com.focela.platform.framework.ip.core.utils.IPUtils;
import com.focela.platform.module.system.controller.admin.ip.dto.AreaNodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - Area")
@RestController
@RequestMapping("/system/area")
@Validated
public class AreaController {

    @GetMapping("/tree")
    @Operation(summary = "Get area tree")
    public CommonResult<List<AreaNodeResponse>> getAreaTree() {
        Area area = AreaUtils.getArea(Area.ID_CHINA);
        Assert.notNull(area, "Cannot get China");
        return success(BeanUtils.toBean(area.getChildren(), AreaNodeResponse.class));
    }

    @GetMapping("/get-by-ip")
    @Operation(summary = "get IP corresponding area name")
    @Parameter(name = "ip", description = "IP", required = true)
    public CommonResult<String> getAreaByIp(@RequestParam("ip") String ip) {
        // get city
        Area area = IPUtils.getArea(ip);
        if (area == null) {
            return success("Unknown");
        }
        // format and return
        return success(AreaUtils.format(area.getId()));
    }

}
