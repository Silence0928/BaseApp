package com.stas.whms.module

import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.BaseBackgroundFormat
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat
import com.bin.david.form.data.format.grid.BaseAbstractGridFormat
import com.bin.david.form.data.format.grid.IGridFormat
import com.bin.david.form.data.format.grid.SimpleGridFormat
import com.bin.david.form.data.style.FontStyle
import com.bin.david.form.data.table.TableData
import com.bin.david.form.data.table.TableData.OnRowClickListener
import com.hjq.toast.ToastUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.stas.whms.R
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.UserInfo
import com.stas.whms.databinding.ActivityStorageCollectionBinding

class TestActivity : BaseMvvmActivity<ActivityStorageCollectionBinding, BaseViewModel>() {

    var mDataList = arrayListOf<UserInfo>()
    override fun initView() {
        title = "入库采集"
        handleBindData()
    }

    override fun onViewEvent() {
        // 保存
        mDataBinding.stvSaveStorageCollection.setOnClickListener {
            if (!isFastClick()) {
                var dataList = arrayListOf<UserInfo>()
                dataList.add(UserInfo(5, "Lucy", 22, "18800000003", "女"))
                mDataBinding.tableStorageCollection.addData(dataList, true)
            }
        }
        // 取消
        mDataBinding.stvCancelStorageCollection.setOnClickListener {
            clearData()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_storage_collection
    }

    override fun getVariableId(): Int {
        return 0
    }

    /**
     * 清除表格数据
     */
    private fun clearData() {
        ToastUtils.show("清除成功")
        mDataList.clear()
        mDataBinding.tableStorageCollection.notifyDataChanged()
    }

//    private fun test() {
//        var tempDataList = arrayListOf<GoodsInfo>()
//        var goods = GoodsInfo()
//        goods.PartsNo = "CW299500-32414B"
//        goods.Qty = "48"
//        goods.CreateDT = "2023/11/24 20:30:58"
//        goods.CreateBy = "0001001M"
//        goods.DocNo = "RE-20231124-002"
//        goods.idNum = 1
//        tempDataList.add(goods)
//        mTempDataList.addAll(tempDataList)
//        // 清除表格数据
//        mDataList.addAll(tempDataList)
//        if (isFirstLoadData) {
//            isFirstLoadData = false
//            mDataBinding.tableRefundCollection.addData(mDataList, true)
//        } else {
//            mDataBinding.tableRefundCollection.notifyDataChanged()
//        }
//        handleTotalNum()
//    }
    /**
     * 手动绑定数据
     */
    private fun handleBindData() {
        //region 给User对象添加数据
//        mDataList.add(UserInfo(1, "Lisa", 26, "18800000000", "男"))
//        mDataList.add(UserInfo(2, "Nana", 25, "18800000001", "男"))
//        mDataList.add(UserInfo(3, "Mia", 24, "18800000002", "女"))
//        mDataList.add(UserInfo(4, "Lucy", 22, "18800000003", "女"))
//        mDataList.add(UserInfo(5, "Jack", 27, "18800000004", "女"))
//        mDataList.add(UserInfo(6, "Jack", 27, "18800000004", "女"))
//        mDataList.add(UserInfo(7, "Jack", 27, "18800000004", "女"))
//        mDataList.add(UserInfo(8, "Jack", 27, "18800000004", "女"))
//        mDataList.add(UserInfo(9, "Jack", 27, "18800000004", "女"))
//        mDataList.add(UserInfo(10, "Jack", 27, "18800000004", "女"))
//        mDataList.add(UserInfo(11, "Jack", 27, "18800000004", "女"))
//        mDataList.add(UserInfo(12, "Jack", 8, "18800000004", "女"))
//        mDataList.add(UserInfo(13, "Jack", 9, "18800000004", "女"))
//        mDataList.add(UserInfo(14, "Jack", 26, "18800000004", "女"))
//        mDataList.add(UserInfo(15, "Jack", 27, "18800000004", "女"))
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
        //endregion
        mDataBinding.tableStorageCollection.setZoom(true, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableStorageCollection.config.isShowXSequence = false //去掉表格顶部字母
        mDataBinding.tableStorageCollection.config.isShowYSequence = false //去掉左侧数字
        mDataBinding.tableStorageCollection.config.isShowTableTitle = false // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<UserInfo> =
            TableData<UserInfo>("用户信息", mDataList, coId, coName, coAge, coPhone, coSex, coDel)
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableStorageCollection.setTableData(tableData)
        mDataBinding.tableStorageCollection.tableData
            .setOnRowClickListener(OnRowClickListener<Any?> { column, o, col, row ->
                if (col == 5) {
                    ToastUtils.show("删除行----" + (row + 1))
                    mDataList.removeAt(row)
                    var i = 1;
                    for (info in mDataList) {
                        info.id = i
                        i ++
                    }
                    mDataBinding.tableStorageCollection.notifyDataChanged()
                }
            })
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return /*if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(this@TestActivity, com.lib_src.R.color.green_trans_22)
                    } else */ContextCompat.getColor(this@TestActivity, com.lib_src.R.color.white)
                }

                override fun getTextColor(t: CellInfo<*>?): Int {
                    return if (t?.col == 5) ContextCompat.getColor(this@TestActivity, com.lib_src.R.color.blue11) else
                        ContextCompat.getColor(this@TestActivity, com.lib_src.R.color.black04)
                }
            }
        mDataBinding.tableStorageCollection.config.contentCellBackgroundFormat = backgroundFormat
        // 设置列标题背景颜色
        mDataBinding.tableStorageCollection.config.columnTitleBackground =
            BaseBackgroundFormat(ContextCompat.getColor(this@TestActivity, com.lib_src.R.color.main_color))
        // 设置列标题字体颜色
        mDataBinding.tableStorageCollection.config.columnTitleStyle = FontStyle(18, ContextCompat.getColor(this@TestActivity, com.lib_src.R.color.white))
        // 清除表格左右、底部边框线
        mDataBinding.tableStorageCollection.config.tableGridFormat = object: SimpleGridFormat(){
            override fun drawTableBorderGrid(
                canvas: Canvas?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                paint: Paint?
            ) {
                super.drawTableBorderGrid(canvas, 0, top, 0, 0, paint)
            }
        }
    }
}