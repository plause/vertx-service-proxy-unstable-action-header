package com.example.starter

import io.vertx.core.CompositeFuture
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.serviceproxy.ServiceBinder
import io.vertx.serviceproxy.ServiceProxyBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestMainService {
  @Test
  fun `invoke service proxy without headers`(vertx: Vertx, context: VertxTestContext) {
    val consumer = bind(vertx) // register service
    val service = proxy(vertx, null) // no delivery options
    CompositeFuture.all(
      service.doSomething().onComplete(context.succeeding { assertEquals("something", it.getString("source")) }),
      service.doAnother().onComplete(context.succeeding { assertEquals("another", it.getString("source")) }),
      service.doWithArray().onComplete(context.succeeding { assertEquals(2, it.size()) }),
    ).onComplete(context.succeeding {
      assertTrue(it.succeeded())
    }).eventually {
      consumer.unregister()
    }.onComplete(context.succeedingThenComplete())
  }

  @Test
  fun `invoke service proxy with shared headers`(vertx: Vertx, context: VertxTestContext) {
    val consumer = bind(vertx)
    val options = DeliveryOptions().addHeader("test", "test")
    val service = proxy(vertx, options)
    service.doSomething().onComplete(context.succeeding {
      assertEquals("something", it.getString("source"))
      assertEquals(1, options.headers.getAll("action").size) // [doSomething]
    }).onComplete {
      service.doAnother().onComplete(context.succeeding {
        assertNotEquals("another", it.getString("source")) // call succeed but wrong content
        assertEquals(2, options.headers.getAll("action").size) // [doSomething, doAnother]
      })
    }.onComplete {
      service.doWithArray().onComplete(context.failing {
        assertTrue(it is ClassCastException) // call succeed but json object can not cast to json array
        assertEquals(3, options.headers.getAll("action").size) // [doSomething, doAnother, doWithArray]
      })
    }.eventually {
      consumer.unregister()
    }.onComplete(context.succeedingThenComplete())
  }

  private fun proxy(vertx: Vertx, options: DeliveryOptions?) = MainService::class.java.let {
    ServiceProxyBuilder(vertx).setAddress(it.name).setOptions(options).build(it)
  }

  private fun bind(vertx: Vertx) = MainService::class.java.let {
    ServiceBinder(vertx).setAddress(it.name).register(it, MainServiceImpl())
  }
}
