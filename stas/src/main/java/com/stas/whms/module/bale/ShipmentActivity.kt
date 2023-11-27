package com.stas.whms.module.bale

import android.graphics.Canvas
import android.graphics.Paint
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
import com.bin.david.form.data.format.grid.SimpleGridFormat
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
import com.lib_common.view.layout.dialog.vehicleno.VehicleNoKeyBoardDialog
import com.lib_common.webservice.response.WebServiceResponse
import com.stas.whms.R
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.LoginInfo
import com.stas.whms.bean.SaveShipmentPrepareReqInfo
import com.stas.whms.bean.ScannerRequestInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityShipmentBinding
import com.stas.whms.utils.RouteJumpUtil
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_SHIPMENT)
class ShipmentActivity : BaseMvvmActivity<ActivityShipmentBinding, BaseViewModel>() {
    private val REQ_SCANNER_GET = 1
    private val REQ_SCANNER_GET_2 = 2
    private val REQ_SCANNER_SAVE = 4
    private var mCustomerDataList = arrayListOf<GoodsInfo>()
    private var mTempDataList = arrayListOf<GoodsInfo>()
    private lateinit var mVehicleNoKeyBoard: VehicleNoKeyBoardDialog // 车牌号键盘
    private var mProductEnd: GoodsInfo? = null

    override fun initView() {
        title = "出货"
        initDataTable()
        // 车牌键盘
        mVehicleNoKeyBoard = VehicleNoKeyBoardDialog(this)
    }

    override fun onViewEvent() {
        // 车牌号
        mDataBinding.cetCarNo.setOnFocusChangeListener { view, b ->
            if (!b) {
                mDataBinding.cetCarNo.setText(mDataBinding.cetCarNo.text.toString().uppercase())
            }
        }
//        mDataBinding.cetCarNo.addTextChangedListener (object: SimpleTextWatcher() {
//            override fun afterTextChanged(editable: Editable?) {
//                super.afterTextChanged(editable)
//                mDataBinding.cetCarNo.setText(editable.toString().uppercase())
//            }
//        })
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
        return R.layout.activity_shipment
    }

    override fun getVariableId(): Int {
        return 0
    }

    override fun isRegisterScan(): Boolean {
        return true
    }

    override fun scanResultCallBack(result: ScanResult?) {
        val text1 = mDataBinding.cetShipmentInstruction.text.toString()
        if (text1.isEmpty()) {
            getData(result?.data, REQ_SCANNER_GET)
        } else {
            getData(result?.data, REQ_SCANNER_GET_2)
        }

    }

    /**
     * 隐藏输入键盘
     */
    private fun hideVehicleNoKeyBoardView() {
        if (mVehicleNoKeyBoard.isShowing) {
            mVehicleNoKeyBoard.dismiss()
        }
    }

    private fun showVehicleNoKeyBoard() {
        // 隐藏软键盘
        hideSoftKeyboard()
        // 显示车牌号输入键盘
        mVehicleNoKeyBoard.show()
        mVehicleNoKeyBoard.setVehicleNoView(mDataBinding.cetCarNo, false)
        mVehicleNoKeyBoard.setTitle("输入车牌号")
    }

    private fun getData(result: String?, type : Int) {
        if (TextUtils.isEmpty(result)) return
        val req = ScannerRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.TextID = if (type == REQ_SCANNER_GET) "1" else "2"
        req.QrCode = result
        if(type != REQ_SCANNER_GET){
            req.ProductEnd = mProductEnd
        }

        showLoading()
        Thread {
            val response = StasHttpRequestUtil.queryShipmentDataResult(JSON.toJSONString(req))
            handleWebServiceResult(response, type)
        }.start()
    }

    private fun saveData() {
        val shipmentProInt = mDataBinding.cetShipmentInstruction.text.toString()
        val trackNo = mDataBinding.cetCarNo.text.toString().trim().replace(" ", "")
        if (shipmentProInt.isEmpty()) {
            ToastUtils.show("请扫描客户受领书！")
            return
        }
        if (mTempDataList.size == 0) {
            ToastUtils.show("请扫描客户看板！")
            return
        }
        if (trackNo.isEmpty()) {
            ToastUtils.show("请输入车牌号！")
            return
        }
        showLoading()
        Thread {
            val req = SaveShipmentPrepareReqInfo()
            req.Remark = mDataBinding.cetRemark.text.toString().trim()
            req.CustemerReceipt = mDataBinding.cetShipmentInstruction.text.toString()
            req.CustomLabelList = mTempDataList
            req.TruckNo = mDataBinding.cetCarNo.text.toString().trim().replace(" ", "")
            req.PdaID = AndroidUtil.getIpAddress()
            req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
            val loginInfoStr = mMMKV.decodeString(MmkvConstants.MMKV_LOGIN_INFO)
            if (loginInfoStr != null) {
                val loginInfo = JSON.parseObject(loginInfoStr, LoginInfo::class.java)
                if (loginInfo != null) {
                    req.CreateBy = loginInfo.UserID
                }
            }
            val result = StasHttpRequestUtil.saveShipmentData(JSON.toJSONString(req))
            handleWebServiceResult(result, REQ_SCANNER_SAVE)
        }.start()
    }

