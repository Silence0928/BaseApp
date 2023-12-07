package com.stas.whms.module.refund

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat
import com.bin.david.form.data.format.grid.SimpleGridFormat
import com.bin.david.form.data.table.TableData
import com.hjq.toast.ToastUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.constants.MmkvConstants
import com.lib_common.dialog.BottomListDialog
import com.lib_common.entity.ScanResult
import com.lib_common.listener.SimpleTextWatcher
import com.lib_common.utils.AndroidUtil
import com.lib_common.utils.DateUtils
import com.lib_common.view.layout.dialog.CommonAlertDialog
import com.lib_common.webservice.response.WebServiceResponse
import com.stas.whms.R
import com.stas.whms.bean.DocInfo
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.InBoundAuditRequestInfo
import com.stas.whms.bean.LoginInfo
import com.stas.whms.bean.ReasonInfo
import com.stas.whms.bean.SaveInBoundAuditReqInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityRefundAuditBinding
import com.stas.whms.utils.RouteJumpUtil
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_REFUND_AUDIT)
class RefundAuditActivity : BaseMvvmActivity<ActivityRefundAuditBinding, BaseViewModel>() {
    private val REQ_IN_BOUND_NO_GET = 1 // 查询退库单号
    private val REQ_IN_BOUND_GET_END = 2 // 查询制造完了标签数据
    private val REQ_IN_BOUND_REASON_GET = 4 // 查询退库原因
    private val REQ_IN_BOUND_GET = 3 // 查询退库数据
    private val REQ_IN_BOUND_SAVE = 5 // 保存
    private var mOrderNoList = arrayListOf<String>()
    private var mDataList = arrayListOf<GoodsInfo>()
    private var mTempDataList = arrayListOf<GoodsInfo>()
    private var mReasonInfoList = arrayListOf<ReasonInfo>()
    private var mReasonList = arrayListOf<String>()
    private var mProductEnd: GoodsInfo? = null
    private var isFirstLoadData = true

    override fun initView() {
        title = "退库审核"
        initDataTable()
        getData(null, REQ_IN_BOUND_NO_GET)
        getData(null, REQ_IN_BOUND_REASON_GET)
    }

