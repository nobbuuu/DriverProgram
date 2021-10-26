package com.haylion.android.orderlist.achievement

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amap.api.maps.model.LatLng
import com.amap.api.services.route.BusRouteResult
import com.amap.api.services.route.DriveRouteResult
import com.amap.api.services.route.RideRouteResult
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener
import com.amap.api.services.route.WalkRouteResult
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.haylion.android.R
import com.haylion.android.common.Const
import com.haylion.android.common.view.dialog.DialogUtils
import com.haylion.android.data.model.Order
import com.haylion.android.data.model.OrderStatus
import com.haylion.android.data.util.BusinessUtils
import com.haylion.android.data.util.StringUtil
import com.haylion.android.mvp.util.LogUtils
import com.haylion.android.orderdetail.map.ShowInMapNewActivity
import com.haylion.android.utils.AmapUtils
import com.haylion.android.utils.SpUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AppointMentAdapter : BaseQuickAdapter<Order, BaseViewHolder>(R.layout.book_order_list_item) {

    init {
        addChildClickViewIds(R.id.grab_order)
    }

    override fun getDefItemViewType(position: Int): Int {
        return data[position].orderType
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        var layoutId = R.layout.book_order_list_item
        if (viewType == Order.ORDER_TYPE_SHUNFENG) {
            layoutId = R.layout.shunfeng_order_list_item
        }
        return createBaseViewHolder(parent, layoutId)
    }

    override fun convert(holder: BaseViewHolder, order: Order) {

        val tvContactPassenger = holder.getView<TextView>(R.id.tv_contact_passenger)
        tvContactPassenger.setOnClickListener { v: View? ->
            DialogUtils.showRealCallDialog(context, order.userInfo?.phoneNum)
        }
        val carpoolingOrder = holder.getView<View>(R.id.carpooling_order)
        if (order.orderType == Order.ORDER_TYPE_SEND_CHILD) {
            if (order.isCarpoolOrder) { // 送你上学 - 拼车单
                carpoolingOrder.visibility = View.VISIBLE
            } else {
                carpoolingOrder.visibility = View.GONE
            }
        } else {
            carpoolingOrder.visibility = View.GONE
        }

        val tvShowMap = holder.getView<TextView>(R.id.viewmap_tv)
        tvShowMap.setOnClickListener { v: View? ->
            val intent = Intent(context, ShowInMapNewActivity::class.java)
            intent.putExtra(ShowInMapNewActivity.EXTRA_GRAB_ENABLED, false)
            val start = order.startAddr
            intent.putExtra(ShowInMapNewActivity.ORDER_START_ADDR, start)
            val end = order.endAddr
            intent.putExtra(ShowInMapNewActivity.ORDER_END_ADDR, end)
            context.startActivity(intent)
        }

        val tvOrderStatusHeader = holder.getView<TextView>(R.id.order_status_header)
        tvOrderStatusHeader.visibility = View.GONE
        val tvOrderNumber = holder.getView<TextView>(R.id.tv_order_number)
        tvOrderNumber.text = order.orderCode
        val tvOrderGetOn = holder.getView<TextView>(R.id.tv_get_on_addr)
        tvOrderGetOn.text = order.startAddr?.name
        val tvOrderGetOff = holder.getView<TextView>(R.id.tv_get_off_addr)
        tvOrderGetOff.text = order.endAddr?.name

        val orderTimeTv = holder.getView<TextView>(R.id.tv_order_time)
        var timeFormat: String ? = ""
        if (order.orderType == Order.ORDER_TYPE_SHUNFENG){
            timeFormat = order.estimateArriveTime
        }else{
            timeFormat = order.orderTime
        }
        if (!TextUtils.isEmpty(timeFormat)) {
            if (order.isParentOrder) {
                timeFormat = formatParentOrderDate(order.startTime, order.endTime)
            } else {
                try {
                    val milliSecond = BusinessUtils.stringToLong(order.orderTime, "yyyy-MM-dd HH:mm")
                    timeFormat = BusinessUtils.getDateToStringIncludeYearWhenCrossYear(milliSecond, "")
                    Log.d("AppointAdapter", "order time, timeFormat: $timeFormat")
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            if (order.orderType == Order.ORDER_TYPE_SHUNFENG){
                orderTimeTv?.text = "预约" + timeFormat + "送达"
            }else{
                orderTimeTv?.text = timeFormat
            }
        }

        var startTime: String = order.startTime ?: ""
        val endTime: String = order.endTime ?: ""
        if (!startTime.isNullOrEmpty() && !endTime.isNullOrEmpty() && startTime == endTime) {
            startTime = "今日"
        }
        val orderStatus = holder.getView<TextView>(R.id.order_status)
        val grabOrder = holder.getView<TextView>(R.id.grab_order)
        val orderDates = order.orderDates
        if (orderDates != null && orderDates.size > 0) {
            val takeSpan = StringUtil.setTextPartSizeColor(
                "每日 ",
                order.orderTime,
                " 取货",
                R.color.part_text_bg
            )
            orderStatus.text = takeSpan
            grabOrder.text = "选择抢单日期"
        } else {
            val takeSpan = StringUtil.setTextPartSizeColor(
                if (startTime == null) "" else "$startTime ",
                order.orderTime,
                " 取货",
                R.color.part_text_bg
            )
            orderStatus.text = takeSpan
            grabOrder.text = "抢单"
        }
        grabOrder.visibility = View.VISIBLE

        // 只有预约单大厅才显示“查看地图”
        if (order.orderStatus == OrderStatus.ORDER_STATUS_INIT.status) {
            tvShowMap.visibility = View.VISIBLE
        } else {
//            tvShowMap.setVisibility(GONE);
        }
        //订单类型
        val tvTotalDistance = holder.getView<TextView>(R.id.tv_total_distance)
        tvTotalDistance.visibility = View.VISIBLE
        AmapUtils.caculateDistance(
            order.startAddr.latLng,
            order.endAddr.latLng,
            object : OnRouteSearchListener {
                override fun onBusRouteSearched(busRouteResult: BusRouteResult, i: Int) {}
                override fun onDriveRouteSearched(driveRouteResult: DriveRouteResult, i: Int) {
                    val distance = driveRouteResult.paths[0].distance
                    if (order.orderType == Order.ORDER_TYPE_SHUNFENG) {
                        tvTotalDistance.text =
                            "取送距离" + BusinessUtils.formatDistance(distance.toDouble())
                    } else {
                        tvTotalDistance.text = BusinessUtils.formatDistance(distance.toDouble())
                    }
                }

                override fun onWalkRouteSearched(walkRouteResult: WalkRouteResult, i: Int) {}
                override fun onRideRouteSearched(rideRouteResult: RideRouteResult, i: Int) {}
            })
        if (order.orderType == Order.ORDER_TYPE_SHUNFENG) {
            orderStatus.visibility = View.VISIBLE
            val locationLat = SpUtils.getParam(Const.CUR_LATITUTE, "0") as String
            val locationLong = SpUtils.getParam(Const.CUR_LONGITUDE, "0") as String
            if (locationLat != "0" && locationLong != "0") {
                AmapUtils.caculateDistance(
                    LatLng(
                        locationLat.toDouble(),
                        locationLong.toDouble()
                    ), order.startAddr.latLng, object : OnRouteSearchListener {
                        override fun onBusRouteSearched(busRouteResult: BusRouteResult, i: Int) {}
                        override fun onDriveRouteSearched(
                            driveRouteResult: DriveRouteResult,
                            i: Int
                        ) {
                            val distance = driveRouteResult.paths[0].distance
                            val instanceFromme = holder.getView<TextView>(R.id.instance_fromme)
                            instanceFromme?.let {
                                instanceFromme.text =
                                    "距你" + BusinessUtils.formatDistance(distance.toDouble())
                            }
                        }

                        override fun onWalkRouteSearched(
                            walkRouteResult: WalkRouteResult,
                            i: Int
                        ) {
                        }

                        override fun onRideRouteSearched(
                            rideRouteResult: RideRouteResult,
                            i: Int
                        ) {
                        }
                    })
            }
            val orderMoney = holder.getView<TextView>(R.id.order_money)
            orderMoney.text = (order.totalMoney / 100).toString()
        }else{
            orderStatus.visibility = View.GONE
            val addressFromeTv = holder.getView<TextView>(R.id.tv_get_on_desc)
            val addressDesTv = holder.getView<TextView>(R.id.tv_get_off_desc)
            addressFromeTv.text = order.startAddr?.addressDetail
            addressDesTv.text = order.endAddr?.addressDetail
        }
    }

    private fun formatParentOrderDate(startTimeText: String, endTimeText: String): String {
        if (TextUtils.isEmpty(startTimeText) ||
            TextUtils.isEmpty(endTimeText)
        ) {
            return ""
        }
        var simpleDateFormat = SimpleDateFormat(
            "yyyy-MM-dd HH:mm", Locale.getDefault()
        )
        var startTime: Date? = Date()
        var endTime: Date? = Date()
        try {
            startTime = simpleDateFormat.parse(startTimeText)
            endTime = simpleDateFormat.parse(endTimeText)
        } catch (e: ParseException) {
            LogUtils.e("订单日期格式有误：$startTimeText - $endTimeText")
            e.printStackTrace()
        }
        val calendar = Calendar.getInstance()
        val thisYear = calendar[Calendar.YEAR]
        calendar.time = startTime
        val startYear = calendar[Calendar.YEAR]
        calendar.time = endTime
        val endYear = calendar[Calendar.YEAR]
        if (startYear != thisYear || endYear != thisYear) { // 订单跨年了
            simpleDateFormat = SimpleDateFormat(
                "yyyy年MM月dd日", Locale.getDefault()
            )
            val formattedTime =
                simpleDateFormat.format(startTime) + " - " + simpleDateFormat.format(endTime)
            simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return formattedTime + " " + simpleDateFormat.format(startTime)
        }
        simpleDateFormat = SimpleDateFormat(
            "MM月dd日", Locale.getDefault()
        )
        val formattedTime =
            simpleDateFormat.format(startTime) + " - " + simpleDateFormat.format(endTime)
        simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formattedTime + " " + simpleDateFormat.format(startTime)
    }
}