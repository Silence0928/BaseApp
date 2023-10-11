package com.stas.whms.module.entry

import android.text.Editable
import android.view.View
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
import com.lib_common.constants.Constants
import com.lib_common.dialog.BottomListDialog
import com.lib_common.dialog.DateSelectDialog
import com.lib_common.entity.ScanResult
import com.lib_common.listener.SimpleTextWatcher
import com.lib_common.utils.AndroidUtil
import com.lib_common.utils.DateUtils
import com.lib_common.view.layout.dialog.CommonAlertDialog
import com.lib_common.webservice.response.WebServiceResponse
import com.stas.whms.R
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.InBoundAuditRequestInfo
import com.stas.whms.bean.SaveInBoundAuditReqInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityStorageAuditBinding
import com.stas.whms.utils.RouteJumpUtil
import com.stas.whms.utils.StasHttpRequestUtil
import java.util.Date

@Route(path = RoutePathConfig.ROUTE_STORAGE_AUDIT)
class StorageAuditActivity : BaseMvvmActivity<ActivityStorageAuditBinding, BaseViewModel>() {

    private val REQ_IN_BOUND_NO_GET = 2
    private val REQ_IN_BOUND_GET_END = 3
    private val REQ_IN_BOUND_GET = 4
    private val REQ_IN_BOUND_SAVE = 5
    private var mOrderNoList = arrayListOf<String>()
    private var mDataList = arrayListOf<GoodsInfo>()
    private var mTempDataList = arrayListOf<GoodsInfo>()
    private var mProductEnd: GoodsInfo? = null

    override fun initView() {
        title = "入库审核"
        initDataTable()
        mDataBinding.cetStorageDate.text = DateUtils.getFormatDate(DateUtils.getDateBefore(1), Constants.DATE_FORMAT_LINE)
        getData(null, REQ_IN_BOUND_NO_GET)
    }

