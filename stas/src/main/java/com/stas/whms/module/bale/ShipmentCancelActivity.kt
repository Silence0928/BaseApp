package com.stas.whms.module.bale

import android.content.Context
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat
import com.bin.david.form.data.format.draw.ImageResDrawFormat
import com.bin.david.form.data.table.TableData
import com.bin.david.form.utils.DensityUtils
import com.hjq.toast.ToastUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.dialog.BottomListDialog
import com.lib_common.entity.ScanResult
import com.lib_common.utils.AndroidUtil
import com.lib_common.utils.DateUtils
import com.lib_common.view.layout.dialog.CommonAlertDialog
import com.lib_common.webservice.response.WebServiceResponse
import com.stas.whms.R
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.ReasonInfo
import com.stas.whms.bean.SaveShipmentPrepareReqInfo
import com.stas.whms.bean.ScannerRequestInfo
import com.stas.whms.bean.ShipmentInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityShipmentCancelBinding
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_SHIPMENT_CANCEL)
class ShipmentCancelActivity : BaseMvvmActivity<ActivityShipmentCancelBinding, BaseViewModel>() {

    private val REQ_SCANNER_GET = 1
    private val REQ_SCANNER_GET_2 = 2
    private val REQ_SCANNER_SAVE = 3
    private var mDataList = arrayListOf<ShipmentInfo>()
    private var mTempDataList = arrayListOf<ShipmentInfo>()
    private var mReasonDataList = arrayListOf<ReasonInfo>()
    private var mReasonStrList = arrayListOf<String>()

    override fun initView() {
        title = "出货取消"
        initDataTable()
        getData("11", REQ_SCANNER_GET)
        getData("11", REQ_SCANNER_GET_2)
    }

    override fun onViewEvent() {
        mDataBinding.cetRefundInstruction.setOnFocusChangeListener { view, b ->
            if (!b) {
//                getData(mDataBinding.cetMadeFinishedTag.text.toString().trim())
            }
        }
        // 出货取消原因
        mDataBinding.cetRefundReason.setOnClickListener {
            if (mReasonStrList.size == 0) {
                ToastUtils.show("无可选择的出货取消原因")
                return@setOnClickListener
            }
            BottomListDialog(this).setItems(mReasonStrList)
                .setOnConfirmSelectListener { position: Int, name: String? ->
                    mDataBinding.cetRefundReason.text = name
                }
                .setCurrentItem(if (mReasonStrList.size > 0) mReasonStrList.indexOf(mDataBinding.cetRefundReason.text.toString()) else 0)
                .show()
        }
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
        return R.layout.activity_shipment_cancel
    }

    override fun getVariableId(): Int {
        return 0
    }

    override fun isRegisterScan(): Boolean {
        return true
    }

    override fun scanResultCallBack(result: ScanResult?) {
        getData(result?.data!!, REQ_SCANNER_GET)
    }

    private fun getData(result: String?, type: Int) {
        if (type == REQ_SCANNER_GET && TextUtils.isEmpty(result)) return
        val req = ScannerRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.TextID = if (type == REQ_SCANNER_GET) "1" else "2"
        req.QrCode =
            if (type == REQ_SCANNER_GET) "08080181000160001511CW296100-32454B0001056CW299500-32414B0003840CW299500-32814B0000576"
            else null
        showLoading()
        Thread {
            val response = StasHttpRequestUtil.queryShipmentCancelDataResult(JSON.toJSONString(req))
            handleWebServiceResult(response, type)
        }.start()
    }

    private fun getCheckedData(): List<ShipmentInfo> {
        val listData = arrayListOf<ShipmentInfo>()
        if (mTempDataList.size > 0) {
            for (i in mTempDataList) {
                if (i.checked) {
                    listData.add(i)
                }
            }
        }
        return listData
    }

    private fun saveData() {
        val shipmentProInt = mDataBinding.cetRefundInstruction.text.toString()
        if (shipmentProInt.isEmpty()) {
            ToastUtils.show("请扫描出货指示书")
            return
        }
        val shipmentCancelReason = mDataBinding.cetRefundReason.text.toString()
        if (shipmentCancelReason.isEmpty()) {
            ToastUtils.show("请选择出货取消原因")
            return
        }
        if (getCheckedData().isEmpty()) {
            ToastUtils.show("请选择待保存的品番数据")
            return
        }
        showLoading()
        Thread {
//            val outPlanList = arrayListOf<ShipmentInfo>()
//            outPlanList.add(mShipmentIntroduction!!)
            val req = SaveShipmentPrepareReqInfo()
            req.Remark = mDataBinding.cetRemark.text.toString().trim()
            req.CustemerReceipt = mDataBinding.cetRefundInstruction.text.toString()
//            req.OutPlanList = outPlanList
            req.OutPlanList = mTempDataList
            val result = StasHttpRequestUtil.saveShipmentCancelData(JSON.toJSONString(req))
            handleWebServiceResult(result, REQ_SCANNER_SAVE)
        }.start()
    }

