package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.POJO.BO.ClassBO;
import com.exam.POJO.DTO.ClassSelectDTO;
import com.exam.common.Result;
import com.exam.entity.Class;
import com.exam.mapper.ClassMapper;
import com.exam.service.IClassService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.utils.PageUtils;
import com.exam.utils.RedisCache;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 班级 服务实现类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-16
 */
@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class> implements IClassService {
    @Resource
    ClassMapper classMapper;
    @Autowired
    private RedisCache redisCache;


    @Override
    public Result<?> getAllClass() {
        List<Class> classes = classMapper.selectList(new LambdaQueryWrapper<Class>().select(Class::getId, Class::getClassName));
        ArrayList<ClassBO> classBOS = new ArrayList<>();
        if (classes != null && !classes.isEmpty()) {
            for (Class c : classes) {
                ClassBO classBO = new ClassBO();
                BeanUtils.copyProperties(c, classBO);
                classBOS.add(classBO);
            }
            return Result.success(classBOS);
        }
        return Result.fail("班级列表为空");
    }

    @Override
    public Result<?> getAllClassWithPage(ClassSelectDTO classSelectDTO) {
        // 分页查询
        Page<Class> classPage = new Page<>(classSelectDTO.getCurrent(), classSelectDTO.getPageSize());
        LambdaQueryWrapper<Class> wrapper = new LambdaQueryWrapper<>();
        String className = classSelectDTO.getClassName();

        if(!Objects.equals(className, "") && !StringUtils.isEmpty(className)){
            wrapper.like(Class::getClassName, className);
        }
        if(classSelectDTO.getId()!= null){
            wrapper.eq(Class::getId, classSelectDTO.getId());
        }
        Page<Class> classPage1 = classMapper.selectPage(classPage, wrapper);

        // 封装返回数据
        List<ClassBO> classBOS = new ArrayList<>();
        List<Class> records = classPage1.getRecords();
        if (records != null && !records.isEmpty()) {
            for (Class c : records) {
                ClassBO classBO = new ClassBO();
                BeanUtils.copyProperties(c, classBO);
                classBOS.add(classBO);
            }
        }

        HashMap<String, Object> classList = PageUtils.getResMapByPage(classPage1, "classList",classBOS );
        return Result.success(classList);
    }

    @Override
    public Result<?> addClass(ClassBO classBo) {
        // 新增班级
        Class addClass = new Class();
        BeanUtils.copyProperties(classBo, addClass);
        // 查看班级是否已经存在
        LambdaQueryWrapper<Class> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Class::getClassName, classBo.getClassName());
        Class class1 = classMapper.selectOne(wrapper);
        if(class1 != null){
            return Result.fail("班级名称已存在");
        }
        //设置id
        addClass.setId(redisCache.getUID());
        classMapper.insert(addClass);
        return Result.success("新增班级成功");
    }

    @Override
    public Result<?> updateClass(ClassBO classBo) {
        // 更新班级
        Class updateClass = new Class();
        BeanUtils.copyProperties(classBo, updateClass);
        LambdaQueryWrapper<Class> wrapper = new LambdaQueryWrapper<>();
        try {
            wrapper.eq(Class::getId, classBo.getId());
            classMapper.update(updateClass, wrapper);
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("更新失败,请检查id是否正确");
        }
        return Result.success("更新班级成功");
    }


    @Override
    public Result<?> deleteClass(Integer id) {
        // 删除班级
        LambdaQueryWrapper<Class> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Class::getId, id);
        try {
            classMapper.delete(wrapper);
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("删除失败,请检查id是否正确");
        }
        return Result.success("删除班级成功");
    }
}
