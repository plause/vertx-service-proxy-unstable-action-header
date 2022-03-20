package com.example.starter

import io.vertx.codegen.annotations.ProxyGen
import io.vertx.codegen.annotations.VertxGen
import io.vertx.core.Future
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

@ProxyGen
@VertxGen
interface MainService {
  fun doSomething(): Future<JsonObject>
  fun doAnother(): Future<JsonObject>
  fun doWithArray(): Future<JsonArray>
}
