package com.yayiabc.http.mvc.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yayiabc.common.enums.ErrorCodeEnum;
import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.http.mvc.dao.CartDao;
import com.yayiabc.http.mvc.dao.UserCenterStarDao;
import com.yayiabc.http.mvc.dao.UtilsDao;
import com.yayiabc.http.mvc.pojo.jpa.Cart;
import com.yayiabc.http.mvc.pojo.jpa.ItemStar;
import com.yayiabc.http.mvc.service.CartService;

@Service
public class CartServiceImpl implements CartService {
	@Autowired
	CartDao cartDao;
	@Autowired
	UtilsDao utilsDao;
	@Autowired
	UserCenterStarDao userCenterStarDao;

	@Override
	public DataWrapper<List<Cart>> list(String token) {
		DataWrapper<List<Cart>> dataWrapper = new DataWrapper<List<Cart>>();
		String userId=utilsDao.getUserID(token);
		if (userId == null) {
			dataWrapper.setErrorCode(ErrorCodeEnum.Error);
			dataWrapper.setMsg("token错误");
		} else {
			List<Cart> list = cartDao.list(userId);
			dataWrapper.setData(list);
		}
		return dataWrapper;
	}

	@Override
	public DataWrapper<Void> delete(String itemSKU,String token) {
		DataWrapper<Void> dataWrapper = new DataWrapper<Void>();
		String userId=utilsDao.getUserID(token);
		
		if (userId == null) {
			dataWrapper.setErrorCode(ErrorCodeEnum.Error);
			dataWrapper.setMsg("token错误");
		} else {
			int sign = cartDao.delete(userId, itemSKU);
			if (sign > 0) {
				dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
			} else {
				dataWrapper.setErrorCode(ErrorCodeEnum.Error);
			}
		}
		return dataWrapper;
	}

	@Override
	public DataWrapper<ItemStar> star(String itemId,String itemSKU,String token) {
		DataWrapper<ItemStar> dataWrapper = new DataWrapper<ItemStar>();
		String userId=utilsDao.getUserID(token);
		List<Integer> list=userCenterStarDao.queryOne(itemId,userId);
		if (userId == null) {
			dataWrapper.setErrorCode(ErrorCodeEnum.Error);
			dataWrapper.setMsg("token错误");
		} else {
			if(list.size()==0){
				ItemStar itemStar = new ItemStar();
				itemStar.setUserId(userId);
				itemStar.setItemId(itemId);
				int id = cartDao.star(itemStar);
				if (id > 0) {
					dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
					this.delete(itemSKU, token); // 收藏后调用删除方法，从购物车中移除
				} else {
					dataWrapper.setErrorCode(ErrorCodeEnum.Error);
				}
			}else{
				dataWrapper.setErrorCode(ErrorCodeEnum.Error);
				dataWrapper.setMsg("商品已收藏");
			}
		}
		return dataWrapper;
	}

	@Override
	public DataWrapper<Cart> add(Integer num,String itemSKU,String token) {
		DataWrapper<Cart> dataWrapper = new DataWrapper<Cart>();
		String userId=utilsDao.getUserID(token);
		if (userId == null) {
			dataWrapper.setErrorCode(ErrorCodeEnum.Error);
			dataWrapper.setMsg("token错误");
		} else {
			Cart cart =new Cart();
			cart = cartDao.queryBySKU(itemSKU);
			cart.setNum(num);
			cart.setUserId(userId);
			int isItem = cartDao.getCountItemSKU(cart.getItemSKU());	//判断购物车内是否已存在该商品
			if (isItem == 0) {
				int id = cartDao.add(cart);
				if (id > 0) {
					dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
				} else {
					dataWrapper.setErrorCode(ErrorCodeEnum.Error);
				}
			} else if (isItem > 0) {
				cartDao.updateOne(userId, cart.getItemSKU(), cart.getNum());
			}

		}
		return dataWrapper;
	}

	@Override
	public DataWrapper<Void> updateNum(Integer num,String itemSKU,String token) {
		DataWrapper<Void> dataWrapper = new DataWrapper<Void>();
		String userId=utilsDao.getUserID(token);
		if (userId == null) {
			dataWrapper.setErrorCode(ErrorCodeEnum.Error);
			dataWrapper.setMsg("token错误");
		} else {
			int sign = cartDao.updateNum(userId, num, itemSKU);
			if (sign > 0) {
				dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
			} else {
				dataWrapper.setErrorCode(ErrorCodeEnum.Error);
			}
		}

		return dataWrapper;
	}

}
