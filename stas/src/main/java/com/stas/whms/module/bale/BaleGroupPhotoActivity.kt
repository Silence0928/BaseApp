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
import com.bin.david.form.data.table.TableData.OnRowClickListener
import com.hjq.toast.ToastUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.entity.ScanResult
import com.lib_common.listener.SimpleTextWatcher
import com.lib_common.utils.AndroidUtil
import com.lib_common.utils.DateUtils
import com.lib_common.view.layout.dialog.CommonAlertDialog
import com.lib_common.webservice.response.WebServiceResponse
import com.stas.whms.R
import com.stas.whms.bean.CustomerInfo
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.SaveShipmentPrepareReqInfo
import com.stas.whms.bean.ScannerRequestInfo
import com.stas.whms.bean.ShipmentInfo
import com.stas.whms.bean.UserInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityBaleGroupPhotoBinding
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_BALE_GROUP_PHOTO)
class BaleGroupPhotoActivity : BaseMvvmActivity<ActivityBaleGroupPhotoBinding, BaseViewModel>() {

    private val REQ_SCANNER_GET = 1
    private val REQ_SCANNER_GET_2 = 2
    private val REQ_SCANNER_GET_3 = 3
    private val REQ_SCANNER_SAVE = 4
    private var mCustomerDataList = arrayListOf<CustomerInfo>()
    private var mTempDataList = arrayListOf<CustomerInfo>()
    private var mShipmentIntroduction: CustomerInfo? = null // 出货指示书

    override fun initView() {
        title = "捆包照合"
        initDataTable()
    }

