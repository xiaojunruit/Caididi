package com.laoodao.caididi.ui.my.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flyco.tablayout.widget.MsgView;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.util.DateUtils;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.retrofit.user.ChatAvatar;
import com.laoodao.caididi.retrofit.user.LoginInfo;
import com.laoodao.caididi.retrofit.user.User;
import com.laoodao.caididi.ui.my.activity.ChatActivity;
import com.laoodao.caididi.utils.PermissionUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/8/11.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.AnswerHolder> {

    private Context mContext;
    private List<EMConversation> conversationList;
    private List<EMConversation> mData;
    private List<ChatAvatar> mAvatarData;
    private ConversationFilter conversationFilter;
    private OnDelListener mOnDelListener;

    public void setOnDelListener(OnDelListener onDelListener) {
        mOnDelListener = onDelListener;
    }

    public ContactsAdapter(Context context, List<EMConversation> objects, List<ChatAvatar> avatarData) {
        this.mContext = context;
        conversationList = new ArrayList<>();
        conversationList.addAll(objects);
        mData = new ArrayList<>();
        this.mData.addAll(objects);
        mAvatarData = new ArrayList<>();
        this.mAvatarData.addAll(avatarData);
    }

    public void setData(List<EMConversation> objects, List<ChatAvatar> avatarData) {
        conversationList = new ArrayList<>();
        conversationList.addAll(objects);
        mData = new ArrayList<>();
        this.mData.addAll(objects);
        mAvatarData = new ArrayList<>();
        this.mAvatarData.addAll(avatarData);
        notifyDataSetChanged();
    }

    private void requestPermission(String toUserName, AnswerHolder holder) {
        PermissionUtil.recordAudio(new PermissionUtil.RequestPermissionListener() {
            @Override
            public void success() {
                LoginInfo info = Global.info();
                ChatActivity.start(mContext, toUserName, holder.avatarData, info.avatar, holder.phone, holder.nameTxt);
            }

            @Override
            public void failure() {
                Toast.makeText(mContext, "请求权限失败,请前往设置开启权限！", Toast.LENGTH_SHORT).show();
            }
        }, new RxPermissions((Activity) mContext));
    }


    @Override
    public AnswerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnswerHolder(LayoutInflater.from(mContext).inflate(R.layout.item_ease_row_chat_history, parent, false));
    }

    public interface OnDelListener {
        void setOnDelListener(int count);
    }

    @Override
    public void onBindViewHolder(AnswerHolder holder, final int position) {
        String toUserName = mData.get(position).conversationId();
        holder.mRlChatList.setOnClickListener(v -> {
            requestPermission(toUserName, holder);

        });
        holder.mBtnDelete.setOnClickListener(v -> {
            //删除和某个user会话，如果需要保留聊天记录，传false
            EMClient.getInstance().chatManager().deleteConversation(toUserName, false);
            mData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            mOnDelListener.setOnDelListener(mData.size());
        });
        holder.mBtnRead.setOnClickListener(v -> {
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toUserName);
            //指定会话消息未读数清零
            conversation.markAllMessagesAsRead();
            //把一条消息置为已读
            conversation.markMessageAsRead(toUserName);
            notifyDataSetChanged();
        });
        // get conversation
        EMConversation conversation = mData.get(position);
        // get username or group id
        String username = conversation.conversationId();
        if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
            String groupId = conversation.conversationId();
            if (EaseAtMessageHelper.get().hasAtMeMsg(groupId)) {
                holder.motioned.setVisibility(View.VISIBLE);
            } else {
                holder.motioned.setVisibility(View.GONE);
            }
            // group message, show group avatar
            holder.avatar.setImageResource(R.mipmap.icon_default_avatar);
            EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
//            holder.name.setText(group != null ? group.getGroupName() : username);
        } else if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
            holder.avatar.setImageResource(R.mipmap.icon_default_avatar);
            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(username);