    override fun handleWebServiceSuccess(response: WebServiceResponse?, fromSource: Int) {
        if (fromSource == REQ_SCANNER_GET) {
            if (mTempDataList.size > 0) {
                mTempDataList.clear()
                mDataList.clear()
                mDataBinding.tableShipmentCancel.notifyDataChanged()
                mDataBinding.cetRefundInstruction.setText("")
            }
            if (response?.data != null) {
                val obj3 = JSONObject.parseArray(response.data, ShipmentInfo::class.java)
                if (obj3 != null && obj3.size > 0) {
                    mTempDataList = obj3 as ArrayList<ShipmentInfo>
                    var i = 1
                     for (t in obj3) {
                        t.idNum = i
                        i ++
                    }
                    mDataBinding.tableShipmentCancel.addData(obj3, false)
                    mDataBinding.cetRefundInstruction.setText(mTempDataList[0]?.PartsNo)
                }
            }
        } else if (fromSource == REQ_SCANNER_GET_2) {
            if (response?.data != null) {
                val jArray = JSONObject.parseArray(response.data, ReasonInfo::class.java)
                if (jArray != null && jArray.size > 0) {
                    mReasonDataList = jArray as ArrayList<ReasonInfo>
                    for (a in mReasonDataList) {
                        if (a.ReasonName?.isNotEmpty() == true) {
                            mReasonStrList.add(a.ReasonName!!)
                        }
                    }
                    mDataBinding.cetRefundReason.text = mReasonStrList[0]
                }
            }
        } else {
            ToastUtils.show("保存成功")
            finish()
        }
    }

    private fun initDataTable() {
        //region 声明表格列
        val coId = Column<String>("序号", "idNum")
        coId.isFixed = true
        coId.isAutoCount = true
        val size = DensityUtils.dp2px(this, 15f)
        val coChecked =
            Column<Boolean>("选择", "checked", object : ImageResDrawFormat<Boolean>(size, size) {
                override fun getContext(): Context {
                    return this@ShipmentCancelActivity
                }

                override fun getResourceID(t: Boolean, value: String?, position: Int): Int {
                    return if (t) com.lib_src.R.drawable.icon_checked else com.lib_src.R.drawable.icon_unchecked
                }
            })
        coChecked.isFixed = true
        val coPartsNo = Column<String>("电装品番", "PartsNo")
        val coCustomLabel = Column<String>("客户看板编号", "Customer")
        val coNum = Column<String>("数量", "Num")
        val coBoxSum = Column<String>("已采集数量", "ActualNum")
        //endregion
        mDataBinding.tableShipmentCancel.setZoom(true, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableShipmentCancel.config.setShowXSequence(false) //去掉表格顶部字母
        mDataBinding.tableShipmentCancel.config.setShowYSequence(false) //去掉左侧数字
        mDataBinding.tableShipmentCancel.config.setShowTableTitle(false) // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<ShipmentInfo> =
            TableData<ShipmentInfo>(
                "出货信息",
                mDataList,
                coChecked,
                coId,
                coPartsNo,
                coCustomLabel,
                coNum,
                coBoxSum
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableShipmentCancel.setTableData(tableData)
        mDataBinding.tableShipmentCancel.tableData
            .setOnRowClickListener { column, o, col, row ->
                if (col == 0) {
                    // 选择
                    mDataList[row].checked = !mDataList[row].checked
                    mDataBinding.tableShipmentCancel.notifyDataChanged()
                }
            }
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(
                            this@ShipmentCancelActivity,
                            com.lib_src.R.color.green_trans_22
                        )
                    } else TableConfig.INVALID_COLOR
                }

            }
        mDataBinding.tableShipmentCancel.config.contentCellBackgroundFormat = backgroundFormat

    }

    override fun onBackPressed() {
        if (mTempDataList.size > 0) {
            CommonAlertDialog(this).builder().setTitle("提示")
                .setMsg("取消将清空已采集数据，是否确认？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认") {
                    finish()
                }.show()
        } else {
            finish()
        }
    }

    private fun clearResource() {
    }
}