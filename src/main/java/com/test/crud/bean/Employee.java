package com.test.crud.bean;



import javax.validation.constraints.Pattern;

public class Employee {
    private Integer empId;

    @Pattern(regexp = "(^[a-z0-9_-]{3,16}$)|(^[\u2E80-\u9FFF]{2,5})",message = "用户名必须是6-16位数字和字母的组合，或者2-5位中文")
    private String empName;

    private String gander;

//    @Email
    @Pattern(regexp = "^([a-zA-Z]|[0-9])(\\w|\\-)+@[a-zA-Z0-9]+\\.([a-zA-Z]{2,4})$" ,message = "邮箱格式不正确")
    private String email;

    private Integer dId;
    //希望查询员工的同时部门信息也是查询好的
    private Department department;

    public Employee() {
    }

    public Employee(Integer empId, String empName, String gander, String email, Integer dId) {
        this.empId = empId;
        this.empName = empName;
        this.gander = gander;
        this.email = email;
        this.dId = dId;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName == null ? null : empName.trim();
    }

    public String getGander() {
        return gander;
    }

    public void setGander(String gander) {
        this.gander = gander == null ? null : gander.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getdId() {
        return dId;
    }

    public void setdId(Integer dId) {
        this.dId = dId;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", empName='" + empName + '\'' +
                ", gander='" + gander + '\'' +
                ", email='" + email + '\'' +
                ", dId=" + dId +
                ", department=" + department +
                '}';
    }
}