//            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
            holder.motioned.setVisibility(View.GONE);
        } else {
//            EaseUserUtils.setUserAvatar(mContext, username, holder.avatar);

            comparisonValue(holder, position);
//            EaseUserUtils.setUserNick(username, holder.name);
            holder.motioned.setVisibility(View.GONE);
        }
        EaseAvatarOptions avatarOptions = EaseUI.getInstance().getAvatarOptions();
        if (avatarOptions != null && holder.avatar instanceof EaseImageView) {
            EaseImageView avatarView = ((EaseImageView) holder.avatar);
            avatarView.setRadius(50);
            if (avatarOptions.getAvatarShape() != 0)
                avatarView.setShapeType(avatarOptions.getAvatarShape());
            if (avatarOptions.getAvatarBorderWidth() != 0)
                avatarView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
            if (avatarOptions.getAvatarBorderColor() != 0)
                avatarView.setBorderColor(avatarOptions.getAvatarBorderColor());
            if (avatarOptions.getAvatarRadius() != 0)
//                avatarView.setRadius(avatarOptions.getAvatarRadius());
                avatarView.setRadius(50);
        }
        if (conversation.getUnreadMsgCount() > 0) {
            // show unread message count
            holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }
        if (conversation.getAllMsgCount() != 0) {
            // show the content of latest message
            EMMessage lastMessage = conversation.getLastMessage();
            String content = null;
            if (cvsListHelper != null) {
                content = cvsListHelper.onSetItemSecondaryText(lastMessage);
            }
            holder.message.setText(EaseSmileUtils.getSmiledText(mContext, EaseCommonUtils.getMessageDigest(lastMessage, (mContext))),
                    TextView.BufferType.SPANNABLE);
            if (content != null) {
                holder.message.setText(content);
            }
            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }
    }

    public Filter getFilter() {
        conversationFilter = new ConversationFilter(conversationList);
        return conversationFilter;
    }

    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = mData;
                results.count = mData.size();
            } else {
                if (mData.size() > mOriginalValues.size()) {
                    mOriginalValues = mData;
                }
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.conversationId();

                    EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
                    if (group != null) {
                        username = group.getGroupName();
                    } else {
                        EaseUser user = EaseUserUtils.getUserInfo(username);
                        // TODO: not support Nick anymore
//                        if(user != null && user.getNick() != null)
//                            username = user.getNick();
                    }

                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mData.clear();
            if (results.values != null) {
                mData.addAll((List<EMConversation>) results.values);
                notifyDataSetChanged();
            }
        }
    }


    private void comparisonValue(AnswerHolder holder, int position) {
        for (int i = 0; i < mAvatarData.size(); i++) {
            if (mAvatarData.get(i).uid.equals(mData.get(position).conversationId())) {
                Glide.with(mContext).load(mAvatarData.get(i).avatar).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.avatar);
                holder.avatarData = mAvatarData.get(i).avatar;
                holder.phone = mAvatarData.get(i).contacmobile;
                holder.name.setText(mAvatarData.get(i).nickname);
                holder.nameTxt = mAvatarData.get(i).nickname;
                break;
            }
        }
    }

    private EaseConversationList.EaseConversationListHelper cvsListHelper;

    public void setCvsListHelper(EaseConversationList.EaseConversationListHelper cvsListHelper) {
        this.cvsListHelper = cvsListHelper;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    class AnswerHolder extends RecyclerView.ViewHolder {

        /**
         * who you chat with
         */
        TextView name;
        /**
         * unread message count
         */
        MsgView unreadLabel;
        /**
         * content of last message
         */
        TextView message;
        /**
         * time of last message
         */
        TextView time;
        /**
         * avatar
         */
        ImageView avatar;
        /**
         * status of last message
         */
        View msgState;
        TextView motioned;
        RelativeLayout mRlChatList;
        Button mBtnDelete;
        Button mBtnRead;
        String avatarData;
        String phone;
        String nameTxt;

        public AnswerHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            unreadLabel = (MsgView) itemView.findViewById(R.id.unread_msg_number);
            message = (TextView) itemView.findViewById(R.id.message);
            time = (TextView) itemView.findViewById(R.id.time);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            msgState = itemView.findViewById(R.id.msg_state);
            motioned = (TextView) itemView.findViewById(R.id.mentioned);
            mRlChatList = (RelativeLayout) itemView.findViewById(R.id.rl_chat_list);
            mBtnDelete = (Button) itemView.findViewById(R.id.btn_delete);
            mBtnRead = (Button) itemView.findViewById(R.id.btn_read);
        }
    }

    public interface OnClickListener {
        void setOnClickListener();
    }

}
