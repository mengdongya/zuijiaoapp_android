package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Attendee;
import com.zuijiao.android.zuijiao.model.Banquent.SellerStatus;
import com.zuijiao.android.zuijiao.model.user.User;

import java.util.Calendar;


/**
 * Created by Chen Hao on 3/23/15.
 */
public enum RouterAccount {
    INSTANCE;

    private IRouterAccount service = Router.getInstance().restAdapter.create(IRouterAccount.class);

    public void updateAvatar(String avatarUrl
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.updateAvatar(avatarUrl, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void update(User user
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        String gender = user.getProfile().getGender();
        Integer provinceId = user.getProfile().getProvinceId();
        Integer cityId = user.getProfile().getCityId();

        Integer year = null, month = null, day = null;
        if (user.getProfile().getBirthday().isPresent()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(user.getProfile().getBirthday().get());
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DATE);
        }
        Integer holdCount=0,recCount=0,takeCount = 0;

        String comment = null,skilled=null,cooking=null,place=null,address=null;
        String tags = null, languages = null, education = null, career = null, introduce = null;
        String hobby = null, email = null, avatarUrl = null, nickname = null, story = null;
        /*if (user.getProfile().getCooking().isPresent())
            cooking = user.getProfile().getCooking().get();

        if (user.getProfile().getSkilled().isPresent())
            skilled = Router.convertJsonFromList(user.getProfile().getSkilled().get());

        if (user.getProfile().getPlace().isPresent())
            place=user.getProfile().getPlace().get();

        if (user.getProfile().getAddress().isPresent())
            address = user.getProfile().getAddress().get();*/

        if (user.getProfile().getTasteTags().isPresent())
            tags = Router.convertJsonFromList(user.getProfile().getTasteTags().get());

        if (user.getProfile().getLanguages().isPresent())
            languages = Router.convertJsonFromList(user.getProfile().getLanguages().get());

        if (user.getProfile().getEducationBackground().isPresent())
            education = user.getProfile().getEducationBackground().get();

        if (user.getProfile().getCareer().isPresent())
            career = user.getProfile().getCareer().get();

        if (user.getProfile().getSelfIntroduction().isPresent())
            introduce = user.getProfile().getSelfIntroduction().get();

        if (user.getProfile().getHobby().isPresent())
            hobby = user.getProfile().getHobby().get();

        if (user.getContactInfo().isPresent())
            email = user.getContactInfo().get().getEmail();

        if (user.getAvatarURLSmall().isPresent())
            avatarUrl = user.getAvatarURLSmall().get();

        if (user.getNickname().isPresent())
            nickname = user.getNickname().get();

        if (user.getProfile().getStory().isPresent())
            story = user.getProfile().getStory().get();


        service.update(gender
                , provinceId
                , cityId
                , story
                , year
                , month
                , day
                , tags
               /* , skilled
                , cooking
                , place
                , address
                , comment
                , holdCount
                , recCount
                , takeCount*/
                , education
                , career
                , languages
                , introduce
                , hobby
                , nickname
                , avatarUrl
                , email
                , CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void updatePhoneNumber(String phoneNumber
            , String securityCode
            , LambdaExpression successCallback
            , OneParameterExpression<String> failureCallback) {
        service.updatePhoneNumber(phoneNumber, securityCode, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void fetchUserInfoById(Integer userId
            , OneParameterExpression<User> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.fetchUserInfoByIdentifier(userId, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void fetchMyInfo(OneParameterExpression<User> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.fetchMyInfo(CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    // MARK: - banquent

    public void masterInfo(Integer chefId, OneParameterExpression<Attendee> successCallback
            , OneParameterExpression<String> failureCallback) {
        service.masterInfo(chefId, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void attendeeInfo(Integer attendeeId, OneParameterExpression<Attendee> successCallback
            , OneParameterExpression<String> failureCallback) {
        service.attendeeInfo(attendeeId, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }


    public void banquetUserInfo(Integer userId ,OneParameterExpression<Attendee> successCallback
            , OneParameterExpression<String> failureCallback){
        service.banquetUserInfo(userId, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }



    public void sellerStatus(OneParameterExpression<SellerStatus> successCallback
            , OneParameterExpression<String> failureCallback){
        service.sellerStatus(CallbackFactory.getInstance().callback(successCallback, failureCallback));

    }
}
