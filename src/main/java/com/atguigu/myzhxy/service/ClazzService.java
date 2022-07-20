package com.atguigu.myzhxy.service;

import com.atguigu.myzhxy.pojo.Clazz;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author shkstart
 * @create 2022-06-02 22:05
 */
public interface ClazzService extends IService<Clazz> {
    IPage<Clazz> getClazzByOpr(Page<Clazz> pages, Clazz clazz);

    List<Clazz> getClazzs();
}
