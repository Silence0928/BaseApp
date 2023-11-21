package com.stas.whms.module.bale

import android.text.Editable
import android.text.TextUtils
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
import com.lib_common.constants.MmkvConstants
import com.lib_common.entity.ScanResult
import com.lib_common.listener.SimpleTextWatcher
import com.lib_common.utils.AndroidUtil
import com.lib_common.utils.DateUtils
import com.lib_common.view.layout.dialog.CommonAlertDialog
import com.lib_common.webservice.response.WebServiceResponse
import com.stas.whms.R
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.LoginInfo
import com.stas.whms.bean.SaveShipmentPrepareReqInfo
import com.stas.whms.bean.ScannerRequestInfo
import com.stas.whms.bean.ShipmentInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityShipmentPrepareBinding
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_SHIPMENT_PREPARE)
class ShipmentPrepareActivity : BaseMvvmActivity<ActivityShipmentPrepareBinding, BaseViewModel>() {
    private val REQ_SCANNER_GET = 1
    private val REQ_SCANNER_GET_2 = 2
    private val REQ_SCANNER_GET_3 = 3
    private val REQ_SCANNER_SAVE = 4
    private var mDataList = arrayListOf<ShipmentInfo>()
    private var mCustomerDataList = arrayListOf<GoodsInfo>()
    private var mTempDataList = arrayListOf<GoodsInfo>()

    override fun initView() {
        title = "出货准备"
        initDataTable()
        initCustomerDataTable()
    }

