package com.atguigu.myzhxy.controller;

import com.atguigu.myzhxy.pojo.Clazz;
import com.atguigu.myzhxy.service.ClazzService;
import com.atguigu.myzhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shkstart
 * @create 2022-06-02 22:30
 */@RestController//ResponseBody的controller
@RequestMapping("/sms/clazzController")
@Api("班级信息管理")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

        @ApiOperation("获取所有班级信息")
        @GetMapping("/getClazzs")
        public Result getClazzs(){
           List<Clazz> list =  clazzService.getClazzs();
            return  Result.ok(list);
        }

    @ApiOperation("删除和批量删除")
    @DeleteMapping("/deleteClazz")
    public Result deleteClazz(@ApiParam("要删除所有clazz的id的JSON集合")@RequestBody List<Integer> ids){
            clazzService.removeByIds(ids);
            return Result.ok();
    }


    @ApiOperation("新增或修改班级信息")
    @PostMapping("/saveOrUpdateClazz")
    public Result saveOrUpdateClazz(
            @ApiParam("json格式的班级信息")@RequestBody Clazz clazz
    ){
        clazzService.saveOrUpdate(clazz);
        return Result.ok();
    }
    @GetMapping("/getClazzsByOpr/{pageNo}/{pageSize}")
    public Result getClazzByOpr(
            @ApiParam("分页数") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页大小") @PathVariable("pageSize") Integer pageSize,
            @ApiParam("查询条件") Clazz clazz
    ){
        Page<Clazz> pages = new Page<>(pageNo,pageSize);
       IPage<Clazz> ipage =  clazzService.getClazzByOpr(pages,clazz);
       return  Result.ok(ipage);
    }
}
