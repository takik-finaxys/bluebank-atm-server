
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://178.33.83.130:8222")
		.inferHtmlResources()

	val headers_0 = Map(
		"Accept" -> "application/font-woff2;q=1.0,application/font-woff;q=0.9,*/*;q=0.8",
		"Accept-Encoding" -> "identity")

	val headers_1 = Map("Origin" -> "http://178.33.83.130:8222")

    val uri1 = "178.33.83.130"

	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get("/fonts/fontawesome-webfont.woff2?v=4.5.0")
			.headers(headers_0)
			.check(status.is(304)))
		.pause(9)
		.exec(http("request_1")
			.get("http://" + uri1 + ":8180/accounts")
			.headers(headers_1))
		.pause(7)
		.exec(http("request_2")
			.get("/views/account.html")
			.resources(http("request_3")
			.get("http://" + uri1 + ":8180/accounts/b73cf3a6-8f29-4ef1-955a-94c7efae01af")
			.headers(headers_1)))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}