package com.lingxi.scs.interfaces.rest;

import com.lingxi.scs.application.service.EmployeeApplicationService;
import com.lingxi.scs.common.context.BaseContext;
import com.lingxi.scs.common.result.R;
import com.lingxi.scs.domain.model.entity.Employee;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeApplicationService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("员工登录：{}", employee.getUsername());
        Employee emp = employeeService.login(employee.getUsername(), employee.getPassword());
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(@RequestBody Employee employee) {
        log.info("新增员工：{}", employee.getName());
        Long operatorId = BaseContext.getCurrentId();
        employeeService.addEmployee(employee, operatorId);
        return R.success("新增员工成功");
    }

    @GetMapping("/list")
    public R<List<Employee>> list() {
        log.info("查询员工列表");
        List<Employee> employees = employeeService.getAllEmployees();
        return R.success(employees);
    }

    @GetMapping("/page")
    public R<Page<Employee>> page(@RequestParam(defaultValue = "1") int page, 
                                   @RequestParam(defaultValue = "10") int pageSize,
                                   @RequestParam(required = false, defaultValue = "") String name) {
        log.info("分页查询员工，页码：{}, 每页大小：{}, 姓名：{}", page, pageSize, name);
        Page<Employee> employeePage = employeeService.pageByName(page, pageSize, name);
        return R.success(employeePage);
    }

    @PutMapping
    public R<String> update(@RequestBody Employee employee) {
        log.info("修改员工：{}", employee.getId());
        Long operatorId = BaseContext.getCurrentId();
        employeeService.updateEmployee(employee, operatorId);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable String id) {
        log.info("查询员工：{}", id);
        Employee employee = employeeService.getEmployeeById(Long.parseLong(id));
        return R.success(employee);
    }
}