    override fun onViewEvent() {
        // 出货指示书
        mDataBinding.cetStorageDate.setOnFocusChangeListener { view, b ->
            if (!b) {
                getData(mDataBinding.cetStorageDate.text.toString().trim(), REQ_SCANNER_GET)
            }
        }
        // 客户受领书
        mDataBinding.cetCustomerAcceptLetter.setOnFocusChangeListener { view, b ->
            if (!b) {
                getData(mDataBinding.cetCustomerAcceptLetter.text.toString().trim(), REQ_SCANNER_GET_2)
            }
        }
        // 客户看板
        mDataBinding.cetCustomerBulletinBoard.setOnFocusChangeListener { view, b ->
            if (!b) {
                getData(mDataBinding.cetCustomerBulletinBoard.text.toString().trim(), REQ_SCANNER_GET_3)
            }
        }
        // 备注
        mDataBinding.cetRemark.addTextChangedListener (object: SimpleTextWatcher() {
            override fun afterTextChanged(editable: Editable?) {
                super.afterTextChanged(editable)
                "${editable?.length}/100".also { mDataBinding.tvLimit.text = it }
            }
        })
        // 保存
        mDataBinding.stvSaveStorageCollection.setOnClickListener {
            if (!isFastClick()) {
                saveData()
            }
        }
        // 取消
        mDataBinding.stvCancelStorageCollection.setOnClickListener {
            onBackPressed()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_shipment_prepare
    }

    override fun getVariableId(): Int {
        return 0
    }

    override fun isRegisterScan(): Boolean {
        return true
    }

    override fun scanResultCallBack(result: ScanResult?) {
        // 出货指示书、客户受领书采集一次，客户看板采集多次
        val text1 = mDataBinding.cetStorageDate.text.toString()
        val text2 = mDataBinding.cetCustomerAcceptLetter.text.toString()
        if (text1.isEmpty() && mDataList.isEmpty()) {
            getData(result?.data, REQ_SCANNER_GET)
        } else if (text2.isEmpty() && !mDataList.isEmpty()) {
            getData(result?.data, REQ_SCANNER_GET_2)
        } else {
            getData(result?.data, REQ_SCANNER_GET_3)
        }
    }

    private fun getData(result: String?, type : Int) {
        if (TextUtils.isEmpty(result)) return
        val req = ScannerRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.TextID = if (type == REQ_SCANNER_GET) "1" else if (type == REQ_SCANNER_GET_2) "2" else "3"
        req.QrCode = result
        if(type == REQ_SCANNER_GET_2){
            req.OutPlanList = mDataList
        }
        else if( type==REQ_SCANNER_GET_3){
            req.OutPlanList = mDataList
            req.CustemerReceipt=mDataBinding.cetCustomerAcceptLetter.text.toString();
        }
        showLoading()
        Thread {
            val response = StasHttpRequestUtil.queryShipmentPrepareResult(JSON.toJSONString(req))
            handleWebServiceResult(response, type)
        }.start()
    }

    private fun saveData() {
        if (mDataList.size == 0) {
            ToastUtils.show("请扫描出货指示书！")
            return
        }
        if (mTempDataList.size == 0) {
            ToastUtils.show("请扫描客户看板！")
            return
        }
        showLoading()
        Thread {
            val req = SaveShipmentPrepareReqInfo()
            req.Remark = mDataBinding.cetRemark.text.toString().trim()
            req.CustemerReceipt = mDataBinding.cetCustomerAcceptLetter.text.toString()
            req.OutPlanList = mDataList
            req.CustomLabelList = mTempDataList
            req.PdaID = AndroidUtil.getIpAddress()
            req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
            val loginInfoStr = mMMKV.decodeString(MmkvConstants.MMKV_LOGIN_INFO)
            if (loginInfoStr != null) {
                val loginInfo = JSON.parseObject(loginInfoStr, LoginInfo::class.java)
                if (loginInfo != null) {
                    req.CreateBy = loginInfo.UserID
                }
            }
            val result = StasHttpRequestUtil.saveShipmentPrepareData(JSON.toJSONString(req))
            handleWebServiceResult(result, REQ_SCANNER_SAVE)
        }.start()
    }

    override fun handleWebServiceSuccess(response: WebServiceResponse?, fromSource: Int) {
        if (fromSource == REQ_SCANNER_GET) {
            if (response?.data != null) {
                val jArray = JSONObject.parseArray(response.data, ShipmentInfo::class.java)
                if (jArray != null && jArray.size > 0) {
                    var i = 1
                    for (a in jArray) {
                        a.idNum = i
                        i++
                    }
                    mDataBinding.tableShipment.addData(jArray, true)
                }
            }
            if (response?.obj != null) { // 出货指示书
                val obj1 = JSONObject.parseObject(response.obj, GoodsInfo::class.java)
                mDataBinding.cetStorageDate.text = obj1.CustemerReceipt
            }
        } else if (fromSource == REQ_SCANNER_GET_2) {
            if (response?.obj != null) { // 客户受领书
                val obj1 = JSONObject.parseObject(response.obj, GoodsInfo::class.java)
                mDataBinding.cetCustomerAcceptLetter.text = obj1.CustemerReceipt
            }
        } else if (fromSource == REQ_SCANNER_GET_3) {
            if (response?.obj != null) { // 客户看板编号
                val obj2 = JSONObject.parseObject(response.obj, GoodsInfo::class.java)
                if (obj2 != null) {
                    mDataBinding.cetCustomerBulletinBoard.text = obj2.CustomLabel
                    mTempDataList.add(obj2)
                    obj2.idNum = mTempDataList.size
                    val array2 = arrayListOf<GoodsInfo>()
                    array2.add(obj2)
                    mDataBinding.tableCustomer.addData(array2, true)
                }
            }
        } else {
            ToastUtils.show("保存成功")
            mDataList.clear()
            mTempDataList.clear()
            mCustomerDataList.clear()
            mDataBinding.cetCustomerAcceptLetter.text = ""
            mDataBinding.cetCustomerBulletinBoard.text = ""
            mDataBinding.cetRemark.setText("")
            mDataBinding.cetStorageDate.text = ""
            mDataBinding.tableShipment.notifyDataChanged()
            mDataBinding.tableCustomer.notifyDataChanged()
        }
    }

    private fun initDataTable() {
        //region 声明表格列
        val coId = Column<String>("序号", "idNum")
        coId.isFixed = true
        coId.isAutoCount = true
        //一致是因为需要用字段名来解析List对象

        val coCustomer = Column<String>("客户编号", "Customer")
        val coTruckNo = Column<String>("车次号", "TruckNo")
        val coPartsNo = Column<String>("电装品番", "PartsNo")
        val coQty = Column<String>("数量", "Num")
        //endregion
        mDataBinding.tableShipment.setZoom(false, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableShipment.config.isShowXSequence = false //去掉表格顶部字母
        mDataBinding.tableShipment.config.isShowYSequence = false //去掉左侧数字
        mDataBinding.tableShipment.config.isShowTableTitle = false // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<ShipmentInfo> =
            TableData<ShipmentInfo>(
                "出货信息",
                mDataList,
                coId,
                coCustomer,
                coTruckNo,
                coPartsNo,
                coQty,
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableShipment.setTableData(tableData)
        mDataBinding.tableShipment.tableData
            .setOnRowClickListener { column, o, col, row ->
//                if (col == 2) {
                    // 删除
//                    CommonAlertDialog(this).builder().setTitle("提示")
//                        .setMsg("是否确认删除？")
//                        .setNegativeButton("取消", null)
//                        .setPositiveButton("确认") {
//                            mDataList.removeAt(row)
//                            mTempDataList.removeAt(row)
//                            var i = 1
//                            for (info in mDataList) {
//                                info.idNum = i
//                                i++
//                            }
//                            mDataBinding.tableShipment.notifyDataChanged()
//                        }.show()
//                } else {
//                    RouteJumpUtil.jumpToDocumentDetail()
//                }
            }
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(
                            this@ShipmentPrepareActivity,
                            com.lib_src.R.color.green_trans_22
                        )
                    } else TableConfig.INVALID_COLOR
                }

//                override fun getTextColor(t: CellInfo<*>?): Int {
//                    return if (t?.col == 5) ContextCompat.getColor(
//                        this@ShipmentPrepareActivity,
//                        com.lib_src.R.color.blue11
//                    ) else
//                        ContextCompat.getColor(
//                            this@ShipmentPrepareActivity,
//                            com.lib_src.R.color.black04
//                        )
//                }
            }
        mDataBinding.tableShipment.config.contentCellBackgroundFormat = backgroundFormat

    }

