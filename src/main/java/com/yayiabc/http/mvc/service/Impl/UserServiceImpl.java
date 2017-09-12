package com.yayiabc.http.mvc.service.Impl;

import com.yayiabc.common.enums.ErrorCodeEnum;
import com.yayiabc.common.sessionManager.SessionManager;
import com.yayiabc.common.sessionManager.VerifyCodeManager;
import com.yayiabc.common.utils.*;
import com.yayiabc.http.mvc.controller.unionpay.sdk.LogUtil;
import com.yayiabc.http.mvc.dao.SaleLogDao;
import com.yayiabc.http.mvc.dao.UserDao;
import com.yayiabc.http.mvc.dao.UserManageListDao;
import com.yayiabc.http.mvc.dao.WxAppDao;
import com.yayiabc.http.mvc.pojo.jpa.QbRecord;
import com.yayiabc.http.mvc.pojo.jpa.SaleInfo;
import com.yayiabc.http.mvc.pojo.jpa.User;
import com.yayiabc.http.mvc.service.TokenService;
import com.yayiabc.http.mvc.service.UserMyQbService;
import com.yayiabc.http.mvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private WxAppDao wxAppDao;
    @Autowired
    SaleLogDao saleLogDao;
    @Autowired
    private UserMyQbService userMyQbService;
    @Autowired
    private UserManageListDao userManageListDao;
    @Autowired
    private TokenService tokenService;


    public DataWrapper<Void> getVerifyCode(String phone) {
        DataWrapper<Void> dataWrapper = new DataWrapper<Void>();
        String code = VerifyCodeManager.newPhoneCode(phone);
        if (code == null) {
            dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_5min);
            dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
            return dataWrapper;
        }
        boolean result = HttpUtil.sendPhoneVerifyCode(code, phone);
        if (result) {
            dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
            dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
        } else {
            dataWrapper.setErrorCode(ErrorCodeEnum.Error);
            dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
        }
        return dataWrapper;
    }
    
   

    @Override
    public DataWrapper<User> register(String phone, String password, String code,String openid) {
        DataWrapper<User> dataWrapper = new DataWrapper<User>();
        if (userDao.getUserByPhone(phone) == null) {
            //楠岃瘉鐮佹湇鍔�
            String serverCode = VerifyCodeManager.getPhoneCode(phone);
            if (serverCode.equals("noCode")) {
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_notExist);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
            } else if (serverCode.equals("overdue")) {
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_5min);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
            } else if (serverCode.equals(code)) {
                User newUser = new User();
                newUser.setUserId(UUID.randomUUID().toString());
                newUser.setPhone(phone);
                newUser.setPwd(MD5Util.getMD5String(password));
                Integer count=userDao.getSaleCount(phone);
                if(count==0){
                	if (1 == userDao.register(newUser)) {
	                    //绉婚櫎楠岃瘉鐮�
	                    VerifyCodeManager.removePhoneCodeByPhoneNum(phone);
	                    String token = tokenService.getToken(newUser.getUserId());
	                    QbRecord qbRecord=new QbRecord();
	                    qbRecord.setQbRget(60);
	                    qbRecord.setRemark("注册送60乾币");
	                    qbRecord.setQbType("qb_balance");
	                    userMyQbService.add(qbRecord, token);
	                    dataWrapper.setToken(token);
	                    dataWrapper.setData(newUser);
	                    dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
	                    dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
                    if (openid != null) wxAppDao.addUser(newUser.getUserId(),openid);
	                } else {
	                    dataWrapper.setErrorCode(ErrorCodeEnum.Register_Error);
	                    dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
	                }
                }else{
                	dataWrapper.setErrorCode(ErrorCodeEnum.NO_POWER);
                	dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
                }
               
            } else {
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_Error);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
            }
        } else {
            dataWrapper.setErrorCode(ErrorCodeEnum.Username_Already_Exist);
            dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
        }
        return dataWrapper;
    }

    @Override
    public DataWrapper<User> noteLogin(String phone, String code) {
        DataWrapper<User> dataWrapper = new DataWrapper<User>();
        User user = userDao.getUserByPhone(phone);
        if (user != null) {
            String serverCode = VerifyCodeManager.getPhoneCode(phone);
            if (serverCode.equals("noCode")) {
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_notExist);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
            } else if (serverCode.equals("overdue")) {
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_5min);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
            } else if (serverCode.equals(code)) {
                dataWrapper.setData(user);
                dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
                int num = userDao.getCartNum(user);
                dataWrapper.setNum(num);
                String token =tokenService.getToken(user.getUserId());
                //--


                if (SessionManager.getSessionByUserID(user.getUserId()) == null) {
                    String sessionKey = SessionManager.newSession(user);
                    dataWrapper.setToken(token);
                    return dataWrapper;
                } else {
                    dataWrapper.setMsg("该账户已经登录");
                    dataWrapper.setErrorCode(ErrorCodeEnum.Error);
                    return dataWrapper;
                }

                //--

            } else {
                System.out.println("code:" + code);
                System.out.println("VerifyCode:" + VerifyCodeManager.getPhoneCode(phone));
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_Error);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
            }
        } else {
            dataWrapper.setErrorCode(ErrorCodeEnum.Username_NOT_Exist);
            dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
        }
        return dataWrapper;
    }

    @Override
    public DataWrapper<User> pwdLogin(String phone, String password) {
        DataWrapper<User> dataWrapper = new DataWrapper<User>();
        User seUser = userDao.getUserByPhone(phone);
        String token = null;
        if (seUser != null) {
            if (MD5Util.getMD5String(password).equals(seUser.getPwd())) {
                dataWrapper.setData(seUser);
                int num = userDao.getCartNum(seUser);
                dataWrapper.setNum(num);
                dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
                String userId = seUser.getUserId();
                token = tokenService.getToken(userId);
                dataWrapper.setToken(token);
            } else {
                dataWrapper.setErrorCode(ErrorCodeEnum.Password_error);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
            }
            //--


			/*if(SessionManager.getSessionByUserID(seUser.getUserId())==null){
                String sessionKey=SessionManager.newSession(seUser);
				dataWrapper.setToken(token);
				return dataWrapper;
			}else{
				dataWrapper.setMsg("该账户已经登录");
				dataWrapper.setErrorCode(ErrorCodeEnum.Error);
				return dataWrapper;
			}*/

            //--
        } else {
            dataWrapper.setErrorCode(ErrorCodeEnum.Username_NOT_Exist);
            dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
        }
        return dataWrapper;
    }

    @Override
    public DataWrapper<Void> reLogin(String token) {
        DataWrapper<Void> dataWrapper = new DataWrapper<Void>();
        userDao.deleteToken(token);
        dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
        dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
        //--
        /*String userId=utilsDao.getUserID(token);
		SessionManager.removeSessionByUserId(userId);*/
        //---
        return dataWrapper;
    }

    @Override
    public DataWrapper<Void> forgetPwd(String phone, String code, String password) {
        DataWrapper<Void> dataWrapper = new DataWrapper<Void>();
        if (userDao.getUserByPhone(phone) != null) {
            String serverCode = VerifyCodeManager.getPhoneCode(phone);
            if (serverCode.equals("noCode")) {
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_notExist);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
            } else if (serverCode.equals("overdue")) {
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_5min);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
            } else if (serverCode.equals(code)) {
                try {
                    userDao.updatePwd(phone,MD5Util.getMD5String(password));
                    dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
                    dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
                } catch (Exception e) {
                    e.printStackTrace();
                    dataWrapper.setErrorCode(ErrorCodeEnum.Error);
                    dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
                }
            } else {
                System.out.println("code:" + code);
                System.out.println("VerifyCode:" + VerifyCodeManager.getPhoneCode(phone));
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_Error);
                dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
            }
        } else {
            dataWrapper.setErrorCode(ErrorCodeEnum.Username_NOT_Exist);
            dataWrapper.setMsg(dataWrapper.getErrorCode().getLabel());
        }
        return dataWrapper;
    }

    @Override
    public DataWrapper<Void> bindSale(String token, String salePhone) {
        DataWrapper<Void> dataWrapper = new DataWrapper<Void>();
        String userId = userDao.getUserIdByToken(token);
        SaleInfo saleInfo = saleLogDao.getSaleInfoByPhone(salePhone);
        if(saleInfo!=null) {
            int i = userDao.bindSale(userId,saleInfo.getSaleId());
            userManageListDao.bindUpdateNum(saleInfo.getSaleId());
            boolean result=HttpUtil.sendUserBind(userDao.getUserPhoneByToken(token),salePhone);
            boolean resulta=HttpUtil.sendSaleBind(salePhone,userDao.getUserPhoneByToken(token));
            if(result && resulta){
                LogUtil.writeLog("绑定短信发送成功！");
            }
        }else{
            dataWrapper.setErrorCode(ErrorCodeEnum.Error);
        }
        return dataWrapper;
    }

    @Override
    public DataWrapper<Void> deleteInGrainUser(String userId) {
        DataWrapper<Void> dataWrapper= new DataWrapper<Void>();
        userDao.deleteInGrainUser(userId);
//        SendToSaleMessage sendToSaleMessage= BeanUtil.getBean("SendToSaleMessage");
//        sendToSaleMessage.send("42c3ae9e-95d3-4e5b-9347-48fe27f9dfda","fd48c46d9499408b9a9a05215edae56e701");
        return dataWrapper;
    }


}
