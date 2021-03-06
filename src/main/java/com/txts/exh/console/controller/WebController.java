package com.txts.exh.console.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.txts.exh.console.beans.StorageItem;
import com.txts.exh.console.service.SqlserverConnectService;

@Controller
public class WebController {

	/**
	 * 直接显示网页
	 * 
	 * @param pageName
	 * @return
	 */
	@RequestMapping(value = "/{p}")
	public String getStrString(@PathVariable("p") String pageName) {
		return "common/" + pageName;
	}

	@Autowired
	private SqlserverConnectService scs;

	// private static List<StorageItem> warehouses;// 立体库

	/**
	 * 获取立体库所有储位信息 数据库获取详情
	 * 
	 * @return
	 */
	@RequestMapping("/warehouse/details")
	@ResponseBody
	public List<StorageItem> getWarehouseDetails() {
		return scs.getWarehousDetails();
	}

	@RequestMapping("/warehouse/in")
	@ResponseBody
	public int inStorage(Integer id, String qrCode) {
		try {
			qrCode = new String(qrCode.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("qrCode:" + qrCode);
		return scs.inStorage(id, qrCode);
	}

	/**
	 * 选择一个储位上的物料开始整个流程
	 * 
	 * @param id
	 * @return System.out.println("准备开始流程!"); boolean flag = true;
	 *         Iterator<StorageItem> itr = warehouses.iterator(); StorageItem
	 *         storage = null; while (itr.hasNext()) { StorageItem item =
	 *         itr.next(); if (item.getId() == id) { if (item.isState()) { storage =
	 *         item; } continue; } if (!item.isState()) { flag = false;
	 *         System.out.println("流程中已有物料在加工"); } } if (storage != null && flag)
	 *         {// 找到物料且其他物料都没有在加工则开始整个流程 System.out.println("任务流开始!\n加工物料rfid:" +
	 *         storage.getRfid()); // TODO 开始整个流程 for (int i = 0; i <
	 *         warehouses.size(); i++) { if (warehouses.get(i).getId() == id) {
	 *         warehouses.get(i).setState(false); } } return true; }
	 */
	@RequestMapping("/start_process")
	@ResponseBody
	public boolean startTaskPool(Integer id) {

		return false;
	}
}
