package com.stas.whms.module.bale

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat
import com.bin.david.form.data.format.draw.ImageResDrawFormat
import com.bin.david.form.data.format.grid.SimpleGridFormat
import com.bin.david.form.data.table.TableData
import com.bin.david.form.utils.DensityUtils
import com.hjq.toast.ToastUtils
import com.lib_common.app.BaseApplication
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
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.LoginInfo
import com.stas.whms.bean.SaveShipmentPrepareReqInfo
import com.stas.whms.bean.ScannerRequestInfo
import com.stas.whms.bean.ShipmentInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityLightReleaseBinding
import com.stas.whms.utils.StasHttpRequestUtil


@Route(path = RoutePathConfig.ROUTE_LIGHT_RELEASE)
class LightReleaseActivity : BaseMvvmActivity<ActivityLightReleaseBinding, BaseViewModel>() {
    private val REQ_SCANNER_GET = 1
    private val REQ_SCANNER_GET_2 = 2
    private val REQ_SCANNER_SAVE = 3
    private var mCustomerDataList = arrayListOf<GoodsInfo>()
    private var mTempDataList = arrayListOf<GoodsInfo>()
    private var mOutPlanList = arrayListOf<ShipmentInfo>() // 出货指示书
    private var mPartsNoList = arrayListOf<String>() // 电装品番
    private var isSingleSelect = false // 是否单选

    override fun initView() {
        title = "照合解除"
        initDataTable()
//        getData("08080181000160001511CW296100-32454B0001056CW299500-32414B0003840CW299500-32814B0000576", REQ_SCANNER_GET)
    }

