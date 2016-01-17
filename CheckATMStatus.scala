
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class CheckATMStatus extends Simulation {

	val httpProtocol = http
		.baseURL("http://178.33.83.130:1880")
		.inferHtmlResources()

	val headers_0 = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

	val headers_1 = Map(
		"Accept" -> "application/font-woff2;q=1.0,application/font-woff;q=0.9,*/*;q=0.8",
		"Accept-Encoding" -> "identity")

	val headers_3 = Map("Accept" -> "*/*")

	val headers_5 = Map("Accept" -> "text/plain")

	val headers_6 = Map("Accept" -> "application/json, text/plain, */*")

	val headers_18 = Map(
		"Accept" -> "application/json, text/plain, */*",
		"Origin" -> "http://178.33.83.130:1880")

    val uri1 = "clients1.google.com"
    val uri2 = "178.33.83.130"

	val scn = scenario("CheckATMStatus")
		// log on main page
		.exec(http("request_0")
			.get("/")
			.headers(headers_0)
			.resources(http("request_1")
			.get("http://" + uri2 + ":1880/fonts/fontawesome-webfont.woff2?v=4.5.0")
			.headers(headers_1),
            http("request_2")
			.post("http://" + uri1 + "/ocsp")
			.headers(headers_0)
			.body(RawFileBody("CheckATMStatus_0002_request.txt")),
            http("request_3")
			.get("http://" + uri2 + ":1880/scripts/main.js")
			.headers(headers_3),
            http("request_4")
			.get("http://" + uri2 + ":1880/favicon.ico")
			.headers(headers_0),
            http("request_5")
			.get("http://" + uri2 + ":1880/proto/atm.proto")
			.headers(headers_5),
            http("request_6")
			.get("http://" + uri2 + ":1880/views/main.html")
			.headers(headers_6),
            http("request_7")
			.get("http://" + uri2 + ":1880/images/dollars.7de7f15f.png"),
            http("request_8")
			.get("http://" + uri2 + ":1880/images/machine_bottom.6272169d.jpg"),
            http("request_9")
			.get("http://" + uri2 + ":1880/images/machine_top.cfd74753.jpg"),
            http("request_10")
			.get("http://" + uri2 + ":1880/images/green-light-off.4b159c79.png"),
            http("request_11")
			.get("http://" + uri2 + ":1880/images/orange-light-on.884634e7.png"),
            http("request_12")
			.get("http://" + uri2 + ":1880/images/card-holder_top.7b1bac6c.jpg"),
            http("request_13")
			.get("http://" + uri2 + ":1880/images/card-holder--bottom.dd0c8eff.png"),
            http("request_14")
			.get("http://" + uri2 + ":1880/images/green-light-on.1412f579.png")))
		.pause(21)
		// check monitoring
		.exec(http("request_15")
			.get("/views/monitoring.html")
			.headers(headers_6)
			.resources(http("request_16")
			.get("http://" + uri2 + ":1880/images/ko.7b28887e.png"),
            http("request_17")
			.get("http://" + uri2 + ":1880/images/ok.d076834e.png"),
            http("request_18")
			.get("http://" + uri2 + ":8180/admin/healthcheck")
			.headers(headers_18)))
		.pause(17)
		// chech accounts
		.exec(http("request_19")
			.get("/views/accounts.html")
			.headers(headers_6)
			.resources(http("request_20")
			.get("http://" + uri2 + ":8180/accounts")
			.headers(headers_18)))
		.pause(22)
		// see account details
		.exec(http("request_21")
			.get("/views/account.html")
			.headers(headers_6)
			.resources(http("request_22")
			.get("http://" + uri2 + ":8180/accounts/c75c7ab8-5cc9-46c5-b6fd-e2996a38f5d5")
			.headers(headers_18)))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}