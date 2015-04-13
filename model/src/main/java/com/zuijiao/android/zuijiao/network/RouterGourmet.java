package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Comments;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.Gourmets;
import com.zuijiao.android.zuijiao.model.user.WouldLikeToEatUsers;

import java.util.List;

/**
 * Created by Chen Hao on 3/18/15.
 */
public enum RouterGourmet {
    INSTANCE;

    private IRouterGourmet service = Router.getInstance().restAdapter.create(IRouterGourmet.class);

    public void favoriteAdd(Integer identifier
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.addFavorite(identifier, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void removeFavorite(Integer gourmetId
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.removeFavorite(gourmetId, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void fetchFavorites(Integer sinceId
            , Integer toId
            , Integer count
            , OneParameterExpression<Gourmets> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.fetchFavorites(sinceId
                , toId
                , count
                , CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void fetchFavoritesByUserId(Integer userId
            , Integer sinceId
            , Integer toId
            , Integer count
            , OneParameterExpression<Gourmets> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.fetchFavoritesByUserId(userId
                , sinceId
                , toId
                , count
                , CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void fetchWouldLikeToListByGourmetId(Integer gourmetId
            , Integer count
            , OneParameterExpression<WouldLikeToEatUsers> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.fetchWouldLikeToListByGourmetId(gourmetId, count
                , CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void fetchComments(Integer gourmetId
            , Integer sinceId
            , Integer toId
            , Integer count
            , OneParameterExpression<Comments> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.fetchComments(gourmetId
                , sinceId
                , toId
                , count
                , CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void postComment(Integer gourmetId
            , String comment
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.postComment(gourmetId
                , comment
                , CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void removeComment(Integer commentId
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.removeComment(commentId, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void replyCommentTo(Integer commentId
            , String comment
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.replyCommentTo(commentId, comment, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    void addGourmet(String name
            , String address
            , String price
            , String description
            , List<String> imageUrlList
            , List<String> tagList
            , Integer provinceId
            , Integer cityId
            , Boolean isPrivate
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        String imagesUrlJson = Router.convertJsonFromList(imageUrlList);
        String tagsJson = Router.convertJsonFromList(tagList);

        service.addGourmet(name
                , address
                , price
                , description
                , imagesUrlJson
                , tagsJson
                , provinceId
                , cityId
                , isPrivate
                , CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    void updateGourmet(Integer gourmetId
            , String address
            , String price
            , String description
            , List<String> imageUrlList
            , List<String> tagList
            , Boolean isPrivate
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        String imagesUrlJson = Router.convertJsonFromList(imageUrlList);
        String tagsJson = Router.convertJsonFromList(tagList);

        service.updateGourmet(gourmetId
                , address
                , price
                , description
                , imagesUrlJson
                , tagsJson
                , isPrivate
                , CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void fetchGourmetInformation(Integer gourmetId
            , OneParameterExpression<Gourmet> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.fetchGourmetInformation(gourmetId, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void fetchMyRecommendationList(Integer toGourmetId
            , Integer count
            , OneParameterExpression<Gourmet> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.fetchMyRecommendationList(toGourmetId
                , count
                , CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    void fetchRecommendationListByUserId(Integer userId
            , Integer fromGourmetId
            , Integer toGourmetId
            , Integer count
            , OneParameterExpression<Gourmet> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.fetchRecommendationListByUserId(userId
                , fromGourmetId
                , toGourmetId
                , count,
                CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void fetchOurChoice(Integer fromGourmetId
            , Integer toGourmetId
            , Integer count
            , OneParameterExpression<Gourmets> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.fetchOurChoice(fromGourmetId
                , toGourmetId
                , count
                , CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void removeMyRecommendation(Integer gourmetId, LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.removeMyRecommendation(gourmetId
                , CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

}