    override fun handleWebServiceSuccess(response: WebServiceResponse?, fromSource: Int) {
        if (fromSource == REQ_SCANNER_GET) {
            if (response?.obj != null) {
                mProductEnd = JSONObject.parseObject(response.obj, GoodsInfo::class.java)
                mDataBinding.cetShipmentInstruction.setText(mProductEnd?.CustemerReceipt)
            }
        } else if (fromSource == REQ_SCANNER_GET_2) {
            if (response?.obj != null) { // 客户看板编号
                val obj3 = JSONObject.parseObject(response.obj, GoodsInfo::class.java)
                if (obj3 != null) {
                    mDataBinding.cetCustomerBoard.setText("")
                    if (isCanSave(obj3)) {
                        mTempDataList.add(obj3)
                        obj3.idNum = mTempDataList.size
                        val array2 = arrayListOf<GoodsInfo>()
                        array2.add(obj3)
                        mDataBinding.tableCustomer.addData(array2, true)
                        handleTotalNum()
                    } else {
                        ToastUtils.show("采集的数据已存在，请重新扫描")
                    }
                }
            }
        } else {
            mCustomerDataList.clear()
            mTempDataList.clear()
            mProductEnd = null
            mDataBinding.cetShipmentInstruction.setText("")
            mDataBinding.cetCustomerBoard.setText("")
            mDataBinding.cetRemark.setText("")
            mDataBinding.cetCarNo.setText("")
            mDataBinding.tableCustomer.notifyDataChanged()
            handleTotalNum()
            ToastUtils.show("保存成功")
        }
    }

    private fun isCanSave(goods: GoodsInfo?): Boolean {
        if (mTempDataList.size == 0) return true
        if (goods == null) return false
        var canSave = true
        for (i in mTempDataList) {
            if (i.TagSerialNo == goods.TagSerialNo) {
                canSave = false
                break
            }
        }
        return canSave
    }

    private fun handleTotalNum() {
        // 总箱数
        mDataBinding.cetTotalBoxNum.text = getTotalBoxNum()
        // 总数量
        mDataBinding.cetTotalNum.text = getTotalNum()
        // 已采集数量
        val totalSize = mTempDataList.size
        mDataBinding.cetCollectedTotalBoxNum.text = totalSize.toString()
    }

    private fun getTotalBoxNum(): String {
        var totalCount = 0
        for (g in mTempDataList) {
            totalCount += if (g.BoxSum == null) 0 else g.BoxSum?.toInt()!!
        }
        return totalCount.toString()
    }

    private fun getTotalNum(): String {
        var totalCount = 0
        for (g in mTempDataList) {
            totalCount += if (g.QtySum == null) 0 else g.QtySum?.toInt()!!
        }
        return totalCount.toString()
    }

    private fun initDataTable() {
        //region 声明表格列
        val coId = Column<String>("序号", "idNum")
        coId.isFixed = true
        coId.isAutoCount = true
        //一致是因为需要用字段名来解析List对象
        val coCustomLabel = Column<String>("客户看板编号", "CustomLabel")
//        val coDel = Column<String>("操作", "del")
        //endregion
        mDataBinding.tableCustomer.setZoom(false, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableCustomer.config.isShowXSequence = false //去掉表格顶部字母
        mDataBinding.tableCustomer.config.isShowYSequence = false //去掉左侧数字
        mDataBinding.tableCustomer.config.isShowTableTitle = false // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<GoodsInfo> =
            TableData<GoodsInfo>(
                "出货信息",
                mCustomerDataList,
                coId,
                coCustomLabel
//                coDel
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableCustomer.setTableData(tableData)
        mDataBinding.tableCustomer.tableData
            .setOnRowClickListener { column, o, col, row ->
//                if (col == 2) {
//                 // 删除
//                    CommonAlertDialog(this).builder().setTitle("提示")
//                        .setMsg("是否确认删除？")
//                        .setNegativeButton("取消", null)
//                        .setPositiveButton("确认") {
//                            mCustomerDataList.removeAt(row)
//                            mTempDataList.removeAt(row)
//                            var i = 1
//                            for (info in mCustomerDataList) {
//                                info.idNum = i
//                                i++
//                            }
//                            mDataBinding.tableCustomer.notifyDataChanged()
//                        }.show()
//                } else {
//                    RouteJumpUtil.jumpToDocumentDetail(mTempDataList[row].TagSerialNo)
//                }
            }
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(
                            this@ShipmentActivity,
                            com.lib_src.R.color.green_trans_22
                        )
                    } else TableConfig.INVALID_COLOR
                }

//                override fun getTextColor(t: CellInfo<*>?): Int {
//                    return if (t?.col == 2) ContextCompat.getColor(
//                        this@ShipmentActivity,
//                        com.lib_src.R.color.blue11
//                    ) else
//                        ContextCompat.getColor(
//                            this@ShipmentActivity,
//                            com.lib_src.R.color.black04
//                        )
//                }
            }
        mDataBinding.tableCustomer.config.contentCellBackgroundFormat = backgroundFormat
        // 清除表格左右、底部边框线
        mDataBinding.tableCustomer.config.tableGridFormat = object: SimpleGridFormat(){
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
        if (mCustomerDataList.size > 0) {
            CommonAlertDialog(this).builder().setTitle("提示")
                .setMsg("取消将清空已采集数据，是否确认？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认") { finish() }.show()
        } else {
            finish()
        }
    }
}