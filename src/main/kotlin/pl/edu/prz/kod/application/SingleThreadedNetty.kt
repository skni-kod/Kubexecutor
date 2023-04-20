package pl.edu.prz.kod.application

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.HttpServerKeepAliveHandler
import io.netty.handler.stream.ChunkedWriteHandler
import org.http4k.core.HttpHandler
import org.http4k.server.Http4kChannelHandler
import org.http4k.server.Http4kServer
import org.http4k.server.ServerConfig
import java.net.InetSocketAddress
import java.time.Duration
import java.util.concurrent.TimeUnit

class SingleThreadedNetty(val port: Int = 8000, override val stopMode: ServerConfig.StopMode) : ServerConfig {
    constructor(port: Int = 8000) : this(port, ServerConfig.StopMode.Graceful(Duration.ofSeconds(15)))

    val shutdownTimeoutMillis = when (stopMode) {
        is ServerConfig.StopMode.Graceful -> stopMode.timeout.toMillis()
        is ServerConfig.StopMode.Immediate -> 0
    }

    override fun toServer(http: HttpHandler): Http4kServer = object : Http4kServer {
        private val commonGroup = NioEventLoopGroup(1)
        private var closeFuture: ChannelFuture? = null
        private lateinit var address: InetSocketAddress

        override fun start(): Http4kServer = apply {
            val bootstrap = ServerBootstrap()
            bootstrap.group(commonGroup)
                .channelFactory { NioServerSocketChannel() }
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    public override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast("codec", HttpServerCodec())
                        ch.pipeline().addLast("keepAlive", HttpServerKeepAliveHandler())
                        ch.pipeline().addLast("aggregator", HttpObjectAggregator(Int.MAX_VALUE))

                        ch.pipeline().addLast("streamer", ChunkedWriteHandler())
                        if (http != null) ch.pipeline().addLast("httpHandler", Http4kChannelHandler(http))
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 1000)
                .childOption(ChannelOption.SO_KEEPALIVE, true)

            val channel = bootstrap.bind(port).sync().channel()
            address = channel.localAddress() as InetSocketAddress
            closeFuture = channel.closeFuture()
        }

        override fun stop() = apply {
            closeFuture?.cancel(false)

            val sleepTime = minOf(2000L, shutdownTimeoutMillis)
            commonGroup.shutdownGracefully(sleepTime, shutdownTimeoutMillis, TimeUnit.MILLISECONDS).sync()
        }

        override fun port(): Int = if (port > 0) port else address.port
    }
}