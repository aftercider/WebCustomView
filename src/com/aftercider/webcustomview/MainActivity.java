package com.aftercider.webcustomview;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
	private WebView myWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// myWebView�̃C���X�^���X���擾
		myWebView = (WebView) findViewById(R.id.webview);

		// �J�X�^��WebView��ݒ肷��
		myWebView.setWebViewClient(new CustomWebView());

		// Google��\������
		myWebView.loadUrl("http://www.mizukinana.jp/blog/index.html");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Google");
		menu.add(0, 1, 1, "IT TRICK");
		menu.add(0, 2, 2, "ERROR");
		return true;
	}

	// �I�v�V�������j���[�I�����ꂽ�ꍇ�A�I�����ڂɍ��킹��
	// WebView�̕\����URL��ύX����B
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		int itemId = item.getItemId();
		switch (itemId) {
		case 0:
			myWebView.loadUrl("http://www.mizukinana.jp/blog/index.html");
			break;
		case 1:
			HTMLTask preTask = new HTMLTask();
			preTask.execute();
			break;
		case 2:
			String summary = "<html><body><iframe src='http://www.mizukinana.jp/blog/index.html' height=1200 width=1200>���̕����� iframe �Ή��̃u���E�U�Ō��Ă��������B</iframe></body></html>";
			myWebView.loadData(summary, "text/html", null);
			break;
		}
		return true;
	}

	// Android��Back�L�[�������ꂽ���̃C�x���g���󂯎��A
	// �O��\�������y�[�W�ɖ߂�B
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {
			myWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// �O����
	public class HTMLTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient c = new DefaultHttpClient();
			HttpGet g = new HttpGet("http://mizukinana.jp/blog/index.html");
			String ret = "";

			HttpResponse response = null;
			try {
				response = c.execute(g);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
				try {
					ret = EntityUtils.toString(response.getEntity(), "UTF-8");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
			}
			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			result = convert(result);
		}
		protected String convert(String original){
			String ret = "";
			String start = "<div id=\"menu\">";
			String end = "<div id=\"container\">";
			ret += original.substring(0, original.indexOf(start));
			ret += original.substring(original.indexOf(end)+end.length() , original.length());
			myWebView.loadData(ret, "text/html; charset=utf-8", "utf-8");
			return ret;
		}
	}

	/**
	 * WebViewClient�N���X���p�������J�X�^��WebView�i�����N���X�j
	 * 
	 */
	private class CustomWebView extends WebViewClient {

		// �y�[�W�̓ǂݍ��݊J�n
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Toast.makeText(view.getContext(), "onPageStarted",
					Toast.LENGTH_LONG).show();
		}

		// �y�[�W�̓ǂݍ��݊���
		@Override
		public void onPageFinished(WebView view, String url) {
			Toast.makeText(view.getContext(), "onPageFinished",
					Toast.LENGTH_LONG).show();
		}

		// �y�[�W�̓ǂݍ��ݎ��s
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			Toast.makeText(view.getContext(), "onReceivedError",
					Toast.LENGTH_LONG).show();
		}
	}
}