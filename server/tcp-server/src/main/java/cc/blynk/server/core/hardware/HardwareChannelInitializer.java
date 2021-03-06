package cc.blynk.server.core.hardware;

import cc.blynk.common.handlers.common.decoders.ReplayingMessageDecoder;
import cc.blynk.common.handlers.common.encoders.DeviceMessageEncoder;
import cc.blynk.common.stats.GlobalStats;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.handlers.common.ClientChannelStateHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
* The Blynk Project.
* Created by Dmitriy Dumanskiy.
* Created on 11.03.15.
*/
final class HardwareChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SessionsHolder sessionsHolder;
    private final GlobalStats stats;
    private final HardwareHandlersHolder handlersHolder;
    private final int hardTimeoutSecs;

    public HardwareChannelInitializer(SessionsHolder sessionsHolder, GlobalStats stats, HardwareHandlersHolder handlersHolder, int hardTimeoutSecs) {
        this.sessionsHolder = sessionsHolder;
        this.stats = stats;
        this.handlersHolder = handlersHolder;
        this.hardTimeoutSecs = hardTimeoutSecs;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //non-sharable handlers
        pipeline.addLast(new ReadTimeoutHandler(hardTimeoutSecs));
        pipeline.addLast(new ClientChannelStateHandler(sessionsHolder));
        pipeline.addLast(new ReplayingMessageDecoder(stats));
        pipeline.addLast(new DeviceMessageEncoder());

        //sharable business logic handlers initialized previously
        for (ChannelHandler handler : handlersHolder.getAllHandlers()) {
            pipeline.addLast(handler);
        }
    }
}
