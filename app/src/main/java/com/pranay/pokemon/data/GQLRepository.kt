package com.pranay.pokemon.data

import com.pranay.pokemon.data.dto.GQLRequestBody

abstract class GQLRepository {
    abstract val gqlQuery: String
    abstract val operationName: String
    protected fun getRequestBody(variables: Map<String, Any>): GQLRequestBody =
        GQLRequestBody(gqlQuery, variables, operationName)
}