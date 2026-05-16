package com.focela.platform.system.service.user;

import cn.hutool.core.util.RandomUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.exception.ServiceException;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.ArrayUtils;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.infra.api.config.ConfigApi;
import com.focela.platform.infra.api.file.FileApi;
import com.focela.platform.system.controller.admin.user.dto.profile.UserProfileUpdatePasswordRequest;
import com.focela.platform.system.controller.admin.user.dto.profile.UserProfileUpdateRequest;
import com.focela.platform.system.controller.admin.user.dto.UserImportExcelDto;
import com.focela.platform.system.controller.admin.user.dto.UserImportResponse;
import com.focela.platform.system.controller.admin.user.dto.UserPageRequest;
import com.focela.platform.system.controller.admin.user.dto.UserSaveRequest;
import com.focela.platform.system.entity.department.DepartmentEntity;
import com.focela.platform.system.entity.department.PostEntity;
import com.focela.platform.system.entity.department.UserPostEntity;
import com.focela.platform.system.entity.tenant.TenantEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.focela.platform.system.repository.mapper.department.UserPostMapper;
import com.focela.platform.system.repository.mapper.user.UserMapper;
import com.focela.platform.system.enums.common.SexEnum;
import com.focela.platform.system.service.department.DepartmentService;
import com.focela.platform.system.service.department.PostService;
import com.focela.platform.system.service.oauth2.OAuth2TokenService;
import com.focela.platform.system.service.permission.PermissionService;
import com.focela.platform.system.service.tenant.TenantService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.common.utils.collection.SetUtils.asSet;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static com.focela.platform.system.service.user.DefaultUserService.USER_INIT_PASSWORD_KEY;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Import(DefaultUserService.class)
public class DefaultUserServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultUserService userService;

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserPostMapper userPostMapper;

    @MockitoBean
    private DepartmentService deptService;
    @MockitoBean
    private PostService postService;
    @MockitoBean
    private PermissionService permissionService;
    @MockitoBean
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private TenantService tenantService;
    @MockitoBean
    private FileApi fileApi;
    @MockitoBean
    private ConfigApi configApi;
    @MockitoBean
    private OAuth2TokenService oauth2TokenService;

    @BeforeEach
    public void before() {
        // mock initial password
        when(configApi.getConfigValueByKey(USER_INIT_PASSWORD_KEY)).thenReturn("focelasecret");
    }

    @Test
    public void testCreatUser_success() {
        // prepare parameters
        UserSaveRequest request = randomPojo(UserSaveRequest.class, o -> {
            o.setSex(RandomUtil.randomEle(SexEnum.values()).getSex());
            o.setMobile(randomString());
            o.setPostIds(asSet(1L, 2L));
        }).setId(null); // avoid id being assigned
        // mock sufficient account quota
        TenantEntity tenant = randomPojo(TenantEntity.class, o -> o.setAccountCount(1));
        doNothing().when(tenantService).handleTenantInfo(argThat(handler -> {
            handler.handle(tenant);
            return true;
        }));
        // mock deptService  method
        DepartmentEntity dept = randomPojo(DepartmentEntity.class, o -> {
            o.setId(request.getDeptId());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        when(deptService.getDept(eq(dept.getId()))).thenReturn(dept);
        // mock postService  method
        List<PostEntity> posts = CollectionUtils.convertList(request.getPostIds(), postId ->
                randomPojo(PostEntity.class, o -> {
                    o.setId(postId);
                    o.setStatus(CommonStatusEnum.ENABLE.getStatus());
                }));
        when(postService.getPostList(eq(request.getPostIds()), isNull())).thenReturn(posts);
        // mock passwordEncoder  method
        when(passwordEncoder.encode(eq(request.getPassword()))).thenReturn("focelasecret");

        // invoke
        Long userId = userService.createUser(request);
        // assert
        UserEntity user = userMapper.selectById(userId);
        assertPojoEquals(request, user, "password", "id");
        assertEquals("focelasecret", user.getPassword());
        assertEquals(CommonStatusEnum.ENABLE.getStatus(), user.getStatus());
        // assert associated post
        List<UserPostEntity> userPosts = userPostMapper.selectListByUserId(user.getId());
        assertEquals(1L, userPosts.get(0).getPostId());
        assertEquals(2L, userPosts.get(1).getPostId());
    }

    @Test
    public void testCreatUser_max() {
        // prepare parameters
        UserSaveRequest request = randomPojo(UserSaveRequest.class);
        // mock insufficient account quota
        TenantEntity tenant = randomPojo(TenantEntity.class, o -> o.setAccountCount(-1));
        doNothing().when(tenantService).handleTenantInfo(argThat(handler -> {
            handler.handle(tenant);
            return true;
        }));

        // invoke, and assert exception
        assertServiceException(() -> userService.createUser(request), USER_COUNT_MAX, -1);
    }

    @Test
    public void testUpdateUser_success() {
        // mock data
        UserEntity dbUser = randomAdminUserDO(o -> o.setPostIds(asSet(1L, 2L)));
        userMapper.insert(dbUser);
        userPostMapper.insert(new UserPostEntity().setUserId(dbUser.getId()).setPostId(1L));
        userPostMapper.insert(new UserPostEntity().setUserId(dbUser.getId()).setPostId(2L));
        // prepare parameters
        UserSaveRequest request = randomPojo(UserSaveRequest.class, o -> {
            o.setId(dbUser.getId());
            o.setSex(RandomUtil.randomEle(SexEnum.values()).getSex());
            o.setMobile(randomString());
            o.setPostIds(asSet(2L, 3L));
        });
        // mock deptService  method
        DepartmentEntity dept = randomPojo(DepartmentEntity.class, o -> {
            o.setId(request.getDeptId());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        when(deptService.getDept(eq(dept.getId()))).thenReturn(dept);
        // mock postService  method
        List<PostEntity> posts = CollectionUtils.convertList(request.getPostIds(), postId ->
                randomPojo(PostEntity.class, o -> {
                    o.setId(postId);
                    o.setStatus(CommonStatusEnum.ENABLE.getStatus());
                }));
        when(postService.getPostList(eq(request.getPostIds()), isNull())).thenReturn(posts);

        // invoke
        userService.updateUser(request);
        // assert
        UserEntity user = userMapper.selectById(request.getId());
        assertPojoEquals(request, user, "password");
        // assert associated post
        List<UserPostEntity> userPosts = userPostMapper.selectListByUserId(user.getId());
        assertEquals(2L, userPosts.get(0).getPostId());
        assertEquals(3L, userPosts.get(1).getPostId());
    }

    @Test
    public void testUpdateUserLogin() {
        // mock data
        UserEntity user = randomAdminUserDO(o -> o.setLoginDate(null));
        userMapper.insert(user);
        // prepare parameters
        Long id = user.getId();
        String loginIp = randomString();

        // invoke
        userService.updateUserLogin(id, loginIp);
        // assert
        UserEntity dbUser = userMapper.selectById(id);
        assertEquals(loginIp, dbUser.getLoginIp());
        assertNotNull(dbUser.getLoginDate());
    }

    @Test
    public void testUpdateUserProfile_success() {
        // mock data
        UserEntity dbUser = randomAdminUserDO();
        userMapper.insert(dbUser);
        // prepare parameters
        Long userId = dbUser.getId();
        UserProfileUpdateRequest request = randomPojo(UserProfileUpdateRequest.class, o -> {
            o.setMobile(randomString());
            o.setSex(RandomUtil.randomEle(SexEnum.values()).getSex());
            o.setAvatar(randomURL());
        });

        // invoke
        userService.updateUserProfile(userId, request);
        // assert
        UserEntity user = userMapper.selectById(userId);
        assertPojoEquals(request, user);
    }

    @Test
    public void testUpdateUserPassword_success() {
        // mock data
        UserEntity dbUser = randomAdminUserDO(o -> o.setPassword("encode:tudou"));
        userMapper.insert(dbUser);
        // prepare parameters
        Long userId = dbUser.getId();
        UserProfileUpdatePasswordRequest request = randomPojo(UserProfileUpdatePasswordRequest.class, o -> {
            o.setOldPassword("tudou");
            o.setNewPassword("yuanma");
        });
        // mock the method
        when(passwordEncoder.encode(anyString())).then(
                (Answer<String>) invocationOnMock -> "encode:" + invocationOnMock.getArgument(0));
        when(passwordEncoder.matches(eq(request.getOldPassword()), eq(dbUser.getPassword()))).thenReturn(true);

        // invoke
        userService.updateUserPassword(userId, request);
        // assert
        UserEntity user = userMapper.selectById(userId);
        assertEquals("encode:yuanma", user.getPassword());
    }

    @Test
    public void testUpdateUserPassword02_success() {
        // mock data
        UserEntity dbUser = randomAdminUserDO();
        userMapper.insert(dbUser);
        // prepare parameters
        Long userId = dbUser.getId();
        String password = "focela";
        // mock the method
        when(passwordEncoder.encode(anyString())).then(
                (Answer<String>) invocationOnMock -> "encode:" + invocationOnMock.getArgument(0));

        // invoke
        userService.updateUserPassword(userId, password);
        // assert
        UserEntity user = userMapper.selectById(userId);
        assertEquals("encode:" + password, user.getPassword());
    }

    @Test
    public void testUpdateUserStatus() {
        // mock data
        UserEntity dbUser = randomAdminUserDO();
        userMapper.insert(dbUser);
        // prepare parameters
        Long userId = dbUser.getId();
        Integer status = randomCommonStatus();

        // invoke
        userService.updateUserStatus(userId, status);
        // assert
        UserEntity user = userMapper.selectById(userId);
        assertEquals(status, user.getStatus());
    }

    @Test
    public void testDeleteUser_success(){
        // mock data
        UserEntity dbUser = randomAdminUserDO();
        userMapper.insert(dbUser);
        // prepare parameters
        Long userId = dbUser.getId();

        // invoke data
        userService.deleteUser(userId);
        // verify result
        assertNull(userMapper.selectById(userId));
        // verify call count
        verify(permissionService, times(1)).processUserDeleted(eq(userId));
    }

    @Test
    public void testGetUserByUsername() {
        // mock data
        UserEntity dbUser = randomAdminUserDO();
        userMapper.insert(dbUser);
        // prepare parameters
        String username = dbUser.getUsername();

        // invoke
        UserEntity user = userService.getUserByUsername(username);
        // assert
        assertPojoEquals(dbUser, user);
    }

    @Test
    public void testGetUserByMobile() {
        // mock data
        UserEntity dbUser = randomAdminUserDO();
        userMapper.insert(dbUser);
        // prepare parameters
        String mobile = dbUser.getMobile();

        // invoke
        UserEntity user = userService.getUserByMobile(mobile);
        // assert
        assertPojoEquals(dbUser, user);
    }

    @Test
    public void testGetUserPage() {
        // mock data
        UserEntity dbUser = initGetUserPageData();
        // prepare parameters
        UserPageRequest request = new UserPageRequest();
        request.setUsername("tu");
        request.setMobile("1560");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());
        request.setCreateTime(buildBetweenTime(2020, 12, 1, 2020, 12, 24));
        request.setDeptId(1L); // Note: 1L is the parent department of 2L
        // mock the method
        List<DepartmentEntity> deptList = newArrayList(randomPojo(DepartmentEntity.class, o -> o.setId(2L)));
        when(deptService.getChildDeptList(eq(request.getDeptId()))).thenReturn(deptList);

        // invoke
        PageResult<UserEntity> pageResult = userService.getUserPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbUser, pageResult.getList().get(0));
    }

    /**
     * initialize test data for getUserPage method
     */
    private UserEntity initGetUserPageData() {
        // mock data
        UserEntity dbUser = randomAdminUserDO(o -> { // will be queried later
            o.setUsername("tudou");
            o.setMobile("15601691300");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCreateTime(buildTime(2020, 12, 12));
            o.setDeptId(2L);
        });
        userMapper.insert(dbUser);
        // test username mismatch
        userMapper.insert(cloneIgnoreId(dbUser, o -> o.setUsername("dou")));
        // test mobile mismatch
        userMapper.insert(cloneIgnoreId(dbUser, o -> o.setMobile("18818260888")));
        // test status mismatch
        userMapper.insert(cloneIgnoreId(dbUser, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // test createTime mismatch
        userMapper.insert(cloneIgnoreId(dbUser, o -> o.setCreateTime(buildTime(2020, 11, 11))));
        // test dept mismatch
        userMapper.insert(cloneIgnoreId(dbUser, o -> o.setDeptId(0L)));
        return dbUser;
    }

    @Test
    public void testGetUser() {
        // mock data
        UserEntity dbUser = randomAdminUserDO();
        userMapper.insert(dbUser);
        // prepare parameters
        Long userId = dbUser.getId();

        // invoke
        UserEntity user = userService.getUser(userId);
        // assert
        assertPojoEquals(dbUser, user);
    }

    @Test
    public void testGetUserListByDeptIds() {
        // mock data
        UserEntity dbUser = randomAdminUserDO(o -> o.setDeptId(1L));
        userMapper.insert(dbUser);
        // test deptId mismatch
        userMapper.insert(cloneIgnoreId(dbUser, o -> o.setDeptId(2L)));
        // prepare parameters
        Collection<Long> deptIds = singleton(1L);

        // invoke
        List<UserEntity> list = userService.getUserListByDeptIds(deptIds);
        // assert
        assertEquals(1, list.size());
        assertEquals(dbUser, list.get(0));
    }

    /**
     * Scenario 1: validation fails, causing insert failure
     */
    @Test
    public void testImportUserList_01() {
        // prepare parameters
        UserImportExcelDto importUser = randomPojo(UserImportExcelDto.class, o -> {
            o.setEmail(randomEmail());
            o.setMobile(randomMobile());
        });
        // mock the method, simulate failure
        doThrow(new ServiceException(DEPT_NOT_FOUND)).when(deptService).validateDeptList(any());

        // invoke
        UserImportResponse response = userService.importUserList(newArrayList(importUser), true);
        // assert
        assertEquals(0, response.getCreateUsernames().size());
        assertEquals(0, response.getUpdateUsernames().size());
        assertEquals(1, response.getFailureUsernames().size());
        assertEquals(DEPT_NOT_FOUND.getMsg(), response.getFailureUsernames().get(importUser.getUsername()));
    }

    /**
     * case 2: does not exist, perform insert
     */
    @Test
    public void testImportUserList_02() {
        // prepare parameters
        UserImportExcelDto importUser = randomPojo(UserImportExcelDto.class, o -> {
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // ensure status range
            o.setSex(randomEle(SexEnum.values()).getSex()); // ensure sex range
            o.setEmail(randomEmail());
            o.setMobile(randomMobile());
        });
        // mock deptService  method
        DepartmentEntity dept = randomPojo(DepartmentEntity.class, o -> {
            o.setId(importUser.getDeptId());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        when(deptService.getDept(eq(dept.getId()))).thenReturn(dept);
        // mock passwordEncoder  method
        when(passwordEncoder.encode(eq("focelasecret"))).thenReturn("java");

        // invoke
        UserImportResponse response = userService.importUserList(newArrayList(importUser), true);
        // assert
        assertEquals(1, response.getCreateUsernames().size());
        UserEntity user = userMapper.selectByUsername(response.getCreateUsernames().get(0));
        assertPojoEquals(importUser, user);
        assertEquals("java", user.getPassword());
        assertEquals(0, response.getUpdateUsernames().size());
        assertEquals(0, response.getFailureUsernames().size());
    }

    /**
     * case 3: exists but no force update
     */
    @Test
    public void testImportUserList_03() {
        // mock data
        UserEntity dbUser = randomAdminUserDO();
        userMapper.insert(dbUser);
        // prepare parameters
        UserImportExcelDto importUser = randomPojo(UserImportExcelDto.class, o -> {
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // ensure status range
            o.setSex(randomEle(SexEnum.values()).getSex()); // ensure sex range
            o.setUsername(dbUser.getUsername());
            o.setEmail(randomEmail());
            o.setMobile(randomMobile());
        });
        // mock deptService  method
        DepartmentEntity dept = randomPojo(DepartmentEntity.class, o -> {
            o.setId(importUser.getDeptId());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        when(deptService.getDept(eq(dept.getId()))).thenReturn(dept);

        // invoke
        UserImportResponse response = userService.importUserList(newArrayList(importUser), false);
        // assert
        assertEquals(0, response.getCreateUsernames().size());
        assertEquals(0, response.getUpdateUsernames().size());
        assertEquals(1, response.getFailureUsernames().size());
        assertEquals(USER_USERNAME_EXISTS.getMsg(), response.getFailureUsernames().get(importUser.getUsername()));
    }

    /**
     * case 4: exists, force update
     */
    @Test
    public void testImportUserList_04() {
        // mock data
        UserEntity dbUser = randomAdminUserDO();
        userMapper.insert(dbUser);
        // prepare parameters
        UserImportExcelDto importUser = randomPojo(UserImportExcelDto.class, o -> {
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // ensure status range
            o.setSex(randomEle(SexEnum.values()).getSex()); // ensure sex range
            o.setUsername(dbUser.getUsername());
            o.setEmail(randomEmail());
            o.setMobile(randomMobile());
        });
        // mock deptService  method
        DepartmentEntity dept = randomPojo(DepartmentEntity.class, o -> {
            o.setId(importUser.getDeptId());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        when(deptService.getDept(eq(dept.getId()))).thenReturn(dept);

        // invoke
        UserImportResponse response = userService.importUserList(newArrayList(importUser), true);
        // assert
        assertEquals(0, response.getCreateUsernames().size());
        assertEquals(1, response.getUpdateUsernames().size());
        UserEntity user = userMapper.selectByUsername(response.getUpdateUsernames().get(0));
        assertPojoEquals(importUser, user);
        assertEquals(0, response.getFailureUsernames().size());
    }

    @Test
    public void testValidateUserExists_notExists() {
        assertServiceException(() -> userService.validateUserExists(randomLongId()), USER_NOT_EXISTS);
    }

    @Test
    public void testValidateUsernameUnique_usernameExistsForCreate() {
        // prepare parameters
        String username = randomString();
        // mock data
        userMapper.insert(randomAdminUserDO(o -> o.setUsername(username)));

        // invoke, verify exception
        assertServiceException(() -> userService.validateUsernameUnique(null, username),
                USER_USERNAME_EXISTS);
    }

    @Test
    public void testValidateUsernameUnique_usernameExistsForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String username = randomString();
        // mock data
        userMapper.insert(randomAdminUserDO(o -> o.setUsername(username)));

        // invoke, verify exception
        assertServiceException(() -> userService.validateUsernameUnique(id, username),
                USER_USERNAME_EXISTS);
    }

    @Test
    public void testValidateEmailUnique_emailExistsForCreate() {
        // prepare parameters
        String email = randomString();
        // mock data
        userMapper.insert(randomAdminUserDO(o -> o.setEmail(email)));

        // invoke, verify exception
        assertServiceException(() -> userService.validateEmailUnique(null, email),
                USER_EMAIL_EXISTS);
    }

    @Test
    public void testValidateEmailUnique_emailExistsForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String email = randomString();
        // mock data
        userMapper.insert(randomAdminUserDO(o -> o.setEmail(email)));

        // invoke, verify exception
        assertServiceException(() -> userService.validateEmailUnique(id, email),
                USER_EMAIL_EXISTS);
    }

    @Test
    public void testValidateMobileUnique_mobileExistsForCreate() {
        // prepare parameters
        String mobile = randomString();
        // mock data
        userMapper.insert(randomAdminUserDO(o -> o.setMobile(mobile)));

        // invoke, verify exception
        assertServiceException(() -> userService.validateMobileUnique(null, mobile),
                USER_MOBILE_EXISTS);
    }

    @Test
    public void testValidateMobileUnique_mobileExistsForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String mobile = randomString();
        // mock data
        userMapper.insert(randomAdminUserDO(o -> o.setMobile(mobile)));

        // invoke, verify exception
        assertServiceException(() -> userService.validateMobileUnique(id, mobile),
                USER_MOBILE_EXISTS);
    }

    @Test
    public void testValidateOldPassword_notExists() {
        assertServiceException(() -> userService.validateOldPassword(randomLongId(), randomString()),
                USER_NOT_EXISTS);
    }

    @Test
    public void testValidateOldPassword_passwordFailed() {
        // mock data
        UserEntity user = randomAdminUserDO();
        userMapper.insert(user);
        // prepare parameters
        Long id = user.getId();
        String oldPassword = user.getPassword();

        // invoke, verify exception
        assertServiceException(() -> userService.validateOldPassword(id, oldPassword),
                USER_PASSWORD_FAILED);
        // verify call
        verify(passwordEncoder, times(1)).matches(eq(oldPassword), eq(user.getPassword()));
    }

    @Test
    public void testUserListByPostIds() {
        // prepare parameters
        Collection<Long> postIds = asSet(10L, 20L);
        // mock user1 data
        UserEntity user1 = randomAdminUserDO(o -> o.setPostIds(asSet(10L, 30L)));
        userMapper.insert(user1);
        userPostMapper.insert(new UserPostEntity().setUserId(user1.getId()).setPostId(10L));
        userPostMapper.insert(new UserPostEntity().setUserId(user1.getId()).setPostId(30L));
        // mock user2 data
        UserEntity user2 = randomAdminUserDO(o -> o.setPostIds(singleton(100L)));
        userMapper.insert(user2);
        userPostMapper.insert(new UserPostEntity().setUserId(user2.getId()).setPostId(100L));

        // invoke
        List<UserEntity> result = userService.getUserListByPostIds(postIds);
        // assert
        assertEquals(1, result.size());
        assertEquals(user1, result.get(0));
    }

    @Test
    public void testGetUserList() {
        // mock data
        UserEntity user = randomAdminUserDO();
        userMapper.insert(user);
        // test id mismatch
        userMapper.insert(randomAdminUserDO());
        // prepare parameters
        Collection<Long> ids = singleton(user.getId());

        // invoke
        List<UserEntity> result = userService.getUserList(ids);
        // assert
        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
    }

    @Test
    public void testGetUserMap() {
        // mock data
        UserEntity user = randomAdminUserDO();
        userMapper.insert(user);
        // test id mismatch
        userMapper.insert(randomAdminUserDO());
        // prepare parameters
        Collection<Long> ids = singleton(user.getId());

        // invoke
        Map<Long, UserEntity> result = userService.getUserMap(ids);
        // assert
        assertEquals(1, result.size());
        assertEquals(user, result.get(user.getId()));
    }

    @Test
    public void testGetUserListByNickname() {
        // mock data
        UserEntity user = randomAdminUserDO(o -> o.setNickname("Focela"));
        userMapper.insert(user);
        // test nickname mismatch
        userMapper.insert(randomAdminUserDO(o -> o.setNickname("source")));
        // prepare parameters
        String nickname = "Focela";

        // invoke
        List<UserEntity> result = userService.getUserListByNickname(nickname);
        // assert
        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
    }

    @Test
    public void testGetUserListByStatus() {
        // mock data
        UserEntity user = randomAdminUserDO(o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        userMapper.insert(user);
        // test status mismatch
        userMapper.insert(randomAdminUserDO(o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus())));
        // prepare parameters
        Integer status = CommonStatusEnum.DISABLE.getStatus();

        // invoke
        List<UserEntity> result = userService.getUserListByStatus(status);
        // assert
        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
    }

    @Test
    public void testValidateUserList_success() {
        // mock data
        UserEntity userEntity = randomAdminUserDO().setStatus(CommonStatusEnum.ENABLE.getStatus());
        userMapper.insert(userEntity);
        // prepare parameters
        List<Long> ids = singletonList(userEntity.getId());

        // invoke, no assertion needed
        userService.validateUserList(ids);
    }

    @Test
    public void testValidateUserList_notFound() {
        // prepare parameters
        List<Long> ids = singletonList(randomLongId());

        // invoke and assert exception
        assertServiceException(() -> userService.validateUserList(ids), USER_NOT_EXISTS);
    }

    @Test
    public void testValidateUserList_notEnable() {
        // mock data
        UserEntity userEntity = randomAdminUserDO().setStatus(CommonStatusEnum.DISABLE.getStatus());
        userMapper.insert(userEntity);
        // prepare parameters
        List<Long> ids = singletonList(userEntity.getId());

        // invoke and assert exception
        assertServiceException(() -> userService.validateUserList(ids), USER_IS_DISABLE,
                userEntity.getNickname());
    }

    // ========== random object ==========

    @SafeVarargs
    private static UserEntity randomAdminUserDO(Consumer<UserEntity>... consumers) {
        Consumer<UserEntity> consumer = (o) -> {
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // ensure status range
            o.setSex(randomEle(SexEnum.values()).getSex()); // ensure sex range
        };
        return randomPojo(UserEntity.class, ArrayUtils.append(consumer, consumers));
    }

}
