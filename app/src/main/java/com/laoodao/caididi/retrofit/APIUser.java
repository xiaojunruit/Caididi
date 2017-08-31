package com.laoodao.caididi.retrofit;

import com.laoodao.caididi.common.api.FarlandPage;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.api.Result;
import com.laoodao.caididi.retrofit.main.FollowUser;
import com.laoodao.caididi.retrofit.user.AbortTime;
import com.laoodao.caididi.retrofit.user.BuyListInfo;
import com.laoodao.caididi.retrofit.user.ChatAvatar;
import com.laoodao.caididi.retrofit.user.CheckMessage;
import com.laoodao.caididi.retrofit.user.DemKind;
import com.laoodao.caididi.retrofit.user.DemandDetail;
import com.laoodao.caididi.retrofit.user.Dynamic;
import com.laoodao.caididi.retrofit.user.DynamicMenu;

import com.laoodao.caididi.retrofit.user.ExperList;
import com.laoodao.caididi.retrofit.user.FarmlandDetail;
import com.laoodao.caididi.retrofit.user.FarmlandDetailList;
import com.laoodao.caididi.retrofit.user.GreensRecord;
import com.laoodao.caididi.retrofit.user.Invite;
import com.laoodao.caididi.retrofit.user.InviteList;
import com.laoodao.caididi.retrofit.user.LandOperate;

import com.laoodao.caididi.retrofit.user.FarmlandManagement;

import com.laoodao.caididi.retrofit.user.LoginInfo;
import com.laoodao.caididi.retrofit.user.MechanicalType;
import com.laoodao.caididi.retrofit.user.Message;
import com.laoodao.caididi.retrofit.user.MessageDetail;
import com.laoodao.caididi.retrofit.user.MyAnswer;
import com.laoodao.caididi.retrofit.user.MyComment;
import com.laoodao.caididi.retrofit.user.MyProblem;
import com.laoodao.caididi.retrofit.user.Notice;
import com.laoodao.caididi.retrofit.user.OpDetail;
import com.laoodao.caididi.retrofit.user.OperateHistory;
import com.laoodao.caididi.retrofit.user.OrderDetail;
import com.laoodao.caididi.retrofit.user.Qrcode;
import com.laoodao.caididi.retrofit.user.ReserveList;
import com.laoodao.caididi.retrofit.user.ReserveDetail;
import com.laoodao.caididi.retrofit.user.Collection;
import com.laoodao.caididi.retrofit.user.CollectionDetail;
import com.laoodao.caididi.retrofit.user.User;
import com.laoodao.caididi.retrofit.user.WantKnowAnswer;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by ezy on 15-9-29.
 */


public interface APIUser {


    @GET("?act=m_home")
    Observable<Result<LoginInfo>>
    loginInfo();

    /**
     * 退出
     *
     * @return
     */
    @GET("?act=cdd&op=logout")
    Observable<Response>
    logout();

    /**
     * 发送手机验证码
     *
     * @param phone 手机号
     * @param rule  规则
     * @return
     */
    @GET("?act=cdd&op=send_code")
    Observable<Response>
    sendCode(@Query("phone") String phone, @Query("rule") String rule);

    /**
     * 登录
     *
     * @param account  登录账号
     * @param password 登录密码
     * @param clientId 机器id
     * @return
     */
    @FormUrlEncoded
    @POST("?act=cdd&op=login")
    Observable<Result<LoginInfo>>
    login(@Field("m") String account, @Field("p") String password, @Field("ClientID") String clientId);


    // 第三方注册
    @FormUrlEncoded
    @POST("?act=cdd&op=login_third")
    Observable<Result<LoginInfo>>
    login3rd(@Field("platform") String platform, @Field("openId") String openId,
             @Field("token") String token, @Field("ClientID") String clientId,
             @Field("type") int type, @Field("mobile") String mobile, @Field("code") String code,
             @Field("pwd") String pwd
    );

    // 第三方登陆
    @FormUrlEncoded
    @POST("?act=cdd&op=login_third")
    Observable<Result<LoginInfo>>
    login3rd(@Field("platform") String platform, @Field("openId") String openId,
             @Field("token") String token, @Field("ClientID") String clientId
    );

    /**
     * 手机注册
     *
     * @param account  账号
     * @param code     验证码
     * @param password 密码
     * @param clientId 机器码
     * @return
     */
    @FormUrlEncoded
    @POST("index.php?act=cdd&op=register")
    Observable<Result<LoginInfo>>
    register(@Field("phone") String account, @Field("code") String code, @Field("password") String password, @Field("ClientID") String clientId);

