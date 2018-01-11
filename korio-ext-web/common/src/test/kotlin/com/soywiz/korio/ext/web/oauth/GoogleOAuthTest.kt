package com.soywiz.korio.ext.web.oauth

import com.soywiz.korio.async.syncTest
import com.soywiz.korio.net.http.FakeHttpClient
import kotlin.test.assertEquals

class GoogleOAuthTest {
	val client = FakeHttpClient()
	val oauth = GoogleOAuth(clientId = "myclient", clientSecret = "mysecret", client = client)
	val redirUrl = "https://myredir"

	@kotlin.test.Test
	fun testGetTokenUserId() = syncTest {
		client.onRequest().response("{\"user_id\":\"myuserid\"}")
		assertEquals("myuserid", oauth.getUserId("mytoken"))
		assertEquals(
				listOf("""GET, /oauth2/v1/tokeninfo?id_token=tokenId, Headers((Content-Type, [application/json])), null"""),
				client.log
		)
	}

	@kotlin.test.Test
	fun testGetTokenId() = syncTest {
		client.onRequest().response("{\"id_token\":\"mytoken\"}")
		assertEquals("mytoken", oauth.getTokenId("mycode", redirUrl))
		assertEquals(
				listOf("""POST, https://accounts.google.com/o/oauth2/token, Headers((content-length, [118]), (Content-Type, [application/x-www-form-urlencoded])), code=mycode&client_id=myclient&client_secret=mysecret&redirect_uri=https%3A%2F%2Fmyredir&grant_type=authorization_code"""),
				client.log
		)
	}

	@kotlin.test.Test
	fun testGenerateUrl() {
		assertEquals(
				"https://accounts.google.com/o/oauth2/auth?client_id=myclient&response_type=code&scope=openid+email&redirect_uri=https%3A%2F%2Fmyredir&state=state",
				oauth.generateUrl("state", redirUrl)
		)
	}
}