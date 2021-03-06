package com.yayiabc.api.Before;

public interface UserMyQbApi {
	
	/**
     * @api {get} http://47.93.48.111:6181/api/userMyQb/query （前台）获取乾币记录信息列表
     * @apiName query
     * @apiGroup userMyQb
     * @apiVersion 0.1.0
     * @apiDescription 获取乾币记录信息列表
     *
     * @apiParam {int} currentPage （非必须）
     * @apiParam {int} numberPerPage （非必须）
     * @apiParam {String} token 身份凭证（必须）
     *
     * @apiSuccessExample {json} Success-Response:
     *  HTTP/1.1 200 OK
     * {
     * callStatus:"SUCCEED",
     * errorCode:"No_Error",
     * data:[{
     * 		userId:"a4fe3-c2cd-417f",
     * 		qbRget:200,
     * 		qbRout:0,
     * 		qbBalance:200,
     * 		qbTime:"2017-05-27 16:43",
     * 		remark:"注册了账户"
     * },{
     *      userId:"a4fe3-c2cd-417f",
     * 		qbRget:100,
     * 		qbRout:0,
     * 		qbBalance:400,
     * 		qbTime:"2017-05-26 16:43",
     * 		remark:"完善了个人资料"
     * }],
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
     * @api {post} http://47.93.48.111:6181/api/userMyQb/add （前台）新增我的乾币记录
     * @apiName add
     * @apiGroup userMyQb
     * @apiVersion 0.1.0
     * @apiDescription 新增我的乾币记录
     *
     * @apiParam {String} qbRget 收入（收入和支出选填一项，输正数）
     * @apiParam {String} qbRout 支出（收入和支出选填一项，输负数）
     * @apiParam {String} remark 描述（非必须）
     * @apiParam {String} token 身份凭证（必须）
     *
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * callStatus:"SUCCEED",
     * errorCode:"No_Error",
     * data:null,
     * token:"SK1d7a4fe3-c2cd-417f-8f6f-bf7412592996",
     * numberPerPage:0,
     * currentPage:0,
     * totalNumber:0,
     * totalPage:0,
     * num :null,
     * msg :null,
     *  }
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
     * totalPage:0,
     * num :null,
     * msg :null,
     *  }
     *
     */
}
