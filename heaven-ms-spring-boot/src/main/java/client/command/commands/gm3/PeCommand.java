package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import net.MaplePacketHandler;
import net.PacketProcessor;
import tools.FilePrinter;
import tools.HexTool;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.data.output.MaplePacketLittleEndianWriter;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PeCommand extends Command {
    {
        setDescription("模拟接收特定封包以进行调试。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        String packet = "";
        try {
            // 从文件加载封包数据
            InputStreamReader is = new FileReader("pe.txt");
            Properties packetProps = new Properties();
            packetProps.load(is);
            is.close();
            packet = packetProps.getProperty("pe");
        } catch (IOException ex) {
            ex.printStackTrace();
            player.yellowMessage("加载pe.txt失败");
            return;
        }

        // 创建封包写入器并将封包数据写入
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(HexTool.getByteArrayFromHexString(packet));

        // 创建封包读取器
        SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(mplew.getPacket()));

        // 读取封包ID
        short packetId = slea.readShort();

        // 获取与封包ID相关联的封包处理器
        final MaplePacketHandler packetHandler = PacketProcessor.getProcessor(0, c.getChannel()).getHandler(packetId);

        if (packetHandler != null && packetHandler.validateState(c)) {
            try {
                // 打印封包信息并处理封包
                player.yellowMessage("接收封包数据: " + packet);
                packetHandler.handlePacket(slea, c);
            } catch (final Throwable t) {
                // 处理异常情况并打印错误日志
                FilePrinter.printError(FilePrinter.PACKET_HANDLER + packetHandler.getClass().getName() + ".txt", t, "错误信息：" + (c.getPlayer() == null ? "" : "玩家 ; " + c.getPlayer() + " 在地图 ; " + c.getPlayer().getMapId() + " - ") + "账号 ; " + c.getAccountName() + "\r\n" + slea.toString());
            }
        }
    }
}