package com.stas.whms.module.entry

import android.text.Editable
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.hjq.toast.ToastUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.constants.MmkvConstants
import com.lib_common.dialog.BottomListDialog
import com.lib_common.entity.ScanResult
import com.lib_common.listener.SimpleTextWatcher
import com.lib_common.utils.AndroidUtil
import com.lib_common.utils.DateUtils
import com.lib_common.webservice.response.WebServiceResponse
import com.stas.whms.R
import com.stas.whms.bean.GoodsInfo
import com.stas.whms.bean.LoginInfo
import com.stas.whms.bean.ReasonInfo
import com.stas.whms.bean.SaveInBoundAuditReqInfo
import com.stas.whms.bean.ScannerRequestInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityAdjustmentLibraryBinding
import com.stas.whms.utils.StasHttpRequestUtil

@Route(path = RoutePathConfig.ROUTE_ADJUSTMENT_LIBRARY)
class AdjustmentLibraryActivity : BaseMvvmActivity<ActivityAdjustmentLibraryBinding, BaseViewModel>() {

    private val REQ_SCANNER_GET = 3
    private val REQ_SCANNER_GET_REASON = 4
    private val REQ_SCANNER_SAVE = 5
    private var mGoodsInfo: GoodsInfo? = null
    private var mReasonInfoList = arrayListOf<ReasonInfo>()
    private var mReasonList = arrayListOf<String>()

    override fun initView() {
        title = "在库调整"
        getData(null, REQ_SCANNER_GET_REASON)
    }

    override fun onViewEvent() {
        mDataBinding.cetMadeFinishedTag.setOnFocusChangeListener { view, b ->
            if (!b) {
//                getData(REQ_SCANNER_GET)
            }
        }
        // 退库原因
        mDataBinding.cetReason.setOnClickListener {
            BottomListDialog(this).setItems(mReasonList)
                .setOnConfirmSelectListener { position: Int, name: String? ->
                    mDataBinding.cetReason.text = name
                }
                .setCurrentItem(if (mReasonList.size > 0) mReasonList.indexOf(mDataBinding.cetReason.text.toString()) else 0)
                .show()
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
        mDataBinding.cetRemark.addTextChangedListener (object: SimpleTextWatcher() {
            override fun afterTextChanged(editable: Editable?) {
                super.afterTextChanged(editable)
                "${editable?.length}/100".also { mDataBinding.tvLimit.text = it }
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_adjustment_library
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

    private fun getData(res: String?, type: Int) {
        if (type == REQ_SCANNER_GET && res?.isEmpty() == true) return
        val req = ScannerRequestInfo()
        req.PdaID = AndroidUtil.getIpAddress()
        req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
        req.TextID = if (type == REQ_SCANNER_GET) "1" else "2"
        req.QrCode = res
//        req.QrCode =
//            if (type == REQ_SCANNER_GET) "DISC5060020000010091000210125104151120712305152071530815408155092123810-E0150                095440-12800J0000002Z999 0070380        00000000         "
//            else null
        showLoading()
        Thread {
            val result = StasHttpRequestUtil.queryAdjustmentLibrariesData(JSON.toJSONString(req))
            handleWebServiceResult(result, type)
        }.start()
    }

    private fun saveData() {
        if (mGoodsInfo == null) {
            ToastUtils.show("无待调整的的入库单！")
            return
        }
        if (mDataBinding.cetGoodsNum.text.toString().isEmpty()) {
            ToastUtils.show("数量不能为空！")
            return
        }
        if (mDataBinding.cetReason.text.toString().isEmpty()) {
            ToastUtils.show("请选择调整原因！")
            return
        }
        showLoading()
        Thread {
            val req = SaveInBoundAuditReqInfo()
            val listData = arrayListOf<GoodsInfo>()
            mGoodsInfo!!.Qty = mDataBinding.cetGoodsNum.text.toString()
            listData.add(mGoodsInfo!!)
            req.Remark = mDataBinding.cetRemark.text.toString().trim()
            req.ListData = listData
            req.ReasonID = getReasonID()
            req.PdaID = AndroidUtil.getIpAddress()
            req.TimeStamp = DateUtils.getCurrentDateMilTimeStr()
            val loginInfoStr = mMMKV.decodeString(MmkvConstants.MMKV_LOGIN_INFO)
            if (loginInfoStr != null) {
                val loginInfo = JSON.parseObject(loginInfoStr, LoginInfo::class.java)
                if (loginInfo != null) {
                    req.CreateBy = loginInfo.UserID
                }
            }
            val result = StasHttpRequestUtil.saveInBoundAuditData(JSON.toJSONString(req))
            handleWebServiceResult(result, REQ_SCANNER_SAVE)
        }.start()
    }

    private fun getReasonID(): String? {
        if (mReasonInfoList.size > 0) {
            val reason = mDataBinding.cetReason.text.toString()
            for (r in mReasonInfoList) {
                if (reason == r.ReasonName) {
                    return r.ReasonID
                }
            }
        }
        return ""
    }

    override fun handleWebServiceSuccess(response: WebServiceResponse?, fromSource: Int) {
        if (fromSource == REQ_SCANNER_GET) {
            if (response?.data != null) {
                val jArray = JSONObject.parseArray(response.data, GoodsInfo::class.java)
                if (jArray != null && jArray.size > 0) {
                    mGoodsInfo = jArray[0]
                    mDataBinding.cetMadeFinishedTag.setText(jArray[0].PartsNo)
                    mDataBinding.cetRotaryDesignation.text = jArray[0].TagSerialNo
                    mDataBinding.cetForeworkNum.text = jArray[0].FromProCode
                    mDataBinding.cetInLibraryState.text = jArray[0].Status
                    mDataBinding.cetGoodsNum.setText(jArray[0].Qty)
                }
            }
        } else if (fromSource == REQ_SCANNER_GET_REASON) {
            val jArray = JSONObject.parseArray(response?.data, ReasonInfo::class.java)
            for (a in jArray) {
                mReasonInfoList.add(a)
                if (a is ReasonInfo) {
                    mReasonList.add(a.ReasonName!!)
                }
            }
//            if (mReasonList.size > 0) {
//                mDataBinding.cetReason.text = mReasonList[0]
//            }
        } else {
            ToastUtils.show("保存成功")
            finish()
        }
    }
}