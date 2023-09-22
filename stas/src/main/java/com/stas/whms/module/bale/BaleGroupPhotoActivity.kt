package com.stas.whms.module.bale

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
import com.stas.whms.databinding.ActivityBaleGroupPhotoBinding

@Route(path = RoutePathConfig.ROUTE_BALE_GROUP_PHOTO)
class BaleGroupPhotoActivity : BaseMvvmActivity<ActivityBaleGroupPhotoBinding, BaseViewModel>() {

    override fun initView() {
        title = "捆包照合"
        handleBindData()
    }

    override fun onViewEvent() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_bale_group_photo
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
        val coName = Column<String>("入库单号", "Name")
        val coAge = Column<String>("电装品番", "Age")
        val coPhone = Column<String>("单据总数", "Phone")
        val coSex = Column<String>("采集人", "sexy")
        val coDate = Column<String>("采集日期", "sexy")
        //endregion
        mDataBinding.tableBalePhoto.setZoom(true, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableBalePhoto.config.setShowXSequence(false) //去掉表格顶部字母
        mDataBinding.tableBalePhoto.config.setShowYSequence(false) //去掉左侧数字
        mDataBinding.tableBalePhoto.config.setShowTableTitle(false) // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<UserInfo> =
            TableData<UserInfo>("用户信息", userList, coId, coName, coAge, coPhone, coSex, coDate, coDel)
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableBalePhoto.setTableData(tableData)
        mDataBinding.tableBalePhoto.tableData
            .setOnRowClickListener(OnRowClickListener<Any?> { column, o, col, row ->
                if (col == 6) {
                    ToastUtils.show("删除行----" + (row + 1))
                    userList.removeAt(row)
                    var i = 1;
                    for (info in userList) {
                        info.id = i
                        i ++
                    }
                    mDataBinding.tableBalePhoto.notifyDataChanged()
                }
            })
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(this@BaleGroupPhotoActivity, com.lib_src.R.color.green_trans_22)
                    } else TableConfig.INVALID_COLOR
                }

                override fun getTextColor(t: CellInfo<*>?): Int {
                    return if (t?.col == 5) ContextCompat.getColor(this@BaleGroupPhotoActivity, com.lib_src.R.color.blue11) else
                        ContextCompat.getColor(this@BaleGroupPhotoActivity, com.lib_src.R.color.black04)
                }
            }
        mDataBinding.tableBalePhoto.config.contentCellBackgroundFormat = backgroundFormat
    }
}