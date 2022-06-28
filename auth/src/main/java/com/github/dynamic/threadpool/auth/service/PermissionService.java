package com.github.dynamic.threadpool.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.dynamic.threadpool.auth.model.biz.permission.PermissionRespDTO;

/**
 * Permission service.
 *
 * @author chen.ma
 * @date 2021/10/30 22:13
 */
public interface PermissionService {

    /**
     * 分页查询权限列表.
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<PermissionRespDTO> listPermission(int pageNo, int pageSize);

    /**
     * 新增权限.
     *
     * @param role
     * @param resource
     * @param action
     */
    void addPermission(String role, String resource, String action);

    /**
     * 删除权限.
     *
     * @param role
     * @param resource
     * @param action
     */
    void deletePermission(String role, String resource, String action);

}
