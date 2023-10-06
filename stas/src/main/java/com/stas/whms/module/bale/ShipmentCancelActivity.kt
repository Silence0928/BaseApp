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
import com.stas.whms.bean.CustomerInfo
import com.stas.whms.bean.ReasonInfo
import com.stas.whms.bean.SaveShipmentPrepareReqInfo
import com.stas.whms.bean.ScannerRequestInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityShipmentCancelBinding
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_SHIPMENT_CANCEL)
class ShipmentCancelActivity : BaseMvvmActivity<ActivityShipmentCancelBinding, BaseViewModel>() {

    private val REQ_SCANNER_GET = 1
    private val REQ_SCANNER_GET_2 = 2
    private val REQ_SCANNER_SAVE = 3
    private var mDataList = arrayListOf<CustomerInfo>()
    private var mTempDataList = arrayListOf<CustomerInfo>()
    private var mReasonDataList = arrayListOf<ReasonInfo>()
    private var mReasonStrList = arrayListOf<String?>()

    override fun initView() {
        title = "出货取消"
        initDataTable()
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

    private fun getData(result: String, type: Int) {
        if (TextUtils.isEmpty(result)) return
        val req = ScannerRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.TextID = if (type == REQ_SCANNER_GET) "1" else "2"
        req.QrCode =
            if (type == REQ_SCANNER_GET) "DISC5060020000010091000210125104151120712305152071530815408155092123810-E0150                095440-12800J0000002Z999 0070380        00000000         "
            else null
        Thread {
            val response = StasHttpRequestUtil.queryShipmentCancelDataResult(JSON.toJSONString(req))
            handleWebServiceResult(response, REQ_SCANNER_GET)
        }.start()
    }

    private fun getCheckedData(): List<CustomerInfo> {
        val listData = arrayListOf<CustomerInfo>()
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
        Thread {
//            val outPlanList = arrayListOf<ShipmentInfo>()
//            outPlanList.add(mShipmentIntroduction!!)
            val req = SaveShipmentPrepareReqInfo()
            req.Remark = mDataBinding.cetRemark.text.toString().trim()
            req.CustemerReceipt = mDataBinding.cetRefundInstruction.text.toString()
//            req.OutPlanList = outPlanList
            req.CustomLabelList = mTempDataList
            val result = StasHttpRequestUtil.saveShipmentCancelData(JSON.toJSONString(req))
            handleWebServiceResult(result, REQ_SCANNER_SAVE)
        }.start()
    }

    override fun handleWebServiceSuccess(response: WebServiceResponse?, fromSource: Int) {
        if (fromSource == REQ_SCANNER_GET) {
            if (response?.data != null) {
                val obj3 = JSONObject.parseArray(response.data, CustomerInfo::class.java)
                if (obj3 != null) {
                    mTempDataList = obj3 as ArrayList<CustomerInfo>
                    mDataBinding.tableShipmentCancel.addData(obj3, true)
                }
            }
        } else if (fromSource == REQ_SCANNER_GET_2) {
            if (response?.data != null) {
                val jArray = JSONObject.parseArray(response.data, ReasonInfo::class.java)
                if (jArray != null && jArray.size > 0) {
                    mReasonDataList = jArray as ArrayList<ReasonInfo>
                    for (a in mReasonDataList) {
                        mReasonStrList.add(a.ReasonName)
                    }
                    mDataBinding.cetRefundReason.text = mReasonDataList[0].ReasonName
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
        val coCustomLabel = Column<String>("客户编号", "CustomLabel")
        val coNum = Column<String>("数量", "Num")
        val coBoxSum = Column<String>("箱数", "BoxSum")
        val coSingleBoxNum = Column<String>("单箱数量", "SingleBoxNum")
        //endregion
        mDataBinding.tableShipmentCancel.setZoom(true, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableShipmentCancel.config.setShowXSequence(false) //去掉表格顶部字母
        mDataBinding.tableShipmentCancel.config.setShowYSequence(false) //去掉左侧数字
        mDataBinding.tableShipmentCancel.config.setShowTableTitle(false) // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<CustomerInfo> =
            TableData<CustomerInfo>(
                "出货信息",
                mDataList,
                coChecked,
                coId,
                coPartsNo,
                coCustomLabel,
                coNum,
                coBoxSum,
                coSingleBoxNum
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
                .setPositiveButton("确认") { finish() }.show()
        } else {
            finish()
        }
    }
}