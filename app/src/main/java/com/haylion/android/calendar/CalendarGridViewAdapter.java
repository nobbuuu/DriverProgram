package com.haylion.android.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.data.bean.MainCalendarBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 显示日期的adapter
 */
public class CalendarGridViewAdapter extends BaseAdapter {

    /**
     * 日历item中默认id从0xff0000开始
     */
    private final static int DEFAULT_ID = 0xff0000;
    private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
    private Calendar calSelected = Calendar.getInstance(); // 选择的日历
    private int currentMonth = 0;
    private int firstDays;
    private Map<Long, Boolean> mMap;
    /**
     * 标注的日期
     */
    private List<String> markDates;

    private Context mContext;

    private Calendar calToday = Calendar.getInstance(); // 今日
    private ArrayList<MainCalendarBean> titles;

    public ArrayList<MainCalendarBean> getTitles() {
        return titles;
    }

    private ArrayList<MainCalendarBean> getDates() {

        Calendar mCal = calStartDate;
        mCal.set(Calendar.DATE, 1);
        int weekDay = mCal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
        if (weekDay < 0) {
            weekDay = 6;
        }
        int currentMonthLastDay = DateUtil.getCurrentMonthLastDay(mCal);
        int days = weekDay + currentMonthLastDay;
        int size = 35;
        if (days <= 28) {//闰月的时候
            size = 28;
        } else if (days > 35 && days <= 42) {
            size = 42;
        }

        UpdateStartDateForMonth();

        ArrayList<MainCalendarBean> alArrayList = new ArrayList<MainCalendarBean>();
        for (int i = 1; i <= size; i++) {
            MainCalendarBean bean = new MainCalendarBean();
            bean.setDate(calStartDate.getTime());
            for (Map.Entry<Long, Boolean> map : mMap.entrySet()) {
                if (map.getKey() == bean.getDate().getTime()) {
                    bean.setSelect(map.getValue());
                    break;
                }
            }
            alArrayList.add(bean);
            calStartDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        return alArrayList;
    }

    // construct
    public CalendarGridViewAdapter(Context context, Calendar cal, List<String> dates, Map<Long, Boolean> map) {
        calStartDate = cal;
        this.mContext = context;
        mMap = map;
        titles = getDates();
        this.markDates = dates;
//        initData(userId);
    }

    public void initData(String userId) {
        String starDate = DateFormatUtil.getTime(titles.get(0).getDate(), "yyyy-MM-dd");
        String endDate = DateFormatUtil.getTime(titles.get(titles.size() - 1).getDate(), "yyyy-MM-dd");
//        calenderMainPresenter.getCalenders(userId, starDate + Const.startTime, endDate + Const.endTime);
    }

    public void setCurrentMonth(int curMonth){
        currentMonth = curMonth;
    }

    public CalendarGridViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 整个Item
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout itemLayout = new LinearLayout(mContext);
        itemLayout.setLayoutParams(params);
        itemLayout.setId(position + DEFAULT_ID);
        itemLayout.setGravity(Gravity.CENTER);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setBackgroundColor(Color.WHITE);

        MainCalendarBean dataBean = (MainCalendarBean) getItem(position);
        Date myDate = dataBean.getDate();
        itemLayout.setTag(myDate);
        Calendar calCalendar = Calendar.getInstance();
        calCalendar.setTime(myDate);

        //添加标识lay
       /* LinearLayout.LayoutParams lay_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lay_params.height = DensityUtil.dip2px(5);
        LinearLayout topLay = new LinearLayout(mContext);
        topLay.setLayoutParams(lay_params);
        itemLayout.addView(topLay);*/

        // 显示日期day
        TextView textDay = new TextView(mContext);// 日期
        LinearLayout.LayoutParams text_params = new LinearLayout.LayoutParams(DensityUtil.dip2px(26), DensityUtil.dip2px(26));
        textDay.setGravity(Gravity.CENTER);
        int day = myDate.getDate(); // 日期

        int month = calCalendar.get(Calendar.MONTH);
        long time = calCalendar.getTime().getTime();
        /*if (mm.equals(currentMonth)) {
            textDay.setTextColor(Color.BLACK);
        } else {
            textDay.setTextColor(ResourcesUtils.getColor(R.color.cldcontent));
        }*/
        /** 设置标注日期颜色 */
        if (markDates != null) {
            String curDate = DateFormatUtil.getTime(time, "yyyy-MM-dd");
            Log.d("aaa", "curDate = " + curDate);
            for (String date : markDates) {
                if (date.contains(" ")) {
                    String[] tempArry = date.split(" ");
                    Log.d("aaa", "net_date = " + tempArry[0]);
                    if (tempArry[0].equals(curDate)) {
                        dataBean.setBlack(true);
                        break;
                    }
                }
            }
            if (dataBean.isBlack() && month == currentMonth) {
                textDay.setTextColor(Color.BLACK);
            } else {
                textDay.setTextColor(ResourcesUtils.getColor(R.color.cldcontent));
            }
        }
        textDay.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textDay.setText(String.valueOf(day));
        textDay.setId(position + DEFAULT_ID);
        itemLayout.addView(textDay, text_params);
        // 显示公历
        /*TextView chineseDay = new TextView(mContext);
        LinearLayout.LayoutParams chinese_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        chineseDay.setGravity(Gravity.CENTER_HORIZONTAL);
        chineseDay.setTextSize(9);
        CalendarUtil calendarUtil = new CalendarUtil(calCalendar);
        chineseDay.setText(calendarUtil.toString());
        itemLayout.addView(chineseDay, chinese_params);*/

       /* ImageView isWork = new ImageView(mContext);
        LinearLayout.LayoutParams isWork_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        isWork_params.topMargin = 5;
        isWork_params.bottomMargin = 5;
        isWork.setLayoutParams(isWork_params);
//        isWork.setImageResource(R.drawable.shape_circle_cal);
        itemLayout.addView(isWork);*/

        // 如果是当前日期则显示不同颜色
        if (equalsDate(calToday.getTime(), myDate)) {
           /* topLay.setBackgroundColor(ResourcesUtils.getColor(R.color.main_botcandy));
            if (dataBean.isWork()) {
                isWork.setImageResource(R.drawable.shape_circle_cal);
            } else {
                isWork.setImageResource(R.drawable.shape_circle_cal2);
            }*/
        } else {
//            topLay.setBackgroundColor(ResourcesUtils.getColor(R.color.white));
            /*if (dataBean.isWork()) {
                isWork.setImageResource(R.drawable.shape_circle_cal);
            } else {
            }*/
//            isWork.setImageResource(R.drawable.shape_circle_cal1);
        }

        if (dataBean.isSelect()) {
            textDay.setBackgroundResource(R.drawable.shape_circle_cal4);
            textDay.setTextColor(ResourcesUtils.getColor(R.color.white));
        } else {
            textDay.setBackgroundResource(R.drawable.shape_circle_cal5);
            if (dataBean.isBlack()) {
                textDay.setTextColor(ResourcesUtils.getColor(R.color.black));
            } else {
                textDay.setTextColor(ResourcesUtils.getColor(R.color.cldcontent));
            }
        }

        /*// 选择的item
        if (equalsDate(calSelected.getTime(), myDate) && dataBean.isBlack()) {
            dataBean.setSelect(!dataBean.isSelect());
            notifyDataSetChanged();
        }*/
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataBean.isBlack()) {
                    dataBean.setSelect(!dataBean.isSelect());
                    mMap.put(dataBean.getDate().getTime(), dataBean.isSelect());
                    notifyDataSetChanged();
                }
            }
        });
        return itemLayout;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @SuppressWarnings("deprecation")
    private Boolean equalsDate(Date date1, Date date2) {
        if (date1.getYear() == date2.getYear()
                && date1.getMonth() == date2.getMonth()
                && date1.getDate() == date2.getDate()) {
            return true;
        } else {
            return false;
        }

    }

    // 根据改变的日期更新日历
    // 填充日历控件用
    private void UpdateStartDateForMonth() {
        calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天

        // 星期一是2 星期天是1 填充剩余天数
        int iDay = 0;
        int iFirstDayOfWeek = Calendar.MONDAY;
        int iStartDay = iFirstDayOfWeek;
        if (iStartDay == Calendar.MONDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
            if (iDay < 0)
                iDay = 6;
        }
        if (iStartDay == Calendar.SUNDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
            if (iDay < 0)
                iDay = 6;
        }
        Log.e("tag", "iDay=" + iDay);
        calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
        firstDays = iDay;
//        calStartDate.add(Calendar.DAY_OF_MONTH, -1);// 周日第一位
    }

    public void setSelectedDate(Calendar cal) {
        calSelected = cal;
    }

   /* @Override
    public void showData(CalenderedBean dataBean) {
        if (dataBean.getError().equals(Const.success)) {
            List<CalenderedBean.DataBean> data = dataBean.getData();
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    String beginTime = data.get(i).getBeginTime();
                    Date timeb = DateFormatUtil.getTime(beginTime, Const.YMD_HMS);
                    String endTime = data.get(i).getEndTime();
                    Date endD = DateFormatUtil.getTime(endTime, Const.YMD_HMS);
                    long startL = timeb.getTime();
                    long endL = endD.getTime();
                    for (int j = 0; j < titles.size(); j++) {
                        Date currentD = titles.get(j).getDate();
                        String ymd = DateFormatUtil.getTime(currentD, Const.Y_M_D);
                        Date startDL = DateFormatUtil.getTime(ymd + Const.endTime, Const.YMD_HMS);
                        long startTL = startDL.getTime();
                        Date endDd = DateFormatUtil.getTime(ymd + Const.startTime, Const.YMD_HMS);
                        long endDL = endDd.getTime();
                        if (startTL >= startL && endDL <= endL) {
                            titles.get(j).setWork(true);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }
    }*/

}
