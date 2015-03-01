import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.core.assertion._
import java.util.Calendar
import java.text.SimpleDateFormat

class Configuration extends Simulation {

  val httpProtocol = http
    .baseURL("http://localhost:9874")

  val loadUserAgentConfiguration = scenario("UserAgent Configuration")
        .repeat(1) {
            exec(
                http("UserAgent Configuration")
                    .post("/configuration/save")
                    .body(StringBody("{\"name\":\"UserAgent\",\"type\":\"UserAgent\",\"version\":0,\"userAgentField\":\"UserAgent\"}"))
                    .asJSON
            )			
		}
		
val loadStorageConfiguration = scenario("Storage Configuration")
        .repeat(1) {           
            exec(
                http("Storage Configuration")
                    .post("/configuration/save")
                    .body(StringBody("{\"name\":\"Store\",\"type\":\"Store\",\"version\":0,\"timeOutInMilliseconds\":5000,\"bulkSize\":1000,\"bulkPeriodInMilliseconds\":500}"))
                    .asJSON
            )            
		}
  
val loadSequenceConfiguration = scenario("Sequence Configuration")
        .repeat(1) {           
            exec(
                http("Sequence Configuration")
                    .post("/configuration/save")
                    .body(StringBody("{\"taskConfigurations\":{\"0\":\"UserAgent\",\"1\":\"Store\"},\"version\":0,\"name\":\"Sequence\",\"type\":\"Sequence\"}"))
                    .asJSON
            )
		}
		
  setUp(loadUserAgentConfiguration.inject(constantUsersPerSec(1) during (1 seconds)).protocols(httpProtocol),
		loadStorageConfiguration.inject(constantUsersPerSec(1) during (1 seconds)).protocols(httpProtocol),
		loadSequenceConfiguration.inject(constantUsersPerSec(1) during (1 seconds)).protocols(httpProtocol))
    .assertions(global.successfulRequests.percent.is(100))
}
