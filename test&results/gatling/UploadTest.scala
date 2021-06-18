
package gatlingtest

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._


class UploadTest extends Simulation {

	val httpConf = http
		.baseUrl("http://localhost:8080")
	val contentType = Map("Content-Type" -> "multipart/form-data; boundary=----WebKitFormBoundaryJJvRh2v3nGuTTyVN")
	val scn = scenario("form Scenario")
		.exec(http("search request")
		.post("/") 
		.headers(contentType)
		.formUpload("file", "D:\\video\\test12.mp4"))
	setUp(scn.inject(atOnceUsers(20)).protocols(httpConf))
}

