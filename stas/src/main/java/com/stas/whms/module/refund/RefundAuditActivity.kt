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
import com.bin.david.form.data.table.TableData.OnRowClickListener
import com.hjq.toast.ToastUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.constants.Constants
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
import com.stas.whms.bean.UserInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityRefundAuditBinding
import com.stas.whms.utils.RouteJumpUtil
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_REFUND_AUDIT)
class RefundAuditActivity : BaseMvvmActivity<ActivityRefundAuditBinding, BaseViewModel>() {
    private val REQ_IN_BOUND_GET = 1001 // 查询退库数据
    private val REQ_IN_BOUND_NO_GET = 1002 // 查询退库单号
    private val REQ_IN_BOUND_REASON_GET = 1003 // 查询退库原因
    private val REQ_IN_BOUND_SAVE = 1004 // 保存
    private var mOrderNoList = arrayListOf<String>()
    private var mDataList = arrayListOf<GoodsInfo>()
    private var mTempDataList = arrayListOf<GoodsInfo>()
    private var mReasonInfoList = arrayListOf<ReasonInfo>()
    private var mReasonList = arrayListOf<String>()

    override fun initView() {
        title = "退库审核"
        initDataTable()
        getData(REQ_IN_BOUND_NO_GET)
        getData(REQ_IN_BOUND_REASON_GET)
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
                    ToastUtils.show("请输入制造完了标签")
                    return@setOnClickListener
                }
                getData(REQ_IN_BOUND_GET)
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
            if (!isFinishing) {
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
        mDataBinding.cetMadeFinishedTag.setText(result?.data)
    }

    /**
     * type=1 查询单号  =2查询退库数据 =3查询原因
     */
    private fun getData(type: Int) {
        val req = InBoundAuditRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.DocNo = if (type == REQ_IN_BOUND_GET) mDataBinding.cetRefundOrderNo.text.toString() else null
        req.TextID = if (type == REQ_IN_BOUND_NO_GET) "1" else if (type == REQ_IN_BOUND_REASON_GET) "3" else "2"
        req.QrCode = if (type == REQ_IN_BOUND_GET)
            "DISC5060020000010091000210125104151120712305152071530815408155092123810-E0150                095440-12800J0000002Z999 0070380        00000000         "
        else null
        showLoading()
        Thread {
            val result = StasHttpRequestUtil.queryReturnAuditData(JSON.toJSONString(req))
            handleWebServiceResult(result, type)
        }.start()
    }


    override fun handleWebServiceSuccess(response: WebServiceResponse?, fromSource: Int) {
        if (fromSource == REQ_IN_BOUND_GET || fromSource == REQ_IN_BOUND_NO_GET || fromSource == REQ_IN_BOUND_REASON_GET) {
            if (response?.data != null) {
                if (fromSource == REQ_IN_BOUND_NO_GET) {
                    val jArray = JSONObject.parseArray(response.data, DocInfo::class.java)
                    for (a in jArray) {
                        if (a is DocInfo) {
                            mOrderNoList.add(a.DocNoName!!)
                        }
                    }
                    if (mOrderNoList.size > 0) {
                        mDataBinding.cetRefundOrderNo.text = mOrderNoList[0]
                    }
                } else if (fromSource == REQ_IN_BOUND_REASON_GET) {
                    val jArray = JSONObject.parseArray(response.data, ReasonInfo::class.java)
                    for (a in jArray) {
                        mReasonInfoList.add(a)
                        if (a is ReasonInfo) {
                            mReasonList.add(a.ReasonName!!)
                        }
                    }
                    if (mReasonList.size > 0) {
                        mDataBinding.cetRefundReason.text = mReasonList[0]
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
        Thread {
            val req = SaveInBoundAuditReqInfo()
            req.Remark = mDataBinding.cetRemark.text.toString().trim()
            req.Data = mDataList
            req.Reason = mDataBinding.cetRefundReason.text.toString()
            val result = StasHttpRequestUtil.saveReturnAuditData(JSON.toJSONString(req))
            handleWebServiceResult(result, REQ_IN_BOUND_SAVE)
        }.start()
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
        mDataBinding.tableRefundCollection.setZoom(true, 1.0f, 0.5f) //开启缩放功能
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
}