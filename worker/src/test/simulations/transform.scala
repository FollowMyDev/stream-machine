import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.core.assertion._
import java.util.Calendar
import java.text.SimpleDateFormat



class Transform extends Simulation {

  val httpProtocol = http
    .baseURL("http://localhost:9874")

  val loadConfiguration = scenario("Transform Event")
    .repeat(1) {
    exec(
      http("A+B=C Trans")
        .post("/event/transform/AplusBinC")
        .body(StringBody("{\"a\":1,\"b\":2}"))
        .asJSON
    )
  }

  setUp(loadConfiguration.inject(constantUsersPerSec(1) during (1 seconds)).protocols(httpProtocol))
    .assertions(global.successfulRequests.percent.is(100))
}