    private fun initCustomerDataTable() {
        //region 声明表格列
        val coId = Column<String>("序号", "idNum")
        coId.isFixed = true
        coId.isAutoCount = true
        //一致是因为需要用字段名来解析List对象
        val coPartsNo = Column<String>("客户看板编号", "CustomLabel")
        coPartsNo.maxMergeCount = 5
        val coDel = Column<String>("操作", "del")
        //endregion
        mDataBinding.tableCustomer.setZoom(false, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableCustomer.config.isShowXSequence = false //去掉表格顶部字母
        mDataBinding.tableCustomer.config.isShowYSequence = false //去掉左侧数字
        mDataBinding.tableCustomer.config.isShowTableTitle = false // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<GoodsInfo> =
            TableData<GoodsInfo>(
                "客户信息",
                mCustomerDataList,
                coId,
                coPartsNo,
                coDel
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableCustomer.setTableData(tableData)
        mDataBinding.tableCustomer.tableData
            .setOnRowClickListener { column, o, col, row ->
                if (col == 2) {
                    // 删除
                    CommonAlertDialog(this).builder().setTitle("提示")
                        .setMsg("是否确认删除？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认") {
                            mCustomerDataList.removeAt(row)
                            mTempDataList.removeAt(row)
                            var i = 1
                            for (info in mCustomerDataList) {
                                info.idNum = i
                                i++
                            }
                            mDataBinding.tableCustomer.notifyDataChanged()
                        }.show()
                }
            }
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(
                            this@ShipmentPrepareActivity,
                            com.lib_src.R.color.green_trans_22
                        )
                    } else TableConfig.INVALID_COLOR
                }

                override fun getTextColor(t: CellInfo<*>?): Int {
                    return if (t?.col == 5) ContextCompat.getColor(
                        this@ShipmentPrepareActivity,
                        com.lib_src.R.color.blue11
                    ) else
                        ContextCompat.getColor(
                            this@ShipmentPrepareActivity,
                            com.lib_src.R.color.black04
                        )
                }
            }
        mDataBinding.tableCustomer.config.contentCellBackgroundFormat = backgroundFormat

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