    /**
     * 修改昵称
     *
     * @param nickname 新昵称
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_home&op=edit_nick")
    Observable<Response>
    updateNickName(@Field("nickname") String nickname);


    /**
     * 上传头像
     *
     * @param avatar 头像表单下标标识
     * @return
     */
    @Multipart
    @POST("?act=m_home&op=avatar")
    Observable<Response>
    editAvatar(@Part("avatar\";filename=\"avatar.jpeg") RequestBody avatar);

    /**
     * 修改手机号
     *
     * @param phone 新手机号
     * @param code  验证码
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_home&op=edit_phone")
    Observable<Result>
    updatePassword(@Field("phone") String phone, @Field("code") String code);


    // 找回密码:验证
    @FormUrlEncoded
    @POST("?act=cdd&op=getback_auth")
    Observable<Result<Map<String, String>>>
    getbackAuth(@Field("phone") String account, @Field("code") String code);

    // 找回密码:重置
    @FormUrlEncoded
    @POST("?act=cdd&op=getback_reset")
    Observable<Result<LoginInfo>>
    getbackReset(@Field("token") String token, @Field("password") String password, @Field("password_confirm") String confirm, @Field("ClientID") String clientId);

    /**
     * 农田列表
     *
     * @param page 页数
     * @return
     */
    @GET("?act=m_cropland")
    Observable<FarlandPage<FarmlandManagement>>
    cropland(@Query("page") int page);

    /**
     * 获取机械操作种类
     *
     * @return
     */
    @GET("?act=m_cropland&op=getMachine")
    Observable<Result<List<MechanicalType>>>
    getMachine();


    /**
     * 动态菜单
     *
     * @return
     */
    @GET("?act=menu&op=center")
    Observable<Result<List<DynamicMenu>>>
    dynamicMenu();


    @FormUrlEncoded
    @POST("?act=m_cropland&op=addOperate")
    Observable<Response>
    addOperate(@Field("crops_name[]") String[] cropsName, @Field("crops_money[]") String[] cropsMoney,
               @Field("machine_name[]") String[] machineName, @Field("machine_money[]") String[] machineMoney,
               @Field("cropland_id") int croplandid, @Field("type") int type, @Field("type_name") String typeName,
               @Field("remark") String remark, @Field("artificial") String artificial,
               @Field("operate_time") String operateTime
            , @Field("id") String id);

    /**
     * 操作详情
     *
     * @return
     */
    @GET("?act=m_cropland&op=opDetail")
    Observable<Result<OpDetail>>
    opDetail(@Query("id") int opId);


    /**
     * 我的提问
     *
     * @param page 页数
     * @return
     */
    @GET("?act=m_home&op=my_question")
    Observable<Page<MyProblem>>
    myProblem(@Query("page") int page);


    /**
     * 我的评论
     *
     * @param page 页数
     * @return
     */
    @GET("?act=m_home&op=my_comment")
    Observable<Page<MyComment>>
    myComments(@Query("page") int page);

    /**
     * 我的回答
     *
     * @param page 页数
     * @return
     */
    @GET("?act=m_home&op=my_answer")
    Observable<Page<MyAnswer>>
    myAnswer(@Query("page") int page);

    /**
     * 想知道答案
     *
     * @param page 页数
     * @return
     */
    @GET("?act=m_home&op=iwanttoknow")
    Observable<Page<WantKnowAnswer>>
    wantKnowAnswer(@Query("page") int page);

    /**
     * 我的收藏
     *
     * @param page 页数
     * @return
     */
    @GET("?act=m_home&op=my_favorite")
    Observable<Page<WantKnowAnswer>>
    myFavorite(@Query("page") int page);

    /**
     * 个人中心--关注
     *
     * @param page 页数
     * @param type 1:我关注了谁(默认)  2：谁关注了我
     * @return
     */
    @GET("?act=m_home&op=attention")
    Observable<Page<FollowUser>>
    myFollow(@Query("page") int page, @Query("type") int type);

    /**
     * 检查消息是否已读
     *
     * @return
     */
    @GET("?act=m_message")
    Observable<Result<CheckMessage>>
    checkMes();

    /**
     * 清除消息
     *
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_message&op=flushall")
    Observable<Response>
    clearMsg(@Field("tag") String tag);


    /**
     * @param type 1动态消息列表，2回复消息列表，3通知
     * @param page
     * @return
     */
    @GET("?act=m_message&op=my_message")
    Observable<Page<Message>>
    readMes(@Query("type") int type, @Query("page") int page);

    /**
     * 他人首页
     *
     * @param cid 他人id
     * @return
     */
    @GET("?act=c_home")
    Observable<Result<User>>
    user(@Query("cid") int cid);

