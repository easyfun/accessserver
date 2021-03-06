package com.hy.device.resolver;

import com.hy.bean.Header;
import com.hy.bean.MessageTypeResp;
import com.hy.bean.NettyMessage;
import com.hy.device.DeviceThread;
import com.hy.device.SendFileThread;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Created by cpazstido on 2016/6/1.
 */
public class DeviceDataResolver {
    public static Logger logger = Logger.getLogger(DeviceDataResolver.class);


    public NettyMessage loginChallengeResolver(DeviceThread deviceThread, NettyMessage message){
        String body = new String((byte[]) message.getBody());
        //logger.debug("body:"+ body);
        String randomCode = null;
        try {
            //logger.debug("开始解析:"+ body);
            Document doc = null;
            doc = DocumentHelper.parseText(body); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            String EventType = rootElt.attributeValue("EventType");// 拿到根节点的属性
            if(EventType != null &&EventType.equals("LoginChallenge")){
                Element eRandomCode=rootElt.element("RandomCode");
                randomCode = eRandomCode.getText();
                //logger.debug("randomCode:"+randomCode);
                //logger.debug("解析:"+ body+" 完毕！");
            }else{
                logger.error("xml不符合规范！");
            }
        } catch (Exception e) {
            logger.error(e);
        }

        //回复消息
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("eMonitor_XML");
        root.addAttribute("EventType","LoginChallenge");
        Element eRandomCode = root.addElement("RandomCode");
        eRandomCode.setText(randomCode);
        Element eDeviceId = root.addElement("DeviceId");
        eDeviceId.setText(deviceThread.deviceID);
        Element eIPCNum = root.addElement("IPCNum");
        eIPCNum.setText("2");
        Element eSoftwareVersion = root.addElement("SoftwareVersion");
        eSoftwareVersion.setText("4.0");
        Element eData = root.addElement("Data");
        eData.setText("this is a test!");
        String oBody = document.asXML();
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setVersion(message.getHeader().getVersion());
        header.setFlag(message.getHeader().getFlag());
        header.setIndex(message.getHeader().getIndex());
        header.setTypes(MessageTypeResp.LOGIN_Resp.value());
        header.setLen(oBody.length());
        nettyMessage.setHeader(header);
        nettyMessage.setBody(oBody.getBytes());
        return nettyMessage;
    }

    public NettyMessage heartBeatResolver(DeviceThread deviceThread, NettyMessage message){
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setVersion(message.getHeader().getVersion());
        header.setFlag(message.getHeader().getFlag());
        header.setIndex(message.getHeader().getIndex());
        header.setTypes(MessageTypeResp.HEARTBEAT_Resp.value());
        header.setLen(0);
        nettyMessage.setHeader(header);
        return nettyMessage;
    }

