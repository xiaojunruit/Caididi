package com.laoodao.caididi.retrofit;

import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.api.Result;
import com.laoodao.caididi.retrofit.main.Answer;
import com.laoodao.caididi.retrofit.main.AnswerDetail;
import com.laoodao.caididi.retrofit.main.AnswerReply;
import com.laoodao.caididi.retrofit.main.Category;
import com.laoodao.caididi.retrofit.main.ChatMessage;
import com.laoodao.caididi.retrofit.main.Comment;
import com.laoodao.caididi.retrofit.main.FarmingDetail;
import com.laoodao.caididi.retrofit.main.FarmingList;
import com.laoodao.caididi.retrofit.main.FollowUser;
import com.laoodao.caididi.retrofit.main.Home;
import com.laoodao.caididi.retrofit.main.InitConfig;
import com.laoodao.caididi.retrofit.main.NewestInfo;
import com.laoodao.caididi.retrofit.main.Plants;
import com.laoodao.caididi.retrofit.main.Pos;
import com.laoodao.caididi.retrofit.main.Praise;
import com.laoodao.caididi.retrofit.main.ShareInfo;
import com.laoodao.caididi.retrofit.main.Vegetable;
import com.laoodao.caididi.retrofit.user.ConsultUser;
import com.laoodao.caididi.retrofit.weather.Weather;

import java.util.List;
import java.util.Map;

import ezy.boost.update.UpdateInfo;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by ezy on 15-9-29.
 */
public interface APIMain {


    @GET("?act=share&op=social")
    Observable<Result<ShareInfo>>
    socialShare(@Query("type") String type);

    @GET("?act=check_update")
    Observable<Result<UpdateInfo>>
    checkUpdate(@Query("appKey") String appKey, @Query("versionCode") int versionCode, @Query("channel") String channel);

    // 首页

    @GET("?act=index&op=init")
    Observable<Result<InitConfig>>
    init();

    /**
     * //关注作物-分类
     *
     * @return
     */
    @GET("?act=cdd&op=kind")
    Observable<Result<List<Category>>>
    followCropList2();


    /**
     * 关注作物
     *
     * @param ids 分类ID ,多个分类使用逗号链接 1,2,3,4,5...
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_cdd&op=add_crops_attention")
    Observable<Result<Map<String, String>>>
    followCrop(@Field("ids") String ids);

    @GET("?act=cdd&op=kind")
    Observable<Result<List<Plants>>>
    kindList(@Query("kw") String kw);


    /**
     * 提交问题
     *
     * @return
     */
    @POST("?act=m_cdd&op=add_ask")
    Observable<Response>
    addQuestion(@Body RequestBody body);

    /**
     * 最新回答列表接口
     *
     * @param page 页数
     * @param type new：最新，crops：作物 near：附近 user用户 默认不传为new
     * @param pos  经度纬度
     * @param city 切换城市id
     * @param kw   搜索关键字
     * @return
     */
    @GET("?act=cdd&op=newest")
    Observable<Page<Answer>>
    newAnswerList(@Query("page") int page, @Query("type") String type, @Query("pos") String pos, @Query("city") String city, @Query("kw") String kw);


    /**
     * 问题详情
     *
     * @param id 问题id
     * @return
     */
    @GET("?act=cdd&op=detail")
    Observable<Result<AnswerDetail>>
    answerDetailList(@Query("id") int id);


    /**
     * 想知道答案
     *
     * @param id   问题id
     * @param type 1:想知道答案（默认），2：取消想知道答案
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_cdd&op=wanttoknow")
    Observable<Response>
    wanttoknow(@Field("id") int id, @Field("type") int type);

    /**
     * 推荐好友列表
     *
     * @param page 页数
     * @return
     */
    @GET("?act=cdd&op=member_commend")
    Observable<Page<FollowUser>>
    followUserList(@Query("page") int page, @Query("kw") String kw);

    /**
     * 搜索推荐
     *
     * @return
     */
    @GET("?act=cdd&op=search_promote")
    Observable<Page<String>>
    recommendedKeyword();


    /**
     * 关注好友
     *
     * @param id   推荐好友id
     * @param type 1：关注 2：取消关注
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_cdd&op=add_member_attention")
    Observable<Result<Map<String, String>>>
    followUser(@Field("member_id") int id, @Field("type") int type);

    /**
     * 评论区
     *
     * @param page 页数
     * @param id   问题id
     * @return
     */
    @GET("?act=cdd&op=detail_replay")
    Observable<Page<AnswerReply>>
    comments(@Query("page") int page, @Query("id") int id);

    /**
     * 获得最新信息(浏览总数，回答总数,某某某想知道答案,想知道答案的总人数)
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("?act=cdd&op=get_newest_info")
    Observable<Result<List<NewestInfo>>>
    newestInfo(@Field("id") String id);

    /**
     * 收藏
     *
     * @param id   问题ID
     * @param type 1：收藏 2：取消收藏
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_cdd&op=favorite")
    Observable<Response>
    collection(@Field("id") int id, @Field("type") int type);


    /**
     * //回答问题,评论，回复，回复的回复接口
     *
     * @param content //回复内容
     * @param title   //回复标题（可为空）
     * @param pid     //回复的评论ID
     * @method POST
     */
    @FormUrlEncoded
    @POST("?act=m_cdd&op=answer")
    Observable<Result<Comment>>
    answer(@Field("ask_id") int id, @Field("content") String content, @Field("title") String title, @Field("pid") String pid);

