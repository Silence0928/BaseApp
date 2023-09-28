package com.stas.whms.module.entry

import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat
import com.bin.david.form.data.table.TableData
import com.hjq.toast.ToastUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.utils.AndroidUtil
import com.lib_common.utils.DateUtils
import com.stas.whms.R
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.ScannerRequestInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityDocumentDetailBinding
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_DOCUMENT_DETAIL)
class DocumentDetailActivity :
    BaseMvvmActivity<ActivityDocumentDetailBinding, BaseViewModel>() {
    private var mDataList = arrayListOf<GoodsInfo>()

    @JvmField
    @Autowired
    var documentNo : String? = null
    override fun initView() {
        title = "单据明细"
        initDataTable()
        getData()
    }

    override fun onViewEvent() {
        // 取消
        mDataBinding.stvClose.setOnClickListener {
            onBackPressed()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_document_detail
    }

    override fun getVariableId(): Int {
        return 0
    }

    private fun getData() {
        if (TextUtils.isEmpty(documentNo)) return
        var req = ScannerRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.QrCode =
            "DISC5060020000010091000210125104151120712305152071530815408155092132140074     CW298000-03524C0000004P100 1032507 00000000"
        Thread {
            val result = StasHttpRequestUtil.queryScannerResult(JSON.toJSONString(req))
            if (result?.errorCode == 200) {
                if (result.obj != null) {
                    val goodsInfo =
                        JSON.parseObject(result.obj.toString(), GoodsInfo::class.java)
                    if (isCanSave(goodsInfo)) {
                        goodsInfo.idNum = mDataList.size + 1
                        val tempList = arrayListOf<GoodsInfo>()
                        tempList.add(goodsInfo)
                        mDataBinding.tableGoods.addData(tempList, true)
                        handleTotalNum()
                    }
                }
            } else {
                ToastUtils.show(result?.reason)
            }
        }.start()
    }

    private fun isCanSave(goods: GoodsInfo?): Boolean {
        if (mDataList.size == 0) return true
        if (goods == null) return false
        var canSave = true
        for (i in mDataList) {
            if (i.TagSerialNo == goods?.TagSerialNo) {
                canSave = false
                break
            }
        }
        return canSave
    }

    private fun handleTotalNum() {
        val totalSize = mDataList.size + 1
        mDataBinding.cetTotalBoxNum.text = totalSize.toString()
        mDataBinding.cetTotalNum.text = getTotalNum()
    }

    private fun getTotalNum(): String {
        var totalCount = 0
        for (g in mDataList) {
            totalCount += if (g.BoxSum == null) 0 else g.BoxSum?.toInt()!!
        }
        return totalCount.toString()
    }

    private fun initDataTable() {
        //region 声明表格列
        val coId = Column<String>("序号", "idNum")
        coId.isFixed = true
        coId.isAutoCount = true
        //一致是因为需要用字段名来解析List对象
        val coPartsNo = Column<String>("品番", "PartsNo")
        val coTagSerialNo = Column<String>("回转号", "TagSerialNo")
        val coBoxSum = Column<String>("包装数", "BoxSum")
        val coFromProCode = Column<String>("前工程", "FromProCode")
        val coCollectionTime = Column<String>("入库时间", "del")
        val coCollectionPeople = Column<String>("采集人", "del")
        //endregion
        mDataBinding.tableGoods.setZoom(true, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableGoods.config.setShowXSequence(false) //去掉表格顶部字母
        mDataBinding.tableGoods.config.setShowYSequence(false) //去掉左侧数字
        mDataBinding.tableGoods.config.setShowTableTitle(false) // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<GoodsInfo> =
            TableData<GoodsInfo>(
                "商品信息",
                mDataList,
                coId,
                coPartsNo,
                coTagSerialNo,
                coBoxSum,
                coFromProCode,
                coCollectionTime,
                coCollectionPeople
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableGoods.setTableData(tableData)
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(
                            this@DocumentDetailActivity,
                            com.lib_src.R.color.green_trans_22
                        )
                    } else TableConfig.INVALID_COLOR
                }
            }
        mDataBinding.tableGoods.config.contentCellBackgroundFormat = backgroundFormat
    }

}