    override fun onViewEvent() {
        // 入库时间
        mDataBinding.cetStorageDate.setOnClickListener {
            DateSelectDialog(this) { date: Date?, v: View? ->
                mDataBinding.cetStorageDate.text = DateUtils.getFormatDate(date, Constants.DATE_FORMAT_LINE)
                getData(null, REQ_IN_BOUND_NO_GET)
            }.build().show()
        }
        // 入库单号
        mDataBinding.cetStorageOrderNo.setOnClickListener {
            if (mOrderNoList.isEmpty()) {
                ToastUtils.show("没有待审核的入库单号")
                return@setOnClickListener
            }
            BottomListDialog(this).setItems(mOrderNoList)
                .setOnConfirmSelectListener { position: Int, name: String? ->
                    mDataBinding.cetStorageOrderNo.text = name
                }
                .setCurrentItem(if (mOrderNoList.size > 0) mOrderNoList.indexOf(mDataBinding.cetStorageOrderNo.text.toString()) else 0)
                .show()
        }
        // 查询
        mDataBinding.stvQueryStorageCollection.setOnClickListener {
            if (!isFastClick()) {
                val orderNoStr = mDataBinding.cetStorageOrderNo.text.toString()
                val madeFinishedTag = mDataBinding.cetMadeFinishedTag.text.toString()
                if (orderNoStr.isEmpty()) {
                    ToastUtils.show("请选择入库单号")
                    return@setOnClickListener
                }
                if (madeFinishedTag.isEmpty()) {
                    ToastUtils.show("请输入制造完了标签")
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
        return R.layout.activity_storage_audit
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
     * type=2 查询单号  =3查询制造完了标签 =4查询入库数据
     */
    private fun getData(result: String?, type: Int) {
        if (type == REQ_IN_BOUND_GET_END && result?.isEmpty() == true) return
        val req = InBoundAuditRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.Date = mDataBinding.cetStorageDate.text.toString()
        req.DocNo = mDataBinding.cetStorageOrderNo.text.toString()
        req.TextID = type.toString()
        req.ProductEnd = mProductEnd
        req.QrCode = if (type == REQ_IN_BOUND_GET_END)
            "DISC5060020000010091000210125104151120712305152071530815408155092123810-E0150                095440-12800J0000002Z999 0070380        00000000         "
        else null
        showLoading()
        Thread {
            val response = StasHttpRequestUtil.queryInBoundAuditData(JSON.toJSONString(req))
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
        } else if (fromSource == REQ_IN_BOUND_GET || fromSource == REQ_IN_BOUND_NO_GET) {
            if (response?.data != null) {
                val jArray = JSONObject.parseArray(response.data, GoodsInfo::class.java)
                if (fromSource == REQ_IN_BOUND_NO_GET) {
                    for (a in jArray) {
                        if (a is GoodsInfo) {
                            mOrderNoList.add(a.DocNo!!)
                        }
                    }
                    mDataBinding.cetStorageOrderNo.text = mOrderNoList[0]
                } else {
                    var i = 1
                    for (a in jArray) {
                        a.idNum = i
                        i++
                    }
                    mTempDataList = jArray as ArrayList<GoodsInfo>
                    mDataBinding.tableStorageCollection.addData(jArray, true)
                    handleTotalNum()
                }
            }
        } else {
            ToastUtils.show("保存成功")
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
            ToastUtils.show("无待审核的入库单！")
            return
        }
        showLoading()
        Thread {
            val req = SaveInBoundAuditReqInfo()
            req.Remark = mDataBinding.cetRemark.text.toString().trim()
            req.ListData = mDataList
            val result = StasHttpRequestUtil.saveInBoundAuditData(JSON.toJSONString(req))
            handleWebServiceResult(result, REQ_IN_BOUND_SAVE)
        }.start()
    }

    private fun initDataTable() {
        //region 声明表格列
        val coId = Column<String>("序号", "idNum")
        coId.isFixed = true
        coId.isAutoCount = true
        //一致是因为需要用字段名来解析List对象
        val coDocNo = Column<String>("入库单号", "DocNo")
        val coPartsNo = Column<String>("电装品番", "PartsNo")
        val coBoxSum = Column<String>("单据总数", "BoxSum")
        val coCreateBy = Column<String>("采集人", "CreateBy")
        val coCreateDT = Column<String>("入库日期", "CreateDT")
        //endregion
        mDataBinding.tableStorageCollection.setZoom(false, 1.0f, 0.5f) //开启缩放功能
        mDataBinding.tableStorageCollection.config.setShowXSequence(false) //去掉表格顶部字母
        mDataBinding.tableStorageCollection.config.setShowYSequence(false) //去掉左侧数字
        mDataBinding.tableStorageCollection.config.setShowTableTitle(false) // 去掉表头

        //TableData对象，包含了（表格标题，数据源，列1，列2，列3，列4....好多列）
        val tableData: TableData<GoodsInfo> =
            TableData<GoodsInfo>(
                "商品信息",
                mDataList,
                coId,
                coDocNo,
                coPartsNo,
                coBoxSum,
                coCreateBy,
                coCreateDT
            )
        //注意：绑定数据的方法setData换成了setTableData。不再是List对象而是TableData对象
        mDataBinding.tableStorageCollection.setTableData(tableData)
        mDataBinding.tableStorageCollection.tableData
            .setOnRowClickListener { column, o, col, row ->
                RouteJumpUtil.jumpToDocumentDetail(mDataList[row].DocNo)
            }
        // 设置背景和字体颜色
        val backgroundFormat: BaseCellBackgroundFormat<CellInfo<*>?> =
            object : BaseCellBackgroundFormat<CellInfo<*>?>() {
                override fun getBackGroundColor(cellInfo: CellInfo<*>?): Int {
                    return if (cellInfo?.row.let { it!! }.toInt() % 2 != 0) {
                        ContextCompat.getColor(
                            this@StorageAuditActivity,
                            com.lib_src.R.color.green_trans_22
                        )
                    } else TableConfig.INVALID_COLOR
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