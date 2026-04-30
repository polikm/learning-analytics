package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.dto.UserDTO;
import com.example.learning.model.entity.SysUser;
import com.example.learning.model.vo.UserVO;
import com.example.learning.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理Controller
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     *
     * @param page     当前页码
     * @param size     每页大小
     * @param userType 用户类型
     * @param status   状态
     * @param keyword  搜索关键词
     * @return 分页结果
     */
    @Operation(summary = "分页查询用户列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<UserVO>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        PageQueryDTO dto = new PageQueryDTO(page, size);
        Page<SysUser> pageResult = userService.getPage(dto);
        // TODO: 转换为 PageResult<UserVO>，待确认转换逻辑
        PageResult<UserVO> result = PageResult.of(
                pageResult.getRecords().stream().map(user -> {
                    UserVO vo = new UserVO();
                    vo.setId(user.getId());
                    vo.setUsername(user.getUsername());
                    vo.setRealName(user.getRealName());
                    vo.setPhone(user.getPhone());
                    vo.setEmail(user.getEmail());
                    vo.setUserType(user.getUserType());
                    vo.setStatus(user.getStatus());
                    vo.setTenantId(user.getTenantId());
                    vo.setCreatedAt(user.getCreatedAt());
                    return vo;
                }).toList(),
                pageResult.getTotal(),
                pageResult.getCurrent(),
                pageResult.getSize()
        );
        return R.ok(result);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户信息VO
     */
    @Operation(summary = "用户详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<UserVO> getById(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setUserType(user.getUserType());
        vo.setStatus(user.getStatus());
        vo.setTenantId(user.getTenantId());
        vo.setCreatedAt(user.getCreatedAt());
        return R.ok(vo);
    }

    /**
     * 创建用户
     *
     * @param dto 用户信息DTO
     * @return 操作结果
     */
    @Operation(summary = "创建用户")
    @SaCheckLogin
    @PostMapping
    public R<Void> create(@Valid @RequestBody UserDTO dto) {
        userService.create(dto);
        return R.ok();
    }

    /**
     * 更新用户
     *
     * @param id  用户ID
     * @param dto 用户信息DTO
     * @return 操作结果
     */
    @Operation(summary = "更新用户")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        userService.update(id, dto);
        return R.ok();
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @Operation(summary = "删除用户")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return R.ok();
    }
}
