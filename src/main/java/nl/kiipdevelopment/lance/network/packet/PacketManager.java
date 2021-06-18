package nl.kiipdevelopment.lance.network.packet;

import nl.kiipdevelopment.lance.Main;
import nl.kiipdevelopment.lance.network.packet.packets.client.*;
import nl.kiipdevelopment.lance.network.packet.packets.server.*;

import java.util.*;
import java.util.function.Supplier;

public class PacketManager {
	public static short version = -1;

	public static final Map<Byte, Supplier<Packet>> packets = new HashMap<>();
	private static boolean initialised = false;

	public static void init() {
		if (!initialised) {
			initialised = true;

			PacketManager.register(
				new ClientCloseConnectionPacket(),
				new ClientExistsPacket(),
				new ClientGetPacket(),
				new ClientHandshakePacket(),
				new ClientHeartbeatPacket(),
				new ClientListPacket(),
				new ClientPasswordPacket(),
				new ClientSetPacket(),
				new ClientSwitchingStoragePacket(),
				new ServerCloseConnectionPacket(),
				new ServerExistsPacket(),
				new ServerGetPacket(),
				new ServerHandshakePacket(),
				new ServerHeartbeatPacket(),
				new ServerListPacket(),
				new ServerSetPacket(),
				new ServerWelcomePacket()
			);

			String implementationVersion = Main.class.getPackage().getImplementationVersion();

			if (implementationVersion == null) {
				version = Short.MAX_VALUE;
			} else {
				String[] parts = implementationVersion.split("\\.");

				if (parts.length == 3) {
					byte major = Byte.parseByte(parts[0]);
					byte minor = Byte.parseByte(parts[1]);
					byte patch = Byte.parseByte(parts[2]);

					version = (short) (major * 100 + minor * 10 + patch);
				}
			}
		}
	}

	public static void register(Packet packet) {
		packets.put(packet.id, packet.supplier);
	}

	public static void register(Packet packet, Packet... packets) {
		List<Packet> packetList = new ArrayList<>(Arrays.asList(packets));

		packetList.add(packet);

		packetList.forEach(PacketManager::register);
	}

	public static Supplier<Packet> obtain(byte id) {
		if (packets.containsKey(id)) {
			return packets.get(id);
		}

		throw new IllegalStateException("Unexpected value: " + id);
	}
}
