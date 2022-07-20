package com.atguigu.myzhxy.service.impl;

import com.atguigu.myzhxy.mapper.GradeMapper;
import com.atguigu.myzhxy.pojo.Grade;
import com.atguigu.myzhxy.service.GradeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author shkstart
 * @create 2022-06-02 22:07
 */
@Service("gradeServiceImpl")
@Transactional
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {
    @Override
    public IPage<Grade> getGradeByOpr(Page<Grade> pageParam, String gradeName) {
        QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
//        如果年纪名不为空进行查询
        if(!StringUtils.isEmpty(gradeName)){
            queryWrapper.like("name",gradeName);
        }

//        根据id进行降序排序
        queryWrapper.orderByDesc("id");
//    将分页信息和查询条件进行查询
        Page<Grade> gradePage = baseMapper.selectPage(pageParam, queryWrapper);
        return gradePage;
    }

    @Override
    public List<Grade> getGrads() {
//        查询所有
        return baseMapper.selectList(null);
    }
}
