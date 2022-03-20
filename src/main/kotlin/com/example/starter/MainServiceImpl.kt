package com.example.starter

import io.vertx.core.Future
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

class MainServiceImpl : MainService {
  override fun doSomething(): Future<JsonObject> {
    return Future.succeededFuture(json {
      obj("source" to "something")
    })
  }

  override fun doAnother(): Future<JsonObject> {
    return Future.succeededFuture(json {
      obj("source" to "another")
    })
  }

  override fun doWithArray(): Future<JsonArray> {
    return Future.succeededFuture(json {
      array("greetings", "vertx")
    })
  }
}