    @GET("?act=c_home&op=dynamic&page_size=20")
    Observable<Page<Dynamic>>
    dynamic(@Query("cid") int cid, @Query("page") int page);

    /**
     * 他人提问
     *
     * @param cid  他人id
     * @param page 页数
     * @return
     */
    @GET("?act=c_home&op=question")
    Observable<Page<MyProblem>>
    userProblem(@Query("cid") int cid, @Query("page") int page);

    /**
     * 他的回答
     *
     * @param cid  他人id
     * @param page 页数
     * @return
     */
    @GET("?act=c_home&op=answer")
    Observable<Page<MyAnswer>>
    userAnswer(@Query("cid") int cid, @Query("page") int page);

    /**
     * 他人关注
     *
     * @param page 页数
     * @param type 1:他的关注(默认)  2：他的粉丝
     * @return
     */
    @GET("?act=c_home&op=attention")
    Observable<Page<FollowUser>>
    userFollow(@Query("page") int page, @Query("type") int type, @Query("cid") int cid);

    /**
     * 修改个性签名
     *
     * @param signature 签名内容
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_home&op=edit_signature")
    Observable<Response>
    signature(@Field("signature") String signature);

    /**
     * 修改密码
     *
     * @param oldPwd   旧密码
     * @param newPwd   新密码
     * @param cnNewPwd 确认密码
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_home&op=edit_pwd")
    Observable<Response>
    updatePassword(@Field("old_pwd") String oldPwd, @Field("new_pwd") String newPwd, @Field("cn_new_pwd") String cnNewPwd);

    /**
     * 设置是否推送
     *
     * @param isReceivePush 是否推送
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_home&op=set_receive_push")
    Observable<Response>
    setPush(@Field("is_receive_push") int isReceivePush);


    @POST("?act=m_cropland&op=addLand")
    Observable<Response>
    addFarmland(@Body RequestBody body);


    @GET("?act=m_message&op=noticedetail")
    Observable<Result<MessageDetail>>
    getMessageDetail(@Query("id") int id);


    /**
     * 农田操作
     */

    @GET("?act=m_cropland&op=landOperate")
    Observable<Result<List<LandOperate>>>
    farmlandOperate();

    /**
     * 农田详情
     */

    @GET("?act=m_cropland&op=landDetail")
    Observable<Result<FarmlandDetail>>
    landDetail(@Query("id") int id);

    /**
     * 农田详情列表
     */
    @GET("?act=m_cropland&op=landDetailList")
    Observable<Page<FarmlandDetailList>>
    landDetailList(@Query("id") int id, @Query("page") int page);


    /**
     * 农田详情列表
     */
    @GET("?act=demand&op=demlist")
    Observable<Page<BuyListInfo>>
    buyListInfo(@Query("page") int page, @Query("area_id") String areaId,
                @Query("kw") String kw, @Query("expire_time") String expireTime,
                @Query("expire_id") String expireId, @Query("cid ") String cid);


    /**
     * 求购详情
     *
     * @param id 求购id
     * @return
     */
    @GET("?act=demand&op=demDetail")
    Observable<Result<DemandDetail>>
    demandDetail(@Query("id") int id);

    /**
     * 查询品类
     *
     * @return
     */
    @GET("?act=demand&op=demkind")
    Observable<Result<List<DemKind>>>
    listDemKind();

    /**
     * 查询截止时间列表
     *
     * @return
     */
    @GET("?act=vegetable&op=vexpire")
    Observable<Result<List<AbortTime>>>
    listAbortTime();

    /**
     * 求购搜索历史
     *
     * @return
     */
    @GET("?act=demand&op=demhistory")
    Observable<Result<List<String>>>
    searchHistroy();

    /**
     * 求购搜索推荐
     *
     * @return
     */
    @GET("?act=demand&op=dempromote")
    Observable<Result<List<String>>>
    buyRecommendedKeyword();

    /**
     * 历史记录上报
     *
     * @param kw 关键字
     * @return
     */
    @FormUrlEncoded
    @POST("?act=demand&op=demkw")
    Observable<Response>
    saveSearch(@Field("kw") String kw);


    /**
     * 通知
     *
     * @return
     */
    @GET("?act=m_message&op=notice")
    Observable<Page<Notice>>
    notice(@Query("page") int page);

    /**
     * 消息
     *
     * @param page
     * @return
     */
    @GET("?act=m_message&op=notice")
    Observable<Page<Notice>>
    messageNotice(@Query("page") int page);

