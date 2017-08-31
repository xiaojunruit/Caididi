package com.laoodao.caididi.common.binding;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.Route;
import com.laoodao.caididi.common.app.RouterActivity;
import com.laoodao.caididi.common.util.Helper;
import com.laoodao.caididi.common.util.VerticalImageSpan;
import com.laoodao.caididi.common.widget.CommentsLayout;
import com.laoodao.caididi.retrofit.main.Link;
import com.laoodao.caididi.ui.weather.WeatherUtils;
import com.laoodao.caididi.ui.widget.DrawableTextView;

import java.util.List;

import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;
import ezy.lite.util.Spans;
import ezy.widget.view.IconTextButton;
import ezy.widget.view.SettingView;
import ezy.widget.view.ZeeBannerView;

public class CustomBindingAdapter {

    @BindingAdapter("dataList")
    public static void banner(ZeeBannerView banner, List dataList) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        if (!banner.hasItemViewFactory()) {
            banner.setItemViewFactory(new ZeeBannerView.ItemViewFactory() {
                @Override
                public View create(ViewGroup view, int position, Object item) {

                    final ImageView img = (ImageView) LayoutInflater.from(view.getContext()).inflate(R.layout.widget_banner_item, view, false);

                    String src = item instanceof Link ? ((Link) item).image : item.toString();

                    Glide.with(Global.context()).load(src).diskCacheStrategy(DiskCacheStrategy.SOURCE).centerCrop().into(img);
                    img.setOnClickListener(v -> {
                        Route.go(v.getContext(), ((Link) item).url);
                    });
                    return img;
                }
            });
        }
        banner.setDataList(dataList);
        banner.start();
    }


    @BindingAdapter({"android:src", "placeholder", "error"})
    public static void loadImage(ImageView view, String src, int placeholder, int error) {
        Glide.with(view.getContext().getApplicationContext())
                .load(src)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(placeholder)
                .dontAnimate()
                .into(view);
    }

    @BindingAdapter({"android:src", "placeholder"})
    public static void loadImage(ImageView view, String src, int placeholder) {
        Glide.with(view.getContext().getApplicationContext())
                .load(src)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(placeholder)
                .dontAnimate()
                .into(view);
    }

    @BindingAdapter("android:src")
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext().getApplicationContext()).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(view);
    }

    @BindingAdapter("avatar")
    public static void loadAvatar(ImageView view, String url) {


        Object ob = url;
        if (TextUtils.isEmpty(url)) {
            ob = Helper.toUri(view.getContext().getApplicationContext(), R.mipmap.icon_default_avatar);
        }

        Glide.with(view.getContext().getApplicationContext()).load(ob)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.icon_default_avatar).into(view);
    }

    @BindingAdapter("progress")
    public static void bindProgress(ProgressBar view, int value) {
        view.setProgress(value);
    }


    @BindingAdapter("html")
    public static void html(TextView view, String value) {
        if (value == null) {
            view.setText("");
        } else {
            view.setText(Html.fromHtml(value));
        }
    }

    @BindingAdapter("weatherIcon")
    public static void weatherIcon(ImageView view, String id) {

        if (!TextUtils.isEmpty(id)) {
            int resId = WeatherUtils.getWeatherResId(id);
            if (resId != -1) {
                view.setImageResource(resId);
            }
        }
    }


    @BindingAdapter({"cl_time"})
    public static void clTime(CommentsLayout view, String value) {
        if (value == null) {
            view.getTvTime().setText("");
            return;
        }
        view.getTvTime().setText(value);
    }

    @BindingAdapter("cl_avatar")
    public static void clAvatar(CommentsLayout view, String url) {
        Glide.with(view.getContext()).load(url).into(view.getmAvatar());
    }

    @BindingAdapter({"cl_nick_name"})
    public static void clNickName(CommentsLayout view, String value) {
        if (value == null) {
            view.getTvName().setText("");
            return;
        }
        view.getTvName().setText(value);
    }

    @BindingAdapter({"cl_address"})
    public static void clAddress(CommentsLayout view, String value) {
        if (value == null) {
            view.getTvAddress().setText("");
            return;
        }
        view.getTvAddress().setText(value);
    }

    @BindingAdapter({"cl_read_count"})
    public static void clReadCount(CommentsLayout view, String value) {
        if (value == null) {
            view.getTvRead().setText("");
            return;
        }
        view.getTvRead().setText(value);
    }


    @BindingAdapter("left")
    public static void iconLeft(TextView view, Drawable drawable) {
        int size = (int) view.getTextSize();
        int left = view.getPaddingLeft(), top = view.getPaddingTop();
        drawable.setBounds(left, top, left + size, top + size);
        view.setCompoundDrawables(drawable, null, null, null);
    }


    @BindingAdapter("settingValue")
    public static void value(SettingView view, String value) {
        view.setValue(value);
    }

    @BindingAdapter("status_color")
    public static void statusColor(TextView tv, int status) {
        if (status == 2) {
            tv.setTextColor(Color.parseColor("#2AB80E"));
        } else {
            tv.setTextColor(Color.parseColor("#ffac2d"));
        }
    }

    @BindingAdapter("shareImages")
    public static void shareImages(LinearLayout layout, List photos) {
        int count = layout.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView view = (ImageView) layout.getChildAt(i);
            if (i < photos.size()) {
                view.setVisibility(View.VISIBLE);
                Glide.with(layout.getContext()).load(photos.get(i)).into(view);
            } else {
                view.setImageBitmap(null);
                view.setVisibility(View.GONE);
            }
        }
    }

    @BindingAdapter({"android:text", "iconDrawable", "iconSize"})
    public static void textIcon(TextView view, String text, int drawable, int size) {

        size = Device.dp2px(size);

        LogUtil.e("text = " + text + ", drawable = " + drawable + ", size = " + size);
        Drawable d = view.getResources().getDrawable(drawable);
        d.setBounds(0, 0, size, size);
        view.setText(Spans.with(view).img(d).font(text).build());
    }


    @BindingAdapter("iconDrawable")
    public static void iconTextButton(IconTextButton view, String url) {

        Glide.with(view.getContext().getApplicationContext()).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                BitmapDrawable bd = new BitmapDrawable(view.getContext().getApplicationContext().getResources(), resource);
                view.setIconDrawable(bd);
            }
        });

    }


    @BindingAdapter("user_follow_status")
    public static void userTextIcon(ImageView view, int status) {
        if (status == Const.YES_FOLLOW) {
            view.setImageResource(R.mipmap.bg_follow_cancel);
        } else if (status == Const.TOGETHER_FOLLOW) {
            view.setImageResource(R.mipmap.bg_together);
        } else {
            view.setImageResource(R.mipmap.bg_follow);
        }
    }

    @BindingAdapter("settle_state")
    public static void settleState(ImageView view, int status) {
        if (status == Const.NO_SETTLE) {
            view.setBackgroundResource(R.mipmap.bg_no_settle);
        } else if (status == Const.IN_SETTLE) {
            view.setBackgroundResource(R.mipmap.bg_in_settle);
        } else if (status == Const.SU_SETTLE) {
            view.setBackgroundResource(R.mipmap.bg_su_settle);
        }
    }

    @BindingAdapter("booking_status")
    public static void bookingStatus(ImageView view, int status) {
        if (status == Const.IN_SETTLE) {
            view.setBackgroundResource(R.mipmap.bg_no_settle);
        } else if (status == Const.SU_SETTLE) {
            view.setBackgroundResource(R.mipmap.bg_su_reserve);
        }
    }

    @BindingAdapter("follow_status")
    public static void textIcon(DrawableTextView view, int status) {
        int color = 0;
        if (status == Const.YES_FOLLOW) {
            view.setText("取消关注");
            view.setGravity(Gravity.CENTER);
            color = Color.parseColor("#cccccc");
            view.setCompoundDrawables(null, null, null, null);
        } else if (status == Const.TOGETHER_FOLLOW) {
            view.setGravity(Gravity.CENTER);
            view.setText("互相关注");
            color = Color.parseColor("#cccccc");
            view.setCompoundDrawables(null, null, null, null);
        } else {
            Drawable drawable = view.getContext().getResources().getDrawable(R.mipmap.ic_focus_user);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            SpannableStringBuilder sb = new SpannableStringBuilder("+ 关注");

            ImageSpan sp = new VerticalImageSpan(drawable);

            sb.setSpan(sp, 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            view.setText(sb);

            color = Color.parseColor("#2AB80E");
        }

        RoundShape rs = new RoundShape();
        ShapeDrawable drawable = new ShapeDrawable(rs);
        drawable.getPaint().setColor(color);
        drawable.getPaint().setAntiAlias(true);
        drawable.getPaint().setStrokeCap(Paint.Cap.ROUND);
        drawable.getPaint().setStyle(Paint.Style.FILL);//描边
        view.setBackground(drawable);
    }

    public static class RoundShape extends RectShape {

        public RoundShape() {
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            RectF r = rect();
            float round = (r.bottom - r.top) / 2;
            canvas.drawRoundRect(r, round, round, paint);
        }
    }
}