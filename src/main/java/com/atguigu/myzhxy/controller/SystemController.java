package com.atguigu.myzhxy.controller;

import com.atguigu.myzhxy.pojo.Admin;
import com.atguigu.myzhxy.pojo.LoginForm;
import com.atguigu.myzhxy.pojo.Student;
import com.atguigu.myzhxy.pojo.Teacher;
import com.atguigu.myzhxy.service.AdminService;
import com.atguigu.myzhxy.service.StudentService;
import com.atguigu.myzhxy.service.TeacherService;
import com.atguigu.myzhxy.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author shkstart
 * @create 2022-06-02 22:35
 */
//与表格无关的controller
@RestController//ResponseBody的controller
@RequestMapping("/sms/system")
public class SystemController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

//    修改密码
    @PostMapping("/updatePwd/{oldPwd}/{newPwd}")
    public Result updatePwd(
            @RequestHeader("token")String token,
            @PathVariable String oldPwd,
            @PathVariable String newPwd
    ){
        boolean expiration = JwtHelper.isExpiration(token);
        if(expiration){
//            token过期
            return Result.fail().message("session失效，请重新登录");
        }
//        获取用户ID和类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        oldPwd = MD5.encrypt(oldPwd);
        newPwd = MD5.encrypt(newPwd);
        switch (userType){
            case 1:
                QueryWrapper<Admin> wrapper = new QueryWrapper<>();
                wrapper.eq("id",userId.intValue());
                wrapper.eq("password",oldPwd);
                Admin admin = adminService.getOne(wrapper);
                if(admin != null){
//                    修改
                    admin.setPassword(newPwd);
                    adminService.saveOrUpdate(admin);
                }else{
                    return Result.fail().message("原密码有误");
                }
                break;
            case 2:
                QueryWrapper<Student> wrapper2 = new QueryWrapper();
                wrapper2.eq("id",userId.intValue());
                wrapper2.eq("password",oldPwd);
                Student student = studentService.getOne(wrapper2);
                if(student != null){
//                    修改
                    student.setPassword(newPwd);
                    studentService.saveOrUpdate(student);
                }else {
                    return Result.fail().message("原密码有误");
                }
                break;
            case 3:
                QueryWrapper<Teacher> wrapper3 = new QueryWrapper();
                wrapper3.eq("id",userId.intValue());
                wrapper3.eq("password",oldPwd);
                Teacher teacher = teacherService.getOne(wrapper3);
                if(teacher != null){
//                    修改
                    teacher.setPassword(newPwd);
                    teacherService.saveOrUpdate(teacher);
                }else {
                    return Result.fail().message("原密码有误");
                }
        }
        return Result.ok();
    }

//    图片上传功能
    @ApiOperation("文件上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result  headerImgUpload(
            @ApiParam("头像文件") @RequestPart("multipartFile") MultipartFile multipartFile,
            HttpServletRequest request
            ){
//        防止图片名重复，生成uuid
        String s = UUID.randomUUID().toString().replace("-","").toLowerCase();
        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String newFileName = s.concat(originalFilename.substring(i));

        //        保存文件,将文件发送到第三方(真是开发时)
        String portraitPath = "D:/work-space/myzhxy/target/classes/public/upload/".concat(newFileName);
//        响应文件路径
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        响应图片路径
        String path = "upload/".concat(newFileName);
        return Result.ok(path);
    }



    //    登陆后的跳转
        @GetMapping("/getInfo")
    //获取token的请求头信息
    public Result getInfoByToken(@RequestHeader("token")String token){
//        校验token是否过期
        boolean expiration = JwtHelper.isExpiration(token);
//        判断是否过期
        if (expiration) {
            return Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }
//        从token中解析出用户id和类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        Map<String,Object>map = new LinkedHashMap<>();
        switch (userType){
            case 1:
                Admin admin = adminService.getAdminById(userId);
                map.put("userType",1);
                map.put("user",admin);
                break;
            case 2:
                Student student = studentService.getStudentById(userId);
                map.put("userType",2);
                map.put("user",student);
                break;
            case 3:
                Teacher teacher = teacherService.getTeacherById(userId);
                map.put("userType",3);
                map.put("user",teacher);
        }
        return Result.ok(map);
    }


    @PostMapping("/login")
//    RequestBody转为为json格式
    public Result login(@RequestBody LoginForm loginForm, HttpServletRequest request){
//        验证码校验,从域中拿到验证码(获取验证码的方法已经把验证码放到了域中)
        HttpSession session = request.getSession();
        String  sessionVerifiCodever = (String)session.getAttribute("verifiCode");
        String loginVerifiCode = loginForm.getVerifiCode();
        if("".equals(sessionVerifiCodever) || null == sessionVerifiCodever){
            return Result.fail().message("验证码已失效,请重试");
        }
        if(!sessionVerifiCodever.equalsIgnoreCase(loginVerifiCode)){
            return Result.fail().message("验证码错误,请重试");
        }
//        从session移除现验证码
        session.removeAttribute("verifiCode");
//        用户类型校验

//        准备一个map,存放响应的数据
        Map<String,Object> map= new LinkedHashMap<>();
        switch (loginForm.getUserType()) {
            case 1:
                try {
                    Admin admin = adminService.login(loginForm);
                    if(null != admin){
    //                    将用户类型,用户名id转换为密文,已token的名称向客户端反馈
                        map.put("token", JwtHelper.createToken(admin.getId().longValue(),1));
                    }else{
                        throw new RuntimeException("密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
//                    如果有误打印错误信息
                    return Result.fail().message(e.getMessage());
                }
            case 2:
                try {
                    Student student = studentService.login(loginForm);
                    if(null != student){
                        //                    将用户类型,用户名id转换为密文,已token的名称向客户端反馈
                        map.put("token", JwtHelper.createToken(student.getId().longValue(),2));
                    }else{
                        throw new RuntimeException("密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
//                    如果有误打印错误信息
                    return Result.fail().message(e.getMessage());
                }
            case 3:
                try {
                    Teacher teacher = teacherService.login(loginForm);
                    if(null != teacher){
                        //                    将用户类型,用户名id转换为密文,已token的名称向客户端反馈
                        map.put("token", JwtHelper.createToken(teacher.getId().longValue(),3));
                    }else{
                        throw new RuntimeException("密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
//                    如果有误打印错误信息
                    return Result.fail().message(e.getMessage());
                }

        }
        return Result.fail().message("查无此用户");
    }

    @GetMapping("/getVerifiCodeImage")
    public void getVerifiCodeImage(HttpServletRequest request, HttpServletResponse response){
//        获取图片
        BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();
//        获取图片上的验证码
//        因为该方法返回是char所以要转化成String
       String verifiCode = new String(CreateVerifiCodeImage.getVerifiCode());
//        将验证码放入session域,为下一次验证做准备
        HttpSession session = request.getSession();
        session.setAttribute("verifiCode",verifiCode);
//        将验证码图片响应给浏览器
//        将图片信息写出去
        try {
            ImageIO.write(verifiCodeImage,"JPEG",response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