    /**
     * 消息标记已读
     *
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_message&op=readnotice")
    Observable<Response>
    readnotice(@Field("id") int id);


    /**
     * 求购收藏
     *
     * @param id   求购id
     * @param type 1收藏 2取消收藏
     * @return
     */
    @FormUrlEncoded
    @POST("?act=demand&op=favorite")
    Observable<Result<Map<String, String>>>
    buyCollection(@Field("id") int id, @Field("type") int type);

    /**
     * 清除搜索历史
     *
     * @return
     */
    @GET("?act=demand&op=demdel")
    Observable<Result>
    clearHistory();

    /**
     * 我的专属二维码
     *
     * @return
     */
    @GET("?act=share&op=sharecode")
    Observable<Result<Qrcode>>
    sharecode();

    /**
     * 添加预约服务
     *
     * @param body
     * @return
     */
    @POST("?act=m_reserver&op=add_reserver")
    Observable<Result<Map<String, String>>>
    addReserve(@Body RequestBody body);

    /**
     * 我的未回答列表
     *
     * @param page 页数
     * @return
     */
    @GET("?act=m_home&op=my_unanswer")
    Observable<Page<MyProblem>>
    myUnAnswer(@Query("page") int page);

    /**
     * 预约列表
     *
     * @param page 页码
     * @param type 1:未完成（默认） 2：已完成
     * @return
     */
    @GET("?act=m_reserver")
    Observable<Page<ReserveList>>
    myReserver(@Query("page") int page, @Query("type") int type);

    /**
     * 预约详情
     *
     * @param id 预约id
     * @return
     */
    @GET("?act=m_reserver&op=reserDetail")
    Observable<Result<ReserveDetail>>
    reserDetail(@Query("id") int id);

    /**
     * 评价预约
     *
     * @param body
     * @return
     */
    @POST("?act=m_reserver&op=finish")
    Observable<Result<Map<String, String>>>
    reserveFinish(@Body RequestBody body);

    /**
     * 结算记录
     *
     * @param type 1:待结算 2：已结算
     * @return
     */
    @GET("?act=my_purchase")
    Observable<Page<Collection>>
    myPurchase(@Query("page") int page, @Query("type") int type);

    /**
     * 结算详情
     *
     * @param id 结算id
     * @return
     */
    @GET("?act=my_purchase&op=purpaydetail")
    Observable<Result<CollectionDetail>>
    purpayDetail(@Query("id") int id);

    /**
     * 结算列表-->订单详情
     *
     * @param id 结算详情ID
     * @return
     */
    @GET("?act=my_purchase&op=purdetail")
    Observable<Result<OrderDetail>>
    purdetail(@Query("id") int id, @Query("purchase_code") String purchase_code);

    /**
     * 操作历史接口
     *
     * @param id 结算记录ID
     * @return
     */
    @GET("?act=my_purchase&op=operatehis")
    Observable<Result<OperateHistory>>
    operatehis(@Query("id") int id);

    /**
     * 立即收款
     *
     * @param ids id
     * @return
     */
    @FormUrlEncoded
    @POST("?act=my_purchase&op=dopay")
    Observable<Response>
    collection(@Field("id") String ids);


    /**
     * 绑定/解绑 第三方账号
     *
     * @param platform  //来源   wechat 、qq
     * @param openId
     * @param bindToken
     * @param type      1:绑定  2：解绑
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_home&op=third_bind")
    Observable<Result<Map<String, String>>>
    thirdBind(@Field("platform") String platform, @Field("openId") String openId, @Field("bind_token") String bindToken, @Field("type") int type);

    /**
     * 蔬菜销售记录
     *
     * @param page
     * @param type
     * @return
     */
    @GET("?act=my_purchase&op=order_list")
    Observable<Page<GreensRecord>>
    orderList(@Query("page") int page, @Query("type") int type);


    @GET("?act=m_home&op=invitelist")
    Observable<Page<InviteList>>
    inviteList(@Query("page") int page);

    @GET("?act=share&op=invite")
    Observable<Result<Invite>>
    invite();

    /**
     * IM上报后台
     *
     * @return
     */
    @POST("?act=emchat&op=send")
    Observable<Response> getSend(@Body RequestBody body);

    /**
     * 初始化IM数据
     *
     * @return
     */
    @GET("?act=cdd&op=init")
    Observable<Result<Map<String, String>>> getIMPwd();

    /**
     * 获取聊天列表头像
     * @return
     */
    @GET("?act=emchat&op=getAvatar")
    Observable<Result<List<ChatAvatar>>> getChatAvatar(@Query("uids") String uids);
    /**
     * 专家列表
     *
     * @param page
     * @return
     */
    @GET("?act=cdd&op=expert")
    Observable<Page<ExperList>> getExper(@Query("page") int page,@Query("kw") String kw);

}
