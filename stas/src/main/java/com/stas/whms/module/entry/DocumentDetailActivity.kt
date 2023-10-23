package com.stas.whms.module.entry

import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
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
import com.stas.whms.bean.InBoundAuditRequestInfo
import com.stas.whms.bean.ScannerRequestInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityDocumentDetailBinding
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_DOCUMENT_DETAIL)
class DocumentDetailActivity :
    BaseMvvmActivity<ActivityDocumentDetailBinding, BaseViewModel>() {
    private var mDataList = arrayListOf<GoodsInfo>()
    private var mTempDataList = arrayListOf<GoodsInfo>()

    @JvmField
    @Autowired
    var documentNo: String? = null
    override fun initView() {
        title = "单据明细"
        initDataTable()
        mDataBinding.cetDocumentNo.text = documentNo
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
        val req = InBoundAuditRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.DocNo = documentNo
        req.TextID = "4"
        showLoading()
        Thread {
            val result = StasHttpRequestUtil.queryInBoundAuditData(JSON.toJSONString(req))
            if (result?.errorCode == 200) {
                if (result.data != null) {
                    val jArray = JSONObject.parseArray(result.data, GoodsInfo::class.java)
                    mTempDataList = jArray as ArrayList<GoodsInfo>
                    mDataBinding.tableGoods.addData(jArray, true)
                    handleTotalNum()
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
        val totalSize = mTempDataList.size
        mDataBinding.cetTotalBoxNum.text = totalSize.toString()
        mDataBinding.cetTotalNum.text = getTotalNum()
    }

    private fun getTotalNum(): String {
        var totalCount = 0
        for (g in mTempDataList) {
            totalCount += if (g.Qty == null) 0 else g.Qty?.toInt()!!
        }
        return totalCount.toString()
    }

    private fun initDataTable() {
        //region 声明表格列
        val coId = Column<String>("序号", "idNum")
        coId.isFixed = true
        coId.isAutoCount = true
        //一致是因为需要用字段名来解析List对象
        val coPartsNo = Column<String>("电装品番", "PartsNo")
        val coTagSerialNo = Column<String>("回转号", "TagSerialNo")
        val coBoxSum = Column<String>("包装数", "Qty")
        val coFromProCode = Column<String>("前工程", "FromProCode")
        val coCollectionTime = Column<String>("入库时间", "CreateDT")
        val coCollectionPeople = Column<String>("采集人", "CreateBy")
        //endregion
        mDataBinding.tableGoods.setZoom(false, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableGoods.config.isShowXSequence = false //去掉表格顶部字母
        mDataBinding.tableGoods.config.isShowYSequence = false //去掉左侧数字
        mDataBinding.tableGoods.config.isShowTableTitle = false // 去掉表头

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