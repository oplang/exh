package com.txts.task;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.txts.util.agv.AgvUtil;
import com.txts.util.code.CodeUtil;
import com.txts.util.device.DeviceCollectUtil;
import com.txts.util.modbus.ModbusUtil;
import com.txts.util.rfid.RfidLoopRead;
import com.txts.util.visio.VisioUtil;

/**
 * 硬件操作的汇总类
 * 
 * @description
 * 
 * @author lfy
 * @time 2018年9月28日-下午1:24:42
 */
@SuppressWarnings("all")
@Component
public class WarehouseHelper {
	public static String currentOrder = "";// 当前订单
	public static boolean isRun = false;// 是否正在运行
	private static final Logger logger = Logger.getLogger(WarehouseHelper.class);
	private static String ip = "10.0.0.2";// IP地址
	private static int port = 502;// 端口 默认502
	private static int slave = 0;// 从站
	private static Long waitMillis = 2000L;// 默认等待时间

	private static int maxWait = 120;// 最大等待2分钟

	public static void main(String[] args) throws Exception {
		WarehouseHelper w = new WarehouseHelper();
		// w.modbus4xWrite("立体库使用", 35,1);
		w.modbus4xWrite("立体库", 38,7 );
		Thread.sleep(5000);
		w.modbus4xWait("立体库", 37, 1);
		// Thread.sleep(5000);
		// w.modbus4xWait("立体库使用中等待", 37, 111);
		// 读取到9999的时候报警
		//////////////////

	}

	////////////////////////
	////// 通用操作代码
	/////////////////////////

	/**
	 * 阻塞线程 等待机完成读取操作(modbus 3x类型)
	 * 
	 * @author lfy
	 * @time 2018年9月28日-下午2:18:59
	 * @param machineName
	 *            机器名(记录日志用)
	 * @param address
	 *            modbus地址
	 * @param value
	 *            期望值 即读取值会变成这个的时候 完成操作
	 * @return
	 * @throws Exception
	 */
	public static boolean modbus3xWait(String machineName, int address, int value) throws Exception {
		try {
			logger.info("开始等待" + machineName + "完成操作.");
			int read = ModbusUtil.readRegister(ip, port, slave, address);
			int i = 1;
			while (read != value) {
				logger.info("正在读取" + machineName + "信息,当前第" + i + "次读取,读取值为:" + value + ".");
				i++;
				Thread.sleep(waitMillis);
				// 这个方法用于区分3x和4x
				read = ModbusUtil.readRegister(ip, port, slave, address);
			}
			logger.info(machineName + "完成操作,结束等待.");
			return true;
		} catch (Exception e) {
			logger.info(machineName + "等待出现异常.", e);
			e.printStackTrace();
			throw new Exception(machineName + "写入出现异常");
		}
	}

	/**
	 * 阻塞线程 等待机完成读取操作(modbus 4x类型)
	 * 
	 * @author lfy
	 * @time 2018年9月28日-下午2:18:59
	 * @param machineName
	 *            机器名(记录日志用)
	 * @param address
	 *            modbus地址
	 * @param value
	 *            期望值 即读取值会变成这个的时候 完成操作
	 * @return
	 * @throws Exception
	 */
	public static boolean modbus4xWait(String machineName, int address, int value) throws Exception {
		try {
			logger.info("开始等待" + machineName + "完成操作.");

			Integer read = ModbusUtil.readRegister(ip, port, address, slave);
			int i = 1;
			logger.info("正在读取" + machineName + "信息,第" + i + "次读取,读取值为:" + read + ".,期望值为:" + value + ".");
			while (read != value) {
				if (i >= maxWait)
					throw new Exception(machineName + "读取等待时间过久.");
				Thread.sleep(waitMillis);
				i++;
				// 这个方法用于区分3x和4x
				read = ModbusUtil.readRegister(ip, port, address, slave);
				logger.info("正在读取" + machineName + "信息,第" + i + "次读取,读取值为:" + read + ".,期望值为:" + value + ".");

			}
			logger.info(machineName + "完成操作,结束等待.");
			return true;
		} catch (Exception e) {
			logger.info(machineName + "等待出现异常.", e);
			e.printStackTrace();
			throw new Exception(machineName + "读取出现异常");
		}
	}

	/**
	 * 写入操作 将数值写入到modbus
	 * 
	 * @author lfy
	 * @time 2018年9月28日-下午2:22:35
	 * @param machineName
	 *            机器名(记录日志用)
	 * @param address
	 *            modbus地址
	 * @param value
	 *            期望值 即读取值会变成这个的时候 完成操作
	 * @return
	 */
	public static boolean modbus4xWrite(String machineName, int address, int value) {
		try {
			logger.info("开始启动" + machineName + ".");
			ModbusUtil.writeRegister(ip, port, slave, address, value);
			logger.info("启动" + machineName + "完成,未出现异常.");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("启动" + machineName + "失败,出现异常.", e);
			return false;
		}
	}
}
