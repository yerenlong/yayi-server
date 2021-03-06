package com.yayiabc.api.Before;

public interface ItemBrandApi {
	/* *
		*  @api {get} http://47.93.48.111:6181/api/item/brandList   （前台）品牌列表
		*  @apiName brandList
		*  @apiGroup itemBrand
		*  @apiVersion 0.1.0
		*  @apiDescription 鼠标悬停时，获取品牌列表
		*  @apiSuccessExample {json} Success-Response:
		*  HTTP/1.1 200 OK
		*  {
		*  {
		*"callStatus": "SUCCEED",
		*"errorCode": "No_Error",
		*"data": [
		*{
		*"created": null,
		*"updated": null,
		*"itemBrandId": 3,
		*"itemBrandLogo": "http://orl5769dk.bkt.clouddn.com/FhQKBfwde_xDpsk-R77wphNL4pdi",
		*"itemBrandHome": "进口",
		*"itemBrandName": "武汉高登"
		*},...],
		*"token": null,
		*"numberPerPage": 0,
		*"currentPage": 0,
		*"totalNumber": 0,
		*"totalPage": 0,
		*"num": 0,
		*"msg": null
		*}
		*  @apiSuccessExample {json} Error-Response:
		*  HTTP/1.1 200 OK
		*  {
		*  callStatus:"FAILED",
		*  errorCode:"Error",
		*  data:null,
		*  token:null,
		*  numberPerPage:0,
		*  currentPage:0,
		*  totalNumber:0,
		*  totalPage:0
	  	*     }
	  */


	


	 /* *
		*	  @api {post} http://47.93.48.111:6181/api/item/brandItemList  （前台）获取品牌下的所有商品列表
		*	  @apiName brandItemList
		*	  @apiGroup itemBrand
		*	  @apiVersion 0.1.0
		*	  @apiDescription 获取品牌下的所有商品列表
		*
		*	  @apiParam {Integer} itemBrandId 品牌id  (必须)
		*	  @apiParam {Integer} rule   1.时间降序2.销量降序3.价格降序4.价格升序 (非必须，默认为1)
		*	  @apiParam {Integer} currentPage 当前第几页,因为数据少,默认从1开始  (必须,默认为1)
		*	  @apiParam {Integer} numberPerPage 每页显示的条数 (非必须，默认为20)
		*
		*	  @apiSuccessExample {json} Success-Response:
		*	  HTTP/1.1 200 OK
		*	  {
		*	  {
		*	"callStatus": "SUCCEED",
		*	"errorCode": "No_Error",
		*	"data": [
		*	{
		*	"created": null,
		*	"updated": 1502675934000,
		*	"itemId": "170719141237",
		*	"itemName": "精细精磨套装 FG1006D",
		*	"itemValueList": null,
		*	"isThrow": 1,
		*	"itemSort": "耗材类",
		*	"itemStockNum": null,
		*	"itemPrice": 77,
		*	"sales": 1,
		*	"state": null,
		*	"oneClassify": null,
		*	"twoClassify": null,
		*	"threeClassify": null,
		*	"itemPnamea": null,
		*	"itemPnameb": null,
		*	"itemPnamec": null,
		*	"itemDetail": {
		*	"created": null,
		*	"updated": null,
		*	"itemId": null,
		*	"video": null,
		*	"itemPica": "http://orl5769dk.bkt.clouddn.com/FuuaxJNKe5ZdN34c2A01sN_NGnbg",
		*	"itemPicb": null,
		*	"itemPicc": null,
		*	"itemPicd": null,
		*	"itemPice": null,
		*	"commission": null,
		*	"isQbBuy": null,
		*	"qbNum": null,
		*	"storeItemId": "暂无",
		*	"apparatusType": "暂无",
		*	"unit": "暂无",
		*	"producePompany": "暂无",
		*	"registerId": "暂无",
		*	"registerDate": "暂无",
		*	"itemPacking": "暂无",
		*	"itemLevels": "暂无",
		*	"itemRange": "暂无",
		*	"remark": "暂无",
		*	"itemDesc": null,
		*	"itemUse": null
		*	},
		*	"commentList": null,
		*	"itemBrand": null,
		*	"propertyList": null
		*	},...],
		*	"token": null,
		*	"numberPerPage": 10,
		*	"currentPage": 1,
		*	"totalNumber": 19,
		*	"totalPage": 2,
		*	"num": 0,
		*	"msg": null
		*	}
		*	@apiSuccessExample {json} Error-Response:
		*	  HTTP/1.1 200 OK
		*	  {
		*	  callStatus:"FAILED",
		*	  errorCode:"Error",
		*	  data:null,
		*	  token:null,
		*	  numberPerPage:0,
		*	  currentPage:0,
		*	  totalNumber:0,
		*	  totalPage:0
		*	  }
	  */


	
	