    override fun onViewEvent() {
        // 退库单号
        mDataBinding.cetRefundOrderNo.setOnClickListener {
            if (mOrderNoList.isEmpty()) {
                ToastUtils.show("没有待审核的退库单号")
                return@setOnClickListener
            }
            BottomListDialog(this).setItems(mOrderNoList)
                .setOnConfirmSelectListener { position: Int, name: String? ->
                    mDataBinding.cetRefundOrderNo.text = name
                }
                .setCurrentItem(if (mOrderNoList.size > 0) mOrderNoList.indexOf(mDataBinding.cetRefundOrderNo.text.toString()) else 0)
                .show()
        }
        // 退库原因
        mDataBinding.cetRefundReason.setOnClickListener {
            if (mReasonList.size == 0) {
                ToastUtils.show("无可选择的退库原因")
                return@setOnClickListener
            }
            BottomListDialog(this).setItems(mReasonList)
                .setOnConfirmSelectListener { position: Int, name: String? ->
                    mDataBinding.cetRefundReason.text = name
                }
                .setCurrentItem(if (mReasonList.size > 0) mReasonList.indexOf(mDataBinding.cetRefundReason.text.toString()) else 0)
                .show()
        }
        // 查询
        mDataBinding.stvQueryRefundCollection.setOnClickListener {
            if (!isFastClick()) {
                val orderNoStr = mDataBinding.cetRefundOrderNo.text.toString()
                if (orderNoStr.isEmpty()) {
                    ToastUtils.show("请选择退库单号")
                    return@setOnClickListener
                }
                clearData()
                getData(null, REQ_IN_BOUND_GET)

                mDataBinding.cetMadeFinishedTag.setText("")
                mProductEnd = null
                mDataBinding.tableRefundCollection.notifyDataChanged()
            }
        }
        mDataBinding.cetRemark.addTextChangedListener (object: SimpleTextWatcher() {
            override fun afterTextChanged(editable: Editable?) {
                super.afterTextChanged(editable)
                "${editable?.length}/100".also { mDataBinding.tvLimit.text = it }
            }
        })
        // 保存
        mDataBinding.stvSaveRefundCollection.setOnClickListener {
            if (!isFastClick()) {
                saveData()
            }
        }
        // 取消
        mDataBinding.stvCancelRefundCollection.setOnClickListener {
            onBackPressed()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_refund_audit
    }

    override fun getVariableId(): Int {
        return 0
    }

    override fun isRegisterScan(): Boolean {
        return true
    }

    override fun scanResultCallBack(result: ScanResult?) {
        getData(result?.data, REQ_IN_BOUND_GET_END)
    }

    /**
     * type=1 查询单号  =2查询退库数据 =3查询原因
     */
    private fun getData(result: String?, type: Int) {
        if (type == REQ_IN_BOUND_GET_END && result?.isEmpty() == true) return
        val req = InBoundAuditRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.DocNo = mDataBinding.cetRefundOrderNo.text.toString()
        req.TextID = if (type == REQ_IN_BOUND_NO_GET) "1" else if (type == REQ_IN_BOUND_GET_END) "2"  else if (type == REQ_IN_BOUND_REASON_GET) "4" else "3"
        req.ProductEnd = mProductEnd
        req.QrCode = result
//        req.QrCode = if (type == REQ_IN_BOUND_GET_END)
//            "DISC5060020000010091000210125104151120712305152071530815408155092123810-E0150                095440-12800J0000002Z999 0070380        00000000         "
//        else null
        showLoading()
        Thread {
            val response = StasHttpRequestUtil.queryReturnAuditData(JSON.toJSONString(req))
            handleWebServiceResult(response, type)
        }.start()
    }


    override fun handleWebServiceSuccess(response: WebServiceResponse?, fromSource: Int) {
        if (fromSource == REQ_IN_BOUND_GET_END) {// 制造完了标签
            if (response?.obj != null) {
                mProductEnd =
                    JSON.parseObject(response.obj.toString(), GoodsInfo::class.java)
                mDataBinding.cetMadeFinishedTag.setText(mProductEnd?.PartsNo)
            }
        } else if (fromSource == REQ_IN_BOUND_GET || fromSource == REQ_IN_BOUND_NO_GET || fromSource == REQ_IN_BOUND_REASON_GET) {
            if (response?.data != null) {
                if (fromSource == REQ_IN_BOUND_NO_GET) {
                    val jArray = JSONObject.parseArray(response.data, DocInfo::class.java)
                    for (a in jArray) {
                        if (a is DocInfo && a.DocNo?.isNotEmpty() == true) {
                            mOrderNoList.add(a.DocNo!!)
                        }
                    }
//                    if (mOrderNoList.size > 0) {
//                        mDataBinding.cetRefundOrderNo.text = mOrderNoList[0]
//                    }
                } else if (fromSource == REQ_IN_BOUND_REASON_GET) {
                    val jArray = JSONObject.parseArray(response.data, ReasonInfo::class.java)
                    for (a in jArray) {
                        mReasonInfoList.add(a)
                        if (a is ReasonInfo && a.ReasonName?.isNotEmpty() == true) {
                            mReasonList.add(a.ReasonName!!)
                        }
                    }
                } else {
                    mDataBinding.cetMadeFinishedTag.setText("")
                    val jArray = JSONObject.parseArray(response.data, GoodsInfo::class.java)
                    var i = 1
                    for (a in jArray) {
                        a.idNum = i
                        i++
                    }
                    // 清除表格数据
                    mTempDataList.clear()
                    mTempDataList.addAll(jArray)
                    mDataList.clear()
                    mDataList.addAll(jArray)
                    if (isFirstLoadData) {
                        isFirstLoadData = false
                        mDataBinding.tableRefundCollection.addData(jArray, true)
                    } else {
                        mDataBinding.tableRefundCollection.notifyDataChanged()
                    }
                    handleTotalNum()
                }
            }
        } else {
            // 清除表格数据
            clearAllData()
            ToastUtils.show("保存成功")
        }
    }

    /**
     * 清除表格数据
     */
    private fun clearData() {
        mTempDataList.clear()
        mDataList.clear()
        mDataBinding.tableRefundCollection.notifyDataChanged()
        handleTotalNum()
    }

    /**
     * 清除所有数据
     */
    private fun clearAllData() {
        mDataBinding.cetMadeFinishedTag.setText("")
        mDataBinding.cetRefundOrderNo.text = ""
        mDataBinding.cetRefundReason.text = ""
        mDataBinding.cetRemark.setText("")
        mTempDataList.clear()
        mDataList.clear()
        mProductEnd = null
        mDataBinding.tableRefundCollection.notifyDataChanged()
        handleTotalNum()
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

    private fun saveData() {
        if (mDataList.size == 0) {
            ToastUtils.show("无待审核的退库单！")
            return
        }
        if (mDataBinding.cetRefundReason.text.toString().isEmpty()) {
            ToastUtils.show("请选择退库原因！")
            return
        }
        showLoading()
        Thread {
            val req = SaveInBoundAuditReqInfo()
            req.Remark = mDataBinding.cetRemark.text.toString().trim()
            req.ListData = mDataList
            req.ReasonID = getReasonID()
            req.PdaID = AndroidUtil.getIpAddress()
            req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
            val loginInfoStr = mMMKV.decodeString(MmkvConstants.MMKV_LOGIN_INFO)
            if (loginInfoStr != null) {
                val loginInfo = JSON.parseObject(loginInfoStr, LoginInfo::class.java)
                if (loginInfo != null) {
                    req.CreateBy = loginInfo.UserID
                }
            }
            val result = StasHttpRequestUtil.saveReturnAuditData(JSON.toJSONString(req))
            handleWebServiceResult(result, REQ_IN_BOUND_SAVE)
        }.start()
    }

    private fun getReasonID(): String {
        if (mReasonInfoList.size > 0) {
            val reason = mDataBinding.cetRefundReason.text.toString()
            for (r in mReasonInfoList) {
                if (reason == r.ReasonName) {
                    return r.ReasonID!!
                }
            }
        }
        return ""
    }

    private fun initDataTable() {
        //region 声明表格列
        val coId = Column<String>("序号", "idNum")
        coId.isFixed = true
        coId.isAutoCount = true
        //一致是因为需要用字段名来解析List对象
        val coPartsNo = Column<String>("品番", "PartsNo")
//        val coTagSerialNo = Column<String>("回转号", "TagSerialNo")
        val coBoxSum = Column<String>("包装数", "Qty")
//        val coFromProCode = Column<String>("前工程", "FromProCode")
        val coCreateBy = Column<String>("采集人", "CreateBy")
        val coCreateDT = Column<String>("入库日期", "CreateDT")
        val coDel = Column<String>("操作", "del")
        //endregion
        mDataBinding.tableRefundCollection.setZoom(false, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableRefundCollection.config.isShowXSequence = false //去掉表格顶部字母
        mDataBinding.tableRefundCollection.config.isShowYSequence = false //去掉左侧数字
        mDataBinding.tableRefundCollection.config.isShowTableTitle = false // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<GoodsInfo> =
            TableData<GoodsInfo>(
                "商品信息",
                mDataList,
                coId,
                coPartsNo,
                coBoxSum,
                coCreateBy,
                coCreateDT,
                coDel
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableRefundCollection.setTableData(tableData)
        mDataBinding.tableRefundCollection.tableData
            .setOnRowClickListener { column, o, col, row ->
                if (col == 5) {
                    CommonAlertDialog(this).builder().setTitle("提示")
                        .setMsg("是否确认删除？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认") {
                            if (mDataList.size == 0 || mTempDataList.size == 0) {
                                clearData()
                                return@setPositiveButton
                            }
                            mDataList.removeAt(row)
                            if (mTempDataList.size > 0) {
                                mTempDataList.removeAt(row)
                            }
                            if (mDataList.size > 0) {
                                var i = 1
                                for (info in mDataList) {
                                    info.idNum = i
                                    i++
                                }
                            }
                            mDataBinding.tableRefundCollection.notifyDataChanged()
                            handleTotalNum()
                        }.show()
                } else {
                    if (mDataList.size == 0) return@setOnRowClickListener
                    RouteJumpUtil.jumpToDocumentDetail(mDataList[row].DocNo)
                }
            }
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(
                            this@RefundAuditActivity,
                            com.lib_src.R.color.green_trans_22
                        )
                    } else TableConfig.INVALID_COLOR
                }

                override fun getTextColor(t: CellInfo<*>?): Int {
                    return if (t?.col == 5) ContextCompat.getColor(this@RefundAuditActivity, com.lib_src.R.color.blue11) else
                        ContextCompat.getColor(this@RefundAuditActivity, com.lib_src.R.color.black04)
                }

            }
        mDataBinding.tableRefundCollection.config.contentCellBackgroundFormat = backgroundFormat
        // 清除表格左右、底部边框线
        mDataBinding.tableRefundCollection.config.tableGridFormat = object: SimpleGridFormat(){
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

    override fun onBackPressed() {
        if (mDataList.size > 0) {
            CommonAlertDialog(this).builder().setTitle("提示")
                .setMsg("取消将清空已采集数据，是否确认？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认") { finish() }.show()
        } else {
            finish()
        }
    }
}