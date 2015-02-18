import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.core.assertion._
import java.util.Calendar
import java.text.SimpleDateFormat



class Configuration extends Simulation {

  val httpProtocol = http
    .baseURL("http://localhost:9200")

  val loadConfiguration = scenario("Load Configuration")
    .repeat(1) {
    exec(
      http("AplusBinC Configuration")
        .put("/configuration/EventTransformer/AplusBinC")
        .body(StringBody("{\"name\":\"AplusBinC\",\"type\":\"EventTransformer\",\"version\":0,\"template\":\"#macro( put $key $value )#${event.put($key,$value)}#end #macro( sum $keyA $keyB $keyC )#set( $valueA = $event.get($keyA) )#set( $valueB = $event.get($keyB) )#set( $valueC = $valueA+$valueB )#put( $keyC $valueC )#end #sum( \\\"a\\\" \\\"b\\\" \\\"c\\\")\"}"))
        .asJSON
    )
  }

  setUp(loadConfiguration.inject(constantUsersPerSec(1) during (1 seconds)).protocols(httpProtocol))
    .assertions(global.successfulRequests.percent.is(100))
}
