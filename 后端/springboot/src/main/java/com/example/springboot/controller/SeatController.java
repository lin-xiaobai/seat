package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.net.URLEncoder;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Constants;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Result;
import org.springframework.web.multipart.MultipartFile;
import com.example.springboot.entity.User;
import com.example.springboot.utils.TokenUtils;
import com.example.springboot.service.IUserService;

import com.example.springboot.service.ISeatService;
import com.example.springboot.entity.Seat;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 座位管理 前端控制器
 * 主要用mybatis-push简化代码，让代码更加整洁
 * </p>
 */
@RestController
@RequestMapping("/seat")
public class SeatController {

    @Resource
    private ISeatService seatService;
    @Resource
    IUserService userService;

    private final String now = DateUtil.now();

    // 新增或者更新座位
    @PostMapping
    public Result save(@RequestBody Seat seat) {
        if (seat.getId() == null) {
            //seat.setTime(DateUtil.now());
            //seat.setUserName(TokenUtils.getCurrentUser().getNickname());
            //seat.setUserId(TokenUtils.getCurrentUser().getId());
            seat.setState("否");
        }
        seatService.saveOrUpdate(seat);
        return Result.success();
    }
    //通过id删除座位
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        seatService.removeById(id);
        return Result.success();
    }
    //根据一组座位id批量删除座位
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        seatService.removeByIds(ids);
        return Result.success();
    }
    //查看所有座位
    @GetMapping
    public Result findAll() {
        return Result.success(seatService.list());
    }
    //通过id查看某个座位
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(seatService.getById(id));
    }

    //根据id获取座位信息的状态
    @GetMapping("/order/{id}")
    public Result order(@PathVariable Integer id) {
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Seat::getId,id);
        wrapper.eq(Seat::getState,"是");
        Seat one = seatService.getOne(wrapper);
        if (one!=null){
//            通过token标记判断当前的对象是否已经存在了
            if (one.getUserId()==TokenUtils.getCurrentUser().getId()){
                return Result.error(Constants.CODE_600,"您已经预订过这个座位了");
            }else {
                return Result.error(Constants.CODE_600,"已有别的用户预订过这个座位了");
            }
        }
        Seat seat = seatService.getById(id);
        seat.setUserId(TokenUtils.getCurrentUser().getId());
        seat.setUserName(TokenUtils.getCurrentUser().getNickname());
        seat.setState("是");
        seatService.updateById(seat);
        return Result.success();
    }
//取消预约，释放座位
    @GetMapping("/cancel/{id}")
    public Result cancel(@PathVariable Integer id) {
        LambdaUpdateWrapper<Seat> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Seat::getId,id);
        wrapper.set(Seat::getUserId,null);
        wrapper.set(Seat::getUserName,null);
        wrapper.set(Seat::getState,"否");
        seatService.update(wrapper);
        return Result.success();
    }
//通过id进行查找，分页功能
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Seat> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
//        模糊查询
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
//        User currentUser = TokenUtils.getCurrentUser();
//        if (currentUser.getRole().equals("ROLE_USER")) {
//            queryWrapper.eq("userid", currentUser.getId());
//        }
        //查到后进行分页
        return Result.success(seatService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Seat> list = seatService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Seat信息表", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

        }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
        List<Seat> list = reader.readAll(Seat.class);

        seatService.saveBatch(list);
        return Result.success();
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

