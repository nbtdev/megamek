package megamek.common.net;

import megamek.common.net.marshall.PacketMarshaller;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PacketSerializer {
    PacketMarshaller marshaller;

    public PacketSerializer(PacketMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public Packet read(ByteBuffer byteBuffer) throws IOException {
        byte[] frameData = byteBuffer.array();
        DataInputStream in = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(frameData)));

        boolean isZip = in.readBoolean(); // we are always zipped
        int encoding = in.readInt(); // always the same marshalling "encoding" too
        int len = in.readInt();
        byte[] packetData = new byte[len];
        in.read(packetData, 0, len);

        GZIPInputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(packetData));

        try {
            return marshaller.unmarshall(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] write(Packet packet) throws Exception {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        GZIPOutputStream out = new GZIPOutputStream(byteOutputStream);

        marshaller.marshall(packet, out);
        out.close();
        byte[] data = byteOutputStream.toByteArray();

        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();

        DataOutputStream dataStream = new DataOutputStream(new BufferedOutputStream(outBuffer));
        dataStream.writeBoolean(true);
        dataStream.writeInt(PacketMarshaller.NATIVE_SERIALIZATION_MARSHALING);
        dataStream.writeInt(data.length);
        dataStream.write(data);
        dataStream.close();

        return outBuffer.toByteArray();
    }
}
