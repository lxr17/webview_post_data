package com.lxr.postdata.common;

import android.content.Context;
import android.webkit.JavascriptInterface;

import org.jsoup.Jsoup;

import java.io.IOException;


class AjaxInterceptJavascriptInterface {

    private static String interceptHeader = null;
    private WriteHandlingWebViewClient mWebViewClient = null;

    public AjaxInterceptJavascriptInterface(WriteHandlingWebViewClient webViewClient) {
        mWebViewClient = webViewClient;
    }

    public static String enableIntercept(Context context, byte[] data) throws IOException {
        if (interceptHeader == null) {
            interceptHeader = new String(
                    Utils.consumeInputStream(context.getAssets().open("interceptheader.html"))
            );
        }

        org.jsoup.nodes.Document doc = Jsoup.parse(new String(data));
        doc.outputSettings().prettyPrint(true);

        // Prefix every script to capture submits
        // Make sure our interception is the first element in the
        // header
        org.jsoup.select.Elements element = doc.getElementsByTag("head");
        if (element.size() > 0) {
            element.get(0).prepend(interceptHeader);
        }

        String pageContents = doc.toString();
        return pageContents;
    }

    /**
     * js调用该方法，将post参数给客户端
     *
     * @param ID   key值
     * @param body 参数
     */
    @JavascriptInterface
    public void customAjax(final String ID, final String body) {
        mWebViewClient.addAjaxRequest(ID, body);
    }


}
