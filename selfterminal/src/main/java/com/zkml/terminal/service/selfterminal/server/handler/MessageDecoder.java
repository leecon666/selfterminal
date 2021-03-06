package com.zkml.terminal.service.selfterminal.server.handler;

import com.zkml.terminal.service.selfterminal.util.ASCIIUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author: likun
 * @Date: Created in  2018/12/10 14:01
 * @Description:消息解码器
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws
            Exception {
        if (byteBuf != null) {
            byteBuf.markReaderIndex();//标记一下当前的readIndex的位置
            int len = byteBuf.readableBytes();
            if (len > 0) {
                byte[] datas = new byte[len];
                byteBuf.readBytes(datas);
                String result = bytesToHexString(datas);
                if (result != null && !result.equals("") && result.startsWith("564B") == true) {
                    int bodyLength = Integer.parseInt(result.substring(8, 12), 16);
                    String messageBody = result.substring(36, result.length() - 2);
                    int messageBodyLength = messageBody.length();
                    if (messageBodyLength == bodyLength * 2) {
                        String checkCode = result.substring(result.length() - 2, result.length());
                        String calculatedCheckSum = ASCIIUtil.checkingCode(result.substring(4, result.length() - 2));
                        if (checkCode.equals(calculatedCheckSum)) {//验证校验码
                            list.add(result);
                        } else {
                            log.error("消息（{}）校验有问题", result);
                            byteBuf.resetReaderIndex();
                        }
                    } else {
                        log.error("此消息（{}）出现拆包问题", result);
                        byteBuf.resetReaderIndex();
                    }
                } else {
                    log.error("此消息（{}）有问题", result);
                }
            }
        } else {
            log.error("消息为空！");
        }

    }

    /**
     * @Description:byte数组转换成十六进制
     * @Author: likun
     * @Date: 2018/12/10 14:26
     * @param: bytes   byte数组
     */
    public static final String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str;
        for (int i = 0; i < bytes.length; i++) {
            str = Integer.toHexString(0xFF & bytes[i]);
            if (str.length() < 2) {
                sb.append(0);
            }
            sb.append(str.toUpperCase());
        }
        return sb.toString();
    }
}
