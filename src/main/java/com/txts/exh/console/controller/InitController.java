package com.txts.exh.console.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.txts.config.BaseController;
import com.txts.task.HardwareHelper;
import com.txts.task.WarehouseHelper;
import com.txts.util.Json;
import com.txts.util.agv.AgvUtil;
import com.txts.util.code.CodeUtil;
import com.txts.util.rfid.RfidLoopRead;
import com.txts.util.rfid.RfidUtil;
import com.txts.util.visio.VisioUtil;

/**
 * @description
 * 
 * @author lfy
 * @time 2018年10月16日-上午9:20:24
 */
@Controller
@RequestMapping("init")
public class InitController extends BaseController {
	@Autowired
	private CodeUtil cu;
	@Autowired
	private AgvUtil au;
	@Autowired
	private RfidLoopRead rfid;
	@Autowired
	private VisioUtil vu;
	public static boolean isInited = false;// 是否初始化过
	@Autowired
	private WarehouseHelper w;
	@Autowired
	private HardwareHelper h;
	
	@Autowired
	private RfidUtil ru;

	/**
	 * 首页
	 * 
	 * @author lfy
	 * @time 2018年10月16日-上午9:24:15
	 * @return
	 */
	@RequestMapping("index")
	public Object index() {
		// 初始化界面
		return "common/init";
	}

	/**
	 * 初始化
	 * 
	 * @author lfy
	 * @time 2018年10月16日-上午9:24:21
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("init")
	public Object init() throws Exception {
		if (isInited) {
			return new Json(false, "已初始化初始过,如需重置请重启程序.", null);
		}
		Map m = getRequestMap();
		String code = toStr(m.get("code"));
		String agv = toStr(m.get("agv"));
		// Integer rfid = toInteger(m.get("rfid"));
		// this.rfid.start(rfid);
		this.cu.init(code);
		this.au.init(agv);
		vu.init();
		ru.init();
		// 分拣回原点
		h.modbus4xWrite("视觉分拣机器人", 1, 1);
		//立体库
		w.modbus4xWrite("立体库", 38, 1000);
		
		
		//
		h.modbus4xWrite("4号机械手", 35, 0);
		h.modbus4xWrite("机械手4等待点重置", 17, 0);
		
//		h.modbus4xWrite("机械手1-指令1", 31, 2);
//		Thread.sleep(3000L);
		h.modbus4xWrite("机械手1-指令1", 31, 0);
		h.modbus4xWrite("机械手1-指令1", 11, 0);
		
		h.modbus4xWrite("机械手2-指令2", 32, 2);
		Thread.sleep(3000L);
		h.modbus4xWrite("机械手2-指令2", 32, 0);
		
		h.modbus4xWrite("3号机械手-模拟包装", 34, 0);// 关闭机械手指令
				
		h.modbus4xWrite("打钉机", 33, 0);//关闭打钉机是
		h.modbus4xWrite("打钉机", 13, 0);// 打钉等待回写
		
		h.modbus4xWrite("流水线1-指令1", 25, 0);// 关闭流水线1
		h.modbus4xWrite("关闭2号流水线", 26, 0);
		h.modbus4xWrite("关闭分拣流水线", 27, 0);// 关闭分拣流水线
		h.modbus4xWrite("关闭转弯流水线", 28, 0);// 关闭转弯流水线
		h.modbus4xWrite("关闭3号流水线", 29, 0);// 关闭3#流水线
		h.modbus4xWrite("关闭4号流水线", 30, 0);// 关闭4#流水线
		h.modbus4xWrite("5号机械手-分拣", 36, 0);
		
		w.modbus4xWait("立体库", 37, 1);
		isInited = true;
		return new Json(true, "初始化成功", null);
	}

	/**
	 * 让agv回原点
	 * 
	 * @author lfy
	 * @time 2018年10月16日-上午9:24:31
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("agvBack")
	public Object agvBack() throws Exception {
		au.start2();// 启动
		new Thread(new Runnable() {
			public void run() {
				try {
					au.waitend2();// 等待回去
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		return new Json(true, "启动成功", null);
	}

	@ResponseBody
	@RequestMapping("agvGo")
	public Object agvGo() throws Exception {
		au.start2();// 启动
		new Thread(new Runnable() {
			public void run() {
				try {
					au.waitend2();// 等待回去
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		return new Json(true, "启动成功", null);
	}
}
