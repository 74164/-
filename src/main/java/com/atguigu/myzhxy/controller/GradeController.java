package com.atguigu.myzhxy.controller;

import com.atguigu.myzhxy.pojo.Grade;
import com.atguigu.myzhxy.service.GradeService;
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
 */
@Api(tags = "年级控制器")
@RestController//ResponseBody的controller
@RequestMapping("/sms/gradeController")
public class GradeController {

    @Autowired
    private GradeService gradeService;

//    获取年级信息
    @ApiOperation("获取所有年级信息")
    @GetMapping("/getGrades")
    public Result gerGrads(){
        List<Grade>list = gradeService.getGrads();
        return  Result.ok(list);
    }
//    删除和批量删除
    @ApiOperation("删除Grade信息(批量删除)")
    @DeleteMapping("/deleteGrade")
    public Result deleteGrade(
            @ApiParam("要删除所有grade的id的JSON集合") @RequestBody List<Integer>ids
            ){

        gradeService.removeByIds(ids);
        return Result.ok();
    }

//    修改和新增
    @ApiOperation("对Grade新增和删除")
    @PostMapping("/saveOrUpdateGrade")
    public Result saveOrUpdateGrade(
            @RequestBody Grade grade
    ){
//        接收参数
//        调用服务层方法进行修改和添加(Iservice已经自己写好了)
        gradeService.saveOrUpdate(grade);
        return Result.ok();
    }

    @ApiOperation("对Grade进行查询")
    @GetMapping("/getGrades/{pageNo}/{pageSize}")
    public Result getGrades(
            @PathVariable("pageNo") Integer pageNo,
            @PathVariable("pageSize") Integer pageSize,
            String gradeName
    ){
        //分页，带条件查询
        Page<Grade> page = new Page<>(pageNo,pageSize);
//        通过服务层
        IPage<Grade> pageRs =  gradeService.getGradeByOpr(page,gradeName);

//        封装result对象并返回
        return Result.ok(pageRs);
    }
}
