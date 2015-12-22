package com.example.cpl;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	public static final String SEARCH_HISTORY = "search_history";

	private ImageView mIvMenuSearch;

	private View mVBg;

	private MaterialLayout ml;
	private ImageView mIvBack;
	private EditText mEtSearch;
	private ImageView mIvSearchQuery;
	private LinearLayout mLlHistory;
	private ListView mLvAuto;
	private TextView mTvClean;

	private SearchAutoAdapter mSearchAutoAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}

	private void init() {
		initView();
	}

	private void initView() {
		mIvMenuSearch = (ImageView) findViewById(R.id.ivMenuSearch);

		mVBg = (View) findViewById(R.id.vBg);

		ml = (MaterialLayout) findViewById(R.id.ml);
		mIvBack = (ImageView) findViewById(R.id.ivBack);
		mEtSearch = (EditText) findViewById(R.id.etSearch);
		mIvSearchQuery = (ImageView) findViewById(R.id.ivSearchQuery);
		mLlHistory = (LinearLayout) findViewById(R.id.llHistory);
		mLvAuto = (ListView) findViewById(R.id.lvAuto);
		mTvClean = (TextView) findViewById(R.id.tvClean);

		mIvMenuSearch.setOnClickListener(this);
		mVBg.setOnClickListener(this);
		mIvBack.setOnClickListener(this);
		mIvSearchQuery.setOnClickListener(this);
		mTvClean.setOnClickListener(this);

		mSearchAutoAdapter = new SearchAutoAdapter(this, 5, this);
		mLvAuto.setAdapter(mSearchAutoAdapter);
		mLvAuto.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				SearchAutoData data = (SearchAutoData) mSearchAutoAdapter.getItem(position);
				mEtSearch.setText(data.getContent());
				mIvSearchQuery.performClick();
			}
		});

		mEtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mSearchAutoAdapter.performFiltering(s);
				if (s.length() > 0 && mSearchAutoAdapter.getCount() > 0) {
					mLlHistory.setVisibility(View.VISIBLE);
				} else
					mLlHistory.setVisibility(View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivMenuSearch:// 搜索按钮
			clickMenuSearch();
			break;
		case R.id.vBg:
		case R.id.ivBack:// 返回按钮
			clickBack();
			break;
		case R.id.tvClean:// 清除历史记录
			clickClean();
			break;
		case R.id.ivSearchQuery:// 搜索按钮
			cliclSearchQuery();
			break;
		case R.id.auto_add:// "+"号按钮
			clickAdd(v);
			break;
		}
	}

	private void clickMenuSearch() {
		ml.setVisibility(View.VISIBLE);
		mVBg.setVisibility(View.VISIBLE);
		ml.push(mIvMenuSearch);
	}

	private void clickBack() {
		ml.pop();
		mEtSearch.setText("");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				ml.setVisibility(View.GONE);
				mVBg.setVisibility(View.GONE);
				mLlHistory.setVisibility(View.GONE);
			}
		}, 200);
	}

	private void clickClean() {
		cleanHistory();
		mSearchAutoAdapter.initSearchHistory();
		mSearchAutoAdapter.notifyDataSetChanged();
		mLlHistory.setVisibility(View.GONE);
	}

	private void cliclSearchQuery() {
		saveSearchHistory();
		mSearchAutoAdapter.initSearchHistory();
	}

	private void clickAdd(View v) {
		SearchAutoData data = (SearchAutoData) v.getTag();
		mEtSearch.setText(data.getContent());
	}

	/*
	 * 保存搜索记录
	 */
	private void saveSearchHistory() {
		String text = mEtSearch.getText().toString().trim();
		if (text.length() < 1) {
			return;
		}
		SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, 0);
		String longhistory = sp.getString(SEARCH_HISTORY, "");
		ArrayList<String> history = new ArrayList<String>();
		StringTokenizer token = new StringTokenizer(longhistory, ",");
		while (token.hasMoreTokens()) {
			if (history.size() > 50) {
				history.remove(0);
			}
			history.add(token.nextToken());
		}
		if (history.size() > 0) {
			int i;
			for (i = 0; i < history.size(); i++) {
				if (text.equals(history.get(i))) {
					history.remove(i);
					break;
				}
			}
			history.add(0, text);
		}
		if (history.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < history.size(); i++) {
				sb.append(history.get(i) + ",");
			}
			sp.edit().putString(SEARCH_HISTORY, sb.toString()).commit();
		} else {
			sp.edit().putString(SEARCH_HISTORY, text + ",").commit();
		}
	}

	// 清除搜索记录
	public void cleanHistory() {
		SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
		Toast.makeText(this, "清除成功", Toast.LENGTH_SHORT).show();
	}
}
