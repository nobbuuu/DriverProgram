package com.haylion.android.address;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.haylion.android.R;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchAddrActivity extends BaseActivity<SearchAddrContract.Presenter>
        implements SearchAddrContract.View, PoiSearch.OnPoiSearchListener {
    private static final String TAG = "SearchAddrActivity";
    private static final String CITY_NAME = "深圳市";

    @Override
    protected SearchAddrContract.Presenter onCreatePresenter() {
        return new SearchAddrPresenter(this);
    }

    @Override
    public void getConfigSuccess() {

    }

    @Override
    public void getConfigFail() {

    }


    AutoCompleteTextView search_edittext;
    ListView search_list;
    //    TextView city_tv= (TextView) findViewById(R.id.locate_city);
    ImageView search_for_place;
    //    SearchAdapter searchAdapter;
    SearchAddressAdapter searchAdapter;
    LinearLayout llBack;
    ImageView ivDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_address);

        String header = getIntent().getStringExtra("header");
        if (!TextUtils.isEmpty(header)) {
            TextView tvHeader = findViewById(R.id.tv_header);
            tvHeader.setText(header);
        }

        llBack = findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivDelete = findViewById(R.id.search_edit_delete);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edittext.setText("");
            }
        });

     /*   search_edittext = (AutoCompleteTextView) findViewById(R.id.search_edit);
        search_list = (ListView) findViewById(R.id.search_list);*/
        //    TextView city_tv= (TextView) findViewById(R.id.locate_city);
//        search_for_place = (ImageView) findViewById(R.id.search_for_place);

        search_edittext = (AutoCompleteTextView) findViewById(R.id.search_edit);
        search_list = (ListView) findViewById(R.id.search_list);
        search_for_place = (ImageView) findViewById(R.id.search_for_place);

        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = s.toString().trim();//获取自动提示输入框的内容
//                addressSearchByPoiMethod(content, CITY_NAME);
                addressSearchByInputtipsListen(content, CITY_NAME);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Tip> hashMap;
                hashMap = (HashMap<String, Tip>) searchAdapter.getItem(position);
                String name = hashMap.get("address").getName();
                String address = hashMap.get("address").getAddress();
                Log.d(TAG, "name:" + name + "address:" + address + ",latlng:" + hashMap.get("address").getPoint().toString());
                AddressInfo addressInfo = new AddressInfo();
                addressInfo.setName(name);
                addressInfo.setAddressDetail(address);
                addressInfo.setLatLng(new LatLng(hashMap.get("address").getPoint().getLatitude(), hashMap.get("address").getPoint().getLongitude()));
                Intent intent = new Intent();
                intent.putExtra("backHomeAddress", addressInfo);
                setResult(RESULT_OK, intent);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intent);
                finish();
/*                PositionEntity entity = (PositionEntity) searchAdapter.getItem(position);
                if (entity.latitue == 0 && entity.longitude == 0) {
                    PoiSearchTask poiSearchTask = new PoiSearchTask(getApplicationContext(), searchAdapter);
                    poiSearchTask.search(entity.address, RouteTask.getInstance(getApplicationContext()).getStartPoint().city);

                } else {
//            mRouteTask.setEndPoint(entity);
//            mRouteTask.search();
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap = (HashMap<String, String>) searchAdapter.getItem(position);
                    String name = hashMap.get("name");
                    String address = hashMap.get("address");
                    Log.d(TAG, "name:" + name + "address:" + address);
                    Intent intent = new Intent(SearchAddrActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }*/
            }
        });

        search_for_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.search_for_place:
/*                        PoiSearchTask poiSearchTask = new PoiSearchTask(getApplicationContext(), searchAdapter);
                        poiSearchTask.search(search_edittext.getText().toString(), RouteTask.getInstance(getApplicationContext()).getStartPoint().city);*/
                        break;
                }
            }
        });
    }


    private void addressSearchByInputtipsListen(String content, String cityName) {
        InputtipsQuery inputtipsQuery = new InputtipsQuery(content, cityName);//初始化一个输入提示搜索对象，并传入参数
        inputtipsQuery.setCityLimit(true);//将获取到的结果进行城市限制筛选
        Inputtips inputtips = new Inputtips(SearchAddrActivity.this, inputtipsQuery);//定义一个输入提示对象，传入当前上下文和搜索对象
        inputtips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> list, int returnCode) {
                if (returnCode == AMapException.CODE_AMAP_SUCCESS) {//如果输入提示搜索成功
                    List<HashMap<String, Tip>> searchList = new ArrayList<HashMap<String, Tip>>();
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, Tip> tipHashMap = new HashMap<String, Tip>();
                        tipHashMap.put("address", list.get(i));
                        searchList.add(tipHashMap);

                    }
                    searchAdapter = new SearchAddressAdapter(SearchAddrActivity.this, searchList);//新建一个适配器
                    SearchAddrActivity.this.search_list.setAdapter(searchAdapter);//为listview适配
                    searchAdapter.notifyDataSetChanged();//动态更新listview*/
/*                            SimpleAdapter aAdapter = new SimpleAdapter(getApplicationContext(), searchList, R.layout.search_list_item,
                                    new String[]{"name", "address"}, new int[]{R.id.search_item_title, R.id.search_item_text});
                            SearchAddrActivity.this.search_list.setAdapter(aAdapter);
                            aAdapter.notifyDataSetChanged();//动态更新listview*/
                } else {
                    Log.d(TAG, "error code: " + returnCode);

                }
            }
        });//设置输入提示查询的监听，实现输入提示的监听方法onGetInputtips()
        inputtips.requestInputtipsAsyn();//输入查询提示的异步接口实现
    }

    /**
     * 查询poi信息
     *
     * @param keyWord
     * @param cityCode
     */
    public void addressSearchByPoiMethod(String keyWord, String cityCode) {
        PoiSearch.Query query = new PoiSearch.Query(keyWord, "", cityCode);
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        //query.setPageNum(currentPage);//设置查询页码

        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);

        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        List<PoiItem> list = poiResult.getPois();
        List<HashMap<String, Tip>> searchList = new ArrayList<HashMap<String, Tip>>();
        for (int index = 0; index < list.size(); index++) {
            HashMap<String, Tip> tipHashMap = new HashMap<String, Tip>();
            Tip tip = new Tip();
            tip.setName(list.get(index).getTitle());
            tip.setAddress(list.get(index).getSnippet());
            tipHashMap.put("address", tip);
            Log.d(TAG, "onPoiSearched, title:" + list.get(index).getTitle() + ",snippet:" + list.get(index).getSnippet());
            searchList.add(tipHashMap);
        }

        searchAdapter = new SearchAddressAdapter(SearchAddrActivity.this, searchList);//新建一个适配器
        SearchAddrActivity.this.search_list.setAdapter(searchAdapter);//为listview适配
        searchAdapter.notifyDataSetChanged();//动态更新listview*/
/*        SimpleAdapter aAdapter = new SimpleAdapter(getApplicationContext(), searchList, R.layout.search_list_item,
                new String[]{"name", "address"}, new int[]{R.id.search_item_title, R.id.search_item_text});

        SearchAddrActivity.this.search_list.setAdapter(aAdapter);
        aAdapter.notifyDataSetChanged();//动态更新listview*/

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}