	/**
	 * 
	 * @api {get} http://47.93.48.111:6181/api/item/itemDetailDes  （前台）商品详情
	 * @apiName itemDetailDes
	 * @apiGroup itemBrand
	 * @apiVersion 0.1.0
	 * @apiDescription  点击商品，获取商品详情
	 * 
	 * @apiParam {String} itemId 商品id  (必须)
	 * @apiParam {String} token  身份凭证（必须）
	 * 
	 * @apiSuccessExample {json} Success-Response: 
	 * HTTP/1.1 200 OK 
	 * {
	 * callStatus:"SUCCEED", 
	 * errorCode:"No_Error",
	 * data:{
	 * itemId:"15645345345641",
	 * itemName:"牙具",
	 * itemBrand:{
	 * 		itemBrandId:1564135,
	 *      itemBrandLogo:"image/system04.jpg",
	 *      itemBrandHome:"进口",
	 *      itemBrandName:"商品品牌名称"
	 * }
	 * itemStockNum:50,
	 * itemPrice:50,
	 * sales:36,
	 * state:2 (1表示上架，2标示下架), 
	 * oneClassify:"一级分类",
	 * twoClassify:"二级分类",
	 * itemValueList (List):{
	 * 	 itemValueId:01564646,
	 *   itemPropertyName:"商品属性",
	 *   itemPropertyInfo:"商品属性值"
	 * }
	 * itemDetail:{
	 *   itemId:""45645645684,
	 *   video:"商品视频地址",
	 *   itemPica:"商品第一张图片",
	 *   itemPicb:"商品第二张图片",
	 *   itemPicc:"商品第三张图片",
	 *   itemPicd:"商品第四张图片",
	 *   itemPice:"商品第五张图片",
	 *   itemPice:258,
	 *   commission:25646  (商品提成),
	 *   isQbBuy:1  (1表示支持乾币抵扣，2表示不支持),
	 *   qbNum:50   (商品返还的乾币数),
	 *   storeItemId : 16543131346   (商家货号)
	 *   apparatusType: 2 ,
	 *   unit:"单位",
	 *   producePompany:"生产厂家",
	 *   registerId:"4555486489" (注册证号) ,
	 *   registerDate:2017-2-6   (注册证有效期),
	 *   itemPacking:"产品包装",
	 *   itemLevels:"产品标准",
	 *   itemRange:"适用范围",
	 *   remark:"其他",
	 *   itemDesc:"商品描述",
	 *   itemUse:"商品使用说明"
	 * }
	 * commentList (List):{
	 * 		commentId:185463843,
	 *      userName:"评论的发出者姓名",
	 *      userPhone:"15236987856",
	 *      commentGrade:5  (评论星级),
	 *      commentContent:"评论内容",
	 *      replyContent:"回复内容",
	 *      created:07-05-26,
	 *      updated:07-05-26,
	 * }
	 * }
	 * token:"SK1d7a4fe3-c2cd-417f-8f6f-bf7412592996",
	 * numberPerPage:0, 
	 * currentPage:0, 
	 * totalNumber:0,
	 * totalPage:0 
	 *}
	 * 
	 * @apiSuccessExample {json} Error-Response: 
	 * HTTP/1.1 200 OK 
	 * {
	 * callStatus:"FAILED", 
	 * errorCode:"Error", 
	 * data:null,
	 * token:null, 
	 * numberPerPage:0, 
	 * currentPage:0,
	 * totalNumber:0, 
	 * totalPage:0 
	 * }
	 * 
	 */
	
	

}