    public String infoResolver(DeviceThread deviceThread, NettyMessage nettyMessage){
        String body = new String((byte[]) nettyMessage.getBody());
        String info = null;
        try {
            //logger.debug("开始解析:"+ body);
            Document doc = null;
            doc = DocumentHelper.parseText(body); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            String EventType = rootElt.attributeValue("EventType");// 拿到根节点的属性
            if(EventType != null &&EventType.equals("LoginChallenge")){
                Element eInfo=rootElt.element("Info");
                info = eInfo.getText();
                //logger.debug("randomCode:"+randomCode);
                //logger.debug("解析:"+ body+" 完毕！");
            }else{
                logger.error("xml不符合规范！");
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return info;
    }

    public String textDataResolver(DeviceThread deviceThread, NettyMessage nettyMessage){
        String body = new String((byte[]) nettyMessage.getBody());
        return body;
    }

    public NettyMessage xmlResolver(DeviceThread deviceThread, NettyMessage message){
        String body = new String((byte[]) message.getBody());
        try {
            logger.debug("收到xml命令:"+ body);
            Document doc = null;
            doc = DocumentHelper.parseText(body); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            String sEventType = rootElt.attributeValue("EventType");// 拿到根节点的属性
            if(sEventType !=null && sEventType.compareTo("GetDeviceID") ==0){
                return getDeviceID(deviceThread, message);
            }else if(sEventType !=null && sEventType.compareTo("GetPicture1") ==0){
                return getPicture(deviceThread,message);
            }else if(sEventType != null && sEventType.compareTo("SetTime") == 0){
                return setTime(deviceThread,message);
            }else if (sEventType != null && sEventType.compareTo("PTZ") == 0){
                return PTZ(deviceThread,message);
            }else if (sEventType != null && sEventType.compareTo("ChangePTZMode") == 0){
                return ChangePTZMode(deviceThread,message);
            }else if (sEventType != null && sEventType.compareTo("StartRealPlay") == 0){
                return DTOA_XML(deviceThread,message,"StartRealPlay","StartRealPlay Success!");
            }else if (sEventType != null && sEventType.compareTo("StopRealPlay") == 0){
                return DTOA_XML(deviceThread,message,"StopRealPlay","StopRealPlay Success!");
            }else if (sEventType != null && sEventType.compareTo("VideoEncoderConfiguration") == 0){
                return DTOA_XML(deviceThread,message,"VideoEncoderConfiguration","VideoEncoderConfiguration Success!");
            }else if (sEventType != null && sEventType.compareTo("SetPreset") == 0){
                return DTOA_XML(deviceThread,message,"SetPreset","SetPreset Success!");
            }else if (sEventType != null && sEventType.compareTo("GotoPreset") == 0){
                return DTOA_XML(deviceThread,message,"GotoPreset","GotoPreset Success!");
            }else if (sEventType != null && sEventType.compareTo("ClearPreset") == 0){
                return DTOA_XML(deviceThread,message,"ClearPreset","ClearPreset Success!");
            }else if (sEventType != null && sEventType.compareTo("SavePatrol") == 0){
                return DTOA_XML(deviceThread,message,"SavePatrol","SavePatrol Success!");
            }else if (sEventType != null && sEventType.compareTo("StartPatrol") == 0){
                return DTOA_XML(deviceThread,message,"StartPatrol","StartPatrol Success!");
            } else if (sEventType != null && sEventType.compareTo("StopPatrol") == 0){
                return DTOA_XML(deviceThread,message,"StopPatrol","StopPatrol Success!");
            } else if (sEventType != null && sEventType.compareTo("DeletePatrol") == 0){
                return DTOA_XML(deviceThread,message,"DeletePatrol","DeletePatrol Success!");
            } else if (sEventType != null && sEventType.compareTo("TaskVideo") == 0){
                return DTOA_XML(deviceThread,message,"TaskVideo","TaskVideo Success!");
            } else if (sEventType != null && sEventType.compareTo("TaskPic") == 0){
                return DTOA_XML(deviceThread,message,"TaskPic","TaskPic Success!");
            } else if (sEventType != null && sEventType.compareTo("VisualCameraPowerOn") == 0){
                return DTOA_XML(deviceThread,message,"VisualCameraPowerOn","VisualCameraPowerOn Success!");
            } else if (sEventType != null && sEventType.compareTo("VisualCameraPowerOff") == 0){
                return DTOA_XML(deviceThread,message,"VisualCameraPowerOff","VisualCameraPowerOff Success!");
            } else if (sEventType != null && sEventType.compareTo("ResetBoard") == 0){
                return DTOA_XML(deviceThread,message,"ResetBoard","ResetBoard Success!");
            } else if (sEventType != null && sEventType.compareTo("MobileFlow") == 0){
                return DTOA_XML(deviceThread,message,"MobileFlow","MobileFlow Success!");
            } else if (sEventType != null && sEventType.compareTo("GetPicture") == 0){
                return DTOA_XML(deviceThread,message,"GetPicture","GetPicture Success!");
            } else if (sEventType != null && sEventType.compareTo("SetDetectTime") == 0){
                return DTOA_XML(deviceThread,message,"SetDetectTime","SetDetectTime Success!");
            } else if (sEventType != null && sEventType.compareTo("GetResource") == 0){
                return DTOA_XML(deviceThread,message,"GetResource","GetResource Success!");
            } else if (sEventType != null && sEventType.compareTo("GetStat") == 0){
                return DTOA_XML(deviceThread,message,"GetStat","GetStat Success!");
            } else if (sEventType != null && sEventType.compareTo("SetMediaSrvAddr") == 0){
                return DTOA_XML(deviceThread,message,"SetMediaSrvAddr","SetMediaSrvAddr Success!");
            }
        }catch (Exception e){
            logger.debug(e);
        }
        logger.debug("nullllllllllllllllllllllllll");
        return null;
    }

    public NettyMessage getDeviceID(DeviceThread deviceThread, NettyMessage message){
        NettyMessage nettyMessage = new NettyMessage();
        String body = deviceThread.deviceID;
        Header header = new Header();
        header.setVersion(message.getHeader().getVersion());
        header.setFlag(message.getHeader().getFlag());
        header.setIndex(message.getHeader().getIndex());
        header.setTypes(MessageTypeResp.CMMD_RESP_TXT_RESULT.value());
        header.setLen(body.length());
        nettyMessage.setHeader(header);
        nettyMessage.setBody(body.getBytes());
        return nettyMessage;
    }

    public NettyMessage setTime(DeviceThread deviceThread, NettyMessage message){
        NettyMessage nettyMessage = new NettyMessage();
        String body = writeXmlForSetTime("set time success!");
        Header header = new Header();
        header.setVersion(message.getHeader().getVersion());
        header.setFlag(message.getHeader().getFlag());
        header.setIndex(message.getHeader().getIndex());
        header.setTypes(MessageTypeResp.CMMD_RESP_XML_RESULT.value());
        header.setLen(body.length());
        nettyMessage.setHeader(header);
        nettyMessage.setBody(body.getBytes());
        return nettyMessage;
    }

    public NettyMessage PTZ(DeviceThread deviceThread, NettyMessage message){
        NettyMessage nettyMessage = new NettyMessage();
        String body = writeXmlForInfo("PTZ", "PTZ success!");
        Header header = new Header();
        header.setVersion(message.getHeader().getVersion());
        header.setFlag(message.getHeader().getFlag());
        header.setIndex(message.getHeader().getIndex());
        header.setTypes(MessageTypeResp.CMMD_RESP_XML_RESULT.value());
        header.setLen(body.length());
        nettyMessage.setHeader(header);
        nettyMessage.setBody(body.getBytes());
        return nettyMessage;
    }

    public NettyMessage ChangePTZMode(DeviceThread deviceThread, NettyMessage message){
        NettyMessage nettyMessage = new NettyMessage();
        String body = writeXmlForInfo("ChangePTZMode", "ChangePTZMode success!");
        Header header = new Header();
        header.setVersion(message.getHeader().getVersion());
        header.setFlag(message.getHeader().getFlag());
        header.setIndex(message.getHeader().getIndex());
        header.setTypes(MessageTypeResp.CMMD_RESP_XML_RESULT.value());
        header.setLen(body.length());
        nettyMessage.setHeader(header);
        nettyMessage.setBody(body.getBytes());
        return nettyMessage;
    }

    public NettyMessage DTOA_XML(DeviceThread deviceThread, NettyMessage message,String eventType, String info){
        NettyMessage nettyMessage = new NettyMessage();
        String body = writeXmlForInfo(eventType, info);
        Header header = new Header();
        header.setVersion(message.getHeader().getVersion());
        header.setFlag(message.getHeader().getFlag());
        header.setIndex(message.getHeader().getIndex());
        header.setTypes(MessageTypeResp.CMMD_RESP_XML_RESULT.value());
        header.setLen(body.length());
        nettyMessage.setHeader(header);
        nettyMessage.setBody(body.getBytes());
        return nettyMessage;
    }

    public NettyMessage getPicture(DeviceThread deviceThread, NettyMessage message){
        NettyMessage nettyMessage = new NettyMessage();
        try {
            String body = writeXmlForGetPicture("success!picture will be send by data channel!");
            Header header = new Header();
            header.setVersion(message.getHeader().getVersion());
            header.setFlag(message.getHeader().getFlag());
            header.setIndex(message.getHeader().getIndex());
            header.setTypes(MessageTypeResp.CMMD_RESP_XML_RESULT.value());
            header.setLen(body.length());
            nettyMessage.setHeader(header);
            nettyMessage.setBody(body.getBytes());

            logger.debug("发送文件！");
            SendFileThread sendFileThread = new SendFileThread("172.16.16.112",9002);
            sendFileThread.start();
        }catch (Exception e){
            logger.error(e);
        }
        return nettyMessage;
    }

    public String writeXmlForGetPicture(String info) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("eMonitor_XML");
        root.addAttribute("EventType", "GetPicture");
        Element eRandomCode = root.addElement("Info");
        eRandomCode.setText(info);
        return document.asXML();
    }

    public String writeXmlForSetTime(String info) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("eMonitor_XML");
        root.addAttribute("EventType", "SetTime");
        Element eRandomCode = root.addElement("Info");
        eRandomCode.setText(info);
        return document.asXML();
    }

    public String writeXmlForInfo(String eventType, String info) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("eMonitor_XML");
        root.addAttribute("EventType", eventType);
        Element eRandomCode = root.addElement("Info");
        eRandomCode.setText(info);
        return document.asXML();
    }

}
