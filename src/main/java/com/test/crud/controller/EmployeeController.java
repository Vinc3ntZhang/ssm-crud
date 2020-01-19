package com.test.crud.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.test.crud.bean.Employee;
import com.test.crud.bean.Msg;
import com.test.crud.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  处理员工CRUD请求
 * */
@Controller
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    //检验用户名是否可用
    @ResponseBody
    @RequestMapping("/checkuser")
    public Msg checkUser(@RequestParam("empName") String empName) {
        //先判断用户名是否合法的表达式
        String regx = "((^[a-z0-9_-]{3,16}$)|(^[\u2E80-\u9FFF]{2,5}))";
        if (empName.matches(regx)) {
            return Msg.fail().add("va_msg", "用户名必须是6-16位数字和字母的组合，或者2-5位中文");
        }

        //数据库用户名重复校验
        boolean b = employeeService.checkUser(empName);
        if (b) {
            return Msg.success();
        } else {
            return Msg.fail().add("va_msg", "用户名不可用");
        }
    }


    /*
        查询员工数据（分页查询）
     */
    @RequestMapping("/emps")
    @ResponseBody
    public Msg getEmpsWithJson(@RequestParam(value = "pn", defaultValue = "1") Integer pn) {
        PageHelper.startPage(pn, 5);
        //startPage后面紧跟的这个查询就是一个分页查询
        List<Employee> emps = employeeService.getAll();
        //使用PageInfo包装查询后的结果,只需要将PageInfo交给页面就行了
        //封装了详细的分页信息，包括有我们查询出来的信息,传入连续显示的页数
        PageInfo page = new PageInfo(emps, 5);
        return Msg.success().add("pageInfo", page);
    }


    //    @RequestMapping("/emps")
    public String getEmps(@RequestParam(value = "pn", defaultValue = "1") Integer pn, Model model) {
        //这不是一个分页查询
        //引入PageHelper
        //再查询之前只需要调用,传入页面，以及每页大小
        PageHelper.startPage(pn, 5);
        //startPage后面紧跟的这个查询就是一个分页查询
        List<Employee> emps = employeeService.getAll();
        //使用PageInfo包装查询后的结果,只需要将PageInfo交给页面就行了
        //封装了详细的分页信息，包括有我们查询出来的信息,传入连续显示的页数
        PageInfo page = new PageInfo(emps, 5);
        model.addAttribute("pageInfo", page);
        return "list";
    }


    /*
     * 员工保存
     *
     * 1、支持JSR303校验
     * 2、导入Hibernate-Validator
     *
     * */
    @ResponseBody
    @RequestMapping(value = "/emp", method = RequestMethod.POST)
//    public Msg saveEmp(Employee employee) {
    public Msg saveEmp(@Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            //校验失败，应该返回失败,在模态框中显示校验失败的错误信息
            //将错误信息放入map中
            Map<String, Object> map = new HashMap<String, Object>();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError fieldError : errors) {
                System.out.println("错误的字段名：" + fieldError.getField());
                System.out.println("错误信息：" + fieldError.getDefaultMessage());
                map.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return Msg.fail().add("errorFields", map);
        } else {
            employeeService.saveEmp(employee);
            return Msg.success();
        }

    }

    //查询员工请求
    @RequestMapping(value = "emp/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Msg getEmp(@PathVariable("id") Integer id) {
        Employee employee = employeeService.getEmp(id);
        return Msg.success().add("emp", employee);
    }

    /*
       如果直接发送ajax=PUT形式的请求，
        封装的数据
        Employee{empId=3001, empName='null', gander='null', email='null', dId=null}
        问题：
            请求体中有数据，但是Employee对象封装不上
            update tbl_emp where emp_id = 3001

         原因：
            Tomcat:
                1、将请求体中的数据，封装一个map。
                2、request.getParameter("empName")就会从这个map中取值。
                3、SpringMVC封装POJO对象的时候。会把POJO每个属性的值，request.getParameter("email")
         AJAX发送PUT请求引发的血案:
            PUT亲贵，请求体中的数据request.getParameter("empName")拿不到
            Tomcat一看是PUT不会封装请求体中的数据为map，只有POST形式的请求才封装请求体为map

        解决方案
        我们要能支持直接发送PUT之类的请求还要封装请求体中的数据
            1、配置web.xml中的filter --->HttpPutFormContentFilter
            2、它的作用：将请求体中的数据解析包装成一个map。
            3、request被重新包装，request。getParameter()被重写，就会从自己封装的map中取数据

        */
    //员工更新方法
    @RequestMapping(value = "/emp/{empId}", method = RequestMethod.PUT)
    @ResponseBody
    public Msg saveEmp(Employee employee, HttpServletRequest request) {
        System.out.println("请求体中的值" + request.getParameter("gander"));
        System.out.println("将要更新的员工数据" + employee.toString());
        employeeService.updateEmp(employee);
        return Msg.success();
    }

    //删除单个员工
//    @ResponseBody
//    @RequestMapping(value = "/emp/{id}", method = RequestMethod.DELETE)
//    public Msg deleteEmpById(@PathVariable("id") Integer id) {
//        employeeService.deleteEmp(id);
//        return Msg.success();
//    }

    /*
     * 单个批量二合一
     * 批量删除：1-2-3
     * 单个删除：1
     * */
    @ResponseBody
    @RequestMapping(value = "/emp/{ids}", method = RequestMethod.DELETE)
    public Msg deleteEmp(@PathVariable("ids") String ids) {
        //批量删除
        if (ids.contains("-")) {
            String[] str_ids = ids.split("-");
            //组装id的集合
            List<Integer> del_ids = new ArrayList<>();
            for (String id : str_ids) {
                del_ids.add(Integer.parseInt(id));
            }
            employeeService.deleteBatch(del_ids);

        } else {//单个删除
            Integer id = Integer.parseInt(ids);
            employeeService.deleteEmp(id);
        }

        return Msg.success();
    }


}
