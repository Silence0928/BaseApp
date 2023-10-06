package com.stas.whms.module.entry

import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat
import com.bin.david.form.data.table.TableData
import com.hjq.toast.ToastUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.entity.ScanResult
import com.lib_common.utils.AndroidUtil
import com.lib_common.utils.DateUtils
import com.lib_common.webservice.response.WebServiceResponse
import com.stas.whms.R
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.ScannerRequestInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityQueryInLibraryBinding
import com.stas.whms.utils.RouteJumpUtil
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_QUERY_LIBRARY)
class QueryLibraryActivity : BaseMvvmActivity<ActivityQueryInLibraryBinding, BaseViewModel>() {

    private val REQ_SCANNER_GET = 1
    private val REQ_SCANNER_GET_2 = 2
    private val REQ_SCANNER_SAVE = 3
    private var mDataList = arrayListOf<GoodsInfo>()
    private var mTempDataList = arrayListOf<GoodsInfo>()

    override fun initView() {
        title = "在库查询"
        initDataTable()
    }

    override fun onViewEvent() {
        // 查询
        mDataBinding.stvQueryStorageCollection.setOnClickListener {
            if (!isFastClick()) {
                getData(REQ_SCANNER_GET_2)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_query_in_library
    }

    override fun getVariableId(): Int {
        return 0
    }

    override fun isRegisterScan(): Boolean {
        return true
    }

    override fun scanResultCallBack(result: ScanResult?) {
        mDataBinding.cetMadeFinishedTag.setText(result?.data)
        getData(REQ_SCANNER_GET)
    }

    /**
     * type=1 查询单号  =2查询入库数据
     */
    private fun getData(type: Int) {
        val req = ScannerRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.DocNo = mDataBinding.cetForeworkNumber.text.toString()
        req.FromProCode = mDataBinding.cetRotaryDesignation.text.toString()
        req.TextID = if (type == REQ_SCANNER_GET) "1" else "3"
        req.QrCode =
            if (type == REQ_SCANNER_GET) "DISC5060020000010091000210125104151120712305152071530815408155092123810-E0150                095440-12800J0000002Z999 0070380        00000000         "
            else null
        showLoading()
        Thread {
            val result = StasHttpRequestUtil.queryLibrariesData(JSON.toJSONString(req))
            handleWebServiceResult(result, type)
        }.start()
    }


    override fun handleWebServiceSuccess(response: WebServiceResponse?, fromSource: Int) {
        if (fromSource == REQ_SCANNER_GET) {
            if (response?.data != null) {
                val jArray = JSONObject.parseArray(response.data, GoodsInfo::class.java)
                var i = 1
                for (a in jArray) {
                    a.idNum = i
                    i++
                }
                mTempDataList = jArray as ArrayList<GoodsInfo>
                mDataBinding.tableGoods.addData(jArray, true)
                handleTotalNum()
            }
        }
    }

    private fun handleTotalNum() {
        val totalSize = mTempDataList.size
        mDataBinding.cetTotalBoxNum.text = totalSize.toString()
        mDataBinding.cetTotalNum.text = getTotalNum()
    }

    private fun getTotalNum(): String {
        var totalCount = 0
        for (g in mTempDataList) {
            totalCount += if (g.BoxSum == null) 0 else g.BoxSum?.toInt()!!
        }
        return totalCount.toString()
    }

    private fun initDataTable() {
        //region 声明表格列
        val coId = Column<String>("序号", "idNum")
        coId.isFixed = true
        coId.isAutoCount = true
        val coPartsNo = Column<String>("品番", "PartsNo")
        val coDocNo = Column<String>("回转号", "TagSerialNo")
        val coState = Column<String>("状态", "Status")
        val coBoxSum = Column<String>("包装数", "BoxSum")
        val coFromProCode = Column<String>("前工程", "FromProCode")
        val coCreateBy = Column<String>("采集人", "CreateBy")
        val coCreateDT = Column<String>("入库日期", "CreateDT")
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
                coDocNo,
                coState,
                coBoxSum,
                coFromProCode,
                coCreateBy,
                coCreateDT
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableGoods.setTableData(tableData)
        mDataBinding.tableGoods.tableData
            .setOnRowClickListener { column, o, col, row ->
                RouteJumpUtil.jumpToDocumentDetail(mDataList[row].DocNo)
            }
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(
                            this@QueryLibraryActivity,
                            com.lib_src.R.color.green_trans_22
                        )
                    } else TableConfig.INVALID_COLOR
                }

            }
        mDataBinding.tableGoods.config.contentCellBackgroundFormat = backgroundFormat

    }
}