    override fun onViewEvent() {
        // 出货指示书
        mDataBinding.cetShipmentInstruction.setOnFocusChangeListener { view, b ->
            if (!b) {
//                getData(mDataBinding.cetShipmentInstruction.text.toString().trim(), REQ_SCANNER_GET)
            }
        }
        // 生产看板
        mDataBinding.cetProductionSignage.setOnFocusChangeListener { view, b ->
            if (!b) {
//                getData(mDataBinding.cetCustomerAcceptLetter.text.toString().trim(), REQ_SCANNER_GET_2)
            }
        }
        // 客户看板
        mDataBinding.cetCustomerBoard.setOnFocusChangeListener { view, b ->
            if (!b) {
//                getData(mDataBinding.cetCustomerBulletinBoard.text.toString().trim(), REQ_SCANNER_GET_3)
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
        return R.layout.activity_bale_group_photo
    }

    override fun getVariableId(): Int {
        return 0
    }

    override fun isRegisterScan(): Boolean {
        return true
    }

    override fun scanResultCallBack(result: ScanResult?) {
        val text1 = mDataBinding.cetShipmentInstruction.text.toString()
        val text2 = mDataBinding.cetProductionSignage.text.toString()
        if (text1.isEmpty()) {
//            mDataBinding.cetShipmentInstruction.setText(result?.data)
            getData("27300078170Z", REQ_SCANNER_GET)
        } else if (text2.isEmpty()) {
//            mDataBinding.cetProductionSignage.setText(result?.data)
            getData("DISC5060020000010091000210125104151120712305152071530815408155092123810-E0150                095440-12800J0000002Z999 0070380        00000000         ", REQ_SCANNER_GET_2)
        } else {
//            mDataBinding.cetCustomerBoard.setText(result?.data)
            getData("901423101F2020  160786ZU", REQ_SCANNER_GET_3)
        }
    }

    private fun getData(result: String, type : Int) {
        if (TextUtils.isEmpty(result)) return
        val req = ScannerRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.TextID = if (type == REQ_SCANNER_GET) "1" else if (type == REQ_SCANNER_GET_2) "2" else "3"
        req.QrCode = result
        Thread {
            val response = StasHttpRequestUtil.queryBaleDataResult(JSON.toJSONString(req))
            handleWebServiceResult(response, type)
        }.start()
    }

    private fun saveData() {
        val shipmentProInt = mDataBinding.cetShipmentInstruction.text.toString()
        if (shipmentProInt.isEmpty()) {
            ToastUtils.show("请扫描出货指示书！")
            return
        }
        if (mTempDataList.size == 0) {
            ToastUtils.show("请扫描客户看板！")
            return
        }
        Thread {
//            val outPlanList = arrayListOf<ShipmentInfo>()
//            outPlanList.add(mShipmentIntroduction!!)
            val req = SaveShipmentPrepareReqInfo()
            req.Remark = mDataBinding.cetRemark.text.toString().trim()
            req.CustemerReceipt = mDataBinding.cetShipmentInstruction.text.toString()
//            req.OutPlanList = outPlanList
            req.CustomLabelList = mTempDataList
            val result = StasHttpRequestUtil.saveBaleData(JSON.toJSONString(req))
            handleWebServiceResult(result, REQ_SCANNER_SAVE)
        }.start()
    }

    override fun handleWebServiceSuccess(response: WebServiceResponse?, fromSource: Int) {
        if (fromSource == REQ_SCANNER_GET) {
            if (response?.obj != null) {
                mShipmentIntroduction = JSONObject.parseObject(response.obj, CustomerInfo::class.java)
                mDataBinding.cetShipmentInstruction.setText(mShipmentIntroduction?.CustemerReceipt)
            }
        } else if (fromSource == REQ_SCANNER_GET_2) {
            if (response?.obj != null) { // 生产看板
                val obj2 = JSONObject.parseObject(response.obj, GoodsInfo::class.java)
                mDataBinding.cetProductionSignage.setText(obj2.TagSerialNo)
            }
        } else if (fromSource == REQ_SCANNER_GET_3) {
            if (response?.obj != null) { // 客户看板编号
                val obj3 = JSONObject.parseObject(response.obj, CustomerInfo::class.java)
                if (obj3 != null) {
                    mDataBinding.cetProductionSignage.setText("")
                    mDataBinding.cetCustomerBoard.setText("")
                    if (isCanSave(obj3)) {
                        mTempDataList.add(obj3)
                        obj3.idNum = mTempDataList.size
                        val array2 = arrayListOf<CustomerInfo>()
                        array2.add(obj3)
                        mDataBinding.tableBalePhoto.addData(array2, true)
                        handleTotalNum()
                        handlePlanTotalNum()
                    } else {
                        ToastUtils.show("采集的数据已存在，请重新扫描")
                    }
                }
            }
        } else {
            ToastUtils.show("保存成功")
            finish()
        }
    }

    private fun isCanSave(goods: CustomerInfo?): Boolean {
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

    private fun handlePlanTotalNum() {
        val totalSize = mTempDataList.size
        mDataBinding.cetPlanTotalBoxNum.text = totalSize.toString()
        mDataBinding.cetPlanTotalNum.text = getPlanTotalNum()
    }

    private fun getPlanTotalNum(): String {
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
        //一致是因为需要用字段名来解析List对象
        val coPartsNo = Column<String>("电装品番", "PartsNo")
        val coTagSerialNo = Column<String>("回转号", "TagSerialNo")
        val coCustemerReceipt = Column<String>("客户编号", "CustemerReceipt")
        val coCustomLabel = Column<String>("客户看板编号", "CustomLabel")
        val coBoxSum = Column<String>("数量", "BoxSum")
        //endregion
        mDataBinding.tableBalePhoto.setZoom(true, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableBalePhoto.config.setShowXSequence(false) //去掉表格顶部字母
        mDataBinding.tableBalePhoto.config.setShowYSequence(false) //去掉左侧数字
        mDataBinding.tableBalePhoto.config.setShowTableTitle(false) // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<CustomerInfo> =
            TableData<CustomerInfo>(
                "出货信息",
                mCustomerDataList,
                coId,
                coPartsNo,
                coTagSerialNo,
                coCustemerReceipt,
                coCustomLabel,
                coBoxSum
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableBalePhoto.setTableData(tableData)
        mDataBinding.tableBalePhoto.tableData
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
//                            mDataBinding.tableBalePhoto.notifyDataChanged()
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
                            this@BaleGroupPhotoActivity,
                            com.lib_src.R.color.green_trans_22
                        )
                    } else TableConfig.INVALID_COLOR
                }

//                override fun getTextColor(t: CellInfo<*>?): Int {
//                    return if (t?.col == 5) ContextCompat.getColor(
//                        this@BaleGroupPhotoActivity,
//                        com.lib_src.R.color.blue11
//                    ) else
//                        ContextCompat.getColor(
//                            this@BaleGroupPhotoActivity,
//                            com.lib_src.R.color.black04
//                        )
//                }
            }
        mDataBinding.tableBalePhoto.config.contentCellBackgroundFormat = backgroundFormat

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