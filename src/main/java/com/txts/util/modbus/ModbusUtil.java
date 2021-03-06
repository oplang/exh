package com.txts.util.modbus;

import java.net.InetAddress;

import com.txts.task.WarehouseHelper;

import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.ReadCoilsResponse;
import net.wimpi.modbus.msg.ReadInputDiscretesRequest;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.net.TCPMasterConnection;

public class ModbusUtil {
	public static void main(String[] args) throws InterruptedException {
		// ModbusUtil mu = new ModbusUtil();
		String ip = "10.0.0.1";
		int port = 502;
		int readSlave = 1;
		int writeSlava = 1;
		long millis = System.currentTimeMillis();
		WarehouseHelper w = new WarehouseHelper();
		// while (true) {
		// Thread.sleep(3000);
		// w.modbus4xWrite("立体库", 38, 1000);
		// writeRegister(ip, port, writeSlava, 35 ,1);//4#rob stop
		// writeRegister(ip, port, writeSlava, 25 ,0);//1
		/*
		 * for (int i = 0; i < 1000; i++) {
		 * 
		 * writeRegister(ip, port, writeSlava, 3 ,0);//1#rob ng Thread.sleep(5000);
		 * writeRegister(ip, port, writeSlava, 3 ,1);//1#rob ng
		 * 
		 * }
		 */
		// writeRegister(ip, port, writeSlava, 35,0);//写入
		// }

		// writeRegister(ip, port, writeSlava, 3 ,1 );//写入
		// System.out.println("write cost:"+(System.currentTimeMillis()-millis));
		// Thread.sleep(2000);
		// millis = System.currentTimeMillis();
		// System.out.println("read:"+ ModbusUtil.readRegister(ip, port,15,
		// readSlave));;
		// System.out.println("read cost:"+(System.currentTimeMillis()-millis)+"ms");
		for (int i = 1; i < 10000; i++) {

			// System.out.println(i+"read:"+ ModbusUtil.readRegister(ip, port,19,
			// readSlave));;
			// System.out.println("read:"+ ModbusUtil.readRegister(ip, port,19,
			// readSlave));;
			// int v = ModbusUtil.readInputRegister(ip, port, i, readSlave);
			// int rVal = ModbusUtil.readRegister(ip, port, i, readSlave);
			// System.out.println(i+"-read:"+rVal);
			// System.out.println("-----");
		}

	}

	public static void writeTest(String ip, int port, int slaveId, int address, int value) {

		try {
			InetAddress addr = InetAddress.getByName(ip);

			TCPMasterConnection connection = new TCPMasterConnection(addr);
			connection.setPort(port);
			connection.connect();

			ModbusTCPTransaction trans = new ModbusTCPTransaction(connection);

			UnityRegister register = new UnityRegister(value);
			UnityRegister[] rs = { register, register };
			ModbusRequest req = null;
			req = new WriteSingleRegisterRequest(address, register);
			// net.wimpi.modbus.msg
			req = new WriteMultipleRegistersRequest(address, rs);

			req.setUnitID(slaveId);
			trans.setRequest(req);
			System.out.println("ModbusSlave: FC" + req.getFunctionCode() + " value=" + register.getValue());
			trans.execute();
			ModbusResponse response = trans.getResponse();
			System.out.println("res:" + response.getHexMessage());
			connection.close();
		} catch (Exception ex) {
			System.out.println("Error in code");
			ex.printStackTrace();
		}
	}

