package com.app.bean;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

//@SmartTable表格注解 @SmartColumn字段注解
@SmartTable(name="用户信息")        //表格标题
public class UserInfo {
    private int Id;
    @SmartColumn(id =0,name = "姓名")     //id排序，值越小越靠前。name列名
    private String Name;
    @SmartColumn(id =1,name = "年龄")
    private int Age;
    @SmartColumn(id =2,name = "手机号")
    private String Phone;

    @SmartColumn(id =3,name = "性别")
    private String sexy;

    private String del = "删除";

    public UserInfo(int id,String name,int age,String phone,String sexy){
        this.Id = id;
        this.Name = name;
        this.Age = age;
        this.Phone = phone;
        this.sexy = sexy;
    }
}