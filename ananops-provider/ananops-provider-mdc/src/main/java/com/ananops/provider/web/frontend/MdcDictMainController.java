

package com.ananops.provider.web.frontend;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.dto.UpdateStatusDto;
import com.ananops.core.support.BaseController;
import com.ananops.provider.model.domain.MdcDict;
import com.ananops.provider.model.domain.MdcSysDict;
import com.ananops.provider.model.dto.MdcAddDictDto;
import com.ananops.provider.model.dto.MdcEditDictDto;
import com.ananops.provider.model.dto.MdcGetDictDto;
import com.ananops.provider.model.vo.MdcDictVo;
import com.ananops.provider.service.MdcDictService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * The class Mdc dict main controller.
 *
 * @author ananops.com@gmail.com
 */
@RestController
@RequestMapping(value = "/dict", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - MdcDictMainController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MdcDictMainController extends BaseController {

	@Resource
	private MdcDictService mdcDictService;

	/**
	 * 获取字典列表数据
	 *
	 * @return the wrapper
	 */
	@PostMapping(value = "/getTree")
	@ApiOperation(httpMethod = "POST", value = "获取字典树")
	public Wrapper<List<MdcDictVo>> queryDictTreeList() {
		List<MdcDictVo> dictVoList = mdcDictService.getDictTreeList();
		return WrapMapper.ok(dictVoList);
	}

	/**
	 * 创建或编辑字典库
	 *
	 * @param addDictDto HTTP请求参数
	 *
	 * @return 返回
	 */
	@PostMapping(value = "/save")
	@ApiOperation(httpMethod = "POST",value = "创建或编辑字典库")
	public Wrapper<MdcAddDictDto> saveTask(@ApiParam(name = "saveDict",value = "添加或编辑字典库")@RequestBody MdcAddDictDto addDictDto){
		LoginAuthDto loginAuthDto = getLoginAuthDto();
		return WrapMapper.ok(mdcDictService.saveDict(addDictDto,loginAuthDto));
	}

	/**
	 * 根据用户id获取字典库列表
	 *
	 * @param userId HTTP请求参数
	 *
	 * @return 返回
	 */
	@GetMapping(value = "/getDictListByUserId")
	@ApiOperation(httpMethod = "GET",value = "根据用户id获取字典库列表")
	public Wrapper<List<MdcSysDict>> getDictListByUserId(@ApiParam(name = "userId",value = "用户id")@RequestParam("userId") Long userId) {

		return WrapMapper.ok(mdcDictService.getDictListByUserId(userId));
	}

	/**
	 * 根据字典库id删除字典库及其所属字典项
	 *
	 * @param dictId HTTP请求参数
	 *
	 * @return 返回
	 */
	@PostMapping(value = "/deleteDictByDictId/{dictId}")
	@ApiOperation(httpMethod = "POST",value = "根据字典库id删除字典库及其所属字典项")
	public Wrapper<MdcSysDict> deleteDictByDictId(@PathVariable Long dictId) {
		LoginAuthDto loginAuthDto = getLoginAuthDto();
		return WrapMapper.ok(mdcDictService.deleteDictByDictId(dictId,loginAuthDto));
	}


	/**
	 * 根据ID获取字典信息.
	 *
	 * @param id the id
	 *
	 * @return the wrapper
	 */
	@PostMapping(value = "/queryById/{dictId}")
	@ApiOperation(httpMethod = "POST", value = "根据ID获取字典信息")
	public Wrapper<MdcSysDict> queryMdcDictById(@ApiParam(name = "dictId", value = "字典id") @PathVariable Long dictId) {
		logger.info("根据Id查询字典信息, dictId={}", dictId);
		return WrapMapper.ok(mdcDictService.getMdcDictById(dictId));
	}

//	/**
//	 * 根据id修改字典的禁用状态
//	 *
//	 * @return the wrapper
//	 */
//	@PostMapping(value = "/modifyStatus")
//	@ApiOperation(httpMethod = "POST", value = "根据id修改字典的禁用状态")
//	public Wrapper updateMdcDictStatusById(@ApiParam(name = "mdcDictStatusDto", value = "修改字典状态Dto") @RequestBody UpdateStatusDto updateStatusDto) {
//		logger.info("根据id修改字典的禁用状态 updateStatusDto={}", updateStatusDto);
//		LoginAuthDto loginAuthDto = getLoginAuthDto();
//		mdcDictService.updateMdcDictStatusById(updateStatusDto, loginAuthDto);
//		return WrapMapper.ok();
//	}
//
//	@PostMapping(value = "/save")
//	@ApiOperation(httpMethod = "POST", value = "编辑字典")
//	public Wrapper saveDict(@ApiParam(name = "saveDict", value = "编辑字典") @RequestBody MdcEditDictDto mdcDictAddDto) {
//		MdcDict mdcDict = new MdcDict();
//		LoginAuthDto loginAuthDto = getLoginAuthDto();
//		BeanUtils.copyProperties(mdcDictAddDto, mdcDict);
//		mdcDictService.saveMdcDict(mdcDict, loginAuthDto);
//		return WrapMapper.ok();
//	}
//
//	/**
//	 * 根据id删除字典
//	 *
//	 * @param id the id
//	 *
//	 * @return the wrapper
//	 */
//	@PostMapping(value = "/deleteById/{id}")
//	@ApiOperation(httpMethod = "POST", value = "根据id删除字典")
//	public Wrapper<Integer> deleteMdcDictById(@ApiParam(name = "id", value = "字典id") @PathVariable Long id) {
//		logger.info(" 根据id删除字典 id={}", id);
//		// 判断此字典是否有子节点
//		boolean hasChild = mdcDictService.checkDictHasChildDict(id);
//		if (hasChild) {
//			return WrapMapper.wrap(Wrapper.ERROR_CODE, "此字典含有子字典, 请先删除子字典");
//		}
//
//		int result = mdcDictService.deleteByKey(id);
//		return super.handleResult(result);
//	}
}