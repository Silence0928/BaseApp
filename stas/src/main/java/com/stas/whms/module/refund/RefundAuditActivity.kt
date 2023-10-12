package com.stas.whms.module.refund

import android.text.Editable
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
import com.stas.whms.bean.ReasonInfo
import com.stas.whms.bean.SaveInBoundAuditReqInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityRefundAuditBinding
import com.stas.whms.utils.RouteJumpUtil
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_REFUND_AUDIT)
class RefundAuditActivity : BaseMvvmActivity<ActivityRefundAuditBinding, BaseViewModel>() {
    private val REQ_IN_BOUND_NO_GET = 1001 // 查询退库单号
    private val REQ_IN_BOUND_GET_END = 1002 // 查询制造完了标签数据
    private val REQ_IN_BOUND_REASON_GET = 1003 // 查询退库原因
    private val REQ_IN_BOUND_GET = 1004 // 查询退库数据
    private val REQ_IN_BOUND_SAVE = 1005 // 保存
    private var mOrderNoList = arrayListOf<String>()
    private var mDataList = arrayListOf<GoodsInfo>()
    private var mTempDataList = arrayListOf<GoodsInfo>()
    private var mReasonInfoList = arrayListOf<ReasonInfo>()
    private var mReasonList = arrayListOf<String>()
    private var mProductEnd: GoodsInfo? = null

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
                val madeFinishedTag = mDataBinding.cetMadeFinishedTag.text.toString()
                if (orderNoStr.isEmpty()) {
                    ToastUtils.show("请选择退库单号")
                    return@setOnClickListener
                }
                if (madeFinishedTag.isEmpty()) {
                    ToastUtils.show("请扫描制造完了标签")
                    return@setOnClickListener
                }
                getData(null, REQ_IN_BOUND_GET)
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
                    val jArray = JSONObject.parseArray(response.data, GoodsInfo::class.java)
                    var i = 1
                    for (a in jArray) {
                        a.idNum = i
                        i++
                    }
                    mTempDataList = jArray as ArrayList<GoodsInfo>
                    mDataBinding.tableRefundCollection.addData(jArray, true)
                    handleTotalNum()
                }
            }
        } else {
            ToastUtils.show("保存成功")
            finish()
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
        val coTagSerialNo = Column<String>("回转号", "TagSerialNo")
        val coBoxSum = Column<String>("包装数", "BoxSum")
        val coFromProCode = Column<String>("前工程", "FromProCode")
        val coCreateBy = Column<String>("采集人", "CreateBy")
        val coCreateDT = Column<String>("入库日期", "CreateDT")
        val coDel = Column<String>("操作", "del")
        //endregion
        mDataBinding.tableRefundCollection.setZoom(false, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableRefundCollection.config.setShowXSequence(false) //去掉表格顶部字母
        mDataBinding.tableRefundCollection.config.setShowYSequence(false) //去掉左侧数字
        mDataBinding.tableRefundCollection.config.setShowTableTitle(false) // 去掉表头

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
                coCreateBy,
                coCreateDT,
                coDel
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableRefundCollection.setTableData(tableData)
        mDataBinding.tableRefundCollection.tableData
            .setOnRowClickListener { column, o, col, row ->
                if (col == 7) {
                    CommonAlertDialog(this).builder().setTitle("提示")
                        .setMsg("是否确认删除？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认") {
                            mDataList.removeAt(row)
                            mTempDataList.removeAt(row)
                            var i = 1
                            for (info in mDataList) {
                                info.idNum = i
                                i++
                            }
                            mDataBinding.tableRefundCollection.notifyDataChanged()
                            handleTotalNum()
                        }.show()
                } else {
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