package com.exam.controller;


import com.exam.POJO.BO.ClassBO;
import com.exam.POJO.DTO.ClassSelectDTO;
import com.exam.common.Result;
import com.exam.service.impl.ClassServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 班级 前端控制器
 * </p>
 *
 * @author 洛克
 * @since 2024-04-16
 */
@RestController
@RequestMapping("/api/class")
public class ClassController {
    @Resource
    private ClassServiceImpl classService;

    @GetMapping("/getAllClass")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public Result<?> getAllClass() {
        return classService.getAllClass();
    }

    @PostMapping("/getAllClassWithPage")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public Result<?> getAllClassWithPage(@RequestBody ClassSelectDTO classSelectDTO) {
        return classService.getAllClassWithPage(classSelectDTO);
    }

    @PostMapping("/addClass")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> addClass(@RequestBody ClassBO classBO) {
        return classService.addClass(classBO);
    }

    // TODO 删除班级对其他表的影响需要判断一下
    @DeleteMapping("/deleteClass/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteClass(@PathVariable("id") Integer id) {
        return classService.deleteClass(id);
    }

    @PostMapping("/updateClass")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateClass(@RequestBody ClassBO classBo) {
        return classService.updateClass(classBo);
    }

}
