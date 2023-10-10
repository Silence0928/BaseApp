package com.app.whms;

import androidx.core.content.ContextCompat;

import com.app.R;
import com.app.bean.UserInfo;
import com.app.databinding.ActivityTablayoutDemoBinding;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.column.ColumnInfo;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnClickListener;
import com.hjq.toast.ToastUtils;
import com.lib_common.base.mvvm.BaseMvvmActivity;
import com.lib_common.base.mvvm.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class TableDemoActivity extends BaseMvvmActivity<ActivityTablayoutDemoBinding, BaseViewModel> {
    @Override
    protected void onViewEvent() {

    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("数据表格");
        autoBindData();
        handleBindData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tablayout_demo;
    }

    @Override
    protected int getVariableId() {
        return 0;
    }

    /**
     * 注解自动绑定
     */
    private void autoBindData() {
        List<UserInfo> userList = new ArrayList<>();
        userList.add(new UserInfo(0,"Lisa",26,"260", "男"));
        userList.add(new UserInfo(1,"Nana",25,"250", "男"));
        userList.add(new UserInfo(2,"Mia",24,"240", "女"));
        mDataBinding.autoTable.setData(userList);
    }

    /**
     * 手动绑定数据
     */
    private void handleBindData() {
        //region 给User对象添加数据
        List<UserInfo> userList = new ArrayList<>();
        userList.add(new UserInfo(1,"Lisa",26,"18800000000", "男"));
        userList.add(new UserInfo(2,"Nana",25,"18800000001", "男"));
        userList.add(new UserInfo(3,"Mia",24,"18800000002", "女"));
        userList.add(new UserInfo(4,"Lucy",22,"18800000003", "女"));
        userList.add(new UserInfo(5,"Jack",27,"18800000004", "女"));
        //endregion

        //region 声明表格列
        Column<String> coDel = new Column<>("操作", "del");
        coDel.setFixed(true);
        Column<String> coId = new Column<>("编号", "Id");     //注意，这里的“Id”要和User中字段名一致
        coId.setFixed(true);
        //一致是因为需要用字段名来解析List对象
        Column<String> coName = new Column<>("姓名", "Name");
        Column<String> coAge = new Column<>("年龄", "Age");
        Column<String> coPhone = new Column<>("手机号", "Phone");
        Column<String> coSex = new Column<>("性别", "sexy");
        //endregion

        mDataBinding.table.setZoom(false,1,0.5f);                     //开启缩放功能
        mDataBinding.table.getConfig().setShowXSequence(false); //去掉表格顶部字母
        mDataBinding.table.getConfig().setShowYSequence(false); //去掉左侧数字
        mDataBinding.table.getConfig().setShowTableTitle(false); // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        TableData<UserInfo> tableData = new TableData<>("用户信息", userList, coId,coName,coAge,coPhone, coSex, coDel);
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.table.setTableData(tableData);
        mDataBinding.table.getTableData().setOnRowClickListener(new TableData.OnRowClickListener() {
            @Override
            public void onClick(Column column, Object o, int col, int row) {
                if (col == 5) {
                    ToastUtils.show("删除行----" + (row + 1));
                }
            }
        });
        mDataBinding.table.getConfig().setColumnCellBackgroundFormat(new BaseCellBackgroundFormat<Column>() {
            @Override
            public int getBackGroundColor(Column column) {
                return ContextCompat.getColor(TableDemoActivity.this, column.getId() == 0 ? com.luck.picture.lib.R.color.ps_color_blue : R.color.white);
            }

            @Override
            public int getTextColor(Column column) {
                return ContextCompat.getColor(TableDemoActivity.this, column.getId() == 0 ? R.color.white : R.color.black);
            }
        });
    }
}
