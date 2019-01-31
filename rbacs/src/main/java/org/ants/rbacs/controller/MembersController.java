package org.ants.rbacs.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.QueryParam;
import org.ants.common.constants.ErrorConstants;
import org.ants.common.entity.JWTPayloadEntity;
import org.ants.common.entity.Result;
import org.ants.common.utils.ServiceAuthToken;
import org.ants.rbacs.model.RoleModel;
import org.ants.rbacs.config.JWTConfig;
import org.ants.common.entity.MembersEntity;
import org.ants.rbacs.service.MembersService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/members")
/**
 * @Description: 用户管理接口
 * @author: Jerry
 * @date: 2018年12月13日上午9:18:18
 */
public class MembersController {
	private Logger logger = LoggerFactory.getLogger(MembersController.class);
	/// 其他服务端验证时间戳允许误差, 10秒 
	private static final int TIMESTAMP_DEV = 10000;
	@Autowired
	private JWTConfig jwtConfig;
	@Autowired
	private MembersService userService;
	
	@ApiOperation(value="登录验证", notes="传统用户名和密码验证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = false, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String"),
    })
	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result reqLogin(String username, String password) {
		if (StringUtils.isBlank(username) || StringUtils.isEmpty(password)) {
			return Result.fail(ErrorConstants.SE_REQ_PARAMS);
		}
		
		MembersEntity user = userService.login(username, password);
		Result rlt;
		if (null != user) {
			JWTPayloadEntity payload = new JWTPayloadEntity(user.getId(), user.getUsername());
			user.setToken(jwtConfig.generateToken(payload));
			rlt = Result.success(user);
		} else {
			rlt = Result.fail(ErrorConstants.SE_LOGIN_ERROR);
		}
		
		return rlt;
	}
	@ApiOperation(value="web服务端验证", notes="通过签名认证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = false, dataType = "String"),
            @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "long"),
            @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "String")
    })
	@ResponseBody
	@RequestMapping(value = "/service/login", method = RequestMethod.POST)
	public Result webServerLogin(HttpServletRequest request, String serviceName, 
			long timestamp, String sign) {
		long time = Math.abs(System.currentTimeMillis() - timestamp);
		if (StringUtils.isBlank(serviceName)) {
			return Result.fail(ErrorConstants.SE_REQ_PARAMS);
		}
		if (time > TIMESTAMP_DEV) {
			return Result.fail(ErrorConstants.SE_TOKEN_TIMEOUT);
		}
		
		String newSign = ServiceAuthToken.makeToken(serviceName, timestamp, jwtConfig.getSecret());
		Result rlt;
		if (newSign.equals(sign)) {
			JWTPayloadEntity payload = new JWTPayloadEntity(serviceName, serviceName);
			rlt = Result.success(jwtConfig.generateToken(payload));
		} else {
			rlt = Result.fail(ErrorConstants.SE_LOGIN_ERROR);
		}
		
		return rlt;
	}
	@ApiOperation(value="获取用户列表或查找用户", notes="")
    @ApiImplicitParams({
	    	@ApiImplicitParam(name = "page", value = "请求的页码", required = true, dataType = "int"),
	        @ApiImplicitParam(name = "pageSize", value = "每页数量", required = true, dataType = "int"),
            @ApiImplicitParam(name = "filter", value = "members 实体类", required = false, dataType = "MembersEntity")
    })
	@ResponseBody
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Result getUsers(MembersEntity filter,@QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
		
		PageInfo<MembersEntity> userList = userService.getUsers(filter,page,pageSize);
		Result res = Result.success(userList);
		return res;
	}
	@ApiOperation(value="插入 resources Table 接口信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键标识", required = true, dataType = "String"),
            @ApiImplicitParam(name = "members", value = "members Table 映射对象", required = true, dataType = "String")
    })
	@ResponseBody
	@RequestMapping(value = "", method = RequestMethod.POST)
	public Result insertUsers(MembersEntity mm,String userId) {
		
		List<MembersEntity> insertLi = new ArrayList<>();
		insertLi.add(mm);
		
		Result res = userService.insert(insertLi,userId);
		return res;
	}
	@ResponseBody
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public Result deleteRes(HttpServletRequest request,@QueryParam("id") String id) {
		
		return userService.deleteById(id);
	}
	@ApiOperation(value="修改 members Table 用户信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filter", value = "members 实体类", required = false, dataType = "MembersEntity")
    })
	@ResponseBody
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public Result updateRes(MembersEntity members,String userId) {
		
		return userService.update(members,userId);
	}
	@ApiOperation(value="根据用户名统计用户数量", notes="用户添加时判断是否重名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = false, dataType = "String"),
    })
	@ResponseBody
	@RequestMapping(value = "/count/username", method = RequestMethod.GET)
	public Result countByUsername(@QueryParam("username") String username) {
		if (StringUtils.isBlank(username)) {
			return Result.fail(ErrorConstants.SE_REQ_PARAMS);
		}
		
		int count = userService.countByUsername(username);
		Result rlt = Result.success(count);
		
		return rlt;
	}
	@ApiOperation(value="获取用户所属的角色列表", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = false, dataType = "String"),
    })
	@ResponseBody
	@RequestMapping(value = "/roles", method=RequestMethod.GET)
	public Result getUserRoles(@QueryParam("userId") String userId, HttpSession session) {
		if (StringUtils.isBlank(userId)) {
			return Result.fail(ErrorConstants.SE_REQ_PARAMS);
		}
		
		Result rlt = null;
		if (StringUtils.isBlank(userId)) {
			rlt = Result.fail(ErrorConstants.SE_REQ_PARAMS.getCode(), "用户id不能为空");
			logger.info(rlt.getMessage());
		}
		
		List<RoleModel> roleList = userService.findRoles(userId);
		if (null == roleList) {
			rlt = Result.success(roleList);
		} else {
			rlt = Result.fail(ErrorConstants.SE_INTERNAL.getCode(), "获取用户角色列表失败. UserID: "+userId);
			logger.info(rlt.getMessage());
		}
		return rlt;
	}
}
