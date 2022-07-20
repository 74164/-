package com.atguigu.myzhxy.controller;

import com.atguigu.myzhxy.pojo.Student;
import com.atguigu.myzhxy.service.StudentService;
import com.atguigu.myzhxy.util.MD5;
import com.atguigu.myzhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shkstart
 * @create 2022-06-02 22:30
 */
@RestController//ResponseBody的controller
@RequestMapping("/sms/studentController")
public class StudentController {
        @Autowired
        private StudentService studentService;

//        删除和批量删除
    @DeleteMapping("/delStudentById")
    public Result delStudentById(@RequestBody List<Integer> ids){
        studentService.removeByIds(ids);
        return Result.ok();
    }

//        保存或修改学生信息
    @PostMapping("/addOrUpdateStudent")
    public Result addOrUploadStudent(
            @RequestBody Student student
    ){
//        判断是否需要转换密码
        Integer id = student.getId();
        if(null == id || 0==id){
            student.setPassword(MD5.encrypt(student.getPassword()));
        }
        studentService.saveOrUpdate(student);
        return Result.ok();
    }

        //    获取分页信息
    @GetMapping("/getStudentByOpr/{pageNo}/{pageSize}")
    public Result getStudentByOpr(
            @ApiParam("分页数") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页大小") @PathVariable("pageSize") Integer pageSize,
            @ApiParam("查询条件") Student student
    ){
        Page<Student> pages = new Page(pageNo,pageSize);
        IPage<Student> iPage = studentService.getStudentByOpr(pages,student);
        return Result.ok(iPage);
    }
}
