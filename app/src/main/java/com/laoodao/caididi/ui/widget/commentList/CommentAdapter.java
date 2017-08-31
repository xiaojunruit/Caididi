package com.laoodao.caididi.ui.widget.commentList;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.util.SpannableStringUtils;
import com.laoodao.caididi.retrofit.main.AnswerReply;
import com.laoodao.caididi.retrofit.main.Comment;
import com.laoodao.caididi.ui.widget.commentList.spannable.CircleMovementMethod;
import com.laoodao.caididi.ui.widget.commentList.spannable.NameClickListener;
import com.laoodao.caididi.ui.widget.commentList.spannable.NameClickable;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.util.UI;

/**
 * des:评论适配器
 * Created by xsf
 * on 2016.07.11:11
 */
public class CommentAdapter {

    private Context mContext;
    private CommentListView mListview;
    private List<Comment> mDatas;

    public CommentAdapter(Context context) {
        mContext = context;
        mDatas = new ArrayList<Comment>();
    }

    public CommentAdapter(Context context, List<Comment> datas) {
        mContext = context;
        setDatas(datas);
    }

    public void bindListView(CommentListView listView) {
        if (listView == null) {
            throw new IllegalArgumentException("CommentListView is null....");
        }
        mListview = listView;
    }

    public void setDatas(List<Comment> datas) {
        if (datas == null) {
            datas = new ArrayList<Comment>();
        }

        mDatas = datas;
    }

    public List<Comment> getDatas() {
        return mDatas;
    }

    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    public Comment getItem(int position) {
        if (mDatas == null) {
            return null;
        }
        if (position < mDatas.size()) {
            return mDatas.get(position);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private int getColor(int redId) {
        return mContext.getResources().getColor(redId);
    }

    public interface OnItemClickListener {
        public void onClick(int position, Comment comment);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private View getView(final int position) {
        View convertView = View.inflate(mContext,
                R.layout.item_social_comment, null);
        TextView commentTv = (TextView) convertView.findViewById(R.id.commentTv);
        commentTv.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(mContext, "");

        Comment comment = mDatas.get(position);


        if (TextUtils.isEmpty(comment.replayMemberName)) {
            builder.append(comment.memberName);
            builder.setForegroundColor(getColor(R.color.colorAccent));
        } else {
            builder.append(comment.replayMemberName)
                    .setForegroundColor(getColor(R.color.colorAccent))
                    .append(" 回复 ")
                    .append(comment.memberName)
                    .setForegroundColor(getColor(R.color.colorAccent));
        }
        builder.append("：")
                .append(comment.replayContent);
        commentTv.setText(builder.create());

        commentTv.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClick(position, comment);
            }
        });


//        final CircleMovementMethod circleMovementMethod = new CircleMovementMethod(Color.parseColor("#CCCCCC"),
//                Color.parseColor("#ff00ff"));
//
//        final AnswerReply.Comment bean = mDatas.get(position);
//        String name = bean.memberName;
//  //      String id = bean.getId();
//        String toReplyName = "";
//        if (bean.id != null) {
//            toReplyName = bean.replayMemberName;
//        }
//
//        SpannableStringBuilder builder = new SpannableStringBuilder();
//        builder.append(setClickableSpan(name,bean.id, 0));
//
//        if (!TextUtils.isEmpty(toReplyName)) {
//
//            builder.append(" 回复 ");
//            builder.append(setClickableSpan(toReplyName,bean.replayMemberName, 1));
//        }
//        builder.append(": ");
//        //转换表情字符
//        String contentBodyStr = bean.replayContent;
//        //SpannableString contentSpanText = new SpannableString(contentBodyStr);
//        //contentSpanText.setSpan(new UnderlineSpan(), 0, contentSpanText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        builder.append(contentBodyStr);
//        commentTv.setText(builder);
//
//        commentTv.setMovementMethod(circleMovementMethod);
//        commentTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (circleMovementMethod.isPassToTv()) {
//                    mListview.getOnItemClickListener().onItemClick(position);
//                }
//            }
//        });
//        commentTv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (circleMovementMethod.isPassToTv()) {
//                    mListview.getOnItemLongClickListener().onItemLongClick(position);
//                    return true;
//                }
//                return false;
//            }
//        });


        return convertView;
    }

    @NonNull
    private SpannableString setClickableSpan(String textStr, String userId, int position) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new NameClickable(new NameClickListener(
                        subjectSpanText, userId), position), 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }

    public void notifyDataSetChanged() {
        if (mListview == null) {
            throw new NullPointerException("listview is null, please bindListView first...");
        }
        mListview.removeAllViews();
        if (mDatas == null || mDatas.size() == 0) {
            mListview.setVisibility(View.GONE);
            return;
        } else {
            mListview.setVisibility(View.VISIBLE);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < mDatas.size(); i++) {
            final int index = i;
            View view = getView(index);
            if (view == null) {
                throw new NullPointerException("listview item layout is null, please check getView()...");
            }

            mListview.addView(view, index, layoutParams);
        }

    }

}
