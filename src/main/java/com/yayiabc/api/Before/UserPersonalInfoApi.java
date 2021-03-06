package com.yayiabc.api.Before;

public interface UserPersonalInfoApi {
	/**
     * @api {get} http://47.93.48.111:6181/api/userPersonalInfo/detail （前台）获取个人资料详情
     * @apiName detail
     * @apiGroup userPersonalInfo
     * @apiVersion 0.1.0
     * @apiDescription 获取个人资料详情
     *
     * @apiParam {String} token 身份凭证（必须）
     *
     * @apiSuccessExample {json} Success-Response:
     *  HTTP/1.1 200 OK
     * {
     * callStatus:"SUCCEED",
     * errorCode:"No_Error",
     * data:{
     * 		phone:"13122390809",
     * 		tureName:"张三",
     * 		sex:1,   (1男，2女)
     * 		birthday:"2017-10-10",
     * 		qq:"8888888",
     * 		userPic:"image/system05.jpg",
     * 		type:1,  (1个人，2机构),
     * 		companyName:"XX牙医诊所",
     * 		part:"浙江省杭州市西湖区",
     * 		workAddress:"XX路100号",
     * 		doctorPic:"image/system05.jpg",		(医师执业资格证)
     * 		medicalLicense:"null",				(医疗机构执业许可证)
     * 		businessLicense:"null",				(营业执照)
     * 		taxRegistration:"null",				(税务登记证)
     * 		openingPermit:"null",				(开户许可证)
     * 		radiologicalPermit:"null",			(放射诊疗许可证)
     * 		idCardPositive:"null",				(法人身份证正面)
     * 		idCardOtherside:"null",				(法人身份证反面)
     * 		state:"1",	(1待审核，2审核通过，3审核未通过)   
     * 		failReason:"资料不全",	( state为3时显示 )
     * 		judge:1
     * },
     * token:"SK1d7a4fe3-c2cd-417f-8f6f-bf7412592996",
     * numberPerPage:10,
     * currentPage:1,
     * totalNumber:1,
     * totalPage:1,
     * num :null,
     * msg :null,
     *  }
     *
     *  @apiSuccessExample {json} Error-Response:
     *  HTTP/1.1 200 OK
     * {
     * callStatus:"FAILED",
     * errorCode:"Error",
     * data:null,
     * token:null,
     * numberPerPage:0,
     * currentPage:0,
     * totalNumber:0,
     * totalPage:0,
     * num :null,
     * msg :null,
     *  }
     *
     */

	/**
     * @api {post} http://47.93.48.111:6181/api/userPersonalInfo/updateUser （前台）编辑个人资料个人信息
     * @apiName updateUser
     * @apiGroup userPersonalInfo
     * @apiVersion 0.1.0
     * @apiDescription 编辑个人资料个人信息
     *
     * @apiParam {String} trueName 真实姓名（必须）
     * @apiParam {int} sex 性别（必须，1男，2女）
     * @apiParam {date} birthday 生日（非必须）
     * @apiParam {String} qq QQ（非必须）
     * @apiParam {String} userPic 用户头像（非必须）
     * @apiParam {String} token 身份凭证（必须）
     *
     * @apiSuccessExample {json} Success-Response:
     *  HTTP/1.1 200 OK
     * {
     * callStatus:"SUCCEED",
     * errorCode:"No_Error",
     * data:null,
     * token:"SK1d7a4fe3-c2cd-417f-8f6f-bf7412592996",
     * numberPerPage:10,
     * currentPage:1,
     * totalNumber:1,
     * totalPage:1,
     * num :null,
     * msg :null,
     *  }
     *
     *  @apiSuccessExample {json} Error-Response:
     *  HTTP/1.1 200 OK
     * {
     * callStatus:"FAILED",
     * errorCode:"Error",
     * data:null,
     * token:null,
     * numberPerPage:0,
     * currentPage:0,
     * totalNumber:0,
     * totalPage:0,
     * num :null,
     * msg :null,
     *  }
     *
     */
	
	/**
     * @api {post} http://47.93.48.111:6181/api/userPersonalInfo/updateCertification （前台）编辑个人资料资质认证
     * @apiName updateCertification
     * @apiGroup userPersonalInfo
     * @apiVersion 0.1.0
     * @apiDescription 编辑个人资料资质认证
     *
     * @apiParam {int} type 类型（必须，1个人，2机构）
     * @apiParam {String} companyName 单位名称（必须）
     * @apiParam {String} part 单位所在地（非必须）
     * @apiParam {String} workAddress 详细地址（非必须）
     * @apiParam {String} doctorPic 医师资格证图片（必须）
     * @apiParam {int} judge 状态判断（非必须，0或者1）
     * @apiParam {String} token 身份凭证（必须）
     * @apiParam {String} medicalLicense 医疗机构执业许可证（必须）
     * @apiParam {String} businessLicense 营业执照（必须）
     * @apiParam {String} taxRegistration 税务登记证（必须）
     * @apiParam {String} openingPermit 开户许可证（非必须）
     * @apiParam {String} radiologicalPermit 放射诊疗许可证（非必须）
     * @apiParam {String} idCardPositive 法人身份证正面（非必须）
     * @apiParam {String} idCardOtherside 法人身份证反面（非必须）
     *
     * @apiSuccessExample {json} Success-Response:
     *  HTTP/1.1 200 OK
     * {
     * callStatus:"SUCCEED",
     * errorCode:"No_Error",
     * data:null,
     * token:"SK1d7a4fe3-c2cd-417f-8f6f-bf7412592996",
     * numberPerPage:10,
     * currentPage:1,
     * totalNumber:1,
     * totalPage:1,
     * num :null,
     * msg :null,
     *  }
     *
     *  @apiSuccessExample {json} Error-Response:
     *  HTTP/1.1 200 OK
     * {
     * callStatus:"FAILED",
     * errorCode:"Error",
     * data:null,
     * token:null,
     * numberPerPage:0,
     * currentPage:0,
     * totalNumber:0,
     * totalPage:0,
     * num :null,
     * msg :null,
     *  }
     *
     */
	
	/**
     * @api {get} http://47.93.48.111:6181/api/userPersonalInfo/queryBind （前台）查询用户是否已绑定销售员
     * @apiName queryBind
     * @apiGroup userPersonalInfo
     * @apiVersion 0.1.0
     * @apiDescription 查询用户是否已绑定销售员
     *
	 * @apiParam {int} state （非必须）
	 * @apiParam {String} salePhone 销售员手机号（非必须）
     * @apiParam {String} token 身份凭证（必须）
     *
     * @apiSuccessExample {json} Success-Response:
     *  HTTP/1.1 200 OK
     * {
     * callStatus:"SUCCEED",
     * errorCode:"No_Error",
     * data:{
  	 *		isBindSale:1, (1是，2否)
  	 *		saleName:"销售员一号",
  	 *		salePhone:"666"
     * },
     * token:"SK1d7a4fe3-c2cd-417f-8f6f-bf7412592996",
     * numberPerPage:10,
     * currentPage:1,
     * totalNumber:1,
     * totalPage:1,
     * num :null,
     * msg :null,
     *  }
     *
     *  @apiSuccessExample {json} Error-Response:
     *  HTTP/1.1 200 OK
     * {
     * callStatus:"FAILED",
     * errorCode:"Error",
     * data:null,
     * token:null,
     * numberPerPage:0,
     * currentPage:0,
     * totalNumber:0,
     * totalPage:0,
     * num :null,
     * msg :null,
     *  }
     *
     */
}