	/**
	 * 查询Function 为Input Status的寄存器
	 * 
	 * @param ip
	 * @param address
	 * @param count
	 * @param slaveId
	 * @return
	 * @throws ModbusIOException
	 * @throws ModbusSlaveException
	 * @throws ModbusException
	 */
	public static int readDigitalInput(String ip, int port, int address, int slaveId) {
		int data = 0;

		try {
			InetAddress addr = InetAddress.getByName(ip);

			// 建立连接
			TCPMasterConnection con = new TCPMasterConnection(addr);

			con.setPort(port);

			con.connect();

			// 第一个参数是寄存器的地址，第二个参数时读取多少个
			ReadInputDiscretesRequest req = new ReadInputDiscretesRequest(address, 1);

			// 这里设置的Slave Id, 读取的时候这个很重要
			req.setUnitID(slaveId);

			ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

			trans.setRequest(req);

			// 执行查询
			trans.execute();

			// 得到结果
			ReadInputDiscretesResponse res = (ReadInputDiscretesResponse) trans.getResponse();

			if (res.getDiscretes().getBit(0)) {
				data = 1;
			}

			// 关闭连接
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public static int readInputRegister(String ip, int port, int address, int slaveId) {
		int data = 0;

		try {
			InetAddress addr = InetAddress.getByName(ip);
			TCPMasterConnection con = new TCPMasterConnection(addr);

			// Modbus.DEFAULT_PORT;
			con.setPort(port);
			con.connect();

			// 这里重点说明下，这个地址和数量一定要对应起来
			ReadInputRegistersRequest req = new ReadInputRegistersRequest(address, 1);

			// 这个SlaveId一定要正确
			req.setUnitID(slaveId);

			ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

			trans.setRequest(req);

			trans.execute();

			ReadInputRegistersResponse res = (ReadInputRegistersResponse) trans.getResponse();

			data = res.getRegisterValue(0);
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public static int readDigitalOutput(String ip, int port, int address, int slaveId) {
		int data = 0;
		try {
			InetAddress addr = InetAddress.getByName(ip);

			TCPMasterConnection con = new TCPMasterConnection(addr);
			con.setPort(port);
			con.connect();

			ReadCoilsRequest req = new ReadCoilsRequest(address, 1);

			req.setUnitID(slaveId);

			ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

			trans.setRequest(req);

			trans.execute();

			ReadCoilsResponse res = ((ReadCoilsResponse) trans.getResponse());

			if (res.getCoils().getBit(0)) {
				data = 1;
			}

			con.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return data;
	}

	public static int readRegister(String ip, int port, int address, int slaveId) {
		int data = 0;
		try {
			InetAddress addr = InetAddress.getByName(ip);

			TCPMasterConnection con = new TCPMasterConnection(addr);

			con.setPort(port);
			con.connect();
			ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(address, 1);
			req.setUnitID(slaveId);

			ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

			trans.setRequest(req);

			trans.execute();

			ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();

			data = res.getRegisterValue(0);

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * 写入数据到真机，数据类型是RE
	 * 
	 * @param ip
	 * @param port
	 * @param slaveId
	 * @param address
	 * @param value
	 */
	public static void writeRegister(String ip, int port, int slaveId, int address, int value) {

		try {
			InetAddress addr = InetAddress.getByName(ip);

			TCPMasterConnection connection = new TCPMasterConnection(addr);
			connection.setPort(port);
			connection.connect();

			ModbusTCPTransaction trans = new ModbusTCPTransaction(connection);

			UnityRegister register = new UnityRegister(value);
			WriteSingleRegisterRequest req = new WriteSingleRegisterRequest(address, register);

			req.setUnitID(slaveId);
			trans.setRequest(req);

			System.out.println("ModbusSlave: FC" + req.getFunctionCode() + " ref=" + req.getReference() + " value="
					+ register.getValue());
			trans.execute();

			connection.close();
		} catch (Exception ex) {
			System.out.println("Error in code");
			ex.printStackTrace();
		}
	}

	/**
	 * 写入数据到真机的DO类型的寄存器上面
	 * 
	 * @param ip
	 * @param port
	 * @param slaveId
	 * @param address
	 * @param value
	 */
	public static void writeDigitalOutput(String ip, int port, int slaveId, int address, int value) {

		try {
			InetAddress addr = InetAddress.getByName(ip);

			TCPMasterConnection connection = new TCPMasterConnection(addr);
			connection.setPort(port);
			connection.connect();

			ModbusTCPTransaction trans = new ModbusTCPTransaction(connection);

			boolean val = true;

			if (value == 0) {
				val = false;
			}

			WriteCoilRequest req = new WriteCoilRequest(address, val);

			req.setUnitID(slaveId);
			trans.setRequest(req);

			trans.execute();
			connection.close();
		} catch (Exception ex) {
			System.out.println("writeDigitalOutput Error in code");
			ex.printStackTrace();
		}
	}

}