    override fun onViewEvent() {
        // 出货指示书
        mDataBinding.cetShipmentInstruction.setOnFocusChangeListener { view, b ->
            if (!b) {
//                getData(mDataBinding.cetShipmentInstruction.text.toString().trim(), REQ_SCANNER_GET)
            }
        }
        // 电装品番
        mDataBinding.cetDenso.setOnClickListener {
            if (mPartsNoList.isEmpty()) {
                ToastUtils.show("没有电装品番可选择")
                return@setOnClickListener
            }
            BottomListDialog(this).setItems(mPartsNoList)
                .setOnConfirmSelectListener { position: Int, name: String? ->
                    mDataBinding.cetDenso.text = name
                }
                .setCurrentItem(if (mPartsNoList.size > 0) mPartsNoList.indexOf(mDataBinding.cetDenso.text.toString()) else 0)
                .show()
        }
        // 备注
        mDataBinding.cetRemark.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(editable: Editable?) {
                super.afterTextChanged(editable)
                "${editable?.length}/100".also { mDataBinding.tvLimit.text = it }
            }
        })
        // 查询
        mDataBinding.stvQuery.setOnClickListener {
            if (!isFastClick()) {
                if (mDataBinding.cetShipmentInstruction.text.toString().isEmpty()) {
                    ToastUtils.show("请先扫描出货指示书")
                    return@setOnClickListener
                }
                if (mDataBinding.cetDenso.text.toString().isEmpty()) {
                    ToastUtils.show("请选择电装品番")
                    return@setOnClickListener
                }
                getData(null, REQ_SCANNER_GET_2)
            }
        }
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
        // 全选
        mDataBinding.cbAll.setOnCheckedChangeListener { compoundButton, b ->
            if (!isSingleSelect || b) {
                handleAllSelected()
            } else {
                isSingleSelect = false
            }
        }
        mDataBinding.tvAll.setOnClickListener {
            if (!isFastClick()) {
                isSingleSelect = false
                mDataBinding.cbAll.isChecked = !mDataBinding.cbAll.isChecked
            }
        }
    }

    private fun handleAllSelected() {
        // 全选
        showLoading()
        BaseApplication.getMainHandler().postDelayed({
            val dataList = mDataBinding.tableLightRelease.tableData.t
            if (dataList != null && dataList.size > 0) {
                if (!mDataBinding.cbAll.isChecked) {
                    // 取消全选，此时需调整状态为全不选
                    for (d in dataList) {
                        val data = d as GoodsInfo
                        data.checked = false
                    }
                } else {
                    for (d in dataList) {
                        val data = d as GoodsInfo
                        data.checked = true
                    }
                }
                mDataBinding.tableLightRelease.notifyDataChanged()
            }
            isSingleSelect = false
            handleTotalNum()
            dismissLoading()
        }, 200)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_light_release
    }

    override fun getVariableId(): Int {
        return 0
    }

    override fun isRegisterScan(): Boolean {
        return true
    }

    override fun scanResultCallBack(result: ScanResult?) {
        getData(result?.data, REQ_SCANNER_GET)
    }

    private fun getData(result: String?, type: Int) {
        if (type == REQ_SCANNER_GET && TextUtils.isEmpty(result)) return
        val req = ScannerRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.TextID = if (type == REQ_SCANNER_GET) "1" else "2"
        req.OutPlanList = mOutPlanList
        req.PartsNo = mDataBinding.cetDenso.text.toString()
        req.QrCode = result
        showLoading()
        Thread {
            val response = StasHttpRequestUtil.queryLightReleaseDataResult(JSON.toJSONString(req))
            handleWebServiceResult(response, type)
        }.start()
    }

    private fun getCheckedData(): List<GoodsInfo> {
        val listData = arrayListOf<GoodsInfo>()
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
        val shipmentProInt = mDataBinding.cetShipmentInstruction.text.toString()
        if (shipmentProInt.isEmpty()) {
            ToastUtils.show("请扫描出货指示书！")
            return
        }
        if (mDataBinding.cetDenso.text.toString().isEmpty()) {
            ToastUtils.show("请选择电装品番")
            return
        }
        if (getCheckedData().isEmpty()) {
            ToastUtils.show("请选择待保存的品番数据")
            return
        }
        Thread {
            val req = SaveShipmentPrepareReqInfo()
            req.Remark = mDataBinding.cetRemark.text.toString().trim()
            req.OutPlanList = mOutPlanList
            req.ProductEndList = mTempDataList
            req.PdaID = AndroidUtil.getIpAddress()
            req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
            val loginInfoStr = mMMKV.decodeString(MmkvConstants.MMKV_LOGIN_INFO)
            if (loginInfoStr != null) {
                val loginInfo = JSON.parseObject(loginInfoStr, LoginInfo::class.java)
                if (loginInfo != null) {
                    req.CreateBy = loginInfo.UserID
                }
            }
            val result = StasHttpRequestUtil.saveLightReleaseData(JSON.toJSONString(req))
            handleWebServiceResult(result, REQ_SCANNER_SAVE)
        }.start()
    }

    override fun handleWebServiceSuccess(response: WebServiceResponse?, fromSource: Int) {
        if (fromSource == REQ_SCANNER_GET) {
            mDataBinding.cetDenso.text = ""
            if (response?.data != null) {
                val arrayJ = JSONObject.parseArray(response.data, ShipmentInfo::class.java)
                mOutPlanList = arrayJ as ArrayList<ShipmentInfo>
                mPartsNoList.clear()
                mDataBinding.cetShipmentInstruction.setText("")
                mDataBinding.cetDenso.text = ""
                if (mOutPlanList.size > 0) {
                    for (i in mOutPlanList) {
                        if (i.PartsNo?.isNotEmpty() == true) {
                            mPartsNoList.add(i.PartsNo!!)
                        }
                    }
                    mDataBinding.cetShipmentInstruction.setText(mOutPlanList[0].Customer)
                    mDataBinding.cetDenso.text = mPartsNoList[0]
                }
            }
            if (mTempDataList.size > 0) {
                resetData()
            }
        } else if (fromSource == REQ_SCANNER_GET_2) {
            if (mTempDataList.size > 0) {
                resetData()
            }
            if (response?.data != null) {
                val obj3 = JSONObject.parseArray(response.data, GoodsInfo::class.java)
                if (obj3 != null) {
                    mTempDataList = obj3 as ArrayList<GoodsInfo>
                    var i = 1
                    for (t in obj3) {
                        t.idNum = i
                        i++
                    }
                    mDataBinding.tableLightRelease.addData(obj3, true)
                    mDataBinding.llAllSelect.visibility = View.VISIBLE
                    handlePlanTotalNum()
                }
            }
        } else {
            mCustomerDataList.clear()
            mTempDataList.clear()
            mOutPlanList.clear()
            mPartsNoList.clear()
            mDataBinding.cetShipmentInstruction.setText("")
            mDataBinding.cetDenso.text = ""
            mDataBinding.cetRemark.setText("")
            mDataBinding.tableLightRelease.notifyDataChanged()
            handleTotalNum()
            handlePlanTotalNum()
            ToastUtils.show("保存成功")
        }
    }

    private fun resetData() {
        mTempDataList.clear()
        mCustomerDataList.clear()
        mDataBinding.tableLightRelease.notifyDataChanged()
        mDataBinding.cbAll.isChecked = false
        handleTotalNum()
    }

    private fun handleTotalNum() {
        mDataBinding.cetTotalBoxNum.text = getCheckedData().size.toString()
        mDataBinding.cetTotalNum.text = getTotalNum()
    }

    private fun getTotalNum(): String {
        var totalCount = 0
        var checkList = getCheckedData()
        for (g in checkList) {
            totalCount += if (g.Qty == null) 0 else g.Qty?.toInt()!!
        }
        return totalCount.toString()
    }

    private fun handlePlanTotalNum() {
        mDataBinding.cetPlanTotalBoxNum.text = mTempDataList.size.toString()
        mDataBinding.cetPlanTotalNum.text = getPlanTotalNum()
    }

    private fun getPlanTotalNum(): String {
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
        val size = DensityUtils.dp2px(this, 15f)
        val coChecked =
            Column<Boolean>("选择", "checked", object : ImageResDrawFormat<Boolean>(size, size) {
                override fun getContext(): Context {
                    return this@LightReleaseActivity
                }

                override fun getResourceID(t: Boolean, value: String?, position: Int): Int {
                    return if (t) com.lib_src.R.drawable.icon_checked else com.lib_src.R.drawable.icon_unchecked
                }
            })
        coChecked.isFixed = true
        val coPartsNo = Column<String>("电装品番", "PartsNo")
        val coTagSerialNo = Column<String>("回转号", "TagSerialNo")
        val coCustomLabel = Column<String>("客户看板编号", "CustomLabel")
        val coBoxSum = Column<String>("数量", "Qty")
        //endregion
        mDataBinding.tableLightRelease.setZoom(false, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableLightRelease.config.isShowXSequence = false //去掉表格顶部字母
        mDataBinding.tableLightRelease.config.isShowYSequence = false //去掉左侧数字
        mDataBinding.tableLightRelease.config.isShowTableTitle = false // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<GoodsInfo> =
            TableData<GoodsInfo>(
                "出货信息",
                mCustomerDataList,
                coChecked,
                coId,
                coPartsNo,
                coTagSerialNo,
                coCustomLabel,
                coBoxSum
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableLightRelease.setTableData(tableData)
        mDataBinding.tableLightRelease.tableData
            .setOnRowClickListener { column, o, col, row ->
                if (col == 0 && !column.isParent) {
                    // 选择,添加延迟是为了避免频繁刷新导致数组越界
                    showLoading()
                    BaseApplication.getMainHandler().postDelayed({
                        val dataList = mDataBinding.tableLightRelease.tableData.t
                        if (dataList != null && dataList.size > 0) {
                            val data = dataList[row] as GoodsInfo
                            data.checked = !data.checked
                            mDataBinding.tableLightRelease.notifyDataChanged()
                            // 判断是否全选
                            isSingleSelect = true
                            mDataBinding.cbAll.isChecked = getCheckedData().size == dataList.size
                            handleTotalNum()
                        }
                        dismissLoading()
                    }, 200)
                }
            }
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(
                            this@LightReleaseActivity,
                            com.lib_src.R.color.green_trans_22
                        )
                    } else TableConfig.INVALID_COLOR
                }

            }
        mDataBinding.tableLightRelease.config.contentCellBackgroundFormat = backgroundFormat
        // 清除表格左右、底部边框线
        mDataBinding.tableLightRelease.config.tableGridFormat = object: SimpleGridFormat(){
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