package com.syroniko.casseteapp.utils

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import net.openid.appauth.*


const val AUTH_REQUEST_CODE = 999
private const val SPOTIFY_CLIENT_ID = "846a7d470725449994155b664cb7959b"
private const val SPOTIFY_REDIRECT_URI = "com.syroniko.casseteapp://auth"

object SpotifyAuthRequest{

    private const val authEndpoint = "https://accounts.spotify.com/authorize"
    private const val tokenEndpoint = "https://accounts.spotify.com/api/token"
    private val scopes = listOf(
        "user-read-playback-state",
        "playlist-modify-private",
        "user-read-currently-playing",
        "app-remote-control",
        "playlist-modify-public",
        "playlist-modify-private",
        "playlist-read-private",
        "user-read-email",
        "user-read-private"
    )

    private var authState: AuthState = AuthState()

    private fun startAuthService(): AuthorizationServiceConfiguration{
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse(authEndpoint),
            Uri.parse(tokenEndpoint)
        )

        authState = AuthState(serviceConfig)

        return serviceConfig
    }

    private fun getAuthCode(activity: Activity) {
        val builder = AuthorizationRequest.Builder(
            startAuthService(),
            SPOTIFY_CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(SPOTIFY_REDIRECT_URI)
        )

        val authRequest = builder
            .setScopes(scopes)
            .build()

        val authService = AuthorizationService(activity)
        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        activity.startActivityForResult(authIntent, AUTH_REQUEST_CODE)
    }

    fun updateAuth(context: Context, response: AuthorizationResponse, exception: AuthorizationException?){
        authState.update(response, exception)
        writeAuthState(context, authState)
    }

    fun updateAuth(context: Context, response: TokenResponse, exception: AuthorizationException?){
        authState.update(response, exception)
        writeAuthState(context, authState)
    }

    private fun readAuthState(context: Context): AuthState{
        val authPrefs = context.getSharedPreferences("auth", MODE_PRIVATE)
        val stateJson = authPrefs.getString("stateJson", null)
        return if (stateJson != null) {
            AuthState.jsonDeserialize(stateJson)
        } else {
            AuthState()
        }
    }


    private fun writeAuthState(context: Context, state: AuthState){
        val authPrefs = context.getSharedPreferences("auth", MODE_PRIVATE)
        authPrefs.edit()
            .putString("stateJson", state.jsonSerializeString())
            .apply()
    }


    fun getToken(context: Context, callback: (String) -> Unit){
        val authService = AuthorizationService(context)
        authState.performActionWithFreshTokens(authService) { accessToken, _, _ ->
            if (accessToken == null){
                return@performActionWithFreshTokens
            }

            callback(accessToken)
        }
    }


    fun initSpotifyAuth(activity: Activity){
        authState = readAuthState(activity)
        if (!authState.isAuthorized){
            getAuthCode(activity)
        }
    }


    fun isAuthorized(context: Context): Boolean {
        authState = readAuthState(context)
        return authState.isAuthorized
    }
}