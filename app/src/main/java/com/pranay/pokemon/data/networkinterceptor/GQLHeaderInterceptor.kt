package com.pranay.pokemon.data.networkinterceptor

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.POST
import javax.inject.Inject

class GQLHeaderInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //Adds necessary headers for GQL request
        var request = chain.request()
        if (request.method == POST::class.simpleName && request.url.pathSegments[0]=="graphql") {
            request = request.newBuilder()
                .addHeader("Content-Type","application/json")
                .addHeader("X-Method-Used","graphiql")
                .build()
        }
        return chain.proceed(request)
    }
}