    /**
     * 赞
     *
     * @param id   问题id
     * @param type 1：赞 2：取消赞
     * @return
     */
    @FormUrlEncoded
    @POST("?act=m_cdd&op=praise")
    Observable<Result<Praise>>
    praise(@Field("id") int id, @Field("type") int type);

    /**
     * 天气
     */

    @GET("?act=weather&op=winfo")
    Observable<Result<Weather>>
    getWeather(@Query("city") String city,@Query("lon")String lon,@Query("lat") String lat);


    /**
     * 最近聊天记录
     *
     * @param id     求购信息ID
     * @param cursor 聊天记录的最后一次ID(默认为0)
     * @return
     */
    @GET("?act=demand&op=demchatlist")
    Observable<Result<List<ChatMessage>>>
    demChatList(@Query("id") int id, @Query("cursor") int cursor);

    /**
     * 聊天
     *
     * @param content image内容
     * @return
     */
    @POST("?act=demand&op=demsend")
    Observable<Result<ChatMessage>>
    chatSend(@Body RequestBody content);

//    @Part("image\";filename=\"image.jpeg")

    /**
     * 聊天
     *
     * @param content 文字
     * @return
     */
    @FormUrlEncoded
    @POST("?act=demand&op=demsend")
    Observable<Result<ChatMessage>>
    chatSend(@Field("id") int id, @Field("type") int type, @Field("content") String content);

    /**
     * 历史聊天记录
     *
     * @param id     求购信息id
     * @param cursor 最前一次聊天的ID
     * @param page   页数
     * @return
     */
    @GET("?act=demand&op=demchathistory")
    Observable<Page<ChatMessage>>
    chatOlder(@Query("id") int id, @Query("cursor") int cursor, @Query("page") int page);


    /**
     * 聊天对象信息
     *
     * @param id 聊天对象基本信息
     * @return
     */
    @GET("?act=demand&op=demchatinit")
    Observable<Result<ConsultUser>>
    chatInit(@Query("id") int id);

    /**
     * 消息资讯
     *
     * @return
     */
    @GET("?act=menu&op=newstype")
    Observable<Result<List<Plants>>>
    newstype();

    /**
     * 病虫害库菜单列表
     * @return
     */
    @GET("?act=menu&op=insecttype")
    Observable<Result<List<Plants>>>
    insecttype();


    /**
     * 农业列表接口
     *
     * @param id   分类id
     * @param kw   搜索关键字
     * @param page 页码
     * @return
     */
    @GET("?act=news&op=nlist")
    Observable<Page<FarmingList>>
    nlist(@Query("gc_id") int id, @Query("kw") String kw, @Query("page") int page);

    /**
     * 农业详情
     *
     * @param id 资讯id
     * @return
     */
    @GET("?act=news&op=ndetail")
    Observable<Result<FarmingDetail>>
    ndetail(@Query("id") int id);

    /**
     * 农业推荐搜索
     *
     * @return
     */
    @GET("?act=news&op=nsearchkw")
    Observable<Page<String>>
    nsearchkw();

    /**
     * 农业历史搜索
     *
     * @return
     */
    @GET("?act=news&op=nsearchhis")
    Observable<Result<List<String>>>
    nsearchhis();

    /**
     * 搜索记录上报
     *
     * @param kw 搜索关键字
     * @return
     */
    @FormUrlEncoded
    @POST("?act=news&op=nsearchup")
    Observable<Response>
    nsearchup(@Field("kw") String kw);

    @GET("?act=cdd&op=newindex")
    Observable<Result<Home>>
    home(@Query("city") String city, @Query("pos") String pos);

    /**
     * 农业清除历史记录
     *
     * @return
     */
    @GET("?act=news&op=nsearchdel")
    Observable<Response>
    farmingClearRecord();

    /**
     * 菜价列表
     *
     * @param areaId 搜索地区ID
     * @param kw     搜索关键字
     * @param gcId   分类id
     * @param timeId 日期id
     * @param time   搜索截止日期
     * @param page   页码
     * @return
     */
    @GET("?act=vegetable&op=index")
    Observable<Page<Vegetable>>
    vegetableIndex(@Query("area_id") String areaId, @Query("kw") String kw, @Query("gc_id") int gcId, @Query("expire_id") String timeId, @Query("expire_time") String time, @Query("page") int page);

    /**
     * 搜索历史
     *
     * @return
     */
    @GET("?act=vegetable&op=vhistory")
    Observable<Result<List<String>>>
    vhistory();


    /**
     * 定位信息
     *
     * @return
     */
    @GET("?act=cdd&op=pos")
    Observable<Result<Pos>>
    getPos(@Query("pos") String pos);

    @FormUrlEncoded
    @POST("?act=vegetable&op=vkw")
    Observable<Response>
    vkw(@Field("kw") String kw);

    /**
     * 清空搜索记录
     *
     * @return
     */
    @GET("?act=vegetable&op=vdel")
    Observable<Response>
    vdel();



}
