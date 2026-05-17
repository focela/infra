package com.focela.platform.system.converter.auth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.api.sms.dto.code.SmsCodeSendRpcRequest;
import com.focela.platform.system.api.sms.dto.code.SmsCodeUseRpcRequest;
import com.focela.platform.system.api.social.dto.SocialUserBindRpcRequest;
import com.focela.platform.system.controller.admin.auth.dto.AuthPermissionInfoResponse;
import com.focela.platform.system.controller.admin.auth.dto.AuthSmsLoginRequest;
import com.focela.platform.system.controller.admin.auth.dto.AuthSmsSendRequest;
import com.focela.platform.system.controller.admin.auth.dto.AuthSocialLoginRequest;
import com.focela.platform.system.entity.permission.MenuEntity;
import com.focela.platform.system.entity.permission.RoleEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.focela.platform.system.enums.permission.MenuTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.focela.platform.common.utils.collection.CollectionUtils.convertSet;
import static com.focela.platform.common.utils.collection.CollectionUtils.filterList;
import static com.focela.platform.system.entity.permission.MenuEntity.ID_ROOT;

@Mapper
public interface AuthConverter {

    AuthConverter INSTANCE = Mappers.getMapper(AuthConverter.class);

    default AuthPermissionInfoResponse convert(UserEntity user, List<RoleEntity> roleList, List<MenuEntity> menuList) {
        return AuthPermissionInfoResponse.builder()
                .user(BeanUtils.toBean(user, AuthPermissionInfoResponse.UserInfo.class))
                .roles(convertSet(roleList, RoleEntity::getCode))
                // permission identifier information
                .permissions(convertSet(menuList, MenuEntity::getPermission))
                // menu tree
                .menus(buildMenuTree(menuList))
                .build();
    }

    /**
     * Build a menu tree from a menu list
     *
     * @param menuList menu list
     * @return menu tree
     */
    default List<AuthPermissionInfoResponse.MenuInfo> buildMenuTree(List<MenuEntity> menuList) {
        if (CollUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }
        // remove buttons
        menuList.removeIf(menu -> menu.getType().equals(MenuTypeEnum.BUTTON.getType()));
        // sort to keep menus in order
        menuList.sort(Comparator.comparing(MenuEntity::getSort));

        // build the menu tree
        // LinkedHashMap is used to preserve order. Stream API could also work but would be uglier.
        Map<Long, AuthPermissionInfoResponse.MenuInfo> treeNodeMap = new LinkedHashMap<>();
        menuList.forEach(menu -> treeNodeMap.put(menu.getId(),
                BeanUtils.toBean(menu, AuthPermissionInfoResponse.MenuInfo.class)));
        // process parent-child relations
        treeNodeMap.values().stream().filter(node -> ObjUtil.notEqual(node.getParentId(), ID_ROOT)).forEach(childNode -> {
            // get the parent node
            AuthPermissionInfoResponse.MenuInfo parentNode = treeNodeMap.get(childNode.getParentId());
            if (parentNode == null) {
                LoggerFactory.getLogger(getClass()).error("[buildRouterTree][resource({}) cannot find parent resource({})]",
                        childNode.getId(), childNode.getParentId());
                return;
            }
            // add itself to the parent node
            if (parentNode.getChildren() == null) {
                parentNode.setChildren(new ArrayList<>());
            }
            parentNode.getChildren().add(childNode);
        });
        // get all root nodes
        return filterList(treeNodeMap.values(), node -> ID_ROOT.equals(node.getParentId()));
    }

    SocialUserBindRpcRequest convert(Long userId, Integer userType, AuthSocialLoginRequest request);

    SmsCodeSendRpcRequest convert(AuthSmsSendRequest request);

    SmsCodeUseRpcRequest convert(AuthSmsLoginRequest request, Integer scene, String usedIp);

}
