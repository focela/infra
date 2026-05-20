package com.focela.platform.system.controller.app.area;

import cn.hutool.core.lang.Assert;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.ip.core.Area;
import com.focela.platform.ip.core.utils.AreaUtils;
import com.focela.platform.system.controller.app.area.response.AppAreaNodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "User App - Area")
@RestController
@RequestMapping("/system/area")
@Validated
public class AppAreaController {

    @GetMapping("/tree")
    @Operation(summary = "Get area tree")
    @PermitAll
    public CommonResult<List<AppAreaNodeResponse>> getAreaTree() {
        Area area = AreaUtils.getArea(Area.ID_CHINA);
        Assert.notNull(area, "Cannot find China");
        return success(BeanUtils.toBean(area.getChildren(), AppAreaNodeResponse.class));
    }

}
