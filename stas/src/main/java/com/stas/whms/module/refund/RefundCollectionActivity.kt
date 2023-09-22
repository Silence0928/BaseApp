package com.stas.whms.module.refund

import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat
import com.bin.david.form.data.table.TableData
import com.bin.david.form.data.table.TableData.OnRowClickListener
import com.hjq.toast.ToastUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.stas.whms.R
import com.stas.whms.bean.UserInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityRefundCollectionBinding

@Route(path = RoutePathConfig.ROUTE_REFUND_COLLECTION)
class RefundCollectionActivity : BaseMvvmActivity<ActivityRefundCollectionBinding, BaseViewModel>() {

    override fun initView() {
        title = "退库采集"
        handleBindData()
    }

    override fun onViewEvent() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_refund_collection
    }

    override fun getVariableId(): Int {
        return 0
    }

    /**
     * 手动绑定数据
     */
    private fun handleBindData() {
        //region 给User对象添加数据
        val userList = arrayListOf<UserInfo>()
        userList.add(UserInfo(1, "Lisa", 26, "18800000000", "男"))
        userList.add(UserInfo(2, "Nana", 25, "18800000001", "男"))
        userList.add(UserInfo(3, "Mia", 24, "18800000002", "女"))
        userList.add(UserInfo(4, "Lucy", 22, "18800000003", "女"))
        userList.add(UserInfo(5, "Jack", 27, "18800000004", "女"))
        userList.add(UserInfo(6, "Jack", 27, "18800000004", "女"))
        userList.add(UserInfo(7, "Jack", 27, "18800000004", "女"))
        userList.add(UserInfo(8, "Jack", 27, "18800000004", "女"))
        userList.add(UserInfo(9, "Jack", 27, "18800000004", "女"))
        userList.add(UserInfo(10, "Jack", 27, "18800000004", "女"))
        userList.add(UserInfo(11, "Jack", 27, "18800000004", "女"))
        userList.add(UserInfo(12, "Jack", 8, "18800000004", "女"))
        userList.add(UserInfo(13, "Jack", 9, "18800000004", "女"))
        userList.add(UserInfo(14, "Jack", 26, "18800000004", "女"))
        userList.add(UserInfo(15, "Jack", 27, "18800000004", "女"))
        //endregion

        //region 声明表格列
        val coDel = Column<String>("操作", "del")
        val coId = Column<String>("序号", "Id") //注意，这里的“Id”要和User中字段名一致
        coId.isFixed = true
        coId.isAutoCount = true
        //一致是因为需要用字段名来解析List对象
        val coName = Column<String>("品番", "Name")
        val coAge = Column<String>("回转号", "Age")
        val coPhone = Column<String>("包装数", "Phone")
        val coSex = Column<String>("前工程", "sexy")
        val coTime = Column<String>("采集时间", "sexy")
        //endregion
        mDataBinding.tableRefundCollection.setZoom(true, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableRefundCollection.config.setShowXSequence(false) //去掉表格顶部字母
        mDataBinding.tableRefundCollection.config.setShowYSequence(false) //去掉左侧数字
        mDataBinding.tableRefundCollection.config.setShowTableTitle(false) // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<UserInfo> =
            TableData<UserInfo>("用户信息", userList, coId, coName, coAge, coPhone, coSex, coTime, coDel)
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableRefundCollection.setTableData(tableData)
        mDataBinding.tableRefundCollection.tableData
            .setOnRowClickListener(OnRowClickListener<Any?> { column, o, col, row ->
                if (col == 5) {
                    ToastUtils.show("删除行----" + (row + 1))
                    userList.removeAt(row)
                    var i = 1;
                    for (info in userList) {
                        info.id = i
                        i ++
                    }
                    mDataBinding.tableRefundCollection.notifyDataChanged()
                }
            })
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(this@RefundCollectionActivity, com.lib_src.R.color.green_trans_22)
                    } else TableConfig.INVALID_COLOR
                }

                override fun getTextColor(t: CellInfo<*>?): Int {
                    return if (t?.col == 5) ContextCompat.getColor(this@RefundCollectionActivity, com.lib_src.R.color.blue11) else
                        ContextCompat.getColor(this@RefundCollectionActivity, com.lib_src.R.color.black04)
                }
            }
        mDataBinding.tableRefundCollection.config.contentCellBackgroundFormat = backgroundFormat
    }
}