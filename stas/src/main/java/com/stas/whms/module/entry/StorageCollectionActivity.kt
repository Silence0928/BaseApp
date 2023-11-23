package com.stas.whms.module.entry

import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat
import com.bin.david.form.data.table.TableData
import com.bin.david.form.data.table.TableData.OnRowClickListener
import com.hjq.toast.ToastUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.constants.MmkvConstants
import com.lib_common.entity.ScanResult
import com.lib_common.utils.AndroidUtil
import com.lib_common.utils.DateUtils
import com.lib_common.view.layout.dialog.CommonAlertDialog
import com.lib_common.webservice.response.WebServiceResponse
import com.stas.whms.R
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.LoginInfo
import com.stas.whms.bean.ScannerRequestInfo
import com.stas.whms.bean.UserInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityStorageCollectionBinding
import com.stas.whms.utils.RouteJumpUtil
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_STORAGE_COLLECTION)
class StorageCollectionActivity :
    BaseMvvmActivity<ActivityStorageCollectionBinding, BaseViewModel>() {
    private val REQ_SCANNER_GET = 1
    private val REQ_SCANNER_SAVE = 2
    private var mDataList = arrayListOf<GoodsInfo>()
    private var mTempDataList = arrayListOf<GoodsInfo>()
    override fun initView() {
        title = "入库采集"
        initDataTable()
    }

    override fun onViewEvent() {
        mDataBinding.cetMadeFinishedTag.setOnFocusChangeListener { view, b ->
            if (!b) {
//                getData(mDataBinding.cetMadeFinishedTag.text.toString().trim())
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
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_storage_collection
    }

    override fun getVariableId(): Int {
        return 0
    }

    override fun isRegisterScan(): Boolean {
        return true
    }

    override fun scanResultCallBack(result: ScanResult?) {
        getData(result?.data)
    }

    private fun getData(result: String?) {
        if (TextUtils.isEmpty(result)) return
        var req = ScannerRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.QrCode = result
//        req.QrCode =
//            "DISC5060020000010091000210125104151120712305152071530815408155092123810-E0150                095440-12800J0000002Z999 0070380        00000000         "
        showLoading()
        Thread {
            val response = StasHttpRequestUtil.queryScannerResult(JSON.toJSONString(req))
            handleWebServiceResult(response, REQ_SCANNER_GET)
        }.start()
    }

    override fun handleWebServiceSuccess(response: WebServiceResponse?, fromSource: Int) {
        if (fromSource == REQ_SCANNER_GET) {
            if (response?.obj != null) {
                val goodsInfo =
                    JSON.parseObject(response.obj.toString(), GoodsInfo::class.java)
                if (isCanSave(goodsInfo)) {
                    mDataBinding.cetMadeFinishedTag.setText(goodsInfo?.PartsNo)
                    goodsInfo.idNum = mDataList.size + 1
                    val tempList = arrayListOf<GoodsInfo>()
                    tempList.add(goodsInfo)
                    mTempDataList.add(goodsInfo)
                    mDataBinding.tableStorageCollection.addData(tempList, true)
                    handleTotalNum()
                } else {
                    ToastUtils.show("采集的数据已存在，请重新扫描")
                }
            }
        } else if (fromSource == REQ_SCANNER_SAVE) {
            ToastUtils.show("保存成功")
            // 清除表格数据
            mDataBinding.cetMadeFinishedTag.setText("")
            mTempDataList.clear()
            mDataList.clear()
            mDataBinding.tableStorageCollection.setData(arrayListOf<GoodsInfo>())
            mDataBinding.tableStorageCollection.notifyDataChanged()
            handleTotalNum()
        }
    }

    private fun isCanSave(goods: GoodsInfo?): Boolean {
        if (mDataList.size == 0) return true
        if (goods == null) return false
        var canSave = true
        for (i in mDataList) {
            if (i.TagSerialNo == goods?.TagSerialNo && i.PartsNo == goods?.PartsNo) {
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
            totalCount += if (g.Qty == null) 0 else g.Qty?.toInt()!!
        }
        return totalCount.toString()
    }

    private fun saveData() {
        if (mTempDataList.size == 0) {
            ToastUtils.show("请采集制品箱后，点击保存")
            return
        }
        showLoading()
        Thread {
            val req = HashMap<String, Any?>()
            req["PdaID"] = AndroidUtil.getIpAddress()
            req["ListData"] = mTempDataList
            req["TimeStamp"] = DateUtils.getCurrentDateMilTimeStr()
            val loginInfoStr = mMMKV.decodeString(MmkvConstants.MMKV_LOGIN_INFO)
            if (loginInfoStr != null) {
                val loginInfo = JSON.parseObject(loginInfoStr, LoginInfo::class.java)
                if (loginInfo != null) {
                    req["CreateBy"] = loginInfo.UserID
                }
            }
            val result = StasHttpRequestUtil.saveInBound(JSON.toJSONString(req))
            handleWebServiceResult(result, REQ_SCANNER_SAVE)
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
        val coBoxSum = Column<String>("包装数", "Qty")
        val coFromProCode = Column<String>("前工程", "FromProCode")
        val coDel = Column<String>("操作", "del")
        //endregion
        mDataBinding.tableStorageCollection.setZoom(false, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableStorageCollection.config.isShowXSequence = false //去掉表格顶部字母
        mDataBinding.tableStorageCollection.config.isShowYSequence = false //去掉左侧数字
        mDataBinding.tableStorageCollection.config.isShowTableTitle = false // 去掉表头

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
                coDel
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableStorageCollection.setTableData(tableData)
        mDataBinding.tableStorageCollection.tableData
            .setOnRowClickListener { column, o, col, row ->
                if (col == 5) {
                    // 删除
                    CommonAlertDialog(this).builder().setTitle("提示")
                        .setMsg("是否确认删除？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认") {
                            if (mDataList.size == 0) {
                                handleTotalNum()
                                return@setPositiveButton
                            }
                            mDataList.removeAt(row)
                            mTempDataList.removeAt(row)
                            var i = 1
                            for (info in mDataList) {
                                info.idNum = i
                                i++
                            }
                            mDataBinding.tableStorageCollection.notifyDataChanged()
                            handleTotalNum()
                        }.show()
                } else {
//                    RouteJumpUtil.jumpToDocumentDetail()
                }
            }
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(
                            this@StorageCollectionActivity,
                            com.lib_src.R.color.green_trans_22
                        )
                    } else TableConfig.INVALID_COLOR
                }

                override fun getTextColor(t: CellInfo<*>?): Int {
                    return if (t?.col == 5) ContextCompat.getColor(
                        this@StorageCollectionActivity,
                        com.lib_src.R.color.blue11
                    ) else
                        ContextCompat.getColor(
                            this@StorageCollectionActivity,
                            com.lib_src.R.color.black04
                        )
                }
            }
        mDataBinding.tableStorageCollection.config.contentCellBackgroundFormat = backgroundFormat

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