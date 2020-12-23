package com.wangsc.mylocation.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.wangsc.mylocation.R;

public class DialogUtils {
        private static Dialog pd;
//        public static void show(Context context) {
//            show(context, true);
//        }
//
//        public static void show(Context context, boolean cancelable) {
//            try {
//                dismiss();
//                //拿到dialog
//                pd = getLoadingDialog(context);
//                //设置点击外围是否消失
//                pd.setCancelable(cancelable);
//                pd.show();
//            } catch (Exception e) {
//            }
//        }
////        这里的加载效果自己随意定义
//        public static Dialog getLoadingDialog(Context context) {
//            Dialog loading_dialog = new Dialog(context, R.style.dialog_loading);
//            View view  = LayoutInflater.from(context).inflate(R.layout.layout_loading,null);
//            loading_dialog.setContentView(view);
//            GifView loadingView = (GifView) view.findViewById(R.id.loadingView);
//            // 设置背景gif图片资源
//            loadingView.setMovieResource(R.raw.loading);
//            loadingView.setMinimumWidth(AppUtils.getScreenDispaly(context)[0]/4);
//            loadingView.setMinimumHeight(AppUtils.getScreenDispaly(context)[0]/4);
//            loading_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
//            loading_dialog.setCancelable(false);
//            return loading_dialog;
//        }


        //在这里直接做了判断，大胆的放心的使用，保证不会出现空指针
        public static void dismiss() {
            try {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                pd = null;
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                pd = null;
            }
        }


        public static boolean isShowing() {
            try {
                if (pd != null) {
                    return pd.isShowing();
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            return false;
        }

        /**
         * 设置dialog点击外部是否可以消失
         */
        public static void setCanceledOnTouchOutside(boolean b) {
            try {
                if (pd != null && pd.isShowing()) {
                    pd.setCanceledOnTouchOutside(b);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
}
